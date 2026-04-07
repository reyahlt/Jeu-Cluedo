package cluedo.metier;

import cluedo.carte.CarteArme;
import cluedo.carte.CarteLieu;
import cluedo.carte.CartePersonnage;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;

/**
 * Représente l'énigme secrète du Cluedo : un personnage coupable,
 * un lieu du crime et une arme utilisée.
 */
public class Enigme {

    private final CartePersonnage personnage;
    private final CarteLieu lieu;
    private final CarteArme arme;

    public Enigme(CartePersonnage personnage, CarteLieu lieu, CarteArme arme) {
        if (personnage == null || lieu == null || arme == null) {
            throw new IllegalArgumentException("Les trois cartes de l'énigme sont obligatoires.");
        }

        this.personnage = personnage;
        this.lieu = lieu;
        this.arme = arme;
    }

    public CartePersonnage getPersonnage() {
        return personnage;
    }

    public CarteLieu getLieu() {
        return lieu;
    }

    public CarteArme getArme() {
        return arme;
    }

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