package cluedo.metier;

import cluedo.carte.Carte;
import cluedo.enums.EArme;
import cluedo.enums.ELieu;
import cluedo.enums.EPersonnage;
import cluedo.plateau.CaseCluedo;
import cluedo.plateau.PlateauCluedo;
import exception.PlateauCluedoException;
import exception.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Le Superviseur est l'objet central de la partie métier du Cluedo.
 *
 * Il orchestre la partie : gestion des joueurs, respect des règles,
 * déroulement des tours, soupçons, accusations.
 *
 * Il utilise directement {@link PlateauCluedo} et {@link CaseCluedo}
 * tels que fournis par l'enseignant.
 *
 * Aucune IHM, réseau ou base de données dans cette classe.
 */
public class Superviseur {

    /**
     * Positions de départ des joueurs (ligne, colonne), dans l'ordre d'inscription.
     */
    public static final int[][] CASES_DEPART = {
            {0, 16}, {5, 0}, {7, 23}, {18, 0}, {24, 9}, {24, 14}
    };

    public static final int MIN_JOUEURS = 3;
    public static final int MAX_JOUEURS = 6;

    private final ArrayList<Joueur> joueurs;
    private final Partie partie;
    private final PlateauCluedo plateau;

    private int indexJoueurCourant;
    private boolean partieDemarree;
    private static Superviseur instance;

    /** État métier pour gérer un soupçon en plusieurs étapes. */
    private boolean modeSoupcon;
    private Soupcon soupconEnCours;

    private int indexJoueurSoupcon;
    private int indexJoueurDevantRefuter;

    public static Superviseur getInstance() {
        if (instance == null)
            throw new IllegalStateException("Superviseur non initialisé.");
        return instance;
    }

    public Superviseur(PlateauCluedo plateau) {
        if (plateau == null) throw new IllegalArgumentException("Le plateau ne peut pas être nul.");
        if (instance != null)
            throw new IllegalStateException("Le Superviseur a déjà été créé !");

        Partie.reset();
        this.joueurs = new ArrayList<>();
        this.partie = Partie.getInstance();
        this.plateau = plateau;
        this.indexJoueurCourant = 0;
        this.partieDemarree = false;
        this.modeSoupcon = false;
        this.soupconEnCours = null;
        instance = this;
    }

    // =========================================================================
    // Gestion des joueurs
    // =========================================================================

    /**
     * Ajoute un joueur à la partie avant son démarrage.
     *
     * @param nom        le nom du joueur
     * @param personnage le personnage choisi
     * @throws PartieDejaDemarreeException si la partie est déjà démarrée
     * @throws JoueurDejaExistantException si le nom ou le personnage est déjà pris
     */
    public void ajouterJoueur(String nom, EPersonnage personnage)
            throws PartieDejaDemarreeException, JoueurDejaExistantException {
        if (partieDemarree)
            throw new PartieDejaDemarreeException("Impossible d'ajouter un joueur : la partie est déjà démarrée.");
        if (joueurs.size() >= MAX_JOUEURS)
            throw new IllegalArgumentException("Nombre maximum de joueurs atteint (" + MAX_JOUEURS + ").");
        for (Joueur j : joueurs) {
            if (j.getNom().equalsIgnoreCase(nom))
                throw new JoueurDejaExistantException("Le nom '" + nom + "' est déjà utilisé.");
            if (j.getPersonnage() == personnage)
                throw new JoueurDejaExistantException("Le personnage '" + personnage + "' est déjà pris.");
        }
        joueurs.add(new Joueur(nom, personnage));
    }

    // =========================================================================
    // Démarrage de la partie
    // =========================================================================

    /**
     * Démarre la partie : tire l'énigme, distribue les 18 cartes, place les joueurs.
     *
     * @throws NombreJoueursInsuffisantException si moins de 3 joueurs inscrits
     * @throws PartieDejaDemarreeException       si la partie est déjà démarrée
     */
    public void demarrerPartie()
            throws NombreJoueursInsuffisantException, PartieDejaDemarreeException {
        if (partieDemarree)
            throw new PartieDejaDemarreeException("La partie est déjà démarrée.");
        if (joueurs.size() < MIN_JOUEURS)
            throw new NombreJoueursInsuffisantException(
                    "Il faut au moins " + MIN_JOUEURS + " joueurs (actuellement : " + joueurs.size() + ").");

        // Tirer l'énigme et récupérer les 18 cartes distribuables
        ArrayList<Carte> distribuables = partie.tirerEnigmeEtMelangerCartes();
        distribuerCartes(distribuables);

        // Placer les joueurs sur leurs cases de départ
        for (int i = 0; i < joueurs.size(); i++) {
            try {
                CaseCluedo caseDepart = plateau.getCase(CASES_DEPART[i][0], CASES_DEPART[i][1]);
                joueurs.get(i).setCaseCourante(caseDepart);
            } catch (PlateauCluedoException e) {
                throw new IllegalStateException("Case de départ invalide pour le joueur " + i + " : " + e.getMessage());
            }
        }

        this.indexJoueurCourant = 0;
        this.partieDemarree = true;
    }

