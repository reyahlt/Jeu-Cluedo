import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestLancerDesDes {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;

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

        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }


    @Test
    void lancerDesResultatEntre2Et12() throws Exception {
        int res = superviseur.lancerLesDes(alice);
        assertTrue(res >= 2 && res <= 12,
                "Le résultat doit être entre 2 et 12, obtenu : " + res);
    }

    @Test
    void lancerDesMetAJourDeplacementsRestants() throws Exception {
        int res = superviseur.lancerLesDes(alice);
        assertEquals(res, alice.getDeplacementsRestants());
    }

    @Test
    void lancerDesMetDejaLanceAVrai() throws Exception {
        superviseur.lancerLesDes(alice);
        assertTrue(alice.DejaLanceLeDes());
    }


    @Test
    void seulJoueurCourantPeutLancerLesDes() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(bob));
    }


    @Test
    void joueurNePeutPasLancerDeuxFois() throws Exception {
        superviseur.lancerLesDes(alice);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(alice));
    }


    @Test
    void apresFinTourJoueurSuivantPeutLancer() throws Exception {
        superviseur.lancerLesDes(alice);
        superviseur.finirTour(alice);

        // C'est bob qui peut maintenant lancer
        int res = superviseur.lancerLesDes(bob);
        assertTrue(res >= 2 && res <= 12);
    }

    @Test
    void chaqueDeDonne1A6() throws Exception {
        superviseur.lancerLesDes(alice);
        int v1 = alice.getDe1().getValeur();
        int v2 = alice.getDe2().getValeur();
        assertTrue(v1 >= 1 && v1 <= 6);
        assertTrue(v2 >= 1 && v2 <= 6);
    }
}
