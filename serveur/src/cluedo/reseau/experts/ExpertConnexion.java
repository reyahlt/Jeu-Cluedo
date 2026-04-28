package cluedo.reseau.experts;

import cluedo.enums.EPersonnage;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;
import exception.JoueurDejaExistantException;
import exception.PartieDejaDemarreeException;

public class ExpertConnexion extends ExpertMessage {
    private static final String REGEX = "^@CONNEXION \\p{Alnum}+ [\\p{Alpha}_]+$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion, ServeurCluedo serveur, String message) {
        try {
            String[] mots = message.split(" ");
            String pseudo = mots[1];
            EPersonnage personnage = EPersonnage.valueOf(mots[2]);

            if (connexion.estValide()) {
                return "ERROR Déjà connecté";
            }
            if (serveur.pseudoDejaUtilise(pseudo)) {
                return "ERROR Pseudo déjà utilisé";
            }

            serveur.getSuperviseur().ajouterJoueur(pseudo, personnage);
            connexion.setPseudo(pseudo);
            connexion.setValide(true);
            serveur.diffuser("INFO " + pseudo + " a rejoint la partie avec " + personnage.name());
            return "OK CONNECTE " + pseudo;
        } catch (IllegalArgumentException e) {
            return "ERROR Personnage invalide";
        } catch (JoueurDejaExistantException | PartieDejaDemarreeException e) {
            return "ERROR " + e.getMessage();
        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}
