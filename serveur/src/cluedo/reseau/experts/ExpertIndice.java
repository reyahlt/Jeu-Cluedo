package cluedo.reseau.experts;

import cluedo.carte.Carte;
import cluedo.metier.Joueur;
import cluedo.reseau.metier.ServeurCluedo;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;
/**
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

            Joueur joueurQuiRepond = connexion.getJoueur();
            Joueur joueurQuiASoupconne = serveur.getSuperviseur().getJoueurQuiASoupconne();

            Carte carteChoisie = null;

            if (!nomCarte.equalsIgnoreCase("RIEN")) {
                for (Carte c : joueurQuiRepond.getCartes()) {
                    if (c.getNom().equals(nomCarte)) {
                        carteChoisie = c;
                        break;
                    }
                }
            }

            Carte carteMontree = joueurQuiRepond.montrerCarte(carteChoisie);

            if (carteMontree == null) {
                serveur.diffuser(joueurQuiRepond.getNom()+" n'a pas d'indice" );

                Joueur prochain = serveur.getSuperviseur().getJoueurDevantRefuter();

                if (prochain != null) {
                    serveur.diffuser(prochain.getNom()+" doit donner un indice " );
                } else {
                    serveur.diffuser("INFO FIN_SOUPCON");
                }

                return "Aucune carte a montré";
            }

            ConnexionJoueur connexionSoupconneur = null;

            if (joueurQuiASoupconne != null) {
                connexionSoupconneur = serveur.getConnexionParPseudo(joueurQuiASoupconne.getNom());
            }

            if (connexionSoupconneur != null) {
                connexionSoupconneur.envoyer("L'indice donné est : " + carteMontree.getNom());
            }

            connexion.envoyer("Carte montrée est :" + carteMontree.getNom());

            for (ConnexionJoueur c : serveur.getConnexions()) {
                if (c != connexion && c != connexionSoupconneur) {
                    c.envoyer("Le Joueur :  " + joueurQuiRepond.getNom() + " a refuté");
                }
            }

            serveur.diffuser("FIN_SOUPCON");

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}**/



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

            Joueur joueurQuiRepond = connexion.getJoueur();
            Joueur joueurQuiASoupconne = serveur.getSuperviseur().getJoueurQuiASoupconne();

            Carte carteChoisie = null;

            if (!nomCarte.equalsIgnoreCase("RIEN")) {
                for (Carte c : joueurQuiRepond.getCartes()) {
                    if (c.getNom().equals(nomCarte)) {
                        carteChoisie = c;
                        break;
                    }
                }
            }

            Carte carteMontree = joueurQuiRepond.montrerCarte(carteChoisie);

            if (carteMontree == null) {
                serveur.diffuser(joueurQuiRepond.getNom()+" n'a pas d'indice" );

                Joueur prochain = serveur.getSuperviseur().getJoueurDevantRefuter();

                if (prochain != null) {
                    serveur.diffuser(prochain.getNom()+" doit donner un indice " );
                } else {
                    serveur.diffuser("INFO FIN_SOUPCON");
                }

                return "Aucune carte a montré";
            }

            ConnexionJoueur connexionSoupconneur = null;

            if (joueurQuiASoupconne != null) {
                connexionSoupconneur = serveur.getConnexionParPseudo(joueurQuiASoupconne.getNom());
            }

            if (connexionSoupconneur != null) {
                connexionSoupconneur.envoyer("L'indice donné est : " + carteMontree.getNom());
            }

            connexion.envoyer("Carte montrée est :" + carteMontree.getNom());

            serveur.diffuserSauf(connexion, connexionSoupconneur,
                    "Le Joueur :  " + joueurQuiRepond.getNom() + " a refuté");

            serveur.diffuser("FIN_SOUPCON");

            return "";

        } catch (Exception e) {
            return "ERROR " + e.getMessage();
        }
    }
}