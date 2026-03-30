package cluedo.histoire;

import metiers.Carte;
import metiers.Joueur;
import exception.ReponseDejaDonneeException;

public class Soupcon  {
    private final Joueur joueur; //doivent pas changer apres creation
    private final ELieu lieu;
    private final EPersonnage personnage;
    private final EArme arme;
    private  Carte carteMontrée; //null sinon
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

    public void enregistrerReponse(Carte carte, Joueur joueur) throws ReponseDejaDonneeException {
        if (carte == null || joueur == null)
            throw new IllegalArgumentException("Carte et joueur ne peuvent pas être null.");
        if (this.carteMontrée != null)
            throw new ReponseDejaDonneeException("Une réponse a déjà été enregistrée pour ce soupçon.");

        this.carteMontrée = carte;
        this.joueurRepondant = joueur;
    }

    public boolean aEteRefute() {
        return carteMontrée != null; //true si quelqun a montrée une carte (donc Supcon faux)
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Soupcon)) return false;
        Soupcon s = (Soupcon) o;
        return lieu == s.lieu &&
                personnage == s.personnage &&
                arme == s.arme;
    }


    @Override
    public String toString() {
        return joueur.getNom() + " soupçonne : " + personnage
                + " dans " + lieu + " avec " + arme;
    }

}
