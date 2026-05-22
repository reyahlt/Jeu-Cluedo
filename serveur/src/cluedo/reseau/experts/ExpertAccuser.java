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


            if (accusation.isCorrecte()) {

                serveur.diffuser("ACCUSATION_CORRECTE " + joueur.getNom());

                serveur.diffuser("La Partie est terminée et le Gagnant est : " + joueur.getNom());

                return "";
            }


            serveur.diffuser("ACCUSATION_FAUSSE " + joueur.getNom());
            serveur.diffuser(joueur.getNom()+" est éliminé " );
            serveur.getSuperviseur().finirTour(joueur);
            serveur.diffuser("FIN TOUR.\n  Joueur Courant : " +
                    serveur.getSuperviseur().getJoueurCourant().getNom());
            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}