package egg.edu.tinderFull.excepciones;

/**
 *
 * @author Matias Luca Soto
 */
public class MascotaServiceException extends Exception {

    /**
     * Creates a new instance of <code>MascotaServiceException</code> without detail message.
     */
    public MascotaServiceException() {
    }

    /**
     * Constructs an instance of <code>MascotaServiceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MascotaServiceException(String msg) {
        super(msg);
    }
}
