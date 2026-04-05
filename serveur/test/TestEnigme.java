import cluedo.histoire.*;
import cluedo.histoire.Enigme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Enigme.
 */
class TestEnigme {

    private Enigme enigme;

    @BeforeEach
    void setUp() {
        enigme = new Enigme(
                new Carte(EPersonnage.Colonel_Moutarde),
                new Carte(ELieu.Bureau),
                new Carte(EArme.Chandelier)
        );
    }

    @Test
    void getPersonnage_devrait_RetournerBonneCartePersonnage() {
        assertEquals("Colonel_Moutarde", enigme.getPersonnage().getNom());
        assertEquals(Carte.TypeCarte.PERSONNAGE, enigme.getPersonnage().getType());
    }

    @Test
    void getLieu_devrait_RetournerBonneCarteLieu() {
        assertEquals("Bureau", enigme.getLieu().getNom());
        assertEquals(Carte.TypeCarte.LIEU, enigme.getLieu().getType());
    }

    @Test
    void getArme_devrait_RetournerBonneCarteArme() {
        assertEquals("Chandelier", enigme.getArme().getNom());
        assertEquals(Carte.TypeCarte.ARME, enigme.getArme().getType());
    }

    @Test
    void verifierAccusation_correcte_devrait_RetournerTrue() {
        assertTrue(enigme.verifierAccusation(
                EPersonnage.Colonel_Moutarde,
                ELieu.Bureau,
                EArme.Chandelier));
    }

    @Test
    void verifierAccusation_mauvaisePersonnage_devrait_RetournerFalse() {
        assertFalse(enigme.verifierAccusation(
                EPersonnage.Mademoiselle_Rose,
                ELieu.Bureau,
                EArme.Chandelier));
    }

    @Test
    void verifierAccusation_mauvaislieu_devrait_RetournerFalse() {
        assertFalse(enigme.verifierAccusation(
                EPersonnage.Colonel_Moutarde,
                ELieu.Cuisine,
                EArme.Chandelier));
    }

    @Test
    void verifierAccusation_mauvaiseArme_devrait_RetournerFalse() {
        assertFalse(enigme.verifierAccusation(
                EPersonnage.Colonel_Moutarde,
                ELieu.Bureau,
                EArme.Corde));
    }

    @Test
    void creerEnigme_avecCarteNull_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Enigme(null, new Carte(ELieu.Bureau), new Carte(EArme.Chandelier)));
    }

    @Test
    void creerEnigme_avecMauvaisType_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Enigme(
                        new Carte(EArme.Chandelier),  // mauvais type pour personnage
                        new Carte(ELieu.Bureau),
                        new Carte(EArme.Corde)));
    }
}
