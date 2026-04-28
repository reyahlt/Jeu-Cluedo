package cluedo.reseau.app;

import cluedo.reseau.metier.ServeurCluedo;

public class AppServeurCluedo {
    public static void main(String[] args) {
        int port = 4567;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide, utilisation du port 4567");
            }
        }
        new ServeurCluedo(port);
    }
}