    /**
     * Distribue équitablement les cartes entre les joueurs.
     */
    private void distribuerCartes(List<Carte> cartes) {
        int n = joueurs.size();
        for (int i = 0; i < cartes.size(); i++)
            joueurs.get(i % n).ajouterCarte(cartes.get(i));
    }

    // =========================================================================
    // Tour de jeu
    // =========================================================================

    /**
     * Retourne le joueur dont c'est le tour.
     *
     * @return le joueur courant
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     */
    public Joueur getJoueurCourant() throws PartieNonDemarreeException {
        verifierPartieDemarree();
        return joueurs.get(indexJoueurCourant);
    }

    /**
     * Retourne le joueur suivant (non éliminé) dans l'ordre circulaire.
     *
     * @param joueur le joueur de référence
     * @return le joueur suivant
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     */
    public Joueur getJoueurSuivant(Joueur joueur) throws PartieNonDemarreeException {
        verifierPartieDemarree();
        int index = joueurs.indexOf(joueur);
        if (index == -1) throw new IllegalArgumentException("Joueur inconnu : " + joueur.getNom());
        int next = (index + 1) % joueurs.size();
        while (joueurs.get(next).isElimine() && next != index)
            next = (next + 1) % joueurs.size();
        return joueurs.get(next);
    }

    // =========================================================================
    // Actions de jeu
    // =========================================================================

    /**
     * Fait lancer les dés au joueur courant.
     *
     * @param joueur le joueur qui lance
     * @return le résultat (2 à 12)
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     * @throws ActionIllegaleException    si ce n'est pas son tour ou dés déjà lancés
     */
    public int lancerLesDes(Joueur joueur)
            throws PartieNonDemarreeException, ActionIllegaleException {
        verifierPartieDemarree();

        if (modeSoupcon) {
            throw new ActionIllegaleException("Impossible de lancer les dés : un soupçon est en cours.");
        }
        verifierJoueurCourant(joueur);
        return joueur.lancerLesDes();
    }

