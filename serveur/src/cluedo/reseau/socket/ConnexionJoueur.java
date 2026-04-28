package cluedo.reseau.socket;

import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;

import java.net.Socket;

public class ConnexionJoueur {
    private final Socket socket;
    private final ServeurCluedo serveur;
    private final ThreadConnexion threadConnexion;
    private String pseudo;
    private boolean valide;

    public ConnexionJoueur(Socket socket, ServeurCluedo serveur) {
        this.socket = socket;
        this.serveur = serveur;
        this.valide = false;
        this.threadConnexion = new ThreadConnexion(this, socket);
    }

    public Socket getSocket() {
        return socket;
    }

    public ServeurCluedo getServeur() {
        return serveur;
    }

    public ThreadConnexion getThreadConnexion() {
        return threadConnexion;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean estValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Joueur getJoueur() {
        return serveur.getJoueurDepuisConnexion(this);
    }

    public void envoyer(String message) {
        threadConnexion.envoyerMessage(message);
    }
}
