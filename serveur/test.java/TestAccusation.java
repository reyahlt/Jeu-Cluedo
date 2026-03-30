
import cluedo.histoire.EArme;
import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;
import cluedo.histoire.Enigme;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import exception.ActionIllegaleException;
import exception.PartieNonDemarreeException;
import metiers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests unitaires pour les accusations.
 */
class TestAccusation {

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
    void accuser_correctement_devrait_TerminerLaPartie() throws Exception {
        superviseur.lancerLesDes(alice);
        Enigme enigme = superviseur.getPartie().getEnigme();
        Accusation acc = superviseur.accuser(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                enigme.getArme().getArme());
        assertTrue(acc.isCorrecte());
        assertTrue(superviseur.getPartie().isTerminee());
        assertEquals(alice, superviseur.getPartie().getGagnant());
    }

    @Test
    void accuser_incorrectement_devrait_EliminerJoueur() throws Exception {
        superviseur.lancerLesDes(alice);
        Enigme enigme = superviseur.getPartie().getEnigme();
        EArme mauvaiseArme = trouverMauvaiseArme(enigme);
        superviseur.accuser(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                mauvaiseArme);
        assertTrue(alice.isElimine());
        assertFalse(superviseur.getPartie().isTerminee());
    }

    @Test
    void accuser_incorrectement_partie_continue_avec_autres_joueurs() throws Exception {
        superviseur.lancerLesDes(alice);
        Enigme enigme = superviseur.getPartie().getEnigme();
        superviseur.accuser(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                trouverMauvaiseArme(enigme));
        superviseur.finirTour(alice);
        // Bob doit pouvoir jouer
        assertEquals("Bob", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void accusation_enregistree_dans_historique() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.accuser(alice,
                EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Chandelier);
        assertEquals(1, superviseur.getPartie().getHistoriqueAccusations().size());
    }

    @Test
    void tousLesJoueursElimines_partie_terminee_sans_gagnant() throws Exception {
        Enigme enigme = superviseur.getPartie().getEnigme();

        // Alice accuse mal
        superviseur.lancerLesDes(alice);
        superviseur.accuser(alice,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                trouverMauvaiseArme(enigme));
        superviseur.finirTour(alice);

        // Bob accuse mal
        superviseur.lancerLesDes(bob);
        superviseur.accuser(bob,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                trouverMauvaiseArme(enigme));
        superviseur.finirTour(bob);

        // Charles accuse mal
        superviseur.lancerLesDes(charles);
        superviseur.accuser(charles,
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                trouverMauvaiseArme(enigme));

        assertTrue(superviseur.getPartie().isTerminee());
        assertNull(superviseur.getPartie().getGagnant());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void accuser_pas_son_tour_devrait_LeverException() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.accuser(bob,
                        EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Chandelier));
    }

    @Test
    void accuser_partie_non_demarree_devrait_LeverException() throws PlateauCluedoException {
        PlateauCluedo p =new PlateauCluedo();
        Superviseur sup = new Superviseur(p);
        assertThrows(PartieNonDemarreeException.class, () ->
                sup.accuser(alice,
                        EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Chandelier));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private EArme trouverMauvaiseArme(Enigme enigme) {
        for (EArme a : EArme.values())
            if (a != enigme.getArme().getArme()) return a;
        throw new IllegalStateException("Impossible de trouver une mauvaise arme");
    }
}
