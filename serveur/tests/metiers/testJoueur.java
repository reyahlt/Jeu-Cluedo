package metiers;

import cluedo.histoire.EPersonnage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class testJoueur {
    @Test
    public void testCreerJoueur() {
        joueur joueurr = new joueur("Alice",EPersonnage.Mademoiselle_Rose);
        assertEquals("Alice", joueurr.getNom());
        assertEquals(EPersonnage.Mademoiselle_Rose, joueurr.getPersonnage());

    }
}
