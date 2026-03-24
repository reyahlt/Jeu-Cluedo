package cluedo.histoire;

import metiers.Carte;
import metiers.Joueur;

import java.util.Objects;

public class Soupcon  {
    private Joueur joueur;
    private  ELieu lieu;
    private  EPersonnage personnage;
    private  EArme arme;
    private Carte carteMontrée; //null sinon
    private Joueur joueurRepondant; //qui montre la carte


    /**
     * soupçon.
     *
     * @param auteur     le joueur qui soupçonne
     * @param personnage le personnage soupçonné
     * @param lieu       le lieu soupçonné
     * @param arme       l'arme soupçonnée
     */
    public Soupcon(Joueur auteur, EPersonnage personnage, ELieu lieu, EArme arme) {
        if (auteur == null || personnage == null || lieu == null || arme == null)
            throw new IllegalArgumentException("Les paramètres du soupçon sont obligatoires.");
        this.joueur = auteur;
        this.personnage = personnage;
        this.lieu = lieu;
        this.arme = arme;
    }
    public Joueur getAuteur()          { return joueur; }
    public EPersonnage getPersonnage() { return personnage; }
    public ELieu getLieu()             { return lieu; }
    public EArme getArme()             { return arme; }
    public Carte getCarteMontrée()     { return carteMontrée; }
    public Joueur getJoueurRepondant() { return joueurRepondant; }

    public void enregistrerReponse(Carte carte, Joueur joueur) {
        this.carteMontrée = carte;
        this.joueurRepondant = joueur;
    }

    @Override
    public String toString() {
        return joueur.getNom() + " soupçonne : " + personnage
                + " dans " + lieu + " avec " + arme;
    }

}
