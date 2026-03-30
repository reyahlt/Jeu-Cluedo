import cluedo.histoire.*;
import metiers.Carte;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Carte.
 */
class TestCarte {

    @Test
    void creerCartePersonnage_devrait_AvoirBonType() {
        Carte c = new Carte(EPersonnage.Colonel_Moutarde);
        assertEquals(Carte.TypeCarte.PERSONNAGE, c.getType());
    }

    @Test
    void creerCarteLieu_devrait_AvoirBonType() {
        Carte c = new Carte(ELieu.Bureau);
        assertEquals(Carte.TypeCarte.LIEU, c.getType());
    }

    @Test
    void creerCarteArme_devrait_AvoirBonType() {
        Carte c = new Carte(EArme.Chandelier);
        assertEquals(Carte.TypeCarte.ARME, c.getType());
    }

    @Test
    void creerCartePersonnage_devrait_AvoirBonNom() {
        Carte c = new Carte(EPersonnage.Colonel_Moutarde);
        assertEquals("Colonel_Moutarde", c.getNom());
    }

    @Test
    void creerCarteLieu_devrait_AvoirBonNom() {
        Carte c = new Carte(ELieu.Bureau);
        assertEquals("Bureau", c.getNom());
    }

    @Test
    void creerCarteArme_devrait_AvoirBonNom() {
        Carte c = new Carte(EArme.Chandelier);
        assertEquals("Chandelier", c.getNom());
    }

    @Test
    void deuxCartesMemeTypeEtNom_devrait_EtreEgales() {
        Carte c1 = new Carte(EPersonnage.Colonel_Moutarde);
        Carte c2 = new Carte(EPersonnage.Colonel_Moutarde);
        assertEquals(c1, c2);
    }

    @Test
    void deuxCartesTypeDifferent_devrait_EtreInegales() {
        Carte c1 = new Carte(EPersonnage.Colonel_Moutarde);
        Carte c2 = new Carte(EArme.Chandelier);
        assertNotEquals(c1, c2);
    }

    @Test
    void creerCarte_typeNull_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Carte(null, "test"));
    }

    @Test
    void creerCarte_nomVide_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Carte(Carte.TypeCarte.PERSONNAGE, ""));
    }

    @Test
    void creerCarte_nomNull_devrait_LeverException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Carte(Carte.TypeCarte.PERSONNAGE, null));
    }

    @Test
    void toString_devrait_Contenir_TypeEtNom() {
        Carte c = new Carte(EArme.Chandelier);
        assertTrue(c.toString().contains("ARME"));
        assertTrue(c.toString().contains("Chandelier"));
    }
}
