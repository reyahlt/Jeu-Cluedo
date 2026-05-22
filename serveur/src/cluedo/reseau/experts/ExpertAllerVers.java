package cluedo.reseau.experts;

import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertAllerVers extends ExpertMessage {
    private static final String REGEX = "^@ALLER_VERS \\d+ \\d+$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {

        try {
            String[] mots = message.split(" ");
            int ligne = Integer.parseInt(mots[1]);
            int colonne = Integer.parseInt(mots[2]);

            Joueur joueur = connexion.getJoueur();
            if (joueur == null) return "ERROR Joueur non connecté";
            serveur.getSuperviseur().deplacerJoueur(joueur, ligne, colonne);

            serveur.diffuser("DEPLACEMENT " + joueur.getNom() + " VERS " + ligne + " " + colonne);
            return "";
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
