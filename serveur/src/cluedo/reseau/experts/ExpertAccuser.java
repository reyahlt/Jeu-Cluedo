package cluedo.reseau.experts;

import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.metier.Accusation;
import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;

public class ExpertAccuser extends ExpertMessage {

    private static final String REGEX =
            "^@ACCUSER [\\p{Alpha}_]+ [\\p{Alpha}_]+ [\\p{Alpha}_]+$";

    @Override
    protected boolean peutTraiter(String message) {
        return message != null && message.matches(REGEX);
    }

    @Override
    protected String executer(ConnexionJoueur connexion,
                              ServeurCluedo serveur,
                              String message) {

        try {
            if (!connexion.estValide()) {
                return "ERROR Non connecté";
            }

            String[] mots = message.split(" ");

            EPersonnage personnage = EPersonnage.valueOf(mots[1]);
            ELieu lieu = ELieu.valueOf(mots[2]);
            EArme arme = EArme.valueOf(mots[3]);

            Joueur joueur = connexion.getJoueur();

            Accusation accusation =
                    serveur.getSuperviseur().accuser(joueur, personnage, lieu, arme);

            // 🔥 CAS 1 : accusation correcte → FIN DE PARTIE
            if (accusation.isCorrecte()) {

                serveur.diffuser("OK ACCUSATION_CORRECTE " + joueur.getNom());

                serveur.diffuser("INFO PARTIE_TERMINEE GAGNANT " + joueur.getNom());

                return "";
            }

            // ❌ CAS 2 : accusation fausse
            serveur.diffuser("OK ACCUSATION_FAUSSE " + joueur.getNom());
            serveur.diffuser("INFO JOUEUR_ELIMINE " + joueur.getNom());

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}