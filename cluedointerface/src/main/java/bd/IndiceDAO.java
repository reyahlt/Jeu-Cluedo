package bd;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les opérations sur la base de données pour la feuille de marquage.
 */
public class IndiceDAO {

    /**
     * Ajoute un joueur dans la BD (ignoré s'il existe déjà).
     */


    /**
     * Retourne uniquement les indices reçus PAR un joueur spécifique
     */
    public List<String[]> getIndicesPourJoueur(String nomJoueur) throws SQLException {
        List<String[]> liste = new ArrayList<>();
        String sql = """
        SELECT J.nom, C.nom, C.type, I.statut
        FROM INDICE I
        JOIN JOUEUR J ON J.id = I.joueur_id
        JOIN CARTE  C ON C.id = I.carte_id
        WHERE I.soupconneur = ?
        ORDER BY C.type
        """;
        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nomJoueur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new String[]{
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                });
            }
        }
        return liste;
    }
    public void ajouterJoueur(String nomJoueur) throws SQLException {
        String sql = "INSERT IGNORE INTO JOUEUR (nom) VALUES (?)";

        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nomJoueur);
            ps.executeUpdate();
        }
    }
    public List<String> getJoueurs() throws SQLException {
        List<String> joueurs = new ArrayList<>();

        String sql = "SELECT nom FROM JOUEUR ORDER BY id";

        try (Statement st = ConnexionBD.getConnexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                joueurs.add(rs.getString("nom"));
            }
        }

        return joueurs;
    }

    public List<String> getCartes() throws SQLException {
        List<String> cartes = new ArrayList<>();

        String sql = "SELECT nom FROM CARTE ORDER BY type, id";

        try (Statement st = ConnexionBD.getConnexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                cartes.add(rs.getString("nom"));
            }
        }

        return cartes;
    }

    public String getStatut(String nomJoueur, String nomCarte, String soupconneur) throws SQLException {
        String sql = """
        SELECT i.statut
        FROM INDICE i
        JOIN JOUEUR j ON i.joueur_id = j.id
        JOIN CARTE c ON i.carte_id = c.id
        WHERE j.nom = ? AND c.nom = ? AND i.soupconneur = ?
        LIMIT 1
    """;

        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nomJoueur);
            ps.setString(2, nomCarte);
            ps.setString(3, soupconneur);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getString("statut");
        }

        return "";
    }
    public void enregistrerIndice(String nomJoueur, String nomCarte, boolean possede, String soupconneur)
            throws SQLException {

        int joueurId = getId("JOUEUR", nomJoueur);
        int carteId = getId("CARTE", nomCarte);

        String statut = possede ? "possede" : "ne_possede_pas";

        String sql = """
        INSERT INTO INDICE (joueur_id, carte_id, statut, soupconneur)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE statut = VALUES(statut)
    """;

        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, joueurId);
            ps.setInt(2, carteId);
            ps.setString(3, statut);
            ps.setString(4, soupconneur);
            ps.executeUpdate();
        }
    }

    private int getId(String table, String nom) throws SQLException {
        String sql = "SELECT id FROM " + table + " WHERE nom = ?";

        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("id");

            throw new SQLException("Introuvable dans " + table + " : " + nom);
        }
    }

    public void resetBase() throws SQLException {
        try (Statement st = ConnexionBD.getConnexion().createStatement()) {
            st.executeUpdate("DELETE FROM INDICE");
            st.executeUpdate("DELETE FROM JOUEUR");
        }
    }


}