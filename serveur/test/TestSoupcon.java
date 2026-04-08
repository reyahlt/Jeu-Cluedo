import cluedo.carte.CarteArme;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSoupcon {

    private Superviseur superviseur;
    private PlateauCluedo plateau;
    private Joueur alice;
    private Joueur bob;
    private Joueur charles;

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
        charles = superviseur.getJoueurParNom("Charles");
    }

    @Test
    void soupconnerHorsPieceDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne( EPersonnage.Colonel_Moutarde,ELieu.Cuisine, EArme.Corde));
    }

    @Test
    void soupconnerDansMauvaisePieceDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne( EPersonnage.Colonel_Moutarde,ELieu.Cuisine, EArme.Corde));
    }

    @Test
    void soupconnerPasLeTourDoitEchouer() throws Exception {
        bob.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        assertThrows(ActionIllegaleException.class, () ->
                bob.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde));
    }

    @Test
    void soupconnerDeuxFoisMemeTourDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne(EPersonnage.Reverend_Olive,ELieu.Bureau,  EArme.Revolver));
    }

    @Test
    void soupconnerDansBonnePiecePasse() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        assertDoesNotThrow(() ->
                alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde));

        assertTrue(alice.aSoupconne());
    }

    @Test
    void soupconEnregistreDansHistorique() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne( EPersonnage.Colonel_Moutarde,ELieu.Bureau, EArme.Corde);

        assertEquals(1, superviseur.getPartie().getHistoriqueSoupcons().size());
    }

    @Test
    void soupconnerJoueurElimineDoitEchouer() throws Exception {
        alice.eliminer();
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde));
    }

    @Test
    void deplacerPendantSoupconDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne( EPersonnage.Colonel_Moutarde,ELieu.Bureau, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.deplacerJoueur(alice, 2, 1));
    }

    @Test
    void finirTourPendantSoupconDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.finirTour(alice));
    }

    @Test
    void lancerDesPendantSoupconDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(alice));
    }

    @Test
    void accuserPendantSoupconDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.accuser(alice, EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Corde));
    }

    @Test
    void finirTourApresResolutionDuSoupconReinitialiseASoupconne() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        bob.viderCartes();
        charles.viderCartes();

        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertTrue(alice.aSoupconne());

        bob.montrerCarte(null);
        charles.montrerCarte(null);

        superviseur.finirTour(alice);

        assertFalse(alice.aSoupconne());
    }

    @Test
    void soupconDeplaceLeSuspectDansLaPiece() throws Exception {
        bob.setCaseCourante(plateau.getCase(22, 22)); // Cuisine au départ du test
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertEquals(ELieu.Bureau, bob.getPieceActuelle());
    }

    @Test
    void soupconDeplaceLArmeDansLaPiece() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau

        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);

        assertEquals(ELieu.Bureau, superviseur.getPartie().getPositionArme(EArme.Corde));
    }
}