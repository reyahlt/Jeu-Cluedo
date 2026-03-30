
import cluedo.histoire.EPersonnage;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import exception.JoueurDejaExistantException;
import exception.NombreJoueursInsuffisantException;
import exception.PartieDejaDemarreeException;
import metiers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le démarrage de la partie.
 * Scénarios issus du sujet de projet.
 */
class TestDemarragePartie {

    private Superviseur superviseur;

    @BeforeEach
    void setUp() throws PartieDejaDemarreeException, JoueurDejaExistantException, PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
    }

    // -------------------------------------------------------------------------
    // Tests to pass
    // -------------------------------------------------------------------------

    @Test
    void demarrerPartie_devrait_InitialiserEnigme() throws Exception {
        superviseur.demarrerPartie();
        assertNotNull(superviseur.getPartie().getEnigme());
        assertNotNull(superviseur.getPartie().getEnigme().getPersonnage());
        assertNotNull(superviseur.getPartie().getEnigme().getLieu());
        assertNotNull(superviseur.getPartie().getEnigme().getArme());
    }

    @Test
    void demarrerPartie_3joueurs_chacunDevrait_Avoir6Cartes() throws Exception {
        superviseur.demarrerPartie();
        for (Joueur j : superviseur.getJoueurs())
            assertEquals(6, j.getNombreCartes(),
                    j.getNom() + " devrait avoir 6 cartes");
    }

    @Test
    void demarrerPartie_isPartieDemarree_devrait_EtreTrue() throws Exception {
        superviseur.demarrerPartie();
        assertTrue(superviseur.isPartieDemarree());
    }

    @Test
    void demarrerPartie_premierJoueur_devrait_EtreAlice() throws Exception {
        superviseur.demarrerPartie();
        assertEquals("Alice", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void demarrerPartie_joueurSuivantAlice_devrait_EtreBob() throws Exception {
        superviseur.demarrerPartie();
        Joueur alice = superviseur.getJoueurParNom("Alice");
        assertEquals("Bob", superviseur.getJoueurSuivant(alice).getNom());
    }

    @Test
    void demarrerPartie_joueurSuivantBob_devrait_EtreCharles() throws Exception {
        superviseur.demarrerPartie();
        Joueur bob = superviseur.getJoueurParNom("Bob");
        assertEquals("Charles", superviseur.getJoueurSuivant(bob).getNom());
    }

    @Test
    void demarrerPartie_joueurSuivantCharles_devrait_EtreAlice() throws Exception {
        superviseur.demarrerPartie();
        Joueur charles = superviseur.getJoueurParNom("Charles");
        assertEquals("Alice", superviseur.getJoueurSuivant(charles).getNom());
    }

    @Test
    void demarrerPartie_positionAlice_devrait_Etre_0_16() throws Exception {
        superviseur.demarrerPartie();
        Joueur alice = superviseur.getJoueurParNom("Alice");
        assertEquals(0,  alice.getLigne());
        assertEquals(16, alice.getColonne());
    }

    @Test
    void demarrerPartie_positionBob_devrait_Etre_5_0() throws Exception {
        superviseur.demarrerPartie();
        Joueur bob = superviseur.getJoueurParNom("Bob");
        assertEquals(5, bob.getLigne());
        assertEquals(0, bob.getColonne());
    }

    @Test
    void demarrerPartie_positionCharles_devrait_Etre_7_23() throws Exception {
        superviseur.demarrerPartie();
        Joueur charles = superviseur.getJoueurParNom("Charles");
        assertEquals(7,  charles.getLigne());
        assertEquals(23, charles.getColonne());
    }

    @Test
    void demarrerPartie_ajoutJoueurEnsuite_devrait_EtreImpossible() throws Exception {
        superviseur.demarrerPartie();
        assertThrows(PartieDejaDemarreeException.class, () ->
                superviseur.ajouterJoueur("Donald", EPersonnage.Professeur_Violet));
    }

    @Test
    void demarrerPartie_casesDeDepart_doivent_EtreOccupees() throws Exception {
        superviseur.demarrerPartie();
        Joueur alice   = superviseur.getJoueurParNom("Alice");
        Joueur bob     = superviseur.getJoueurParNom("Bob");
        Joueur charles = superviseur.getJoueurParNom("Charles");
        assertFalse(alice.getCaseCourante().estLibre());
        assertFalse(bob.getCaseCourante().estLibre());
        assertFalse(charles.getCaseCourante().estLibre());
    }

    // -------------------------------------------------------------------------
    // Tests to fail
    // -------------------------------------------------------------------------

    @Test
    void demarrerPartie_sansJoueurs_devrait_LeverException() throws PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();
        Superviseur sup = new Superviseur(plateau);
        assertThrows(NombreJoueursInsuffisantException.class, sup::demarrerPartie);
    }

    @Test
    void demarrerPartie_avecDeuxJoueurs_devrait_LeverException()
            throws PartieDejaDemarreeException, JoueurDejaExistantException, PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();
        Superviseur sup = new Superviseur(plateau);
        sup.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        sup.ajouterJoueur("Bob",   EPersonnage.Colonel_Moutarde);
        assertThrows(NombreJoueursInsuffisantException.class, sup::demarrerPartie);
    }

    @Test
    void demarrerPartie_deuxFois_devrait_LeverException() throws Exception {
        superviseur.demarrerPartie();
        assertThrows(PartieDejaDemarreeException.class,
                () -> superviseur.demarrerPartie());
    }
}
