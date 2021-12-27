package egg.edu.tinderFull.controladores;

import egg.edu.tinderFull.entidades.Mascota;
import egg.edu.tinderFull.entidades.Usuario;
import egg.edu.tinderFull.excepciones.MascotaServiceException;
import egg.edu.tinderFull.excepciones.UsuarioServiceException;
import egg.edu.tinderFull.servicios.MascotaService;
import egg.edu.tinderFull.servicios.UsuarioService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/foto")
public class FotoControlador {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MascotaService mascotaService;

    /**
     * Controlador que devuelve el contenido de la imagen de un Usuario ligada al id pasado como PathVariable
     *
     * @param id
     * @return
     */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> fotoUsuario(@PathVariable String id) {

        Usuario usuario = usuarioService.buscarPorId(id);
        try {
            // Condicional para arrojar una excepción en caso de que el Usuario no tenga foto
            if (usuario.getFoto() == null) {
                throw new UsuarioServiceException("El Usuario no cuenta con ninguna foto.");
            }

            //Obtenemos el contenido de la foto en forma de arreglo de bytes
            byte[] foto = usuario.getFoto().getContenido();

            //Instanciamos un HttpHeaders para setear el tipo de contenido del archivo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            //Devolvemos el ResponseEntity con el arreglo de bytes, el header y un código HTTP
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);

        } catch (UsuarioServiceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Controlador que devuelve el contenido de la imagen de una Mascota ligada al id pasado como PathVariable
     *
     * @param id
     * @return
     */
    @GetMapping("/mascota/{id}")
    public ResponseEntity<byte[]> fotoMascota(@PathVariable String id) {

        try {
            Mascota mascota = mascotaService.buscarPorId(id);
            // Condicional para arrojar una excepción en caso de que la Mascota no tenga foto
            if (mascota.getFoto() == null) {
                throw new MascotaServiceException("El Usuario no cuenta con ninguna foto.");
            }

            //Obtenemos el contenido de la foto en forma de arreglo de bytes
            byte[] foto = mascota.getFoto().getContenido();

            //Instanciamos un HttpHeaders para setear el tipo de contenido del archivo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            //Devolvemos el ResponseEntity con el arreglo de bytes, el header y un código HTTP
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);

        } catch (MascotaServiceException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
