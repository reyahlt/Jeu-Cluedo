package cluedo.reseau.socket;

import cluedo.reseau.metier.ServeurCluedo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadAcceptConnexion extends Thread {
    private final ServeurCluedo serveur;
    private final ServerSocket serverSocket;

    public ThreadAcceptConnexion(ServeurCluedo serveur) {
        this.serveur = serveur;
        try {
            this.serverSocket = new ServerSocket(serveur.getPort());
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le port " + serveur.getPort(), e);
        }
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ConnexionJoueur connexion = new ConnexionJoueur(socket, serveur);
                serveur.ajouterConnexion(connexion);
                connexion.envoyer("OK Connexion TCP établie. Identifiez-vous avec @CONNEXION pseudo personnage");
            } catch (IOException e) {
                throw new RuntimeException("Erreur d'acceptation de connexion", e);
            }
        }
    }
}
