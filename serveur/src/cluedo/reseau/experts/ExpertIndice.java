package cluedo.reseau.experts;

import cluedo.carte.Carte;
import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertIndice extends ExpertMessage {
    private static final String REGEX = "^@INDICE [\\p{Alpha}_]+$";

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

            String[] mots = message.split(" ");
            String nomCarte = mots[1];

            Joueur joueur = connexion.getJoueur();
            Carte carteChoisie = null;

            if (!nomCarte.equalsIgnoreCase("RIEN")) {
                for (Carte c : joueur.getCartes()) {
                    if (c.getNom().equals(nomCarte)) {
                        carteChoisie = c;
                        break;
                    }
                }
            }

            Carte carteMontree = joueur.montrerCarte(carteChoisie);

            if (carteMontree == null) {
                serveur.diffuser("INFO INDICE_RIEN " + joueur.getNom());

                Joueur prochain = serveur.getSuperviseur().getJoueurDevantRefuter();

                if (prochain != null) {
                    serveur.diffuser("INFO ATTENTE_INDICE " + prochain.getNom());
                } else {
                    serveur.diffuser("INFO FIN_SOUPCON");
                }

                return "OK INDICE AUCUNE_CARTE";
            }

            serveur.diffuserSauf(connexion, "INFO SOUPCON_REFUTE " + joueur.getNom());
            serveur.diffuser("INFO FIN_SOUPCON");

            return "OK INDICE " + carteMontree.getNom();

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}