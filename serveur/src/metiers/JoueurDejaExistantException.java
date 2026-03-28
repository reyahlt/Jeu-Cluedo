package metiers;
/** Exception levée quand un nom ou un personnage est déjà utilisé dans la partie. */
public class JoueurDejaExistantException extends Exception {
    public JoueurDejaExistantException(String message) { super(message); }
}
