
import cluedo.carte.Carte;
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

import static org.junit.jupiter.api.Assertions.*;

class TestActionsJoueur {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;
    private Joueur charles;

    @BeforeEach
    void setUp() throws Exception {
        Superviseur.reset();
        Partie.reset();
        PlateauCluedo plateau = new PlateauCluedo();
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
    void seulJoueurCourantPeutLancerLesDes() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(bob));
    }

    @Test
    void joueurCourantNePeutPasLancerDeuxFois() throws Exception {
        int res = superviseur.lancerLesDes(alice);
        assertTrue(res >= 2 && res <= 12);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.lancerLesDes(alice));
    }

    @Test
    void finirTourPasseAuJoueurSuivant() throws Exception {
        superviseur.finirTour(alice);
        assertEquals("Bob", superviseur.getJoueurCourant().getNom());
    }

    @Test
    void soupconnerHorsPieceDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconne(alice, EPersonnage.Colonel_Moutarde, ELieu.Cuisine, EArme.Corde));
    }

    @Test
    void soupconnerDansMauvaisePieceDoitEchouer() throws Exception {
        alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.soupconne(alice, EPersonnage.Colonel_Moutarde, ELieu.Cuisine, EArme.Corde));
    }

    @Test
    void soupconnerDansBonnePiecePasse() throws Exception {
        alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau

        assertDoesNotThrow(() ->
                superviseur.soupconne(alice, EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Corde));
    }

    @Test
    void accuserMauvaisJoueurCourantDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.accuser(bob, EPersonnage.Colonel_Moutarde, ELieu.Cuisine, EArme.Corde));
    }

    @Test
    void montrerCarteSansSoupconDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.montrerCarte(bob, null));
    }

    @Test
    void montrerCarteParMauvaisJoueurDoitEchouer() throws Exception {
        alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1));
        superviseur.soupconne(alice, EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Corde);

        assertThrows(ActionIllegaleException.class, () ->
                superviseur.montrerCarte(charles, null));
    }

    @Test
    void montrerCarteInvalideDoitEchouer() throws Exception {
        alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1));
        superviseur.soupconne(alice, EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Corde);

        if (!bob.getCartes().isEmpty()) {
            Carte cartePasForcementBonne = bob.getCartes().get(0);
            assertThrows(CarteInvalideException.class, () ->
                    superviseur.montrerCarte(bob, cartePasForcementBonne));
        }
    }
}