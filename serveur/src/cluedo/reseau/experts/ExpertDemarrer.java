package cluedo.reseau.experts;

import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertDemarrer extends ExpertMessage {
    private static final String REGEX = "^@DEMARRER$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        try {
            serveur.getSuperviseur().demarrerPartie();
            serveur.diffuser("OK PARTIE_DEMARREE JOUEUR_COURANT " +
                    serveur.getSuperviseur().getJoueurCourant().getNom());
            return "";
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
