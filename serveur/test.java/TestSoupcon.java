
import cluedo.histoire.EArme;
import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;
import cluedo.histoire.Soupcon;
import cluedo.plateau.CaseCluedo;
import cluedo.plateau.PlateauCluedo;

import exception.PlateauCluedoException;
import exception.ActionIllegaleException;
import exception.PartieNonDemarreeException;
import metiers.*;

import cluedo.histoire.Enigme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les soupçons.
 */
class TestSoupcon {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;
    private PlateauCluedo plateau;

    @BeforeEach
    void setUp() throws Exception {
         plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }

    private void placerDansPiece(Joueur joueur, int ligne, int colonne) throws Exception {
        CaseCluedo[][] grille = plateau.getGrille();
        joueur.getCaseCourante().liberer();
        joueur.setCaseCourante(grille[ligne][colonne]);
        // Simuler dés lancés
        try {
            var f = Joueur.class.getDeclaredField("aDejeLanceLeDes");
            f.setAccessible(true);
            f.set(joueur, true);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // -------------------------------------------------------------------------
    // Tests to pass
    // -------------------------------------------------------------------------

    @Test
    void soupconner_dans_bonne_piece_devrait_Reussir() throws Exception {
        placerDansPiece(alice, 8, 0); // Bibliothèque
        superviseur.lancerLesDes(alice);
        Soupcon s = superviseur.soupconner(alice,
                EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier);
        assertNotNull(s);
        assertEquals(alice, s.getAuteur());
        assertEquals(EPersonnage.Colonel_Moutarde, s.getPersonnage());
        assertEquals(ELieu.Bibliotheque, s.getLieu());
        assertEquals(EArme.Chandelier, s.getArme());
    }

    @Test
    void soupconner_marque_joueurCommeAyantSoupçonne() throws Exception {
        placerDansPiece(alice, 8, 0);
        superviseur.lancerLesDes(alice);
        superviseur.soupconner(alice,
                EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier);
        assertTrue(alice.IlASoupçonner());
    }

    @Test
    void soupcon_enregistre_dans_historique() throws Exception {
        placerDansPiece(alice, 8, 0);
        superviseur.lancerLesDes(alice);
        superviseur.soupconner(alice,
                EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier);
        assertEquals(1, superviseur.getPartie().getHistoriqueSoupcons().size());
    }

    @Test
    void soupcon_refute_si_joueur_possede_carte() throws Exception {
        placerDansPiece(alice, 8, 0);
        superviseur.lancerLesDes(alice);

        // Trouver un soupçon que Bob ou Charles peut réfuter
        // (ils ont forcément des cartes en main)
        Joueur repondant = superviseur.getJoueurs().get(1); // Bob
        Carte uneCarteDeRepondant = repondant.getCartes().get(0);

        EPersonnage p = EPersonnage.Colonel_Moutarde;
        ELieu l = ELieu.Bibliotheque;
        EArme a = EArme.Chandelier;

        // Construire un soupçon correspondant à la carte de Bob
        if (uneCarteDeRepondant.getType() == Carte.TypeCarte.PERSONNAGE)
            p = EPersonnage.valueOf(uneCarteDeRepondant.getNom());
        else if (uneCarteDeRepondant.getType() == Carte.TypeCarte.LIEU)
            l = ELieu.valueOf(uneCarteDeRepondant.getNom());
        else
            a = EArme.valueOf(uneCarteDeRepondant.getNom());

        // S'assurer que le lieu correspond à la pièce d'Alice
        if (l != ELieu.Bibliotheque) {
            // Skip ce test si on ne peut pas construire le bon soupçon
            return;
        }

        Soupcon s = superviseur.soupconner(alice, p, l, a);
        assertTrue(s.aEteRefute());
        assertNotNull(s.getCarteMontrée());
        assertNotNull(s.getJoueurRepondant());
    }

    @Test
    void soupcon_non_refute_si_personne_na_carte() throws Exception {
        placerDansPiece(alice, 8, 0);
        superviseur.lancerLesDes(alice);

        // Trouver une carte que personne d'autre ne possède
        // (elle est soit dans l'énigme, soit chez Alice)
        Enigme enigme = superviseur.getPartie().getEnigme();
        EPersonnage p = enigme.getPersonnage().getPersonnage();
        ELieu l = ELieu.Bibliotheque;
        EArme a = enigme.getArme().getArme();

        // S'assurer que Alice ne possède pas ces cartes non plus
        boolean alicePossedeP = alice.getCartes().stream()
                .anyMatch(c -> c.getType() == Carte.TypeCarte.PERSONNAGE
                        && c.getNom().equals(p.name()));
        boolean alicePossedeA = alice.getCartes().stream()
                .anyMatch(c -> c.getType() == Carte.TypeCarte.ARME
                        && c.getNom().equals(a.name()));

        if (alicePossedeP || alicePossedeA) return; // skip si Alice les a

        Soupcon s = superviseur.soupconner(alice, p, l, a);
        // L'énigme contient p et a → personne d'autre ne les a
        assertFalse(s.aEteRefute());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void soupconner_hors_de_sa_piece_devrait_LeverException() throws Exception {
        // Alice est dans un couloir (case de départ)
        superviseur.lancerLesDes(alice);
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconner(alice,
                        EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier));
    }

    @Test
    void soupconner_mauvaise_piece_devrait_LeverException() throws Exception {
        placerDansPiece(alice, 8, 0); // Bibliothèque
        superviseur.lancerLesDes(alice);
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconner(alice,
                        EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Chandelier));
    }

    @Test
    void soupconner_deuxFois_devrait_LeverException() throws Exception {
        placerDansPiece(alice, 8, 0);
        superviseur.lancerLesDes(alice);
        superviseur.soupconner(alice,
                EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier);
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconner(alice,
                        EPersonnage.Professeur_Violet, ELieu.Bibliotheque, EArme.Corde));
    }

    @Test
    void soupconner_pas_son_tour_devrait_LeverException() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconner(bob,
                        EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier));
    }

    @Test
    void soupconner_partie_non_demarree_devrait_LeverException() throws PlateauCluedoException {
        PlateauCluedo p= new PlateauCluedo();
        Superviseur sup = new Superviseur(p);
        assertThrows(PartieNonDemarreeException.class, () ->
                sup.soupconner(alice,
                        EPersonnage.Colonel_Moutarde, ELieu.Bibliotheque, EArme.Chandelier));
    }
}
