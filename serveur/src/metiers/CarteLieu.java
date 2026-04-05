package metiers;

import cluedo.histoire.ELieu;

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
}