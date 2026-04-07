package cluedo.carte;


import cluedo.enums.ELieu;

public class CarteLieu implements Carte {
    private ELieu lieu;

    public CarteLieu(ELieu lieu) {
        this.lieu = lieu;
    }

    public ELieu getLieu() {
        return lieu;
    }

    @Override
    public String getNom() {
        return lieu.name();
    }

    @Override
    public String toString() {
        return lieu.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarteLieu)) return false;
        CarteLieu other = (CarteLieu) o;
        return this.lieu == other.lieu;
    }

    @Override
    public int hashCode() {
        return lieu.hashCode();
    }
}