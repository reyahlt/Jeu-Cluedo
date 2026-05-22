package cluedo.reseau.socket;

import cluedo.reseau.metier.ServeurCluedo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadAcceptConnexion extends Thread {
    private final ServeurCluedo serveur;
    private final ServerSocket serverSocket;
    private boolean running;

    public ThreadAcceptConnexion(ServeurCluedo serveur) {
        this.serveur = serveur;
        this.running = true;

        try {
            this.serverSocket = new ServerSocket(serveur.getPort());
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le port " + serveur.getPort(), e);
        }

        start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();

                ConnexionJoueur connexion = new ConnexionJoueur(socket, serveur);
                serveur.ajouterConnexion(connexion);

                connexion.envoyer("Connexion établie. Identifiez-vous avec @CONNEXION pseudo personnage");

            } catch (IOException e) {
                System.err.println("Erreur accept connexion : " + e.getMessage());
            }
        }
    }

    public void arreter() {
        running = false;

        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }
}