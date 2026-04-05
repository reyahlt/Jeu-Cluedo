package metiers;

import cluedo.histoire.EArme;

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
}