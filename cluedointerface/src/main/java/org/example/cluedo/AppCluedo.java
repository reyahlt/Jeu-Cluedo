package org.example.cluedo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import bd.IndiceDAO;


public class AppCluedo extends Application {
    private static final String HOST = "localhost";
    private static final int PORT = 4567;

    private final TextArea journal = new TextArea();
    private final TextArea erreur = new TextArea();
    private final VBox cartesBox = new VBox(10);
    private final Label titreCartes = new Label("Tes cartes");
    private final StackPane plateauPane = new StackPane();
    private final Pane pionsPane = new Pane();
    private ImageView imageView;
    private ClientSocketCluedo client;
    private boolean joueurConnecte = false;
    private Button boutonDemarrer;
    private Circle cercleCouleurTitre;
    private Label texteTitre;
    private final TextField prenomField = new TextField();
    private final ComboBox<String> personnageBox = new ComboBox<>();
    private Label titre;
    private String monPrenom = "Joueur";
    private String monPersonnage = "Mademoiselle_Rose";
    private boolean partieDemarree = false;
    private int deplacementsRestants = 0;
    private final Set<String> casesDejaDemandees = new HashSet<>();
    private String dernierSoupconneur = "";
    private String derniereCartePersonnage = "";
    private String derniereCarteLieu = "";
    private String derniereCarteArme = "";
    // Plateau réel : 25 lignes, 24 colonnes
    private static final int NB_LIGNES        = 25;
    private static final int NB_COLONNES      = 24;
    private static final double MARGE_RELATIVE = 0.045;

    // Positions initiales tirées de Superviseur.CASES_DEPART, dans l'ordre des EPersonnage
    private static final Map<String, int[]> POSITIONS_INITIALES = new LinkedHashMap<>();
    static {
        POSITIONS_INITIALES.put("Mademoiselle_Rose",  new int[]{0,  16});
        POSITIONS_INITIALES.put("Colonel_Moutarde",   new int[]{5,  0});
        POSITIONS_INITIALES.put("Madame_Leblanc",     new int[]{7,  23});
        POSITIONS_INITIALES.put("Reverend_Olive",     new int[]{18, 0});
        POSITIONS_INITIALES.put("Madame_Pervenche",   new int[]{24, 9});
        POSITIONS_INITIALES.put("Professeur_Violet",  new int[]{24, 14});
    }
    private final Map<String, int[]> POSITION_PIECE = Map.of(
            "Bureau", new int[]{1, 2},
            "Bibliotheque", new int[]{6, 3},
            "Salle_de_billard", new int[]{11, 3},
            "Veranda", new int[]{20, 2},
            "Salle_de_bal", new int[]{19, 12},
            "Hall", new int[]{3, 12},
            "Salon", new int[]{3, 20},
            "Salle_a_manger", new int[]{11, 20},
            "Cuisine", new int[]{21, 20}
    );

    private final Map<String, Circle> pions = new LinkedHashMap<>();
    private final Map<String, String> personnageParJoueur = new LinkedHashMap<>();
    private final Map<String, Color> couleursPersonnage = Map.of(
            "Mademoiselle_Rose", Color.HOTPINK,
            "Colonel_Moutarde", Color.GOLD,
            "Reverend_Olive", Color.OLIVEDRAB,
            "Professeur_Violet", Color.PURPLE,
            "Madame_Leblanc", Color.WHITE,
            "Madame_Pervenche", Color.DODGERBLUE
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        personnageBox.getItems().addAll("Mademoiselle_Rose", "Colonel_Moutarde", "Madame_Leblanc", "Reverend_Olive", "Madame_Pervenche", "Professeur_Violet");
        personnageBox.setValue("Mademoiselle_Rose");
        prenomField.setPromptText("Ton prénom");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(8));
        root.setLeft(creerMenuGauche());
        root.setCenter(creerPlateau());
        root.setRight(creerZoneDroite());

