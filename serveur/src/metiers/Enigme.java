package cluedo.histoire;

import metiers.Carte;

/**
 * Représente l'énigme secrète du Cluedo : un personnage coupable,
 * un lieu du crime et une arme utilisée.
 *
 * L'énigme est tirée aléatoirement au démarrage de la partie
 * et placée dans une enveloppe scellée.
 */
public class Enigme {

    private final Carte personnage;
    private final Carte lieu;
    private final Carte arme;

    /**
     * Construit une énigme avec les trois cartes secrètes.
     *
     * @param personnage la carte personnage coupable (type PERSONNAGE)
     * @param lieu       la carte lieu du crime (type LIEU)
     * @param arme       la carte arme utilisée (type ARME)
     * @throws IllegalArgumentException si une carte est null ou de mauvais type
     */
    public Enigme(Carte personnage, Carte lieu, Carte arme) {
        if (personnage == null || lieu == null || arme == null)
            throw new IllegalArgumentException("Les trois cartes de l'énigme sont obligatoires.");
        if (personnage.getType() != Carte.TypeCarte.PERSONNAGE)
            throw new IllegalArgumentException("La carte personnage doit être de type PERSONNAGE.");
        if (lieu.getType() != Carte.TypeCarte.LIEU)
            throw new IllegalArgumentException("La carte lieu doit être de type LIEU.");
        if (arme.getType() != Carte.TypeCarte.ARME)
            throw new IllegalArgumentException("La carte arme doit être de type ARME.");
        this.personnage = personnage;
        this.lieu = lieu;
        this.arme = arme;
    }

    /**
     * Retourne la carte personnage de l'énigme.
     *
     * @return la carte (type PERSONNAGE)
     */
    public Carte getPersonnage() {
        return personnage;
    }

    /**
     * Retourne la carte lieu de l'énigme.
     *
     * @return la carte (type LIEU)
     */
    public Carte getLieu() {
        return lieu;
    }

    /**
     * Retourne la carte arme de l'énigme.
     *
     * @return la carte (type ARME)
     */
    public Carte getArme() {
        return arme;
    }

    /**
     * Vérifie si une accusation correspond exactement à l'énigme.
     *
     * @param personnage le personnage accusé
     * @param lieu       le lieu accusé
     * @param arme       l'arme accusée
     * @return true si les trois éléments correspondent à l'énigme
     */
    public boolean verifierAccusation(EPersonnage personnage, ELieu lieu, EArme arme) {
        return this.personnage.getPersonnage() == personnage
                && this.lieu.getLieu() == lieu
                && this.arme.getArme() == arme;
    }

    @Override
    public String toString() {
        return "Enigme{" + personnage + ", " + lieu + ", " + arme + "}";
    }
}
