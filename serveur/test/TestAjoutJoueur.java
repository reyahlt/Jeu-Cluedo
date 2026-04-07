
import cluedo.enums.EPersonnage;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.JoueurDejaExistantException;
import exception.PartieDejaDemarreeException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAjoutJoueur {

    private Superviseur superviseur;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        PlateauCluedo plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
    }

    @Test
    void ajoutJoueurNominal() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);

        assertEquals(1, superviseur.getJoueurs().size());
        assertEquals("Alice", superviseur.getJoueurs().get(0).getNom());
        assertEquals(EPersonnage.Mademoiselle_Rose, superviseur.getJoueurs().get(0).getPersonnage());
        assertEquals(0, superviseur.getJoueurs().get(0).getNombreCartes());
        assertNull(superviseur.getPartie().getEnigme());
    }

    @Test
    void ajoutJoueurNomDejaPrisDoitEchouer() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);

        assertThrows(JoueurDejaExistantException.class, () ->
                superviseur.ajouterJoueur("Alice", EPersonnage.Colonel_Moutarde));
    }

    @Test
    void ajoutJoueurPersonnageDejaPrisDoitEchouer() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);

        assertThrows(JoueurDejaExistantException.class, () ->
                superviseur.ajouterJoueur("Bob", EPersonnage.Mademoiselle_Rose));
    }

    @Test
    void ajoutJoueurApresDemarrageDoitEchouer() throws Exception {
        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        assertThrows(PartieDejaDemarreeException.class, () ->
                superviseur.ajouterJoueur("Donald", EPersonnage.Reverend_Olive));
    }
}