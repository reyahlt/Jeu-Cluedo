package metiers;

import cluedo.histoire.EArme;
import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;
import cluedo.histoire.Soupcon;
import cluedo.plateau.CaseCluedo;
import exception.*;

import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private String nom;
    private EPersonnage personnage;
    private ArrayList<Carte> cartes;
    private CaseCluedo caseActuel; //celle ou il es positionné sur le plateau
    private int deplacementsRestants;
    private boolean elimine; //vrai si le joueur est eliminé
    private boolean dejaLanceLeDes; //si il a deja lancer le de ou non
    private boolean ilASoupconner;  //vrai si il a deja soupconné ce tour
    private De de1;
    private  De de2;


    public  Joueur(String nom, EPersonnage p) {
        if (nom == null || nom.trim().isEmpty())
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide.");
        if (p == null)
            throw new IllegalArgumentException("Le personnage ne peut pas être nul.");

        this.nom = nom;
        this.personnage = p;
        this.cartes = new ArrayList<>();
        this.deplacementsRestants = 0;
        this.dejaLanceLeDes = false;
        this.ilASoupconner = false;
        this.elimine = false;
        this.de1 = new De();
        this.de2 = new De();
    }

    public void setNom(String alice) {
        this.nom = alice;
    }
    public  String getNom() {
        return nom;
    }
    public ELieu getPieceActuelle() {
        if (caseActuel != null) return caseActuel.getPiece();
        return null;
    }
    public EPersonnage getPersonnage() {
        return personnage;
    }
    public void setPersonnage(EPersonnage p) {
        this.personnage = p;
    }
    public List<Carte> getCartes() { return cartes; }
    public int getNombreCartes() { return cartes.size(); }
    public CaseCluedo getCaseCourante() { return caseActuel; }
    public int getDeplacementsRestants() { return deplacementsRestants; }
    public boolean DejaLanceLeDes() { return dejaLanceLeDes; }
    public boolean IlASoupçonner() { return ilASoupconner; }
    public boolean isElimine() { return elimine; }


    public void ajouterCarte(Carte carte) {
        if (carte == null) throw new IllegalArgumentException("La carte ne peut pas être nulle.");
        cartes.add(carte);
    }

    public boolean possedeCartes(Carte carte) {
        return cartes.contains(carte);
    }

    public void viderCartes() { cartes.clear(); }

    public void reinitialiserTour() {
        this.dejaLanceLeDes = false;
        this.ilASoupconner = false;
        this.deplacementsRestants = 0;
    }

    public void setDeplacementsRestants(int nb) {
        this.deplacementsRestants = nb;
    }

    public void setCaseCourante(CaseCluedo nvlCase) {
        if (caseActuel != null)
            caseActuel.liberer();
        this.caseActuel = nvlCase;
        if (nvlCase != null)
            nvlCase.occuper(this);
    }

    public void eliminer() { this.elimine = true; }

    public void soupconne(ELieu lieu, EPersonnage suspect, EArme arme)
            throws PartieNonDemarreeException, ActionIllegaleException, PlateauCluedoException, ReponseDejaDonneeException {
        Superviseur.getInstance().soupconne(this, suspect, lieu, arme);
    }
    public int lancerLesDes() throws ActionIllegaleException {
        if (dejaLanceLeDes)
            throw new ActionIllegaleException(nom + " a déjà lancé les dés");
        int s = de1.lancer() + de2.lancer();
        setDeplacementsRestants(s);
        this.dejaLanceLeDes = true;
        return s;
    }

    public De getDe1() { return de1; }
    public De getDe2() { return de2; }

    public Carte montrerCarte(Carte carteChoisie)
            throws PartieNonDemarreeException, ActionIllegaleException, CarteInvalideException {
        return Superviseur.getInstance().montrerCarte(this, carteChoisie);
    }

    public ArrayList<Carte> cartesMontrables(Soupcon soupcon) {
        ArrayList<Carte> result = new ArrayList<>();
        for (Carte c : cartes) {
            if (c.getType() == Carte.TypeCarte.PERSONNAGE
                    && c.getNom().equals(soupcon.getPersonnage().name()))
                result.add(c);
            if (c.getType() == Carte.TypeCarte.ARME
                    && c.getNom().equals(soupcon.getArme().name()))
                result.add(c);
            if (c.getType() == Carte.TypeCarte.LIEU
                    && c.getNom().equals(soupcon.getLieu().name()))
                result.add(c);
        }
        return result;
    }

    public void marquerSoupcon() throws ActionIllegaleException {
        if (ilASoupconner)
            throw new ActionIllegaleException(nom + " a déjà soupçonné ce tour.");
        this.ilASoupconner = true;
    }

    public void deplacerVers(CaseCluedo caseCible)
            throws ActionIllegaleException, DeplacementImpossibleException {
        if (caseActuel == null)
            throw new ActionIllegaleException(
                    nom + " n'a pas encore été placé sur le plateau (setCaseCourante non appelé).");
        if (!dejaLanceLeDes)
            throw new ActionIllegaleException(nom + " doit lancer les dés avant de se déplacer.");
        if (deplacementsRestants <= 0)
            throw new DeplacementImpossibleException(nom + " n'a plus de déplacements disponibles.");

        // Déplacement autorisé si case voisine OU déplacement intra-pièce (même pièce)
        boolean memePiece = caseActuel.getPiece() != null
                && caseActuel.getPiece() == caseCible.getPiece();
        if (!caseActuel.estVoisin(caseCible) && !memePiece)
            throw new DeplacementImpossibleException(
                    "La case (" + caseCible.getLigne() + "," + caseCible.getColonne()
                            + ") n'est pas dans le voisinage de " + nom + ".");

        if (!caseCible.estLibre())
            throw new DeplacementImpossibleException(
                    "La case (" + caseCible.getLigne() + "," + caseCible.getColonne()
                            + ") est déjà occupée.");

        ELieu pieceAvant = caseActuel.getPiece();
        caseActuel.liberer();
        this.caseActuel = caseCible;
        caseCible.occuper(this);

        // Passage secret (pièce → pièce différente) : consomme tous les déplacements restants
        if (pieceAvant != null && caseCible.getPiece() != null && caseCible.getPiece() != pieceAvant) {
            this.deplacementsRestants = 0;
        } else {
            this.deplacementsRestants--;
        }
    }

    @Override
    public String toString() {
        return nom + " (" + personnage.name() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Joueur)) return false;
        return nom.equals(((Joueur) o).nom);
    }

    @Override
    public int hashCode() { return nom.hashCode(); }

    public int getLigne() {
        if (caseActuel != null) return caseActuel.getLigne();
        return -1;
    }

    public int getColonne() {
        if (caseActuel != null) return caseActuel.getColonne();
        return -1;
    }



}