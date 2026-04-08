package cluedo.metier;

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
     * Lance le dé et génère une valeur aléatoire.
     *
     * La valeur obtenue est comprise entre 1 et FACES inclus,
     * et est mémorisée dans l'attribut valeur.
     *
     *
     * @return la valeur obtenue après le lancer, entre 1 et FACES inclus
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
