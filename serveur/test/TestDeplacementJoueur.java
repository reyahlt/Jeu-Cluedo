import cluedo.histoire.ELieu;
import cluedo.histoire.EPersonnage;
import cluedo.plateau.CaseCluedo;
import cluedo.plateau.PlateauCluedo;

import exception.ActionIllegaleException;
import exception.DeplacementImpossibleException;
import exception.PlateauCluedoException;
import metiers.Joueur;
import metiers.Superviseur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour les déplacements d'un joueur.
 *
 * Chaque vérification est un scénario indépendant .
 * Pour les scénarios autour de (8,7), on suppose toujours qu'Alice se trouve en (8,7)
 * avec 1 déplacement possible et Bob en (9,7).
 */
class TestDeplacementJoueur {

    private Superviseur superviseur;
    private Joueur alice;
    private Joueur bob;
    private PlateauCluedo plateau;

    @BeforeEach
    void setUp() throws Exception {
        plateau = new PlateauCluedo();
        superviseur = new Superviseur(plateau);
        superviseur.ajouterJoueur("Alice",   EPersonnage.Mademoiselle_Rose);
        superviseur.ajouterJoueur("Bob",     EPersonnage.Colonel_Moutarde);
        superviseur.ajouterJoueur("Charles", EPersonnage.Reverend_Olive);
        superviseur.demarrerPartie();
        alice = superviseur.getJoueurParNom("Alice");
        bob   = superviseur.getJoueurParNom("Bob");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Place Alice en (8,7) avec 1 déplacement, Bob en (9,7). */
    private void placerAlice_8_7_Bob_9_7() throws Exception {
        CaseCluedo[][] g = plateau.getGrille();
        alice.getCaseCourante().liberer();
        bob.getCaseCourante().liberer();
        alice.setCaseCourante(g[8][7]);
        alice.setDeplacementsRestants(1);
        forcerDesLances(alice);
        bob.setCaseCourante(g[9][7]);
    }

    /** Place Alice sur une case donnée avec N déplacements. */
    private void placerAlice(int ligne, int colonne, int deplacements) throws Exception {
        CaseCluedo[][] g = plateau.getGrille();
        alice.getCaseCourante().liberer();
        alice.setCaseCourante(g[ligne][colonne]);
        alice.setDeplacementsRestants(deplacements);
        forcerDesLances(alice);
    }

    private void forcerDesLances(Joueur joueur) {
        try {
            var f = Joueur.class.getDeclaredField("dejaLanceLeDes");
            f.setAccessible(true);
            f.set(joueur, true);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private CaseCluedo case_(int l, int c) throws Exception {
        return plateau.getCase(l, c);
    }

    // =========================================================================
    // Scénarios autour de (8,7) — Alice a 1 déplacement, Bob en (9,7)
    // Chaque test repart de Alice en (8,7) indépendamment
    // =========================================================================

    @Test
    void alice_ne_peut_pas_aller_en_9_7_car_Bob_sy_trouve()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        assertThrows(DeplacementImpossibleException.class, () ->
                superviseur.deplacerJoueur(alice, 9, 7));
    }

    @Test
    void alice_ne_peut_pas_aller_en_10_7_hors_voisinage_immediat()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        assertThrows(DeplacementImpossibleException.class, () ->
                superviseur.deplacerJoueur(alice, 10, 7));
    }

    @Test
    void alice_peut_se_deplacer_vers_7_7() throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 7, 7);
        assertEquals(7, alice.getLigne());
        assertEquals(7, alice.getColonne());
    }

    @Test
    void apres_deplacement_vers_7_7_case_n_est_pas_libre_et_contient_alice()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 7, 7);
        assertFalse(case_(7, 7).estLibre());
        assertEquals(alice, case_(7, 7).getOccupant());
    }

    @Test
    void apres_deplacement_vers_7_7_case_8_7_est_libre() throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 7, 7);
        assertTrue(case_(8, 7).estLibre());
    }

    @Test
    void apres_deplacement_vers_7_7_alice_na_plus_de_deplacement()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 7, 7);
        assertEquals(0, alice.getDeplacementsRestants());
    }

    @Test
    void alice_sans_deplacement_ne_peut_pas_aller_en_8_7() throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 7, 7); // consomme le déplacement
        assertThrows(DeplacementImpossibleException.class, () ->
                superviseur.deplacerJoueur(alice, 8, 7));
    }

    @Test
    void alice_peut_aller_en_8_6_et_se_trouve_dans_la_bibliotheque()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        superviseur.deplacerJoueur(alice, 8, 6);
        assertEquals(ELieu.Bibliotheque, alice.getCaseCourante().getPiece());
    }

    @Test
    void alice_dans_bibliotheque_peut_aller_en_8_0_meme_piece()
            throws Exception {
        // Alice part de (8,6) qui est dans la bibliothèque
        placerAlice(8, 6, 1);
        superviseur.deplacerJoueur(alice, 8, 0);
        assertEquals(ELieu.Bibliotheque, alice.getCaseCourante().getPiece());
    }

    // =========================================================================
    // Scénarios depuis (4,6) — devant la porte du bureau — 2 déplacements
    // =========================================================================

    @Test
    void alice_depuis_4_6_peut_aller_en_1_1() throws Exception {
        placerAlice(4, 6, 2);
        superviseur.deplacerJoueur(alice, 1, 1);
        assertEquals(1, alice.getLigne());
        assertEquals(1, alice.getColonne());
    }

    @Test
    void apres_entree_bureau_case_4_6_est_libre() throws Exception {
        placerAlice(4, 6, 2);
        superviseur.deplacerJoueur(alice, 1, 1);
        assertTrue(case_(4, 6).estLibre());
    }

    @Test
    void apres_entree_bureau_case_1_1_est_occupee() throws Exception {
        placerAlice(4, 6, 2);
        superviseur.deplacerJoueur(alice, 1, 1);
        assertFalse(case_(1, 1).estLibre());
    }

    @Test
    void alice_en_1_1_se_trouve_dans_le_bureau() throws Exception {
        placerAlice(4, 6, 2);
        superviseur.deplacerJoueur(alice, 1, 1);
        assertEquals(ELieu.Bureau, alice.getCaseCourante().getPiece());
    }

    @Test
    void apres_entree_bureau_alice_a_1_deplacement_restant() throws Exception {
        placerAlice(4, 6, 2);
        superviseur.deplacerJoueur(alice, 1, 1);
        assertEquals(1, alice.getDeplacementsRestants());
    }

    @Test
    void alice_depuis_bureau_peut_utiliser_passage_secret_vers_22_22()
            throws Exception {
        placerAlice(1, 1, 1);
        superviseur.deplacerJoueur(alice, 22, 22);
        assertEquals(22, alice.getLigne());
        assertEquals(22, alice.getColonne());
    }

    @Test
    void apres_passage_secret_case_1_1_est_libre() throws Exception {
        placerAlice(1, 1, 1);
        superviseur.deplacerJoueur(alice, 22, 22);
        assertTrue(case_(1, 1).estLibre());
    }

    @Test
    void apres_passage_secret_case_22_22_contient_alice() throws Exception {
        placerAlice(1, 1, 1);
        superviseur.deplacerJoueur(alice, 22, 22);
        assertEquals(alice, case_(22, 22).getOccupant());
    }

    @Test
    void alice_en_22_22_se_trouve_dans_la_cuisine() throws Exception {
        placerAlice(1, 1, 1);
        superviseur.deplacerJoueur(alice, 22, 22);
        assertEquals(ELieu.Cuisine, alice.getCaseCourante().getPiece());
    }

    @Test
    void apres_passage_secret_alice_a_0_deplacement() throws Exception {
        placerAlice(1, 1, 1);
        superviseur.deplacerJoueur(alice, 22, 22);
        assertEquals(0, alice.getDeplacementsRestants());
    }

    // =========================================================================
    // Tests to fail — actions illégales
    // =========================================================================

    @Test
    void deplacer_sans_lancer_les_des_devrait_LeverException()
            throws Exception {
        // Alice est placée mais les dés ne sont PAS forcés
        CaseCluedo[][] g = plateau.getGrille();
        alice.getCaseCourante().liberer();
        alice.setCaseCourante(g[8][7]);
        // Pas de forcerDesLances → aDejeLanceLeDes = false
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.deplacerJoueur(alice, 7, 7));
    }

    @Test
    void deplacer_joueur_pas_son_tour_devrait_LeverException()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        assertThrows(ActionIllegaleException.class, () ->
                superviseur.deplacerJoueur(bob, 8, 7));
    }

    @Test
    void deplacer_coordonnees_hors_plateau_devrait_LeverException()
            throws Exception {
        placerAlice_8_7_Bob_9_7();
        assertThrows(PlateauCluedoException.class, () ->
                superviseur.deplacerJoueur(alice, -1, 0));
    }

    @Test
    void deplacer_avant_placement_sur_plateau_devrait_LeverException() {
        // Nouveau joueur jamais placé sur le plateau
        Joueur nouveau = new Joueur("Nouveau", EPersonnage.Professeur_Violet);
        assertThrows(ActionIllegaleException.class, () ->
                nouveau.deplacerVers(case_(7, 7)));
    }
}