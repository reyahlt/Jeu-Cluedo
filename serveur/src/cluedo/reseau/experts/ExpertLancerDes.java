package cluedo.reseau.experts;

import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertLancerDes extends ExpertMessage {
    private static final String REGEX = "^@LANCER_DES$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {


        try {
            Joueur joueur = connexion.getJoueur();
            if (joueur == null) return "ERROR Joueur non connecté";
            int res = serveur.getSuperviseur().lancerLesDes(joueur);
            serveur.diffuser("Les Des de " + joueur.getNom() + " : " + res);
            return "";
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
