package cluedo.reseau.socket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ThreadConnexion extends Thread {
    private final Socket socket;
    private final ConnexionJoueur connexion;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean fin;

    public ThreadConnexion(ConnexionJoueur connexion, Socket socket) {
        this.connexion = connexion;
        this.socket = socket;
        this.fin = false;

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir les flux de la socket", e);
        }

        start();
    }

    @Override
    public void run() {
        while (!fin) {
            try {
                String message = in.readLine();
                String reponse = connexion.getServeur().traiterMessage(connexion, message);
                if (reponse != null && !reponse.isBlank()) {
                    envoyerMessage(reponse);
                }
                if (message == null) {
                    fin = true;
                }
            } catch (SocketException e) {
                fin = true;
            } catch (IOException e) {
                fin = true;
            }
        }
    }

    public synchronized void envoyerMessage(String message) {
        out.println(message);
    }

    public void fin() {
        fin = true;
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
