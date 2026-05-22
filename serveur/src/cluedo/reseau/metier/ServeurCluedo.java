package cluedo.reseau.metier;

import cluedo.metier.Joueur;
import cluedo.metier.Superviseur;
import cluedo.plateau.PlateauCluedo;
import cluedo.reseau.experts.*;
import cluedo.reseau.protocole.ExpertMessage;
import cluedo.reseau.socket.ConnexionJoueur;
import cluedo.reseau.socket.ThreadAcceptConnexion;
import exception.PlateauCluedoException;

import java.util.ArrayList;
import java.util.List;

public class ServeurCluedo {
    private final int port;
    private final List<ConnexionJoueur> connexions;
    private final Superviseur superviseur;
    private ExpertMessage chaineExperts;

    public ServeurCluedo(int port) {
        this.port = port;
        this.connexions = new ArrayList<>();

        try {
            Superviseur.reset();
            this.superviseur = new Superviseur(new PlateauCluedo());
        } catch (PlateauCluedoException e) {
            throw new RuntimeException("Impossible d'initialiser le plateau : " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur d'initialisation du serveur : " + e.getMessage(), e);
        }

        initialiserChaineExperts();
        new ThreadAcceptConnexion(this);
    }

    private void initialiserChaineExperts() {
        ExpertMessage connexion = new ExpertConnexion();
        ExpertMessage deconnexion = new ExpertDeconnexion();
        ExpertMessage demarrer = new ExpertDemarrer();
        ExpertMessage lancerDes = new ExpertLancerDes();
        ExpertMessage allerVers = new ExpertAllerVers();
        ExpertMessage soupconner = new ExpertSoupconner();
        ExpertMessage accuser = new ExpertAccuser();
        ExpertMessage finTour = new ExpertFinTour();
        ExpertMessage indice = new ExpertIndice();

        connexion.setSuivant(deconnexion);
        deconnexion.setSuivant(demarrer);
        demarrer.setSuivant(lancerDes);
        lancerDes.setSuivant(allerVers);
        allerVers.setSuivant(soupconner);
        soupconner.setSuivant(accuser);
        accuser.setSuivant(finTour);
        finTour.setSuivant(indice);

        this.chaineExperts = connexion;
    }

    public synchronized void ajouterConnexion(ConnexionJoueur connexion) {
        connexions.add(connexion);
    }

    public synchronized void supprimerConnexion(ConnexionJoueur connexion) {
        connexions.remove(connexion);
    }

    public synchronized ConnexionJoueur getConnexionParPseudo(String pseudo) {
        for (ConnexionJoueur c : connexions) {
            if (c.getPseudo() != null && c.getPseudo().equalsIgnoreCase(pseudo)) {
                return c;
            }
        }
        return null;
    }

    public synchronized boolean pseudoDejaUtilise(String pseudo) {
        return getConnexionParPseudo(pseudo) != null;
    }

    public synchronized List<ConnexionJoueur> getConnexions() {
        return new ArrayList<>(connexions);
    }

    public Superviseur getSuperviseur() {
        return superviseur;
    }

    public int getPort() {
        return port;
    }

    public String traiterMessage(ConnexionJoueur connexion, String message) {
        if (message == null) {
            supprimerConnexion(connexion);
            return null;
        }
        return chaineExperts.traiter(connexion, this, message.trim());
    }

    public void diffuser(String message) {
        for (ConnexionJoueur c : getConnexions()) {
            c.envoyer(message);
        }
    }

    public void diffuserSauf(ConnexionJoueur ex1, ConnexionJoueur ex2, String message) {
        for (ConnexionJoueur c : getConnexions()) {
            if (c != ex1 && c != ex2) {
                c.envoyer(message);
            }
        }
    }
    public Joueur getJoueurDepuisConnexion(ConnexionJoueur connexion) {
        if (connexion.getPseudo() == null) {
            return null;
        }
        return superviseur.getJoueurParNom(connexion.getPseudo());
    }
}