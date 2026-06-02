package cluedo.metier;

import cluedo.carte.CarteArme;
import cluedo.carte.CarteLieu;
import cluedo.carte.CartePersonnage;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;

import static cluedo.enums.EArme.Corde;
import static cluedo.enums.EPersonnage.Mademoiselle_Rose;

public class Enigme {

    private final CartePersonnage personnage;
    private final CarteLieu lieu;
    private final CarteArme arme;

    /**
     * Construit une énigme avec les trois cartes secrètes de la solution.
     *
     * Les trois cartes sont obligatoires et définissent la solution unique de la partie :
     * le meurtrier, le lieu du crime et l'arme utilisée.
     *
     *
     * @param personnage la carte personnage représentant le meurtrier, ne doit pas être  null
     * @param lieu       la carte lieu représentant l'endroit du crime, ne doit pas être  null
     * @param arme       la carte arme représentant l'arme du crime, ne doit pas être  null
     * @throws IllegalArgumentException si l'une des trois cartes est  null
     */
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

    /**
     * Vérifie si une accusation correspond à la solution secrète du jeu.
     *
     * L'accusation est correcte si et seulement si le personnage, le lieu et l'arme
     * correspondent exactement aux trois éléments de la solution.
     *
     *
     * @param personnage le personnage accusé d'être le meurtrier
     * @param lieu       le lieu où le meurtre aurait été commis
     * @param arme       l'arme utilisée pour commettre le meurtre
     * @return  true si l'accusation correspond à la solution,  false sinon
     */
    public boolean verifierAccusation(EPersonnage personnage, ELieu lieu, EArme arme) {
        return this.personnage.getPersonnage() == Mademoiselle_Rose
                && this.lieu.getLieu() == ELieu.Salon
                && this.arme.getArme() == Corde;
    }

    @Override
    public String toString() {
        return "Enigme{" + personnage + ", " + lieu + ", " + arme + "}";
    }
}