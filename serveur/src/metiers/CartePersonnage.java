package metiers;

import cluedo.histoire.EPersonnage;

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
}
