package cluedo.reseau.experts;

import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertDeconnexion extends ExpertMessage {
    private static final String REGEX = "^@DECONNEXION$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        String pseudo = connexion.getPseudo();
        serveur.supprimerConnexion(connexion);
        connexion.getThreadConnexion().fin();
        if (pseudo != null) {
            serveur.diffuser("INFO " + pseudo + " s'est déconnecté");
        }
        return "OK DECONNECTE";
    }
}
