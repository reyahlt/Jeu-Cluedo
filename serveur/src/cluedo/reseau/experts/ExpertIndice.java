package cluedo.reseau.experts;



import cluedo.bd.IndiceDAO;
import cluedo.carte.Carte;
import cluedo.metier.Joueur;
import cluedo.metier.Soupcon;
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
            Soupcon s = serveur.getSuperviseur().getSoupconEnCours();
            Joueur soupconneur = serveur.getSuperviseur().getJoueurQuiASoupconne();
            Carte carteMontree = joueurQuiRepond.montrerCarte(carteChoisie);

            if (carteMontree == null) {
                IndiceDAO dao = new IndiceDAO();

                if (s != null) {
                    for (Joueur j : serveur.getSuperviseur().getJoueurs()) {
                        if (!j.getNom().equals(joueurQuiRepond.getNom()) && !j.isElimine()) {
                            dao.enregistrerIndice(joueurQuiRepond.getNom(), s.getPersonnage().name(), false, j.getNom());
                            dao.enregistrerIndice(joueurQuiRepond.getNom(), s.getLieu().name(), false, j.getNom());
                            dao.enregistrerIndice(joueurQuiRepond.getNom(), s.getArme().name(), false, j.getNom());
                        }
                    }
                }

                serveur.diffuser(joueurQuiRepond.getNom()+" n'a pas d'indice" );

                Joueur prochain = serveur.getSuperviseur().getJoueurDevantRefuter();

                if (prochain != null) {
                    serveur.diffuser(prochain.getNom()+" doit donner un indice " );
                } else {
                    serveur.diffuser("INFO FIN_SOUPCON");
                }

                return "Aucune carte a montré";
            }

            // Le joueur montre une carte → on enregistre "possède"
            IndiceDAO dao = new IndiceDAO();

            if (soupconneur != null) {
                dao.enregistrerIndice(joueurQuiRepond.getNom(), carteMontree.getNom(), true, soupconneur.getNom());
            }


            ConnexionJoueur connexionSoupconneur = null;

            if (joueurQuiASoupconne != null) {
                connexionSoupconneur = serveur.getConnexionParPseudo(joueurQuiASoupconne.getNom());
            }

            if (connexionSoupconneur != null) {
                connexionSoupconneur.envoyer("L'indice donné est : " + carteMontree.getNom() + " par " + joueurQuiRepond.getNom());
            }

            connexion.envoyer("Carte montrée est :" + carteMontree.getNom());

            serveur.diffuserSauf(connexion, connexionSoupconneur,
                    "Le Joueur :  " + joueurQuiRepond.getNom() + " a refuté");

            serveur.diffuser("FIN_SOUPCON");

            return "";


        } catch (Exception e) {
            System.err.println("ERREUR : " + e.getMessage());
            return "ERROR " + e.getMessage();
        }

    }
}