package cluedo.histoire;

import java.util.Objects;

public class Soupcon  {
    private final ELieu lieu;
    private final EPersonnage personnage;
    private final EArme arme;

    public Soupcon(ELieu lieu, EPersonnage personnage, EArme arme) {
        this.lieu = lieu;
        this.personnage = personnage;
        this.arme = arme;
    }

    public EPersonnage getPersonnage() {
        return personnage;
    }

    public ELieu getLieu() {
        return lieu;
    }

    public EArme getArme() {
        return arme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Soupcon soupcon = (Soupcon) o;
        return lieu == soupcon.lieu && personnage == soupcon.personnage && arme == soupcon.arme;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(lieu);
        result = 31 * result + Objects.hashCode(personnage);
        result = 31 * result + Objects.hashCode(arme);
        return result;
    }
}
