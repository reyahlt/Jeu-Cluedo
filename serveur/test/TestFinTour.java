
import cluedo.histoire.EPersonnage;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import exception.ActionIllegaleException;
import metiers.Joueur;
import exception.PartieNonDemarreeException;
import metiers.Superviseur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la fin de tour.
 */
class TestFinTour {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;
    private Joueur charles;

    @BeforeEach
    void setUp() throws Exception {
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        alice   = superviseur.getJoueurParNom("Alice");
        bob     = superviseur.getJoueurParNom("Bob");
        charles = superviseur.getJoueurParNom("Charles");
    }

    // -------------------------------------------------------------------------
    // Tests to pass
    // -------------------------------------------------------------------------

    @Test
    void finirTour_passe_au_joueur_suivant() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);
        assertEquals("Bob", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void finirTour_reinitialise_aDejeLanceLeDes() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);
        assertFalse(alice.DejaLanceLeDes());
    }

    @Test
    void finirTour_reinitialise_deplacementsRestants() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);
        assertEquals(0, alice.getDeplacementsRestants());
    }

    @Test
    void finirTour_reinitialise_aDejeSoupçonne() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);
        assertFalse(alice.aSoupçonne());
    }

    @Test
    void ordre_des_tours_est_circulaire() throws Exception {
        // Alice → Bob → Charles → Alice
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);

        superviseur.lancerLesDes(bob);
        superviseur.finirTour(bob);

        superviseur.lancerLesDes(charles);
        superviseur.finirTour(charles);

        assertEquals("Alice", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void joueurElimine_est_saute_dans_ordre_des_tours() throws Exception {
        // Alice accuse mal → éliminée
        superviseur.lancerLesDes(alice);
        superviseur.accuser(alice,
                superviseur.getPartie().getEnigme().getPersonnage().getPersonnage(),
                superviseur.getPartie().getEnigme().getLieu().getLieu(),
                trouverMauvaiseArme());
        superviseur.finirTour(alice);

        // Le tour doit aller à Bob (pas à Alice qui est éliminée)
        assertEquals("Bob", superviseur.getJoueurCourant().getNom());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void finirTour_pas_son_tour_devrait_LeverException() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.finirTour(bob));
    }

    @Test
    void finirTour_partie_non_demarree_devrait_LeverException() throws PlateauCluedoException {
        PlateauCluedo p = new PlateauCluedo();
        Superviseur sup = new Superviseur(p);
        assertThrows(PartieNonDemarreeException.class, () ->
                sup.finirTour(alice));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private cluedo.histoire.EArme trouverMauvaiseArme() {
        cluedo.histoire.EArme armeEnigme =
                superviseur.getPartie().getEnigme().getArme().getArme();
        for (cluedo.histoire.EArme a : cluedo.histoire.EArme.values())
            if (a != armeEnigme) return a;
        throw new IllegalStateException("Impossible de trouver une mauvaise arme");
    }
}
