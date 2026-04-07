import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.CaseCluedo;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;
import exception.DeplacementImpossibleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDeplacements {

    private Superviseur superviseur;
    private PlateauCluedo plateau;
    private Joueur alice;
    private Joueur bob;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);

        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }

    // Helper : place alice en (8,7) avec 1 déplacement prêt
    private void placerAlice8_7Avec1Dep() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);
    }

    // =========================================================
    // Déplacement sans avoir lancé les dés
    // =========================================================

    @Test
    void deplacementSansLancerDesDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        // Ne lance PAS les dés
        assertThrows(ActionIllegaleException.class, () ->
                alice.deplacerVers(plateau.getCase(7, 7)));
    }

    // =========================================================
    // Déplacement vers case occupée
    // =========================================================

    @Test
    void deplacementVersCaseOccupeeDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        bob.setCaseCourante(plateau.getCase(9, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        assertThrows(DeplacementImpossibleException.class, () ->
                alice.deplacerVers(plateau.getCase(9, 7)));
    }

    // =========================================================
    // Déplacement hors voisinage
    // =========================================================

    @Test
    void deplacementHorsVoisinageDoitEchouer() throws Exception {
        placerAlice8_7Avec1Dep();

        assertThrows(DeplacementImpossibleException.class, () ->
                alice.deplacerVers(plateau.getCase(10, 7)));
    }

    // =========================================================
    // Déplacement nominal vers case voisine
    // =========================================================

    @Test
    void deplacementNominalVersCaseVoisine() throws Exception {
        placerAlice8_7Avec1Dep();

        CaseCluedo depart  = plateau.getCase(8, 7);
        CaseCluedo arrivee = plateau.getCase(7, 7);

        alice.deplacerVers(arrivee);

        // La case d'arrivée est occupée par Alice
        assertFalse(arrivee.estLibre());
        assertEquals(alice, arrivee.getOccupant());
        // La case de départ est libérée
        assertTrue(depart.estLibre());
        // Le compteur de déplacements est décrémenté
        assertEquals(0, alice.getDeplacementsRestants());
    }

    // =========================================================
    // Déplacement quand plus de déplacement disponible
    // =========================================================

    @Test
    void deplacementSansMouvementsRestantsDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(0);

        assertThrows(DeplacementImpossibleException.class, () ->
                alice.deplacerVers(plateau.getCase(7, 7)));
    }

    // =========================================================
    // Entrée dans une pièce
    // =========================================================

    @Test
    void entreeDansBibliothequeDepuis8_7Vers8_6() throws Exception {
        placerAlice8_7Avec1Dep();

        alice.deplacerVers(plateau.getCase(8, 6));

        assertEquals("Bibliotheque", alice.getPieceActuelle().name());
    }

    // =========================================================
    // Déplacement intra-pièce (gratuit : même pièce)
    // =========================================================

    @Test
    void deplacementDansMemePieceBibliothequeVers8_0() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 6)); // déjà dans Bibliothèque
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        alice.deplacerVers(plateau.getCase(8, 0));

        assertEquals("Bibliotheque", alice.getPieceActuelle().name());
    }

    // =========================================================
    // Entrée dans Bureau puis passage secret vers Cuisine
    // =========================================================

    @Test
    void entreeDansBureauDepuis4_6() throws Exception {
        alice.setCaseCourante(plateau.getCase(4, 6));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(2);

        alice.deplacerVers(plateau.getCase(1, 1));

        // La case (4,6) est libre
        assertTrue(plateau.getCase(4, 6).estLibre());
        // La case (1,1) est occupée
        assertFalse(plateau.getCase(1, 1).estLibre());
        // Alice est dans le bureau
        assertEquals("Bureau", alice.getPieceActuelle().name());
        // Il reste 1 déplacement
        assertEquals(1, alice.getDeplacementsRestants());
    }

    @Test
    void passageSecretBureauVersCuisine() throws Exception {
        alice.setCaseCourante(plateau.getCase(4, 6));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(2);

        alice.deplacerVers(plateau.getCase(1, 1)); // entre dans le bureau
        alice.deplacerVers(plateau.getCase(22, 22)); // passage secret

        // (1,1) est libre
        assertTrue(plateau.getCase(1, 1).estLibre());
        // (22,22) contient Alice
        assertFalse(plateau.getCase(22, 22).estLibre());
        assertEquals(alice, plateau.getCase(22, 22).getOccupant());
        // Alice est dans la cuisine
        assertEquals("Cuisine", alice.getPieceActuelle().name());
        // Plus de déplacements
        assertEquals(0, alice.getDeplacementsRestants());
    }
}