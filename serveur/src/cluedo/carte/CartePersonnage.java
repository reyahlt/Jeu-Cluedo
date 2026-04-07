package cluedo.carte;


import cluedo.enums.EPersonnage;

public class CartePersonnage implements Carte {
    private EPersonnage personnage;

    public CartePersonnage(EPersonnage personnage) {
        this.personnage = personnage;
    }

    public EPersonnage getPersonnage() {
        return personnage;
    }

    @Override
    public String getNom() {
        return personnage.name();
    }

    @Override
    public String toString() {
        return personnage.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartePersonnage)) return false;
        CartePersonnage other = (CartePersonnage) o;
        return this.personnage == other.personnage;
    }

    @Override
    public int hashCode() {
        return personnage.hashCode();
    }
}
