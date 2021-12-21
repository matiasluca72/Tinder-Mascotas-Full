package egg.edu.tinderFull.servicios;

import egg.edu.tinderFull.entidades.Mascota;
import egg.edu.tinderFull.entidades.Voto;
import egg.edu.tinderFull.excepciones.VotoServiceException;
import egg.edu.tinderFull.repositorios.MascotaRepositorio;
import egg.edu.tinderFull.repositorios.VotoRepositorio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class VotoService {

    //ATRIBUTO - Repositorios
    @Autowired
    private VotoRepositorio votoRespositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    
    //ATRIBUTO - JavaMailSender
    @Autowired
    private NotificacionService notificacionService;

    //Método para realizar un voto
    @Transactional
    public void votar(String idUsuario, String idMascotaOrigin, String idMascotaDestiny) throws VotoServiceException {

        //Creamos el Objeto Voto y seteamos la fecha actual
        Voto voto = new Voto();
        voto.setFecha(new Date());

        //Validamos que las Mascotas sean diferentes entre sí
        if (idMascotaOrigin.equals(idMascotaDestiny)) {
            throw new VotoServiceException("No puede votarse a sí mismo.");
        }

        //Usamos la Clase Optional para buscar la Mascota que originó el voto dentro del repositorio
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascotaOrigin);

        //Verificamos si está presente el resultado que buscamos del repositorio
        if (respuesta.isPresent()) {

            //Si lo está, materializamos a nuestro Objeto
            Mascota mascotaOrigin = respuesta.get();

            //Validamos que el Usuario de la Mascota Origin sea el mismo del que realizó el Voto
            if (mascotaOrigin.getUsuario().getId().equals(idUsuario)) {

                //Si lo es, seteamos la Mascota que mandó el voto dentro del Objeto Voto
                voto.setMascotaOrigin(mascotaOrigin);
            } else {
                //Si el Usuario no coincide, lanzamos la excepción
                throw new VotoServiceException("No tiene permisos para realizar la operación solicitada.");
            }
        } else {
            throw new VotoServiceException("No existe una mascota vinculada a ese identificador.");
        }

        //Verificaciones similares para la Mascota Destino
        Optional<Mascota> respuesta2 = mascotaRepositorio.findById(idMascotaDestiny);
        if (respuesta2.isPresent()) {
            Mascota mascotaDestiny = respuesta2.get();
            voto.setMascotaDestiny(mascotaDestiny);
            
            //Enviamos un correo al Usuario dueño de la Mascota que recibió un Voto
            notificacionService.enviar("¡Tu mascota ha sido votada!", "TinderFull de Mascotas", mascotaDestiny.getUsuario().getMail());
            
        } else {
            throw new VotoServiceException("No se encotró la mascota a votar.");
        }

        votoRespositorio.save(voto);
    }

    //Método para responder un voto de forma positiva
    @Transactional
    public void responder(String idUsuario, String idVoto) throws VotoServiceException {

        //Buscamos el Voto existente en el repositorio con su Id recibido por parámetro
        Optional<Voto> respuesta = votoRespositorio.findById(idVoto);

        //Verificamos si el Voto fue encontrado
        if (respuesta.isPresent()) {

            //Si así fue, materializamos el Voto y le seteamos la respuesta con la fecha actual
            Voto voto = respuesta.get();
            
            //Seteamos la fecha actual a la respuesta para darla como efectuada
            voto.setRespuesta(new Date());

            //Verificamos que el Usuario dueño de la Mascota que recibió el Voto sea el mismo que el que responde
            if (voto.getMascotaDestiny().getUsuario().getId().equals(idUsuario)) {
                //Persistimos la respuesta del Usuario
                votoRespositorio.save(voto);
                //Enviamos un correo al usuario dueño de la Mascota cuyo voto fue respondido
                notificacionService.enviar("¡Tu voto ha sido correspondido!", "TinderFull de Mascotas", voto.getMascotaOrigin().getUsuario().getMail());
            } else {
                throw new VotoServiceException("No tiene permisos para realizar la operación.");
            }

        } else {
            throw new VotoServiceException("No existe el voto solicitado.");
        }
    }

}
