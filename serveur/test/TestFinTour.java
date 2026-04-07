import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestFinTour {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;
    private Joueur charles;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);

        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        alice   = superviseur.getJoueurParNom("Alice");
        bob     = superviseur.getJoueurParNom("Bob");
        charles = superviseur.getJoueurParNom("Charles");
    }

    // =========================================================
    // Cas nominal : passage au joueur suivant
    // =========================================================

    @Test
    void finirTourPasseABob() throws Exception {
        superviseur.finirTour(alice);
        assertEquals("Bob", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void finirTourPasseDeBobaCharles() throws Exception {
        superviseur.finirTour(alice);
        superviseur.finirTour(bob);
        assertEquals("Charles", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void finirTourDeCharlesRevientAAlice() throws Exception {
        superviseur.finirTour(alice);
        superviseur.finirTour(bob);
        superviseur.finirTour(charles);
        assertEquals("Alice", superviseur.getJoueurCourant().getNom());
    }

    // =========================================================
    // Fin de tour réinitialise l'état du joueur
    // =========================================================

    @Test
    void finirTourReinitialiseDejaLanceDes() throws Exception {
        superviseur.lancerLesDes(alice);
        assertTrue(alice.DejaLanceLeDes());

        superviseur.finirTour(alice);
        // Alice a été réinitialisée
        assertFalse(alice.DejaLanceLeDes());
    }

    @Test
    void finirTourReinitialiseDeplacements() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);

        assertEquals(0, alice.getDeplacementsRestants());
    }

    @Test
    void finirTourReinitialiseASoupconne() throws Exception {
        alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau
        bob.viderCartes();
        charles.viderCartes();

        alice.soupconne(cluedo.enums.ELieu.Bureau, EPersonnage.Colonel_Moutarde, cluedo.enums.EArme.Corde);
        assertTrue(alice.aSoupconne());

        bob.montrerCarte(null); // résout le soupçon
        superviseur.finirTour(alice);

        assertFalse(alice.aSoupconne());
    }

    // =========================================================
    // Cas d'échec : ce n'est pas son tour
    // =========================================================

    @Test
    void finirTourMauvaisJoueurDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.finirTour(bob));
    }

    // =========================================================
    // Saut du joueur éliminé dans la rotation
    // =========================================================

    @Test
    void joueurElimineSauteAuTourSuivant() throws Exception {
        bob.eliminer();

        superviseur.finirTour(alice);
        // Bob est éliminé → doit passer directement à Charles
        assertEquals("Charles", superviseur.getJoueurCourant().getNom());
    }
}
