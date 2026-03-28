package metiers;
/** Exception levée quand une action est tentée alors que la partie n'est pas démarrée. */
public class PartieNonDemarreeException extends Exception {
    public PartieNonDemarreeException(String message) { super(message); }
}
