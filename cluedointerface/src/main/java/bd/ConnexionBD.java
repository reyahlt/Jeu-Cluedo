package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gère la connexion unique à la base de données MySQL (XAMPP).
 */
public class ConnexionBD {

    private static final String URL      = "jdbc:mysql://localhost:8889/cluedo_db";
    private static final String USER     = "mohaa";
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