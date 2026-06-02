package cluedo.bd;

import java.sql.*;

/**
 * Gère la connexion unique à la base de données MySQL (XAMPP).
 */
public class ConnexionBD {

    private static final String URL      = "jdbc:mysql://localhost:3306/cluedo_db";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // vide par défaut dans XAMPP

    private static Connection connexion = null;

    /**
     * Retourne la connexion (singleton).
     */
    public static Connection getConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            connexion = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connexion;
    }

    public static void fermer() throws SQLException {
        if (connexion != null && !connexion.isClosed())
            connexion.close();
    }
}