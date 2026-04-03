package metiers;

import cluedo.histoire.*;
import cluedo.histoire.Enigme;


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

    private static Partie instance;
    public static Partie getInstance() {
        if (instance == null) {
            instance = new Partie();
        }
        return instance;
    }

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
        for (EPersonnage p : EPersonnage.values())
            toutesLesCartes.add(new Carte(p));
        for (ELieu l : ELieu.values())
            toutesLesCartes.add(new Carte(l));
        for (EArme a : EArme.values())
            toutesLesCartes.add(new Carte(a));
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

        Carte cPersonnage = null, cLieu = null, cArme = null;
        for (Carte c : cartesPartie) {
            if (cPersonnage == null && c.getType() == Carte.TypeCarte.PERSONNAGE) cPersonnage = c;
            else if (cLieu == null && c.getType() == Carte.TypeCarte.LIEU)  cLieu = c;
            else if (cArme == null && c.getType() == Carte.TypeCarte.ARME)  cArme = c;
            if (cPersonnage != null && cLieu != null && cArme != null) break;
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
}
