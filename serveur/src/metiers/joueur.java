package metiers;

import cluedo.histoire.EPersonnage;

public class joueur {
    private String nom;
    private EPersonnage personnage;
    public void setNom(String alice) {
        this.nom = alice;
    }
    public  String getNom() {
        return this.nom;
    }
    public EPersonnage getPersonnage() {
        return this.personnage;
    }
    public void setPersonnage(EPersonnage p) {
        this.personnage = p;
    }
    public  joueur(String nom, EPersonnage p) {
        this.setNom(nom);
        this.setPersonnage(p);

    }
}
