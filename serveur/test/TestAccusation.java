import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.metier.Accusation;
import cluedo.metier.Enigme;
import cluedo.metier.Joueur;
import cluedo.metier.Partie;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import exception.ActionIllegaleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestAccusation {

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

        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Madame_Leblanc);
        superviseur.demarrerPartie();

        alice   = superviseur.getJoueurParNom("Alice");
        bob     = superviseur.getJoueurParNom("Bob");
        charles = superviseur.getJoueurParNom("Charles");
    }



    @Test
    void accuserMauvaisJoueurDoitEchouer() {
        assertThrows(ActionIllegaleException.class, () ->
                bob.accuse( EPersonnage.Colonel_Moutarde,ELieu.Cuisine, EArme.Corde));
    }



    @Test
    void accusationIncorrectElimineLejoueur() throws Exception {
        Enigme enigme = superviseur.getPartie().getEnigme();

        // Trouver une combinaison qui n'est PAS l'énigme
        EPersonnage mauvaisPersonnage = trouverAutrePersonnage(enigme.getPersonnage().getPersonnage());
        ELieu mauvaisLieu             = trouverAutreLieu(enigme.getLieu().getLieu());
        EArme mauvaiseArme            = trouverAutreArme(enigme.getArme().getArme());

        alice.accuse(mauvaisPersonnage, mauvaisLieu, mauvaiseArme);

        assertTrue(alice.isElimine());
        assertFalse(superviseur.getPartie().isTerminee());
    }


    @Test
    void accusationCorrecteTerminePartie() throws Exception {
        Enigme enigme = superviseur.getPartie().getEnigme();

        EPersonnage p = enigme.getPersonnage().getPersonnage();
        ELieu       l = enigme.getLieu().getLieu();
        EArme       a = enigme.getArme().getArme();

        Accusation acc = alice.accuse( p, l, a);

        assertTrue(acc.isCorrecte());
        assertTrue(superviseur.getPartie().isTerminee());
        assertEquals(alice, superviseur.getPartie().getGagnant());
    }


    @Test
    void accusationEnregistreeDansHistorique() throws Exception {
        Enigme enigme = superviseur.getPartie().getEnigme();
        alice.accuse(
                enigme.getPersonnage().getPersonnage(),
                enigme.getLieu().getLieu(),
                enigme.getArme().getArme());

        assertEquals(1, superviseur.getPartie().getHistoriqueAccusations().size());
    }

    @Test
    void tousJoueursEliminesTerminePartie() throws Exception {
        Enigme enigme = superviseur.getPartie().getEnigme();
        EPersonnage mauvaisP = trouverAutrePersonnage(enigme.getPersonnage().getPersonnage());
        ELieu       mauvaiseL = trouverAutreLieu(enigme.getLieu().getLieu());
        EArme       mauvaiseA = trouverAutreArme(enigme.getArme().getArme());

        // Alice s'accuse (incorrectement) → éliminée
        alice.accuse( mauvaisP, mauvaiseL, mauvaiseA);
        superviseur.finirTour(alice); // même si éliminée, finir le tour pour passer à Bob
        // Bob s'accuse
        bob.accuse( mauvaisP, mauvaiseL, mauvaiseA);
        superviseur.finirTour(bob);
        // Charles s'accuse
        charles.accuse(mauvaisP, mauvaiseL, mauvaiseA);

        assertTrue(superviseur.getPartie().isTerminee());
        assertNull(superviseur.getPartie().getGagnant());
    }



    private EPersonnage trouverAutrePersonnage(EPersonnage enigmeP) {
        for (EPersonnage p : EPersonnage.values())
            if (p != enigmeP) return p;
        throw new IllegalStateException("Impossible");
    }

    private ELieu trouverAutreLieu(ELieu enigmeL) {
        for (ELieu l : ELieu.values())
            if (l != enigmeL) return l;
        throw new IllegalStateException("Impossible");
    }

    private EArme trouverAutreArme(EArme enigmeA) {
        for (EArme a : EArme.values())
            if (a != enigmeA) return a;
        throw new IllegalStateException("Impossible");
    }
}
