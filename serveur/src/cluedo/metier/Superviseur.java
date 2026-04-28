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

    private boolean modeSoupcon;
    private Soupcon soupconEnCours;

    private int indexJoueurSoupcon;
    private int indexJoueurDevantRefuter;

    public static Superviseur getInstance() {
        if (instance == null)
            throw new IllegalStateException("Superviseur non initialisé.");
        return instance;
    }


    /**
     * Construit le Superviseur et initialise une nouvelle partie.
     *
     * Le Superviseur suit le pattern Singleton : une seule instance peut exister
     * à la fois. Toute tentative de créer un second Superviseur sans avoir
     * appelé {@link #reset()} au préalable lèvera une exception.
     *
     * La création du Superviseur réinitialise automatiquement la  Partie
     * afin de garantir un état de jeu propre.
     *
     *
     * @param plateau le plateau de jeu à utiliser, ne doit pas être {@code null}
     * @throws IllegalArgumentException si {@code plateau} est {@code null}
     * @throws IllegalStateException    si une instance du Superviseur existe déjà
     */
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

    /**
     * Passe au joueur suivant devant réfuter le soupçon en cours.
     *
     * Si le joueur suivant est celui qui a émis le soupçon, cela signifie que
     * tous les joueurs ont été consultés sans qu'aucun n'ait pu réfuter.
     * Le mode soupçon est alors terminé sans réfutation.
     * Sinon, l'index du réfuteur courant est mis à jour vers le joueur suivant.
     *
     *
     * @throws PartieNonDemarreeException si la partie n'a pas encore été démarrée
     */
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

        // ce n'est pas le tour de joueur
        if (joueur != getJoueurCourant()) {
         throw new ActionIllegaleException("Ce n'est pas le tour de " + joueur.getNom() + " !");
         }

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
    public Joueur getJoueurDevantRefuter() {
        if (!modeSoupcon || indexJoueurDevantRefuter < 0) {
            return null;
        }
        return joueurs.get(indexJoueurDevantRefuter);
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
     * Termine le tour du joueur courant et passe au joueur suivant non éliminé.
     *
     * Le tour suivant est attribué au prochain joueur actif dans l'ordre circulaire.
     * Les joueurs éliminés sont automatiquement ignorés lors du passage au tour suivant.
     *
     *
     * @param joueur le joueur souhaitant terminer son tour
     * @throws PartieNonDemarreeException si la partie n'a pas encore été démarrée
     * @throws ActionIllegaleException    si un soupçon est en cours et empêche la fin du tour,
     *                                    ou si ce n'est pas le tour du joueur spécifié
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
        for (Joueur j : joueurs) {
            if (j.getNom().equalsIgnoreCase(nom)) {
                return j;
            }
        }
        return null;
    }


    private void verifierPartieDemarree() throws PartieNonDemarreeException {
        if (!partieDemarree)
            throw new PartieNonDemarreeException("La partie n'est pas encore démarrée.");
    }

    private void verifierJoueurCourant(Joueur joueur) throws ActionIllegaleException {
        if (!joueurs.get(indexJoueurCourant).equals(joueur))
            throw new ActionIllegaleException("Ce n'est pas le tour de " + joueur.getNom() + ".");
    }


    /**
     * Permet à un joueur de montrer une carte en réfutation d'un soupçon en cours.
     *
     * La méthode effectue plusieurs validations avant d'enregistrer la réponse :
     *
     *   La partie doit être démarrée et un soupçon doit être en cours.
     *   Le joueur répondant ne peut pas être celui qui a émis le soupçon.
     *
     *   Seul le joueur désigné comme prochain réfuteur peut répondre.
     *   Le joueur répondant ne doit pas être éliminé.
     *   Si le joueur ne possède aucune carte montrable, il passe automatiquement
     *       au réfuteur suivant en fournissant {@code null}.
     *  La carte choisie doit appartenir au joueur et correspondre au soupçon.
     *
     *
     * Si la réfutation est valide, la réponse est enregistrée dans le soupçon en cours
     * et le mode soupçon est terminé.
     *
     *
     * @param repondant   le joueur qui répond au soupçon
     * @param carteChoisie la carte que le joueur souhaite montrer,
     *                     ou {@code null} s'il ne peut montrer aucune carte
     * @return la carte montrée, ou {@code null} si le joueur passe au réfuteur suivant
     * @throws PartieNonDemarreeException si la partie n'a pas encore été démarrée
     * @throws ActionIllegaleException    si aucun soupçon n'est en cours, si le joueur tente
     *                                    de répondre à son propre soupçon, si ce n'est pas
     *                                    son tour de réfuter, ou s'il est éliminé
     * @throws CarteInvalideException     si le joueur possède des cartes montrables mais fournit
     *                                    {@code null}, si la carte ne lui appartient pas,
     *                                    ou si elle ne correspond pas au soupçon
     * @throws ReponseDejaDonneeException si une réponse a déjà été enregistrée pour ce soupçon
     */
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
    /**
     * Termine le mode soupçon et réinitialise tous les états associés.
     *
     * Remet à zéro l'ensemble des attributs liés au soupçon en cours :
     * le mode soupçon est désactivé, le soupçon effacé et les index
     * des joueurs concernés réinitialisés à {@code -1}.
     *
     */
    private void terminerModeSoupcon() {
        this.modeSoupcon = false;
        this.soupconEnCours = null;
        this.indexJoueurSoupcon = -1;
        this.indexJoueurDevantRefuter = -1;
    }

    public static void reset() {
        instance = null;
    }

    /**
     * Recherche et retourne une case libre appartenant à la pièce spécifiée sur le plateau.
     *
     * Parcourt l'intégralité du plateau (25 lignes × 24 colonnes) et retourne
     * la première case trouvée qui appartient à la pièce indiquée et qui est libre.
     *
     *
     * @param lieu la pièce dans laquelle chercher une case libre
     * @return la première {@link CaseCluedo} libre trouvée dans la pièce
     * @throws PlateauCluedoException si le plateau est inaccessible ou invalide
     * @throws IllegalStateException  si aucune case libre n'est disponible dans la pièce spécifiée
     */
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

    /**
     * Déplace le joueur incarnant le personnage spécifié dans la pièce indiquée.
     *
     * Parcourt la liste des joueurs pour trouver celui qui incarne le personnage,
     * puis le place sur une case libre de la pièce cible.
     * Si aucun joueur n'incarne ce personnage, aucune action n'est effectuée.
     *
     *
     * @param personnage le personnage à déplacer
     * @param lieu       la pièce de destination dans laquelle placer le personnage
     * @throws PlateauCluedoException si aucune case libre n'est disponible dans la pièce cible
     */
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