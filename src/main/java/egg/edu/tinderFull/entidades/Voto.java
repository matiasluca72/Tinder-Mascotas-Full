package egg.edu.tinderFull.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Matias Luca Soto
 */
@Entity
public class Voto {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    /* Una mascota realiza uno o varios votos a otras Mascotas */
    @ManyToOne
    private Mascota mascotaOrigin;
    
    /* Una mascota recibe uno o varios votos de otras Mascotas */
    @ManyToOne
    private Mascota mascotaDestiny;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Temporal(TemporalType.TIMESTAMP)
    private Date respuesta;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the respuesta
     */
    public Date getRespuesta() {
        return respuesta;
    }

    /**
     * @param respuesta the respuesta to set
     */
    public void setRespuesta(Date respuesta) {
        this.respuesta = respuesta;
    }

    /**
     * @return the mascotaOrigin
     */
    public Mascota getMascotaOrigin() {
        return mascotaOrigin;
    }

    /**
     * @param mascotaOrigin the mascotaOrigin to set
     */
    public void setMascotaOrigin(Mascota mascotaOrigin) {
        this.mascotaOrigin = mascotaOrigin;
    }

    /**
     * @return the mascotaDestiny
     */
    public Mascota getMascotaDestiny() {
        return mascotaDestiny;
    }

    /**
     * @param mascotaDestiny the mascotaDestiny to set
     */
    public void setMascotaDestiny(Mascota mascotaDestiny) {
        this.mascotaDestiny = mascotaDestiny;
    }

}
