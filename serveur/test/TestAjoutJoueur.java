
import cluedo.histoire.EPersonnage;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import metiers.Joueur;
import exception.JoueurDejaExistantException;
import exception.PartieDejaDemarreeException;
import metiers.Superviseur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'ajout de joueurs.
 */
class TestAjoutJoueur {

    private Superviseur superviseur;

    @BeforeEach
    void setUp() throws PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
    }

    // -------------------------------------------------------------------------
    // Tests to pass
    // -------------------------------------------------------------------------

    @Test
    void ajouterUnJoueurValide_devrait_Reussir()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertEquals(1, superviseur.getJoueurs().size());
    }

    @Test
    void ajouterJoueur_nomEtPersonnageCorrects()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        Joueur j = superviseur.getJoueurs().get(0);
        assertEquals("Alice", j.getNom());
        assertEquals(EPersonnage.Mademoiselle_Rose, j.getPersonnage());
    }

    @Test
    void ajouterTroisJoueurs_devrait_Reussir()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        assertEquals(3, superviseur.getJoueurs().size());
    }

    @Test
    void joueurAvantDepart_devrait_AvoirZeroCartes()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertEquals(0, superviseur.getJoueurs().get(0).getNombreCartes());
    }

    @Test
    void joueurAvantDepart_caseCourante_devrait_EtreNull()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertNull(superviseur.getJoueurs().get(0).getCaseCourante());
    }

    @Test
    void partieAvantDemarrage_enigme_devrait_EtreNull() {
        assertNull(superviseur.getPartie().getEnigme());
    }

    @Test
    void partieAvantDemarrage_isPartieDemarree_devrait_EtreFalse() {
        assertFalse(superviseur.isPartieDemarree());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void ajouterJoueur_nomDejaUtilise_devrait_LeverException()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertThrows(JoueurDejaExistantException.class, () ->
                superviseur.ajouterJoueur("Alice", EPersonnage.Colonel_Moutarde));
    }

    @Test
    void ajouterJoueur_nomDejaUtilise_casseDifferente_devrait_LeverException()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertThrows(JoueurDejaExistantException.class, () ->
                superviseur.ajouterJoueur("alice", EPersonnage.Colonel_Moutarde));
    }

    @Test
    void ajouterJoueur_personnageDejaUtilise_devrait_LeverException()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        assertThrows(JoueurDejaExistantException.class, () ->
                superviseur.ajouterJoueur("Bob", EPersonnage.Mademoiselle_Rose));
    }

    @Test
    void ajouterJoueur_partieDejaDeMarree_devrait_LeverException()
            throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        assertThrows(PartieDejaDemarreeException.class, () ->
                superviseur.ajouterJoueur("Donald", EPersonnage.Professeur_Violet));
    }

    @Test
    void ajouterJoueur_nomVide_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                superviseur.ajouterJoueur("", EPersonnage.Mademoiselle_Rose));
    }

    @Test
    void ajouterJoueur_nomNull_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                superviseur.ajouterJoueur(null, EPersonnage.Mademoiselle_Rose));
    }

    @Test
    void ajouterJoueur_personnageNull_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                superviseur.ajouterJoueur("Alice", null));
    }

    @Test
    void ajouterPlusDeSixJoueurs_devrait_LeverException()
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.ajouterJoueur("Donald",  EPersonnage.Madame_Pervenche);
        superviseur.ajouterJoueur("Eve",     EPersonnage.Professeur_Violet);
        superviseur.ajouterJoueur("Frank",   EPersonnage.Madame_Leblanc);
        assertThrows(IllegalArgumentException.class, () ->
                superviseur.ajouterJoueur("Grace", EPersonnage.Mademoiselle_Rose));
    }
}
