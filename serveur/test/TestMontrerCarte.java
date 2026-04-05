
import cluedo.histoire.*;
import cluedo.histoire.Enigme;
import cluedo.plateau.PlateauCluedo;
import metiers.Joueur;
import metiers.Superviseur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour montrerCarte et cartesMontrables.
 */
class TestMontrerCarte {

    private Joueur alice;
    private Joueur bob;
    private Superviseur superviseur;

    @BeforeEach
    void setUp() throws Exception {
        PlateauCluedo plateau =new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }

    @Test
    void montrerCarte_retourne_null_si_aucune_carte_correspondante() {
        // Construire un soupçon avec des cartes que Bob ne possède pas
        // En utilisant les cartes de l'énigme (personne ne les possède)
        Enigme enigme = superviseur.getPartie().getEnigme();
        Soupcon soupcon = new Soupcon(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                enigme.getArme().getArme());

        // Bob ne possède pas les cartes de l'énigme
        // (sauf coïncidence rare — on vérifie)
        boolean bobPossedeUne = bob.getCartes().stream().anyMatch(c ->
                c.getNom().equals(enigme.getPersonnage().getNom()) ||
                c.getNom().equals(enigme.getLieu().getNom()) ||
                c.getNom().equals(enigme.getArme().getNom()));

        if (!bobPossedeUne) {
            assertNull(bob.montrerCarte(soupcon));
        }
    }

    @Test
    void cartesMontrables_retourne_liste_vide_si_aucune_correspondance() {
        Enigme enigme = superviseur.getPartie().getEnigme();
        Soupcon soupcon = new Soupcon(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                enigme.getArme().getArme());

        boolean bobPossedeUne = bob.getCartes().stream().anyMatch(c ->
                c.getNom().equals(enigme.getPersonnage().getNom()) ||
                c.getNom().equals(enigme.getLieu().getNom()) ||
                c.getNom().equals(enigme.getArme().getNom()));

        if (!bobPossedeUne) {
            assertTrue(bob.cartesMontrables(soupcon).isEmpty());
        }
    }

    @Test
    void montrerCarte_retourne_une_seule_carte() {
        // Prendre la première carte de Bob et construire un soupçon correspondant
        Carte carteDeBob = bob.getCartes().get(0);
        Soupcon soupcon = construireSoupconPour(carteDeBob);

        Carte montree = bob.montrerCarte(soupcon);
        assertNotNull(montree);
        assertEquals(carteDeBob.getNom(), montree.getNom());
    }

    @Test
    void cartesMontrables_contient_carte_si_joueur_la_possede() {
        Carte carteDeBob = bob.getCartes().get(0);
        Soupcon soupcon = construireSoupconPour(carteDeBob);

        List<Carte> montrables = bob.cartesMontrables(soupcon);
        assertFalse(montrables.isEmpty());
        assertTrue(montrables.stream()
                .anyMatch(c -> c.getNom().equals(carteDeBob.getNom())));
    }

    @Test
    void possedeCarte_devrait_RetournerTrue_si_joueur_la_possede() {
        Carte carteDeBob = bob.getCartes().get(0);
        assertTrue(bob.possedeCartes(carteDeBob));
    }

    @Test
    void possedeCarte_devrait_RetournerFalse_si_joueur_ne_la_possede_pas() {
        // Une carte de l'énigme → personne ne la possède
        Carte carteEnigme = superviseur.getPartie().getEnigme().getPersonnage();
        boolean bobLaPossede = bob.getCartes().stream()
                .anyMatch(c -> c.equals(carteEnigme));
        if (!bobLaPossede) {
            assertFalse(bob.possedeCartes(carteEnigme));
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Soupcon construireSoupconPour(Carte carte) {
        EPersonnage p = EPersonnage.Colonel_Moutarde;
        ELieu l       = ELieu.Bibliotheque;
        EArme a       = EArme.Chandelier;

        switch (carte.getType()) {
            case PERSONNAGE -> p = EPersonnage.valueOf(carte.getNom());
            case LIEU       -> l = ELieu.valueOf(carte.getNom());
            case ARME       -> a = EArme.valueOf(carte.getNom());
        }
        return new Soupcon(alice, p, l, a);
    }
}
