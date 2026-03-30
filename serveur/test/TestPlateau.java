

import cluedo.histoire.ELieu;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPlateau {
    @Test
    public void testInitialisationPlateau() throws PlateauCluedoException {
        // ACT : s'il n'y a pas eu d'exception,on vérifie quelques cases au cas où
        PlateauCluedo plateau = new PlateauCluedo();

        // On vérifie les 4 coins
        assertEquals(ELieu.Bureau, plateau.getCase(0,0).getPiece());
        assertEquals(ELieu.Salon, plateau.getCase(0,PlateauCluedo.NOMBRE_COLONNES_PLATEAU-1).getPiece());
        assertEquals(ELieu.Veranda, plateau.getCase(PlateauCluedo.NOMBRE_LIGNES_PLATEAU-2,0).getPiece());
        assertEquals(ELieu.Cuisine, plateau.getCase(PlateauCluedo.NOMBRE_LIGNES_PLATEAU-2,PlateauCluedo.NOMBRE_COLONNES_PLATEAU-1).getPiece());

        // Une porte
        assertNull(plateau.getCase(4,6).getPiece());
        assertTrue(plateau.getCase(4,6).estVoisin(plateau.getCase(3,6)));

        // Un passage secret (du bureau vers la cuisine)
        assertTrue(plateau.getCase(3,0).estVoisin(plateau.getCase(PlateauCluedo.NOMBRE_LIGNES_PLATEAU-2,PlateauCluedo.NOMBRE_COLONNES_PLATEAU-1)));
    }

    @Test
    public void testPlateauExceptions() throws PlateauCluedoException {
        PlateauCluedo plateau = new PlateauCluedo();

        assertThrows(PlateauCluedoException.class, () -> plateau.getCase(25,0));
        assertThrows(PlateauCluedoException.class, () -> plateau.getCase(-1,0));
    }
}