        Scene scene = new Scene(root, 1280, 750);
        stage.setTitle("Jeu Cluedo");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> { if (client != null) client.deconnecter(); });
        stage.show();

        cacherCartes();
        connecterAutomatiquement();
        demanderPrenomEtConnexion();
    }

    private VBox creerMenuGauche() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5));
        box.setPrefWidth(190);

        Button validerJoueur = bouton("Valider joueur", this::connecterJoueur);
        boutonDemarrer = bouton("Demarrer", () -> {
            try {
                IndiceDAO dao = new IndiceDAO();
                dao.resetBase();

                // on remet les joueurs déjà connectés dans la BD
                for (String joueur : personnageParJoueur.keySet()) {
                    dao.ajouterJoueur(joueur);
                }

                // on ajoute aussi le joueur courant
                dao.ajouterJoueur(monPrenom);

            } catch (Exception e) {
                erreur.appendText("Erreur reset BD : " + e.getMessage() + "\n");
            }

            envoyer("@DEMARRER");
        });
        Button des = bouton("Lancer les dés", () -> envoyer("@LANCER_DES"));
        Button fin = bouton("Finir le tour", () -> envoyer("@FIN_TOUR"));
        Button soupconner = bouton("Soupçonner", () -> ouvrirAction("@SOUPCONNER"));
        Button accuser = bouton("Accuser", () -> ouvrirAction("@ACCUSER"));
        Button quitter = bouton("Quitter la partie", () -> {
            envoyer("@DECONNEXION");

            if (client != null) {
                client.deconnecter();
            }

            Platform.exit();
        });
        Button indices = bouton("Voir les indices", this::ouvrirFenetreIndices);

        personnageBox.setMaxWidth(Double.MAX_VALUE);
        titreCartes.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        box.getChildren().addAll(
                boutonDemarrer, des, fin, soupconner, accuser, quitter, indices,
                new Separator(), titreCartes, cartesBox
        );
        return box;
    }

    private Button bouton(String txt, Runnable r) {
        Button b = new Button(txt);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(32);
        b.setOnAction(e -> r.run());
        return b;
    }

    private Button carte(String txt) {
        return bouton(txt, () -> {
            String carte = normaliser(txt);

            enregistrerIndiceLocal(carte);

            envoyer("@INDICE " + carte);
        });
    }

    private StackPane creerPlateau() {
        Image img = new Image(getClass().getResourceAsStream("/images/PlateauCluedo.png"));
        imageView = new ImageView(img);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(760);

        pionsPane.setPickOnBounds(false);
        pionsPane.setMouseTransparent(true);
        pionsPane.prefWidthProperty().bind(imageView.fitWidthProperty());
        pionsPane.prefHeightProperty().bind(imageView.fitWidthProperty());
        pionsPane.maxWidthProperty().bind(imageView.fitWidthProperty());
        pionsPane.maxHeightProperty().bind(imageView.fitWidthProperty());

        plateauPane.getChildren().addAll(imageView, pionsPane);
        StackPane.setAlignment(pionsPane, Pos.CENTER);

        imageView.setOnMouseClicked(e -> {
            double imgW = imageView.getBoundsInLocal().getWidth();
            double imgH = imageView.getBoundsInLocal().getHeight();
            double margeX = imgW * MARGE_RELATIVE;
            double margeY = imgH * MARGE_RELATIVE;
            double zoneW  = imgW - 2 * margeX;
            double zoneH  = imgH - 2 * margeY;

            double xImage = e.getX();
            double yImage = e.getY();

            // 25 lignes, 24 colonnes
            int colonne = (int) Math.floor((xImage - margeX) / (zoneW / NB_COLONNES));
            int ligne   = (int) Math.floor((yImage - margeY) / (zoneH / NB_LIGNES));

            demanderDeplacement(xImage, yImage, ligne, colonne);
        });
        return plateauPane;
    }


    private void demanderDeplacement(double xImage, double yImage, int ligne, int colonne) {
        afficherCoordonnees(xImage, yImage, ligne, colonne);

        if (!partieDemarree) {
            erreur.appendText("Impossible de déplacer le pion : la partie n'est pas démarrée.\n");
            return;
        }

        if (deplacementsRestants <= 0) {
            erreur.appendText("Impossible de déplacer le pion : lance les dés ou attends ton tour.\n");
            return;
        }

        if (ligne < 0 || ligne >= NB_LIGNES || colonne < 0 || colonne >= NB_COLONNES) {
            erreur.appendText("Case en dehors du plateau.\n");
            return;
        }

        String cle = ligne + "," + colonne;
        if (casesDejaDemandees.contains(cle)) {
            erreur.appendText("Déplacement déjà demandé pour cette case. Attends la réponse du serveur.\n");
            return;
        }

        // Important : on ne bouge pas le pion directement ici.
        // Le serveur vérifie si la case est autorisée, puis renvoie OK DEPLACEMENT.
        casesDejaDemandees.add(cle);
        envoyer("@ALLER_VERS " + ligne + " " + colonne);
    }

    private VBox creerZoneDroite() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5));
        box.setPrefWidth(300);
        cercleCouleurTitre = new Circle(8);
        texteTitre = new Label();

        HBox ligneTitre = new HBox(8, cercleCouleurTitre, texteTitre);
        ligneTitre.setAlignment(Pos.CENTER_LEFT);

        titre = texteTitre;
        majTitre();
        titre.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        journal.setEditable(false);
        journal.setPrefHeight(430);
        erreur.setEditable(false);
        erreur.setStyle("-fx-text-fill:red;-fx-border-color:red;");
        erreur.setPrefHeight(180);
        box.getChildren().addAll(ligneTitre, journal, erreur);
        return box;
    }

    private void connecterAutomatiquement() {
        client = new ClientSocketCluedo(HOST, PORT, this::messageRecu);
        if (!client.connecter()) {
            erreur.appendText("Impossible de se connecter. Lance d'abord AppServeurCluedo sur le port 4567.\n");
        }
    }

    private void demanderPrenomEtConnexion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Entrer dans la partie");
        TextField prenom = new TextField();
        prenom.setPromptText("Ton prénom");
        ComboBox<String> personnage = new ComboBox<>();
        personnage.getItems().addAll(personnageBox.getItems());
        personnage.setValue(personnageBox.getValue());
        GridPane g = new GridPane();
        g.setVgap(10);
        g.setHgap(10);
        g.setPadding(new Insets(10));
        g.addRow(0, new Label("Prénom"), prenom);
        g.addRow(1, new Label("Personnage"), personnage);
        dialog.getDialogPane().setContent(g);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = dialog.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            prenomField.setText(prenom.getText());
            personnageBox.setValue(personnage.getValue());
            connecterJoueur();
        }
    }

    private void connecterJoueur() {
        joueurConnecte = false;

        monPrenom = nettoyerPrenom(prenomField.getText());
        monPersonnage = personnageBox.getValue();
        enregistrerJoueur(monPrenom, monPersonnage);
        majTitre();
        try {
            IndiceDAO dao = new IndiceDAO();
            dao.ajouterJoueur(monPrenom);
        } catch (Exception e) {
            erreur.appendText("Erreur ajout joueur BD : " + e.getMessage() + "\n");
        }
        envoyer("@CONNEXION " + monPrenom + " " + monPersonnage);
    }

    private void envoyer(String msg) {
        journal.appendText("> " + msg + "\n");
        if (client == null || !client.estConnecte()) {
            erreur.appendText("Socket non connectée. Vérifie que le serveur est lancé.\n");
            return;
        }
        client.envoyer(msg);
    }

    private void messageRecu(String msg) {
        Platform.runLater(() -> traiterServeur(msg));
    }

    private void traiterServeur(String msg) {

        if (msg == null || msg.isBlank()) return;

        // affichage unique des messages
        if (!msg.startsWith("INFO ")) {
            journal.appendText(msg + "\n");
        }

        if (msg.startsWith("CONNEXION_REUSSIE") || msg.startsWith("OK CONNECTE")) {
            joueurConnecte = true;
            return;
        }

        if (msg.startsWith("ERROR")) {
            erreur.appendText(msg + "\n");
            casesDejaDemandees.clear();

            if (msg.contains("déjà utilisé")
                    || msg.contains("déjà pris")
                    || msg.contains("Déjà connecté")) {

                joueurConnecte = false;
                Platform.runLater(this::demanderPrenomEtConnexion);
            }

            return;
        }

        // Déconnexion
        if (msg.endsWith(" s'est déconnecté")) {

            String joueur = msg.replace(" s'est déconnecté", "").trim();

            Circle pion = pions.remove(joueur);

            if (pion != null) {
                pionsPane.getChildren().remove(pion);
            }

            personnageParJoueur.remove(joueur);

            journal.appendText(joueur + " a quitté la partie.\n");

            return;
        }

        // Joueur courant
        if (msg.startsWith("Le Joueur Courant est : ")) {

            String joueurCourant =
                    msg.replace("Le Joueur Courant est : ", "").trim();

            journal.appendText(
                    "C'est maintenant le tour de : "
                            + joueurCourant + "\n"
            );

            return;
        }

        // Connexion joueurs
        if (msg.startsWith("INFO ")
                && msg.contains(" a rejoint la partie avec ")) {

            String contenu = msg.replace("INFO ", "").trim();

            String[] morceaux =
                    contenu.split(" a rejoint la partie avec ");

            if (morceaux.length == 2) {

                String joueur = morceaux[0].trim();
                String personnage = morceaux[1].trim();

                enregistrerJoueur(joueur, personnage);

                // affichage propre UNE seule fois
                journal.appendText(
                        joueur + " a rejoint la partie avec "
                                + personnage + "\n"
                );
            }

            return;
        }

        // Partie démarrée
        if (msg.startsWith("La partie demmare")) {

            partieDemarree = true;

            if (boutonDemarrer != null) {
                boutonDemarrer.setDisable(true);
            }

            titreCartes.setVisible(true);
            titreCartes.setManaged(true);
            cartesBox.setVisible(true);
            cartesBox.setManaged(true);

            personnageParJoueur.forEach((joueur, personnage) -> {

                int[] pos = POSITIONS_INITIALES.get(personnage);

                if (pos != null) {
                    placerPion(joueur, pos[0], pos[1]);
                }
            });

            return;
        }

        // Cartes
        if (msg.startsWith("Tes cartes sont :")) {

            partieDemarree = true;

            String contenu =
                    msg.replace("Tes cartes sont :", "").trim();

            String[] cartes =
                    contenu.isBlank()
                            ? new String[0]
                            : contenu.split(" ");

            cartesBox.getChildren().clear();

            for (String c : cartes) {
                cartesBox.getChildren().add(
                        carte(c.replace('_', ' '))
                );
            }

            cartesBox.getChildren().add(carte("Rien"));

            titreCartes.setVisible(true);
            titreCartes.setManaged(true);
            cartesBox.setVisible(true);
            cartesBox.setManaged(true);

            return;
        }

        // Dés
        if (msg.startsWith("Les Des de ")) {

            String contenu =
                    msg.replace("Les Des de ", "").trim();

            String[] parts = contenu.split(":");

            if (parts.length == 2) {

                String joueur = parts[0].trim();
                int nb = Integer.parseInt(parts[1].trim());

                if (joueur.equals(monPrenom)) {

                    deplacementsRestants = nb;
                    casesDejaDemandees.clear();

                    journal.appendText(
                            "Déplacements possibles : "
                                    + deplacementsRestants + "\n"
                    );
                }
            }

            return;
        }

        // Déplacement
        if (msg.startsWith("DEPLACEMENT ")) {

            String[] p = msg.split(" ");

            if (p.length >= 5) {

                String joueur = p[1];

                int ligne = Integer.parseInt(p[3]);
                int colonne = Integer.parseInt(p[4]);

                placerPion(joueur, ligne, colonne);

                if (joueur.equals(monPrenom)
                        && deplacementsRestants > 0) {

                    deplacementsRestants--;

                    casesDejaDemandees.clear();

                    journal.appendText(
                            "Déplacements restants : "
                                    + deplacementsRestants + "\n"
                    );
                }
            }

            return;
        }

        // Soupçon
        if (msg.startsWith("Le soupcon en cours :")) {

            String contenu =
                    msg.replace("Le soupcon en cours :", "").trim();

            String[] p = contenu.split(" ");

            if (p.length >= 4) {

                dernierSoupconneur = p[0];
                derniereCartePersonnage = p[1];
                derniereCarteLieu = p[2];
                derniereCarteArme = p[3];

                deplacerPersonnageDansPiece(
                        derniereCartePersonnage,
                        derniereCarteLieu
                );
            }

            return;
        }

        // Fin tour
        if (msg.startsWith("FIN TOUR")
                || msg.contains("Joueur Courant")) {

            deplacementsRestants = 0;
            casesDejaDemandees.clear();

            return;
        }

        // Fin soupçon
        if (msg.contains("FIN_SOUPCON")
                || msg.contains("FIN SOUPCON")) {

            return;
        }
    }


    private void cacherCartes() {
        cartesBox.getChildren().clear();
        titreCartes.setVisible(false);
        titreCartes.setManaged(false);
        cartesBox.setVisible(false);
        cartesBox.setManaged(false);
    }

    private void enregistrerJoueur(String joueur, String personnage) {
        if (joueur == null || joueur.isBlank()) return;
        if (personnage == null || personnage.isBlank()) return;

        personnageParJoueur.put(joueur, personnage);

        Circle pion = pions.get(joueur);
        if (pion != null) {
            pion.setFill(couleursPersonnage.getOrDefault(personnage, Color.BLACK));
        }
    }
    private void deplacerPersonnageDansPiece(String personnage, String lieu) {
        String joueurTrouve = null;

        for (Map.Entry<String, String> entry : personnageParJoueur.entrySet()) {
            if (entry.getValue().equals(personnage)) {
                joueurTrouve = entry.getKey();
                break;
            }
        }

        if (joueurTrouve == null) return;

        int[] pos = POSITION_PIECE.get(lieu);
        if (pos == null) return;

        placerPion(joueurTrouve, pos[0], pos[1]);
    }
    private void placerPion(String nom, int ligne, int colonne) {
        Circle c = pions.computeIfAbsent(nom, n -> {
            Circle cc = new Circle(11, couleurPourJoueur(n));
            cc.setStroke(Color.BLACK);
            cc.setStrokeWidth(2);
            pionsPane.getChildren().add(cc);
            return cc;
        });
        c.setFill(couleurPourJoueur(nom));

        double imgW   = imageView.getBoundsInLocal().getWidth();
        double imgH   = imageView.getBoundsInLocal().getHeight();
        double margeX = imgW * MARGE_RELATIVE;
        double margeY = imgH * MARGE_RELATIVE;
        double zoneW  = imgW - 2 * margeX;
        double zoneH  = imgH - 2 * margeY;

        c.setCenterX(margeX + (colonne + 0.5) * (zoneW / NB_COLONNES));
        c.setCenterY(margeY + (ligne   + 0.5) * (zoneH / NB_LIGNES));
    }

    private Color couleurPourJoueur(String joueur) {
        String personnage = personnageParJoueur.getOrDefault(joueur, joueur.equals(monPrenom) ? monPersonnage : "");
        return couleursPersonnage.getOrDefault(personnage, Color.BLACK);
    }

    private void afficherCoordonnees(double x, double y, int ligne, int colonne) {
        journal.appendText(String.format("(%.1f,%.1f) --> (%d,%d)\n", x, y, ligne, colonne));
    }

    private void ouvrirAction(String cmd) {
        Dialog<String> d = new Dialog<>();
        d.setTitle(cmd);
        ComboBox<String> personnage = new ComboBox<>();
        personnage.getItems().addAll("Mademoiselle_Rose", "Colonel_Moutarde", "Madame_Leblanc", "Reverend_Olive", "Madame_Pervenche", "Professeur_Violet");
        personnage.setValue("Madame_Pervenche");
        ComboBox<String> lieu = new ComboBox<>();
        lieu.getItems().addAll("Bureau", "Bibliotheque", "Salle_de_billard", "Veranda", "Salle_de_bal", "Hall", "Salon", "Salle_a_manger", "Cuisine");
        lieu.setValue("Bureau");
        ComboBox<String> arme = new ComboBox<>();
        arme.getItems().addAll("Poignard", "Revolver", "Chandelier", "Corde", "Cle_anglaise", "Matraque");
        arme.setValue("Chandelier");
        GridPane g = new GridPane();
        g.setVgap(8);
        g.setHgap(8);
        g.addRow(0, new Label("Personnage"), personnage);
        g.addRow(1, new Label("Lieu"), lieu);
        g.addRow(2, new Label("Arme"), arme);
        d.getDialogPane().setContent(g);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        d.setResultConverter(bt -> bt == ButtonType.OK ? cmd + " " + personnage.getValue() + " " + lieu.getValue() + " " + arme.getValue() : null);
        d.showAndWait().ifPresent(this::envoyer);
    }

    private void majTitre() {
        if (texteTitre != null) {
            texteTitre.setText("CLUEDO - " + monPrenom + " - " + monPersonnage.replace('_', ' '));
        }

        if (cercleCouleurTitre != null) {
            cercleCouleurTitre.setFill(couleursPersonnage.getOrDefault(monPersonnage, Color.BLACK));
            cercleCouleurTitre.setStroke(Color.BLACK);
        }
    }

    private String nettoyerPrenom(String p) {
        String propre = p == null ? "" : p.trim().replaceAll("[^A-Za-z0-9]", "");
        return propre.isEmpty() ? "Joueur" + System.currentTimeMillis() % 1000 : propre;
    }

    private String normaliser(String s) {
        return s.trim().replace(' ', '_');
    }
    private void enregistrerIndiceLocal(String carteMontree) {
        try {
            IndiceDAO dao = new IndiceDAO();

            if (dernierSoupconneur == null || dernierSoupconneur.isBlank()) {
                return;
            }

            if (carteMontree.equals("Rien")) {
                dao.enregistrerIndice(monPrenom, derniereCartePersonnage, false, dernierSoupconneur);
                dao.enregistrerIndice(monPrenom, derniereCarteLieu, false, dernierSoupconneur);
                dao.enregistrerIndice(monPrenom, derniereCarteArme, false, dernierSoupconneur);
            } else {
                dao.enregistrerIndice(monPrenom, carteMontree, true, dernierSoupconneur);
            }

        } catch (Exception e) {
            e.printStackTrace();
            erreur.appendText("Erreur BD indice : " + e.getMessage() + "\n");
        }
    }
    private void ouvrirFenetreIndices() {
        Stage stage = new Stage();
        stage.setTitle("Feuille de Marquage");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(35);
        grid.setVgap(10);

        try {
            IndiceDAO dao = new IndiceDAO();

            var joueurs = dao.getJoueurs();
            var cartes = dao.getCartes();

            grid.add(new Label("Cartes"), 0, 0);

            for (int j = 0; j < joueurs.size(); j++) {
                grid.add(new Label(joueurs.get(j)), j + 1, 0);
            }

            for (int i = 0; i < cartes.size(); i++) {
                String carte = cartes.get(i);
                grid.add(new Label(carte), 0, i + 1);

                for (int j = 0; j < joueurs.size(); j++) {
                    String joueur = joueurs.get(j);
                    String statut = dao.getStatut(joueur, carte, monPrenom);

                    Label label = new Label("");

                    if (statut.equals("possede")) {
                        label.setText("V");
                    } else if (statut.equals("ne_possede_pas")) {
                        label.setText("X");
                    }

                    grid.add(label, j + 1, i + 1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            erreur.appendText("Erreur affichage indices : " + e.getMessage() + "\n");
        }

        Button fermer = new Button("Fermer");
        fermer.setOnAction(e -> stage.close());

        VBox root = new VBox(20, grid, fermer);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 850, 700));
        stage.show();
    }
}
