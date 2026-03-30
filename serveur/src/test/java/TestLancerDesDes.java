
import cluedo.histoire.EPersonnage;
import cluedo.plateau.PlateauCluedo;
import cluedo.plateau.PlateauCluedoException;
import metiers.ActionIllegaleException;
import metiers.Joueur;
import metiers.PartieNonDemarreeException;
import metiers.Superviseur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le lancer des dés.
 */
class TestLancerDesDes {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;

    @BeforeEach
    void setUp() throws Exception {
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",   EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }

    // -------------------------------------------------------------------------
    // Tests to pass
    // -------------------------------------------------------------------------

    @Test
    void lancerDes_joueurCourant_devrait_Reussir()
            throws PartieNonDemarreeException, ActionIllegaleException {
        int resultat = superviseur.lancerLesDes(alice);
        assertTrue(resultat >= 2 && resultat <= 12,
                "Résultat attendu entre 2 et 12, obtenu : " + resultat);
    }

    @RepeatedTest(20)
    void lancerDes_resultat_toujours_entre_2_et_12() throws Exception {
        PlateauCluedo plateau = new PlateauCluedo();
        Superviseur sup = new Superviseur(plateau);
        sup.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        sup.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        sup.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        sup.demarrerPartie();
        Joueur a = sup.getJoueurParNom("Alice");
        int r = sup.lancerLesDes(a);
        assertTrue(r >= 2 && r <= 12);
    }

    @Test
    void apresLancerDes_deplacementsRestants_entre_2_et_12()
            throws PartieNonDemarreeException, ActionIllegaleException {
        superviseur.lancerLesDes(alice);
        assertTrue(alice.getDeplacementsRestants() >= 2
                && alice.getDeplacementsRestants() <= 12);
    }

    @Test
    void apresLancerDes_aDejeLanceLeDes_devrait_EtreTrue()
            throws PartieNonDemarreeException, ActionIllegaleException {
        superviseur.lancerLesDes(alice);
        assertTrue(alice.DejaLanceLeDes());
    }

    @Test
    void valeursDes_correspondent_a_somme()
            throws PartieNonDemarreeException, ActionIllegaleException {
        int resultat = superviseur.lancerLesDes(alice);
        assertEquals(resultat,
                alice.getDe1().getValeur() + alice.getDe2().getValeur());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void lancerDes_pastSonTour_devrait_LeverException() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(bob));
    }

    @Test
    void lancerDes_deuxFois_devrait_LeverException()
            throws PartieNonDemarreeException, ActionIllegaleException {
        superviseur.lancerLesDes(alice);
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(alice));
    }

    @Test
    void lancerDes_partieNonDemarree_devrait_LeverException() throws PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();
        Superviseur sup = new Superviseur(plateau);
        assertThrows(PartieNonDemarreeException.class, () ->
                sup.lancerLesDes(alice));
    }
}
