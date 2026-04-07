import cluedo.carte.Carte;
import cluedo.carte.CarteArme;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;
import exception.CarteInvalideException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
                alice.soupconne(ELieu.Cuisine, EPersonnage.Colonel_Moutarde, EArme.Corde));
    }

    @Test
    void soupconnerDansMauvaisePieceDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne(ELieu.Cuisine, EPersonnage.Colonel_Moutarde, EArme.Corde));
    }

    @Test
    void soupconnerPasLeTourDoitEchouer() throws Exception {
        bob.setCaseCourante(plateau.getCase(1, 1));

        assertThrows(ActionIllegaleException.class, () ->
                bob.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde));
    }

    @Test
    void soupconnerDeuxFoisMemeTourDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne(ELieu.Bureau, EPersonnage.Reverend_Olive, EArme.Revolver));
    }

    @Test
    void soupconnerDansBonnePiecePasse() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));

        assertDoesNotThrow(() ->
                alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde));

        assertTrue(alice.aSoupconne());
    }

    @Test
    void soupconEnregistreDansHistorique() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertEquals(1, superviseur.getPartie().getHistoriqueSoupcons().size());
    }

    @Test
    void soupconnerJoueurElimineDoitEchouer() throws Exception {
        alice.eliminer();
        alice.setCaseCourante(plateau.getCase(1, 1));

        assertThrows(ActionIllegaleException.class, () ->
                alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde));
    }

    @Test
    void deplacementImpossiblePendantSoupcon() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.deplacerJoueur(alice, 2, 1));
    }

    @Test
    void montrerCarteValideTermineModeSoupcon() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        List<Carte> montrablesParBob = bob.cartesMontrables(
                superviseur.getPartie().getHistoriqueSoupcons().get(0));

        if (!montrablesParBob.isEmpty()) {
            Carte carte = montrablesParBob.get(0);
            Carte retour = bob.montrerCarte(carte);
            assertEquals(carte, retour);
        }
    }

    @Test
    void montrerCarteNullSiBobNaPasDeCarteMontrable() throws Exception {
        bob.viderCartes();
        charles.viderCartes();

        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        Carte retour = bob.montrerCarte(null);
        assertNull(retour);
    }

    @Test
    void montrerCarteSansSoupconEnCoursDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                bob.montrerCarte(null));
    }

    @Test
    void montrerCarteMauvaisJoueurDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                charles.montrerCarte(null));
    }

    @Test
    void montrerCarteQuiNAppartientPasAuJoueurDoitEchouer() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        Carte carteEtrangere = new CarteArme(EArme.Matraque);
        bob.viderCartes();

        assertThrows(CarteInvalideException.class, () ->
                bob.montrerCarte(carteEtrangere));
    }

    @Test
    void montrerCarteNonLieeAuSoupconDoitEchouer() throws Exception {
        bob.viderCartes();
        bob.ajouterCarte(new CarteArme(EArme.Matraque));

        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertThrows(CarteInvalideException.class, () ->
                bob.montrerCarte(new CarteArme(EArme.Matraque)));
    }

    @Test
    void auteurSoupconNePeutPasRepondre() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1));
        alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                alice.montrerCarte(null));
    }
}