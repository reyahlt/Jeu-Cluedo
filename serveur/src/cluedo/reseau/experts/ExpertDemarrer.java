package cluedo.reseau.experts;

import cluedo.carte.Carte;
import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertDemarrer extends ExpertMessage {
    private static final String REGEX = "^@DEMARRER$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        try {
            if (!connexion.estValide()) {
                return "ERROR Non connecté";
            }

            serveur.getSuperviseur().demarrerPartie();

            serveur.diffuser("La partie demmare et le joueur courant est : "
                    + serveur.getSuperviseur().getJoueurCourant().getNom());

            for (ConnexionJoueur c : serveur.getConnexions()) {
                Joueur j = c.getJoueur();

                if (j != null) {
                    StringBuilder cartes = new StringBuilder();

                    for (Carte carte : j.getCartes()) {
                        cartes.append(carte.getNom()).append(" ");
                    }

                    c.envoyer("Tes cartes sont :" + cartes.toString().trim());

                    int ligne = j.getLigne();
                    int colonne = j.getColonne();
                    serveur.diffuser("DEPLACEMENT " + j.getNom()
                            + " VERS " + ligne + " " + colonne);
                }
            }
            StringBuilder listeJoueurs = new StringBuilder("Liste des Joueurs (dans l'ordre)");
            for (Joueur j : serveur.getSuperviseur().getJoueurs()) {
                listeJoueurs.append(" ").append(j.getNom());
            }
            serveur.diffuser(listeJoueurs.toString());

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}