    /**
     * Déplace le joueur vers la case aux coordonnées données.
     *
     * @param joueur  le joueur à déplacer
     * @param ligne   ligne cible
     * @param colonne colonne cible
     * @throws PartieNonDemarreeException     si la partie n'est pas démarrée
     * @throws ActionIllegaleException        si ce n'est pas son tour ou dés non lancés
     * @throws DeplacementImpossibleException si le déplacement est illégal
     * @throws PlateauCluedoException         si les coordonnées sont hors plateau
     */
    public void deplacerJoueur(Joueur joueur, int ligne, int colonne)
            throws PartieNonDemarreeException, ActionIllegaleException, DeplacementApresSoupconException,
            DeplacementImpossibleException, PlateauCluedoException {
        verifierPartieDemarree();
        if (modeSoupcon) {
            throw new ActionIllegaleException("Impossible de se déplacer : un soupçon est en cours.");
        }
        verifierJoueurCourant(joueur);
        if (joueur.aSoupconne())
            throw new DeplacementApresSoupconException("le Joueur a déjà soupçonné, il ne peut plus se déplacer.\"");
        CaseCluedo caseCible = plateau.getCase(ligne, colonne);
        joueur.deplacerVers(caseCible);
    }
    private void passerAuRefuteurSuivant() throws PartieNonDemarreeException {
        Joueur actuel = joueurs.get(indexJoueurDevantRefuter);
        Joueur suivant = getJoueurSuivant(actuel);

        if (suivant == joueurs.get(indexJoueurSoupcon)) {
            terminerModeSoupcon();
            return;
        }

        indexJoueurDevantRefuter = joueurs.indexOf(suivant);
    }
    /**
     * Formule un soupçon pour le joueur courant.
     * Le joueur doit se trouver dans la pièce correspondant au lieu soupçonné.
     *
     * @param joueur     le joueur qui soupçonne
     * @param personnage le personnage soupçonné
     * @param lieu       le lieu soupçonné
     * @param arme       l'arme soupçonnée
     * @return le soupçon, avec la carte de réfutation si trouvée
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     * @throws ActionIllegaleException    si l'action est illégale
     */
    public void soupconne(Joueur joueur, EPersonnage personnage, ELieu lieu, EArme arme)
            throws PartieNonDemarreeException, ActionIllegaleException, PlateauCluedoException {
        verifierPartieDemarree();
        verifierJoueurCourant(joueur);

        /** if (joueur != getJoueurCourant()) {
         throw new ActionIllegaleException("Ce n'est pas le tour de " + joueur.getNom() + " !");
         }**/

        // Le joueur est éliminé
        if (joueur.isElimine())
            throw new ActionIllegaleException(
                    joueur.getNom() + " est éliminé et ne peut pas soupçonner.");


        // Le joueur a déjà soupçonné ce tour
        if (joueur.aSoupconne())
            throw new ActionIllegaleException(
                    joueur.getNom() + " a déjà soupçonné ce tour.");

        // Le joueur n'est pas dans une pièce
        ELieu pieceCourante = joueur.getCaseCourante().getPiece();
        if (pieceCourante == null)
            throw new ActionIllegaleException(
                    joueur.getNom() + " doit être dans une pièce pour soupçonner.");

        // Le joueur n'est pas dans la bonne pièce
        if (pieceCourante != lieu)
            throw new ActionIllegaleException(
                    joueur.getNom() + " doit être dans la pièce " + lieu
                            + " pour soupçonner (actuellement : " + pieceCourante + ").");
        if (modeSoupcon) {
            throw new ActionIllegaleException("Un soupçon est déjà en cours, impossible d'en émettre un autre.");
        }
        // Les paramètres du soupçon sont nuls
        if (personnage == null || arme == null || lieu == null)
            throw new ActionIllegaleException(
                    "Le soupçon doit contenir un personnage, un lieu et une arme valides.");

        joueur.marquerSoupcon();

        deplacerSuspectDansLaPiece(personnage, lieu);
        partie.deplacerArmeDansLaPiece(arme, lieu);

        Soupcon soupcon = new Soupcon(joueur, personnage, lieu, arme);
        this.soupconEnCours = soupcon;
        this.modeSoupcon = true;
        this.indexJoueurSoupcon = joueurs.indexOf(joueur);
        Joueur suivant = getJoueurSuivant(joueur);


        if (suivant == joueur) {//aucun autre joueur libre pour refuter
            terminerModeSoupcon();
        } else {
            this.indexJoueurDevantRefuter = joueurs.indexOf(suivant);
        }
        partie.enregistrerSoupcon(soupcon);

    }

    /**
     * Formule une accusation finale.
     * Si correcte → la partie se termine, le joueur gagne.
     * Si incorrecte → le joueur est éliminé.
     *
     * @param joueur     le joueur qui accuse
     * @param personnage le personnage accusé
     * @param lieu       le lieu accusé
     * @param arme       l'arme accusée
     * @return l'accusation avec son résultat
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     * @throws ActionIllegaleException    si ce n'est pas son tour
     */
    public Accusation accuser(Joueur joueur, EPersonnage personnage, ELieu lieu, EArme arme)
            throws PartieNonDemarreeException, ActionIllegaleException {
        verifierPartieDemarree();
        if (modeSoupcon) {
            throw new ActionIllegaleException("Impossible d'accuser : un soupçon est en cours.");
        }
        verifierJoueurCourant(joueur);

        Accusation accusation = new Accusation(joueur, personnage, lieu, arme, partie.getEnigme());
        partie.enregistrerAccusation(accusation);

        if (accusation.isCorrecte()) {
            partie.terminer(joueur);
        } else {
            joueur.eliminer();
            boolean tousElimines = true;
            for (Joueur j : joueurs)
                if (!j.isElimine()) {
                    tousElimines = false;
                    break;
                }
            if (tousElimines) partie.terminer(null);
        }
        return accusation;
    }

    /**
     * Termine le tour du joueur courant et passe au suivant.
     *
     * @param joueur le joueur qui finit son tour
     * @throws PartieNonDemarreeException si la partie n'est pas démarrée
     * @throws ActionIllegaleException    si ce n'est pas son tour
     */
    public void finirTour(Joueur joueur)
            throws PartieNonDemarreeException, ActionIllegaleException {
        verifierPartieDemarree();
        if (modeSoupcon) {
            throw new ActionIllegaleException("Impossible de finir le tour : un soupçon est en cours.");
        }
        verifierJoueurCourant(joueur);
        joueur.reinitialiserTour();
        int next = (indexJoueurCourant + 1) % joueurs.size();
        while (joueurs.get(next).isElimine())
            next = (next + 1) % joueurs.size();
        indexJoueurCourant = next;
    }

    // =========================================================================
    // Accesseurs
    // =========================================================================

