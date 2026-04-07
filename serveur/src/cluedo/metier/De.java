package cluedo.metier;

/**
 * Représente un dé à 6 faces utilisé dans le jeu Cluedo.
 *
 * Un dé conserve sa dernière valeur obtenue après un lancer.
 * Le Cluedo utilise deux dés dont la somme détermine
 * le nombre de déplacements du joueur.
 */
public class De {


    public static final int FACES = 6;
    private int valeur;

    /**
     * Constructeur par default
     */
    public De() {
        this.valeur = 1;
    }

    public int getValeur() {
        return valeur;
    }

    /**
     * génère aléatoirement une valeur entre 1 et 6}.
     *
     * @return la valeur obtenue
     */
    public int lancer() {
        valeur = (int) (Math.random() * FACES) + 1;
        return valeur;
    }


    @Override
    public String toString() {
        return "Dé[" + valeur + "]";
    }
}
