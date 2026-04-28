package cluedo.reseau.experts;

import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertSoupconner extends ExpertMessage {
    private static final String REGEX = "^@SOUPCONNER [\\p{Alpha}_]+ [\\p{Alpha}_]+ [\\p{Alpha}_]+$";

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

            EPersonnage personnage = EPersonnage.valueOf(mots[1]);
            ELieu lieu = ELieu.valueOf(mots[2]);
            EArme arme = EArme.valueOf(mots[3]);

            Joueur joueur = connexion.getJoueur();

            joueur.soupconne(personnage, lieu, arme);

            serveur.diffuser("OK SOUPCON " + joueur.getNom() + " "
                    + personnage.name() + " " + lieu.name() + " " + arme.name());

            Joueur refuteur = serveur.getSuperviseur().getJoueurDevantRefuter();

            if (refuteur != null) {
                serveur.diffuser("INFO ATTENTE_INDICE " + refuteur.getNom());
            } else {
                serveur.diffuser("INFO FIN_SOUPCON");
            }

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}