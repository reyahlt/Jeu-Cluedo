
import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.NombreJoueursInsuffisantException;

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

    @Test
    void demarrageImpossibleAvecMoinsDeTroisJoueurs() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);

        assertThrows(NombreJoueursInsuffisantException.class, () ->
                superviseur.demarrerPartie());
    }

    @Test
    void demarragePartieNominal() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);

        superviseur.demarrerPartie();

        assertNotNull(superviseur.getPartie().getEnigme());

        for (Joueur j : superviseur.getJoueurs()) {
            assertEquals(6, j.getNombreCartes());
        }

        assertEquals("Alice", superviseur.getJoueurCourant().getNom());
        assertEquals("Bob", superviseur.getJoueurSuivant(superviseur.getJoueurParNom("Alice")).getNom());
        assertEquals("Charles", superviseur.getJoueurSuivant(superviseur.getJoueurParNom("Bob")).getNom());
        assertEquals("Alice", superviseur.getJoueurSuivant(superviseur.getJoueurParNom("Charles")).getNom());

        assertEquals(0, superviseur.getJoueurParNom("Alice").getLigne());
        assertEquals(16, superviseur.getJoueurParNom("Alice").getColonne());

        assertEquals(5, superviseur.getJoueurParNom("Bob").getLigne());
        assertEquals(0, superviseur.getJoueurParNom("Bob").getColonne());

        assertEquals(7, superviseur.getJoueurParNom("Charles").getLigne());
        assertEquals(23, superviseur.getJoueurParNom("Charles").getColonne());
    }
}