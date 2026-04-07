
import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.CaseCluedo;
import cluedo.plateau.PlateauCluedo;
import exception.DeplacementImpossibleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestDeplacements {

    private Superviseur superviseur;
    private PlateauCluedo plateau;
    private Joueur alice;
    private Joueur bob;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);

        superviseur.ajouterJoueur("Alice", EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob", EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        alice = superviseur.getJoueurParNom("Alice");
        bob = superviseur.getJoueurParNom("Bob");
    }

    @Test
    void deplacementVersCaseOccupeeDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        bob.setCaseCourante(plateau.getCase(9, 7));
        alice.setDeplacementsRestants(1);
        alice.lancerLesDes(); // à adapter si ton code empêche ce montage
        alice.setDeplacementsRestants(1);

        assertThrows(DeplacementImpossibleException.class, () ->
                alice.deplacerVers(plateau.getCase(9, 7)));
    }

    @Test
    void deplacementHorsVoisinageDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        assertThrows(DeplacementImpossibleException.class, () ->
                alice.deplacerVers(plateau.getCase(10, 7)));
    }

    @Test
    void deplacementNominalVersCaseVoisine() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        CaseCluedo depart = plateau.getCase(8, 7);
        CaseCluedo arrivee = plateau.getCase(7, 7);

        alice.deplacerVers(arrivee);

        assertFalse(arrivee.estLibre());
        assertEquals(alice, arrivee.getOccupant());
        assertTrue(depart.estLibre());
        assertEquals(0, alice.getDeplacementsRestants());
    }

    @Test
    void entreeDansBibliothequeDepuis8_7Vers8_6() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 7));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        alice.deplacerVers(plateau.getCase(8, 6));

        assertEquals("Bibliotheque", alice.getPieceActuelle().name());
    }

    @Test
    void deplacementDansMemePieceBibliothequeVers8_0() throws Exception {
        alice.setCaseCourante(plateau.getCase(8, 6));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(1);

        alice.deplacerVers(plateau.getCase(8, 0));

        assertEquals("Bibliotheque", alice.getPieceActuelle().name());
    }

    @Test
    void entreeDansBureauPuisPassageSecretVersCuisine() throws Exception {
        alice.setCaseCourante(plateau.getCase(4, 6));
        alice.lancerLesDes();
        alice.setDeplacementsRestants(2);

        alice.deplacerVers(plateau.getCase(1, 1));

        assertEquals("Bureau", alice.getPieceActuelle().name());
        assertEquals(1, alice.getDeplacementsRestants());

        alice.deplacerVers(plateau.getCase(22, 22));

        assertEquals("Cuisine", alice.getPieceActuelle().name());
        assertEquals(0, alice.getDeplacementsRestants());
    }
}