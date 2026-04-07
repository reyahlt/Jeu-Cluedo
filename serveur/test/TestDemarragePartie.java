import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.NombreJoueursInsuffisantException;
import exception.PartieDejaDemarreeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDemarragePartie {

    private Superviseur superviseur;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
    }

    // =========================================================
    // Cas d'échec : pas assez de joueurs
    // =========================================================

    @Test
    void demarrageImpossibleSansJoueurs() {
        assertThrows(NombreJoueursInsuffisantException.class, () ->
                superviseur.demarrerPartie());
    }

    @Test
    void demarrageImpossibleAvecUnJoueur() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);

        assertThrows(NombreJoueursInsuffisantException.class, () ->
                superviseur.demarrerPartie());
    }

    @Test
    void demarrageImpossibleAvecDeuxJoueurs() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",   EPersonnage.Colonel_Moutarde);

        assertThrows(NombreJoueursInsuffisantException.class, () ->
                superviseur.demarrerPartie());
    }

    @Test
    void demarrageDejaDemarreSouleverException() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        assertThrows(PartieDejaDemarreeException.class, () ->
                superviseur.demarrerPartie());
    }

    // =========================================================
    // Cas nominal : 3 joueurs
    // =========================================================

    @Test
    void demarragePartieNominal() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);

        superviseur.demarrerPartie();

        // Énigme initialisée avec un personnage, un lieu et une arme
        assertNotNull(superviseur.getPartie().getEnigme());
        assertNotNull(superviseur.getPartie().getEnigme().getPersonnage());
        assertNotNull(superviseur.getPartie().getEnigme().getLieu());
        assertNotNull(superviseur.getPartie().getEnigme().getArme());

        // Chaque joueur reçoit 6 cartes (18 cartes / 3 joueurs)
        for (Joueur j : superviseur.getJoueurs()) {
            assertEquals(6, j.getNombreCartes());
        }

        // C'est Alice de jouer en premier
        assertEquals("Alice", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void ordreDesJoueursEstCirculaire() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        Joueur alice   = superviseur.getJoueurParNom("Alice");
        Joueur bob     = superviseur.getJoueurParNom("Bob");
        Joueur charles = superviseur.getJoueurParNom("Charles");

        assertEquals("Bob",   superviseur.getJoueurSuivant(alice).getNom());
        assertEquals("Charles", superviseur.getJoueurSuivant(bob).getNom());
        assertEquals("Alice", superviseur.getJoueurSuivant(charles).getNom());
    }

    @Test
    void positionsDeDepart3Joueurs() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        Joueur alice   = superviseur.getJoueurParNom("Alice");
        Joueur bob     = superviseur.getJoueurParNom("Bob");
        Joueur charles = superviseur.getJoueurParNom("Charles");

        assertEquals(0,  alice.getLigne());
        assertEquals(16, alice.getColonne());

        assertEquals(5, bob.getLigne());
        assertEquals(0, bob.getColonne());

        assertEquals(7,  charles.getLigne());
        assertEquals(23, charles.getColonne());
    }

    @Test
    void distributionCartesAvec4Joueurs() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.ajouterJoueur("Donald",  EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();

        // 18 cartes / 4 joueurs : les 2 premiers en ont 5, les 2 derniers en ont 4
        int total = 0;
        for (Joueur j : superviseur.getJoueurs()) total += j.getNombreCartes();
        assertEquals(18, total);
    }

    @Test
    void partieEstMaerqueeCommeDemarree() throws Exception {
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);

        assertFalse(superviseur.isPartieDemarree());
        superviseur.demarrerPartie();
        assertTrue(superviseur.isPartieDemarree());
    }
}