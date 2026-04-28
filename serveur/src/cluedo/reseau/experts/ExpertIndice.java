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
            String[] mots = message.split(" ");
            String nomCarte = mots[1];

            Joueur joueur = connexion.getJoueur();
            if (joueur == null) return "ERROR Joueur non connecté";

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
            return (carteMontree == null)
                    ? "OK INDICE AUCUNE_CARTE"
                    : "OK INDICE " + carteMontree.getNom();
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
