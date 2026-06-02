package cluedo.bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les opérations sur la base de données pour la feuille de marquage.
 */
public class IndiceDAO {

    /**
     * Ajoute un joueur dans la BD (ignoré s'il existe déjà).
     */




    public void ajouterJoueur(String nomJoueur) throws SQLException {
        String sql = "INSERT IGNORE INTO JOUEUR (nom) VALUES (?)";
        try (PreparedStatement ps = cluedo.bd.ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nomJoueur);
            ps.executeUpdate();
        }
    }
    public void viderIndices() throws SQLException {
        try (Statement st = ConnexionBD.getConnexion().createStatement()) {
            st.execute("DELETE FROM INDICE");
        }
    }


    public void viderJoueurs() throws SQLException {
        try (Statement st = ConnexionBD.getConnexion().createStatement()) {
            st.execute("DELETE FROM JOUEUR");
        }
    }
    /**
     * Ajoute une carte dans la BD (ignorée si elle existe déjà).
     * @param nomCarte  ex: "Madame_Pervenche"
     * @param typeCarte "personnage", "arme" ou "lieu"
     */
    public void ajouterCarte(String nomCarte, String typeCarte) throws SQLException {
        String sql = "INSERT IGNORE INTO CARTE (nom, type) VALUES (?, ?)";
        try (PreparedStatement ps = cluedo.bd.ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nomCarte);
            ps.setString(2, typeCarte);
            ps.executeUpdate();
        }
    }

    /**
     * Enregistre qu'un joueur possède ou ne possède pas une carte.
     *
     * @param nomJoueur   nom du joueur
     * @param nomCarte    nom de la carte
     * @param possede     true = il possède, false = il ne possède pas
     * @param soupconneur
     */
    public void enregistrerIndice(String nomJoueur, String nomCarte, boolean possede , String soupconneur)
            throws SQLException {
        int joueurId = getId("JOUEUR", nomJoueur);
        int carteId  = getId("CARTE",  nomCarte);
        String statut = possede ? "possede" : "ne_possede_pas";
        String sql = "INSERT INTO INDICE (joueur_id, carte_id, statut, soupconneur) VALUES (?, ?, ?,?)";
        try (PreparedStatement ps = ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setInt(1, joueurId);
            ps.setInt(2, carteId);
            ps.setString(3, statut);
            ps.setString(4, soupconneur);
            ps.executeUpdate();
        }
    }



    // Récupère l'id d'un enregistrement par son nom
    private int getId(String table, String nom) throws SQLException {
        String sql = "SELECT id FROM " + table + " WHERE nom = ?";
        try (PreparedStatement ps = cluedo.bd.ConnexionBD.getConnexion().prepareStatement(sql)) {
            ps.setString(1, nom);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            throw new SQLException("Introuvable dans " + table + " : " + nom);
        }
    }
}