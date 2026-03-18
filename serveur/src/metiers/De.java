package cluedo.histoire;

/**
 * Représente un dé à 6 faces utilisé dans le jeu Cluedo.
 *
 * Un dé conserve sa dernière valeur obtenue après un lancer.
 * Le Cluedo utilise deux dés dont la somme détermine
 * le nombre de déplacements du joueur.
 */
public class De {

    /** Nombre de faces du dé. */
    public static final int NB_FACES = 6;

    /** Dernière valeur obtenue (entre 1 et NB_FACES). */
    private int valeur;

    /**
     * Construit un dé avec une valeur initiale de 1.
     */
    public De() {
        this.valeur = 1;
    }

    /**
     * Lance le dé : génère aléatoirement une valeur entre 1 et {@link #NB_FACES}.
     *
     * @return la valeur obtenue
     */
    public int lancer() {
        valeur = (int) (Math.random() * NB_FACES) + 1;
        return valeur;
    }

    /**
     * Retourne la dernière valeur obtenue lors du dernier lancer.
     *
     * @return la valeur courante du dé (entre 1 et NB_FACES)
     */
    public int getValeur() {
        return valeur;
    }

    @Override
    public String toString() {
        return "Dé[" + valeur + "]";
    }
}
