package cluedo.reseau.protocole;

import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.socket.ConnexionJoueur;

public abstract class ExpertMessage {
    private ExpertMessage suivant;

    public void setSuivant(ExpertMessage suivant) {
        this.suivant = suivant;
    }

    public String traiter(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        if (serveur.getSuperviseur().getPartie().isTerminee()) {
            return "ERROR Partie terminée";
        }
        if (peutTraiter(message)) {
            return executer(connexion, serveur, message);
        }
        if (suivant != null) {
            return suivant.traiter(connexion, serveur, message);
        }
        return "ERROR Message inconnu ou mal formé";
    }

    protected abstract boolean peutTraiter(String message);

    protected abstract String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message);
}
