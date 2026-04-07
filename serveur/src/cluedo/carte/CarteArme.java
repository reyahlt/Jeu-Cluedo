package cluedo.carte;

import cluedo.enums.EArme;

;

public class CarteArme implements Carte {
    private EArme arme;

    public CarteArme(EArme arme) {
        this.arme = arme;
    }

    public EArme getArme() {
        return arme;
    }

    @Override
    public String getNom() {
        return arme.name();
    }

    @Override
    public String toString() {
        return arme.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarteArme)) return false;
        CarteArme other = (CarteArme) o;
        return this.arme == other.arme;
    }

    @Override
    public int hashCode() {
        return arme.hashCode();
    }
}