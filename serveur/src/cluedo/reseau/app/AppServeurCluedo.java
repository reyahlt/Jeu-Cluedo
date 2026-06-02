package cluedo.reseau.app;

import cluedo.bd.ConnexionBD;
import cluedo.bd.IndiceDAO;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.reseau.metier.ServeurCluedo;

import java.sql.SQLException;

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


        try {
            IndiceDAO dao = new IndiceDAO();
            dao.viderIndices();
            dao.viderJoueurs();
            for (EPersonnage p : EPersonnage.values())
                dao.ajouterCarte(p.name(), "personnage");
            for (EArme a : EArme.values())
                dao.ajouterCarte(a.name(), "arme");
            for (ELieu l : ELieu.values())
                dao.ajouterCarte(l.name(), "lieu");
            dao.ajouterCarte("Rien", "special");
            System.out.println("Cartes initialisées dans la BD !");
        } catch (Exception e) {
            System.err.println("Erreur BD : " + e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ConnexionBD.fermer();
                System.out.println("Connexion BD fermée.");
            } catch (SQLException e) {
                System.err.println("Erreur fermeture BD : " + e.getMessage());
            }
        }));
        new ServeurCluedo(port);
    }
}
