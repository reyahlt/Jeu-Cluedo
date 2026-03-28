package metiers;

import cluedo.histoire.EArme;
import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;

public class Carte {

    public enum TypeCarte {
        PERSONNAGE,
        LIEU,
        ARME
    }
    private TypeCarte type;
    private String nom;

    public Carte(TypeCarte type, String nom) {
        if (type == null) throw new IllegalArgumentException("Le type de la carte ne peut pas être nul.");
        if (nom == null || nom.trim().isEmpty()) throw new IllegalArgumentException("Le nom de la carte ne peut pas être vide.");
        this.type = type;
        this.nom = nom;
    }

    public Carte(EPersonnage personnage) {
        this(TypeCarte.PERSONNAGE, personnage.name());
    }
    public Carte(EArme arme) {
        this(TypeCarte.ARME, arme.name());
    }
    public Carte(ELieu lieu) {
        this(TypeCarte.LIEU, lieu.name());
    }
    public TypeCarte getType() {
        return type;
    }

    public String getNom() {
        return nom;
    }

    public EPersonnage getPersonnage() {
        if (type != TypeCarte.PERSONNAGE)
            throw new IllegalStateException("Cette carte n'est pas un personnage");
        return EPersonnage.valueOf(nom);
    }

    public ELieu getLieu() {
        if (type != TypeCarte.LIEU)
            throw new IllegalStateException("Cette carte n'est pas un lieu");
        return ELieu.valueOf(nom);
    }

    public EArme getArme() {
        if (type != TypeCarte.ARME)
            throw new IllegalStateException("Cette carte n'est pas une arme");
        return EArme.valueOf(nom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Carte)) return false;
        Carte c = (Carte) o;
        return type == c.type && nom.equals(c.nom);
    }

    @Override
    public String toString() {
        return type + ": " + nom;
    }

}
