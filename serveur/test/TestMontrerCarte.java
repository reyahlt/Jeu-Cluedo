import cluedo.carte.Carte;
import cluedo.carte.CarteArme;
import cluedo.carte.CarteLieu;
import cluedo.carte.CartePersonnage;
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

class TestMontrerCarte {

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

    private void aliceSoupconneDansBureau() throws Exception {
        alice.setCaseCourante(plateau.getCase(1, 1)); // Bureau
        alice.soupconne(EPersonnage.Colonel_Moutarde,ELieu.Bureau,  EArme.Corde);
    }

    @Test
    void montrerCarteSansSoupconDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                bob.montrerCarte(null));
    }

    @Test
    void montrerCarteParMauvaisJoueurDoitEchouer() throws Exception {
        aliceSoupconneDansBureau();

        assertThrows(ActionIllegaleException.class, () ->
                charles.montrerCarte(null));
    }

    @Test
    void auteurSoupconNePeutPasRepondre() throws Exception {
        aliceSoupconneDansBureau();

        assertThrows(ActionIllegaleException.class, () ->
                alice.montrerCarte(null));
    }

    @Test
    void montrerCarteNAppartientPasAuJoueurDoitEchouer() throws Exception {
        bob.viderCartes();
        charles.viderCartes();

        aliceSoupconneDansBureau();

        assertThrows(CarteInvalideException.class, () ->
                bob.montrerCarte(new CarteArme(EArme.Corde)));
    }

    @Test
    void montrerCarteNonLieeAuSoupconDoitEchouer() throws Exception {
        bob.viderCartes();
        charles.viderCartes();
        bob.ajouterCarte(new CarteArme(EArme.Matraque));

        aliceSoupconneDansBureau();

        assertThrows(CarteInvalideException.class, () ->
                bob.montrerCarte(new CarteArme(EArme.Matraque)));
    }

    @Test
    void bobSansCarteMontreNull() throws Exception {
        bob.viderCartes();
        charles.viderCartes();

        aliceSoupconneDansBureau();

        Carte retour = bob.montrerCarte(null);
        assertNull(retour);
    }

    @Test
    void bobMontreCarteCorrespondante() throws Exception {
        bob.viderCartes();
        charles.viderCartes();

        CarteArme corde = new CarteArme(EArme.Corde);
        bob.ajouterCarte(corde);

        aliceSoupconneDansBureau();

        Carte retour = bob.montrerCarte(corde);
        assertEquals(corde, retour);
    }

    @Test
    void joueurElimineNePeutPasRepondre() throws Exception {
        bob.eliminer();
        aliceSoupconneDansBureau();

        assertThrows(ActionIllegaleException.class, () ->
                bob.montrerCarte(null));
    }

    @Test
    void cartesMontrablesBobAvecCartesCorrespondantes() throws Exception {
        bob.viderCartes();
        charles.viderCartes();
        bob.ajouterCarte(new CarteArme(EArme.Corde));
        bob.ajouterCarte(new CarteLieu(ELieu.Bureau));
        bob.ajouterCarte(new CartePersonnage(EPersonnage.Colonel_Moutarde));

        aliceSoupconneDansBureau();

        List<Carte> montrables = bob.cartesMontrables(
                superviseur.getPartie().getHistoriqueSoupcons().get(0));

        assertEquals(3, montrables.size());
    }

    @Test
    void cartesMontrablesBobSansCarteCorrespondante() throws Exception {
        bob.viderCartes();
        charles.viderCartes();
        bob.ajouterCarte(new CarteArme(EArme.Matraque));

        aliceSoupconneDansBureau();

        List<Carte> montrables = bob.cartesMontrables(
                superviseur.getPartie().getHistoriqueSoupcons().get(0));

        assertTrue(montrables.isEmpty());
    }

    @Test
    void montrerNullAlorsQueBobPeutRefuterDoitEchouer() throws Exception {
        bob.viderCartes();
        charles.viderCartes();
        bob.ajouterCarte(new CarteArme(EArme.Corde));

        aliceSoupconneDansBureau();

        assertThrows(CarteInvalideException.class, () ->
                bob.montrerCarte(null));
    }
}