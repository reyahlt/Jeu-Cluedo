Jeu Cluedo

Description

Le projet consiste à développer une version du jeu Cluedo permettant à plusieurs joueurs de participer à une partie.

Le projet utilise notamment :

Java
JavaFX pour l'interface graphique
Sockets pour la communication entre les joueurs
Maven pour la gestion du projet
Base de données pour la gestion des informations du jeu```

Fonctionnalités
Création et gestion d'une partie
Gestion des joueurs
Gestion des personnages et des cartes
Déplacements sur le plateau
Gestion des indices
Communication client/serveur
Interface graphique JavaFX

## Architecture

### Côté Serveur
- **ServeurCluedo** : gère les connexions, diffuse les messages et traite les commandes via une chaîne de responsabilité
- **ExpertMessage** : classe abstraite définissant le pattern chaîne de responsabilité
- **Experts** : chaque commande est gérée par un expert dédié

### Côté Métier
- **Superviseur** : gère la logique du jeu (tours, soupçons, accusations, réfutations)
- **Joueur** : représente un joueur avec ses cartes et son état
- **Plateau** : représente le plateau de jeu

### Messages Serveur → Client

| Message | Description |
|---|---|
| `CONNEXION_REUSSIE` | Connexion acceptée |
| `INFO pseudo a rejoint la partie avec personnage` | Nouveau joueur connecté |
| `LISTE_JOUEURS j1 j2 j3` | Liste des joueurs au démarrage |
| `OK DES joueur valeur` | Résultat du lancer de dés |
| `DEPLACEMENT joueur ligne colonne` | Déplacement d'un joueur |
| `CARTE_MONTREE carte` | Carte montrée au joueur qui réfute |
| `INDICE_RECU carte` | Indice reçu par le soupçonneur |
| `JOUEUR_REPOND joueur` | Notification aux autres qu'un joueur a réfuté |
| `FIN_SOUPCON` | Fin du soupçon en cours |
| `ACCUSATION_CORRECTE joueur` | Accusation correcte, partie terminée |
| `ACCUSATION_FAUSSE joueur` | Accusation incorrecte, joueur éliminé |
| `JOUEUR_ELIMINE joueur` | Joueur éliminé |
| `PARTIE_TERMINEE joueur` | Fin de partie avec le gagnant |
| `ERROR message` | Erreur |
