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

                    boolean etaitJoueurCourant =
                            serveur.getSuperviseur().getJoueurCourant().equals(joueur);

                    // supprimer le joueur de la liste
                    serveur.getSuperviseur().getJoueurs().remove(joueur);

                    // si c'était son tour -> joueur suivant
                    if (etaitJoueurCourant
                            && !serveur.getSuperviseur().getJoueurs().isEmpty()) {

                        serveur.getSuperviseur().finirTour(joueur);

                        serveur.diffuser("Le Joueur Courant est : "
                                + serveur.getSuperviseur().getJoueurCourant().getNom());
                    }

                } catch (Exception ignored) {
                    // ne bloque pas la déconnexion
                }
            }

            serveur.supprimerConnexion(connexion);

            if (pseudo != null) {
                serveur.diffuser(pseudo + " s'est déconnecté");
            }
            try {
                if (!serveur.getSuperviseur().getJoueurs().isEmpty()
                        && serveur.getSuperviseur().getJoueurCourant() != null) {

                    serveur.diffuser("Le Joueur Courant est : "
                            + serveur.getSuperviseur().getJoueurCourant().getNom());
                }
            } catch (Exception ignored) {
            }

            connexion.envoyer("Tu es Deconnecté ");
            connexion.getThreadConnexion().fin();

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}