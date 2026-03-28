package metiers;

import cluedo.histoire.EArme;
import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;
import cluedo.histoire.Enigme;


/**
 * Une accusation finale formulée par un joueur.
 *
 * Contrairement au soupçon, l'accusation est définitive :
 * si correcte, le joueur gagne ; sinon, il est éliminé.
 */
public class Accusation {

    private  Joueur auteur;
    private  EPersonnage personnage;
    private  ELieu lieu;
    private  EArme arme;
    private  boolean correcte;

    /**
     * Construit une accusation et évalue immédiatement son résultat
     * en interrogeant l'énigme.
     *
     * @param auteur     le joueur qui accuse
     * @param personnage le personnage accusé
     * @param lieu       le lieu accusé
     * @param arme       l'arme accusée
     * @param enigme     l'énigme secrète
     */
    public Accusation(Joueur auteur, EPersonnage personnage, ELieu lieu,
                      EArme arme, Enigme enigme) {
        if (auteur == null || personnage == null || lieu == null || arme == null || enigme == null)
            throw new IllegalArgumentException("Les paramètres de l'accusation sont obligatoires.");
        this.auteur = auteur;
        this.personnage = personnage;
        this.lieu = lieu;
        this.arme = arme;
        this.correcte = enigme.verifierAccusation(personnage, lieu, arme);
    }

    public Joueur getAuteur()          { return auteur; }
    public EPersonnage getPersonnage() { return personnage; }
    public ELieu getLieu()             { return lieu; }
    public EArme getArme()             { return arme; }

    /**
     * Indique si l'accusation correspond à l'énigme.
     *
     * @return true si correcte
     */
    public boolean isCorrecte() { return correcte; }

    @Override
    public String toString() {
        return auteur.getNom() + " accuse : " + personnage + " dans " + lieu
                + " avec " + arme + " [" + (correcte ? "CORRECT" : "INCORRECT") + "]";
    }
}
