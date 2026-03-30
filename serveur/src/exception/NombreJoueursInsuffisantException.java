package exception;
/** Exception levée quand la partie tente de démarrer avec moins de 3 joueurs. */
public class NombreJoueursInsuffisantException extends Exception {
    public NombreJoueursInsuffisantException(String message) { super(message); }
}
