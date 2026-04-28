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

            serveur.diffuser("OK PARTIE_DEMARREE JOUEUR_COURANT "
                    + serveur.getSuperviseur().getJoueurCourant().getNom());

            for (ConnexionJoueur c : serveur.getConnexions()) {
                Joueur j = c.getJoueur();

                if (j != null) {
                    StringBuilder cartes = new StringBuilder();

                    for (Carte carte : j.getCartes()) {
                        cartes.append(carte.getNom()).append(" ");
                    }

                    c.envoyer("OK TES_CARTES " + cartes.toString().trim());
                }
            }

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}