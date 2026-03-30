package exception;
/** Exception levée quand on tente d'ajouter un joueur ou de démarrer une partie déjà démarrée. */
public class PartieDejaDemarreeException extends Exception {
    public PartieDejaDemarreeException(String message) { super(message); }
}
