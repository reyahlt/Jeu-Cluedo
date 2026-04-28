package cluedo.reseau.experts;

import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertFinTour extends ExpertMessage {
    private static final String REGEX = "^@FIN_TOUR$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {

        try {
            Joueur joueur = connexion.getJoueur();
            if (joueur == null) return "ERROR Joueur non connecté";
            serveur.getSuperviseur().finirTour(joueur);
            serveur.diffuser("OK FIN_TOUR JOUEUR_COURANT " +
                    serveur.getSuperviseur().getJoueurCourant().getNom());
            return "";
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
