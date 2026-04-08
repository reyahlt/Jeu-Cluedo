package cluedo.metier;

import cluedo.carte.Carte;
import cluedo.carte.CarteArme;
import cluedo.carte.CarteLieu;
import cluedo.carte.CartePersonnage;
import cluedo.enums.*;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente l'état global d'une partie de Cluedo.
 *
 * Gère le deck complet de 21 cartes, tire l'énigme secrète au démarrage,
 * et conserve l'historique des soupçons et accusations.
 */
public class Partie {
    private final List<Carte> toutesLesCartes;
    private Enigme enigme;
    private final ArrayList<Soupcon> historiqueSoupcons;
    private final ArrayList<Accusation> historiqueAccusations;
    private boolean terminee;
    private Joueur gagnant;
    private final java.util.Map<EArme, ELieu> positionsArmes = new java.util.HashMap<>();
    private static Partie instance;


    public static Partie getInstance() {
        if (instance == null) {
            instance = new Partie();
        }
        return instance;
    }
    /**
     * Construit une nouvelle partie de Cluedo
     *
     * Les historiques de soupçons et d'accusations sont initialisés vides,
     * et la partie est marquée comme non terminée sans gagnant désigné.
     *
     */
    public Partie() {
        this.toutesLesCartes = new ArrayList<>();
        this.historiqueSoupcons = new ArrayList<>();
        this.historiqueAccusations = new ArrayList<>();
        this.terminee = false;
        this.gagnant = null;
        initialiserCartesPartie();
    }

    /** Initialise les 21 cartes via la classe Carte unique. */
    private void initialiserCartesPartie() {
        for (EPersonnage p : EPersonnage.values()) {
            toutesLesCartes.add(new CartePersonnage(p));
        }
        for (ELieu l : ELieu.values()) {
            toutesLesCartes.add(new CarteLieu(l));
        }
        for (EArme a : EArme.values()) {
            toutesLesCartes.add(new CarteArme(a));
        }
    }

    /**
     * Tire aléatoirement l'énigme (1 personnage + 1 lieu + 1 arme)
     * et retourne les 18 cartes restantes mélangées prêtes à être distribuées.
     *
     * @return liste des 18 cartes distribuables
     */
    public ArrayList<Carte> tirerEnigmeEtMelangerCartes() {
        ArrayList<Carte> cartesPartie = new ArrayList<>(toutesLesCartes);
        Collections.shuffle(cartesPartie);

        CartePersonnage cPersonnage = null;
        CarteLieu cLieu = null;
        CarteArme cArme = null;

        for (Carte c : cartesPartie) {
            if (cPersonnage == null && c instanceof CartePersonnage cp) {
                cPersonnage = cp;
            } else if (cLieu == null && c instanceof CarteLieu cl) {
                cLieu = cl;
            } else if (cArme == null && c instanceof CarteArme ca) {
                cArme = ca;
            }

            if (cPersonnage != null && cLieu != null && cArme != null) {
                break;
            }
        }

        this.enigme = new Enigme(cPersonnage, cLieu, cArme);

        ArrayList<Carte> distribuables = new ArrayList<>(cartesPartie);
        distribuables.remove(cPersonnage);
        distribuables.remove(cLieu);
        distribuables.remove(cArme);
        return distribuables;
    }


    public Enigme getEnigme() { return enigme; }
    public List<Soupcon> getHistoriqueSoupcons() {
        return Collections.unmodifiableList(historiqueSoupcons);
    }
    public List<Accusation> getHistoriqueAccusations() {
        return Collections.unmodifiableList(historiqueAccusations);
    }
    public boolean isTerminee() { return terminee; }
    public Joueur getGagnant() { return gagnant; }



    public void enregistrerSoupcon(Soupcon soupcon) {
        historiqueSoupcons.add(soupcon);
    }

    public void enregistrerAccusation(Accusation accusation) {
        historiqueAccusations.add(accusation);
    }

    /**
     * Termine la partie.
     *
     * @param gagnant le joueur gagnant, ou null si personne n'a gagné
     */
    public void terminer(Joueur gagnant) {
        this.terminee = true;
        this.gagnant = gagnant;
    }
    /**
     * Réinitialise l'instance unique de Partie.
     * À n'utiliser que dans les tests pour repartir à zéro.
     */
    public static void reset() {
        instance = null;
    }

    void deplacerArmeDansLaPiece(EArme arme, ELieu lieu) {
        positionsArmes.put(arme, lieu);
    }
    public ELieu getPositionArme(EArme arme) {
        return positionsArmes.get(arme);
    }
}
