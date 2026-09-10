package org.example.cluedo;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientSocketCluedo {
    private final String host;
    private final int port;
    private final Consumer<String> onMessage;
    private Socket socket;
    private PrintWriter out;

    public ClientSocketCluedo(String host, int port, Consumer<String> onMessage) {
        this.host = host;
        this.port = port;
        this.onMessage = onMessage;
    }

    public boolean connecter() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Thread t = new Thread(this::ecouter, "ecoute-serveur-cluedo");
            t.setDaemon(true);
            t.start();
            return true;
        } catch (IOException e) {
            onMessage.accept("ERROR Connexion impossible sur " + host + ":" + port + " - " + e.getMessage());
            return false;
        }
    }

    private void ecouter() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String ligne;
            while ((ligne = in.readLine()) != null) onMessage.accept(ligne);
        } catch (IOException e) {
            onMessage.accept("ERROR Connexion serveur fermée");
        }
    }

    public boolean estConnecte() {
        return socket != null && socket.isConnected() && !socket.isClosed() && out != null;
    }

    public void envoyer(String message) {
        if (estConnecte()) out.println(message);
    }

    public void deconnecter() {
        try {
            if (out != null) out.println("@DECONNEXION");
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }
}