    /**
     * @return liste non modifiable des joueurs
     */
    public List<Joueur> getJoueurs() {
        return Collections.unmodifiableList(joueurs);
    }

    /**
     * @return la partie en cours
     */
    public Partie getPartie() {
        return partie;
    }

    /**
     * @return le plateau de jeu
     */
    public PlateauCluedo getPlateau() {
        return plateau;
    }

    /**
     * @return true si la partie est démarrée
     */
    public boolean isPartieDemarree() {
        return partieDemarree;
    }

    /**
     * Recherche un joueur par son nom (insensible à la casse).
     *
     * @param nom le nom cherché
     * @return le joueur, ou null si introuvable
     */
    public Joueur getJoueurParNom(String nom) {
        return joueurs.stream()
                .filter(j -> j.getNom().equalsIgnoreCase(nom))
                .findFirst().orElse(null);
    }

    // =========================================================================
    // Vérifications internes
    // =========================================================================

    private void verifierPartieDemarree() throws PartieNonDemarreeException {
        if (!partieDemarree)
            throw new PartieNonDemarreeException("La partie n'est pas encore démarrée.");
    }

    private void verifierJoueurCourant(Joueur joueur) throws ActionIllegaleException {
        if (!joueurs.get(indexJoueurCourant).equals(joueur))
            throw new ActionIllegaleException("Ce n'est pas le tour de " + joueur.getNom() + ".");
    }

    public Carte montrerCarte(Joueur repondant, Carte carteChoisie)
            throws PartieNonDemarreeException, ActionIllegaleException, CarteInvalideException, ReponseDejaDonneeException {
        verifierPartieDemarree();

        if (!modeSoupcon || soupconEnCours == null) {
            throw new ActionIllegaleException("Aucun soupçon n'est en cours, on ne peut pas montrer de carte.");
        }

        // Le joueur répondant ne peut pas être le joueur qui a soupçonné
        if (repondant.equals(soupconEnCours.getJoueur())) {
            throw new ActionIllegaleException(
                    repondant.getNom() + " ne peut pas répondre à son propre soupçon.");
        }

        if (repondant != joueurs.get(indexJoueurDevantRefuter)) {
            throw new ActionIllegaleException(
                    "Ce n'est pas à " + repondant.getNom() + " de réfuter le soupçon !");
        }

        // Le joueur est éliminé
        if (repondant.isElimine()) {
            throw new ActionIllegaleException(
                    repondant.getNom() + " est éliminé et ne peut pas répondre.");
        }
        ArrayList<Carte> cartesMontrables = repondant.cartesMontrables(soupconEnCours);


        // Cas où le joueur choisit de ne rien montrer
        if (carteChoisie == null) {
            if (cartesMontrables.isEmpty()) {
                passerAuRefuteurSuivant();
                return null;
            } else {
                throw new CarteInvalideException(
                        repondant.getNom() + " doit choisir une carte parmi : " + cartesMontrables);
            }
        }
        if (!repondant.getCartes().contains(carteChoisie))
            throw new CarteInvalideException(
                    repondant.getNom() + " ne possède pas la carte " + carteChoisie.getNom() + ".");

        // La carte choisie ne correspond pas au soupçon
        if (!cartesMontrables.contains(carteChoisie))
            throw new CarteInvalideException(
                    "La carte " + carteChoisie.getNom() + " ne correspond pas au soupçon.");

        soupconEnCours.enregistrerReponse(carteChoisie, repondant);
        terminerModeSoupcon(); //sortir du mode soupcon car on a refuté
        return carteChoisie;
    }

    private void terminerModeSoupcon() {
        this.modeSoupcon = false;
        this.soupconEnCours = null;
        this.indexJoueurSoupcon = -1;
        this.indexJoueurDevantRefuter = -1;
    }
    public static void reset() {
        instance = null;
    }
    private CaseCluedo trouverUneCaseLibreDeLaPiece(ELieu lieu) throws PlateauCluedoException {
        for (int ligne = 0; ligne < 25; ligne++) {
            for (int colonne = 0; colonne < 24; colonne++) {
                CaseCluedo c = plateau.getCase(ligne, colonne);
                if (c.getPiece() == lieu && c.estLibre()) {
                    return c;
                }
            }
        }
        throw new IllegalStateException("Aucune case libre trouvée dans la pièce " + lieu);
    }
    private void deplacerSuspectDansLaPiece(EPersonnage personnage, ELieu lieu)
            throws PlateauCluedoException {
        for (Joueur j : joueurs) {
            if (j.getPersonnage() == personnage) {
                j.setCaseCourante(trouverUneCaseLibreDeLaPiece(lieu));
                return;
            }
        }
    }


}