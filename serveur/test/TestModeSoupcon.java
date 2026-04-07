
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

import static org.junit.jupiter.api.Assertions.assertThrows;

    class TestModeSoupcon {

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
        void accuserPendantSoupconDoitEchouer() throws Exception {
            alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau
            alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

            assertThrows(ActionIllegaleException.class, () ->
                    superviseur.accuser(alice, EPersonnage.Colonel_Moutarde, ELieu.Bureau, EArme.Corde));
        }

        @Test
        void finirTourPendantSoupconDoitEchouer() throws Exception {
            alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau
            alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

            assertThrows(ActionIllegaleException.class, () ->
                    superviseur.finirTour(alice));
        }

        @Test
        void lancerDesPendantSoupconDoitEchouer() throws Exception {
            alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau
            alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

            assertThrows(ActionIllegaleException.class, () ->
                    superviseur.lancerLesDes(alice));
        }
        @Test
        void deplacerPendantSoupconDoitEchouer() throws Exception {
            alice.setCaseCourante(superviseur.getPlateau().getCase(1, 1)); // Bureau

            alice.soupconne(ELieu.Bureau, EPersonnage.Colonel_Moutarde, EArme.Corde);

            assertThrows(ActionIllegaleException.class, () ->
                    superviseur.deplacerJoueur(alice, 2, 1)); // tentative de déplacement
        }
    }

