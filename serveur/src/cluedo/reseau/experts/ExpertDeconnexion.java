package cluedo.reseau.experts;

import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertDeconnexion extends ExpertMessage {
    private static final String REGEX = "^@DECONNEXION$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        try {
            String pseudo = connexion.getPseudo();
            Joueur joueur = connexion.getJoueur();

            if (joueur != null) {
                try {
                    // Si le joueur déconnecté est le joueur courant,
                    // on essaie de passer automatiquement au joueur suivant.
                    if (serveur.getSuperviseur().getJoueurCourant().equals(joueur)) {
                        serveur.getSuperviseur().finirTour(joueur);

                        serveur.diffuser("Le Joueur Courant est : "
                                + serveur.getSuperviseur().getJoueurCourant().getNom());
                    }
                } catch (Exception ignored) {
                    // Si la partie n'est pas démarrée, ou si un soupçon est en cours,
                    // on ne bloque pas la déconnexion.
                }
            }

            serveur.supprimerConnexion(connexion);

            if (pseudo != null) {
                serveur.diffuser( pseudo + " s'est déconnecté");
            }

            connexion.envoyer("Tu es Deconnecté ");
            connexion.getThreadConnexion().fin();

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}