package cluedo.metier;

import cluedo.carte.Carte;
import cluedo.carte.CarteArme;
import cluedo.carte.CarteLieu;
import cluedo.carte.CartePersonnage;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
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
    private boolean aSoupconner;  //vrai si il a deja soupconné ce tour
    private De de1;
    private  De de2;

    /**
     * Construit un joueur avec un nom et un personnage associé.
     *
     * Le joueur est initialisé dans un état prêt pour le début de la partie :
     * aucune carte en main, aucun dé lancé, aucun soupçon émis et non éliminé.
     *
     *
     * @param nom le nom du joueur, ne doit pas être  null ni vide
     * @param p   le personnage incarné par le joueur, ne doit pas être  null
     * @throws IllegalArgumentException si  nom est null ou vide,
     *                                  ou si  p est  null
     */
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
        this.aSoupconner = false;
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
    public boolean aSoupconne() { return aSoupconner; }
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
        this.aSoupconner = false;
        this.deplacementsRestants = 0;
    }

    public void setDeplacementsRestants(int nb) {
        this.deplacementsRestants = nb;
    }

    /**
     * Définit la case courante du joueur sur le plateau.
     *
     * Si le joueur occupe déjà une case, celle-ci est libérée avant le déplacement.
     * La nouvelle case est ensuite marquée comme occupée par ce joueur.
     * Passer  null permet de retirer le joueur du plateau sans l'affecter
     * à une nouvelle case.
     *
     *
     * @param nvlCase la nouvelle case à occuper, ou  null pour retirer
     *                le joueur du plateau
     */
    public void setCaseCourante(CaseCluedo nvlCase) {
        if (caseActuel != null)
            caseActuel.liberer();
        this.caseActuel = nvlCase;
        if (nvlCase != null)
            nvlCase.occuper(this);
    }

    public void eliminer() { this.elimine = true;}

    /**
     * Émet un soupçon au nom du joueur en désignant un suspect, un lieu et une arme.
     *
     * Délègue l'opération au  Superviseur qui valide le contexte de jeu
     * et enregistre le soupçon dans la partie en cours.
     *
     *
     * @param lieu    le lieu où le crime aurait été commis
     * @param suspect le personnage soupçonné d'être le meurtrier
     * @param arme    l'arme supposée avoir été utilisée
     * @throws PartieNonDemarreeException si la partie n'a pas encore été démarrée
     * @throws ActionIllegaleException    si le joueur n'est pas autorisé à émettre un soupçon
     *                                    à ce moment de la partie
     * @throws PlateauCluedoException     si le déplacement du suspect vers le lieu désigné
     *                                    est impossible sur le plateau
     */
    public void soupconne( EPersonnage suspect,ELieu lieu, EArme arme)
            throws ActionIllegaleException, PlateauCluedoException,  PartieNonDemarreeException {
        Superviseur.getInstance().soupconne(this, suspect, lieu, arme);
    }

    public Accusation accuse(EPersonnage suspect, ELieu lieu, EArme arme)
            throws ActionIllegaleException,  PartieNonDemarreeException {
       return  Superviseur.getInstance().accuser(this, suspect, lieu, arme);

    }

    /**
     * Lance les deux dés du joueur et initialise son nombre de déplacements pour ce tour.
     *
     * La somme des deux dés détermine le nombre de cases dont le joueur
     * pourra se déplacer. Les dés ne peuvent être lancés qu'une seule fois par tour.
     *
     *
     * @return la somme des deux dés, comprise entre 2 et 12 inclus
     * @throws ActionIllegaleException si le joueur a déjà lancé les dés durant ce tour
     */
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
    /**
     * Permet au joueur de montrer une de ses cartes en réponse à une suggestion.
     *
     * Délègue l'opération au  Superviseur qui valide le contexte de jeu
     * et l'action avant de l'enregistrer.
     *
     *
     * @param carteChoisie la carte que le joueur souhaite montrer
     * @return la carte montrée
     * @throws PartieNonDemarreeException si la partie n'a pas encore été démarrée
     * @throws ActionIllegaleException    si le joueur n'est pas autorisé à effectuer cette action à ce moment
     * @throws CarteInvalideException     si la carte choisie n'appartient pas au joueur ou est invalide
     * @throws ReponseDejaDonneeException si le joueur a déjà répondu à la suggestion en cours
     */
    public Carte montrerCarte(Carte carteChoisie)
            throws PartieNonDemarreeException, ActionIllegaleException, CarteInvalideException, ReponseDejaDonneeException {
        return Superviseur.getInstance().montrerCarte(this, carteChoisie);
    }


    /**
     * Retourne la liste des cartes du joueur qui peuvent être montrées en réponse à un soupçon.
     *
     * Une carte est montrable si elle correspond à l'un des trois éléments du soupçon :
     * le personnage, l'arme ou le lieu. La liste retournée peut contenir entre 0 et 3 cartes.
     *
     *
     * @param soupcon le soupçon émis par un joueur adverse, contenant un personnage, une arme et un lieu
     * @return la liste des cartes du joueur correspondant au soupçon, vide si aucune carte ne correspond
     */

    public ArrayList<Carte> cartesMontrables(Soupcon soupcon) {
        ArrayList<Carte> result = new ArrayList<>();

        for (Carte c : cartes) {

            if (c instanceof CartePersonnage cp) {
                if (cp.getPersonnage() == soupcon.getPersonnage()) {
                    result.add(c);
                }
            }

            if (c instanceof CarteArme ca) {
                if (ca.getArme() == soupcon.getArme()) {
                    result.add(c);
                }
            }

            if (c instanceof CarteLieu cl) {
                if (cl.getLieu() == soupcon.getLieu()) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    /**
     * Marque le joueur comme ayant déjà émis un soupçon durant ce tour.
     *
     * Un joueur ne peut émettre qu'un seul soupçon par tour. Toute tentative
     * d'en émettre un second lèvera une exception.
     *
     *
     * @throws ActionIllegaleException si le joueur a déjà émis un soupçon durant ce tour
     */
    public void marquerSoupcon() throws ActionIllegaleException {
        if (aSoupconner)
            throw new ActionIllegaleException(nom + " a déjà soupçonné ce tour.");
        this.aSoupconner = true;
    }


    /**
     * Déplace le joueur vers la case cible spécifiée.
     *
     * Le déplacement est autorisé dans les deux cas suivants :
     *
     *
     *   La case cible est voisine de la case actuelle du joueur.
     *   La case cible appartient à la même pièce que la case actuelle (déplacement intra-pièce).
     *
     *
     * En cas de passage secret (déplacement d'une pièce vers une pièce différente),
     * tous les déplacements restants sont consommés. Sinon, un seul déplacement est déduit.
     *
     *
     * @param caseCible la case vers laquelle le joueur souhaite se déplacer
     * @throws ActionIllegaleException      si le joueur n'a pas encore été placé sur le plateau,
     *                                      ou s'il n'a pas encore lancé les dés
     * @throws DeplacementImpossibleException si le joueur n'a plus de déplacements disponibles,
     *                                        si la case cible n'est pas accessible depuis la position actuelle,
     *                                        ou si la case cible est déjà occupée
     */
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