package egg.edu.tinderFull.controladores;

import egg.edu.tinderFull.entidades.Mascota;
import egg.edu.tinderFull.entidades.Usuario;
import egg.edu.tinderFull.enumeraciones.Sexo;
import egg.edu.tinderFull.enumeraciones.Tipo;
import egg.edu.tinderFull.excepciones.MascotaServiceException;
import egg.edu.tinderFull.servicios.MascotaService;
import egg.edu.tinderFull.servicios.UsuarioService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Matias Luca Soto
 */
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
@Controller
@RequestMapping("/mascota")
public class MascotaControlador {

    // ATRIBUTOS
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MascotaService mascotaService;

    /**
     * Método para responder al verbo GET y devolver la vista con el form para crear/actualizar una Mascota
     *
     * @param session
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam(required = false) String id, ModelMap model) {

        //Verificación inicial de si el Usuario está logueado
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";
        }
        
        // Creamos un Objeto Mascota provisorio para inyectarlo en el ModelMap
        Mascota mascota = new Mascota();
        // Si el id no está null ni vacío, traemos a la Mascota ligada a ese id
        if (id != null && !id.isEmpty()) {
            try {
                mascota = mascotaService.buscarPorId(id);
            } catch (MascotaServiceException ex) {
                Logger.getLogger(MascotaControlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        model.put("perfil", mascota);

        //Inyección de combos (enumeraciones)
        model.put("tipos", Tipo.values());
        model.put("sexos", Sexo.values());

        return "mascota.html";
    }

    @PostMapping("/actualizar-perfil")
    public String actualizar(ModelMap model, HttpSession session,
            MultipartFile archivo, @RequestParam String id,
            @RequestParam String nombre, @RequestParam Tipo tipo, @RequestParam Sexo sexo) {

        // Traemos al Usuario que esté en la sesión usando el atributo 'session' de tipo HttpSession
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/inicio";
        }
        
        // Si el id está nulo, agregamos una nueva Mascota. Si tiene algún valor, la Mascota ya existe y la modificamos
        try {
            if (id == null || id.isEmpty()) {
                mascotaService.agregarMascota(archivo, login.getId(), nombre, sexo, tipo);
            } else {
                mascotaService.modificarMascota(archivo, login.getId(), id, nombre, sexo, tipo);
            }
            // Volvemos al inicio si todo salió bien
            return "redirect:/inicio";

        } catch (MascotaServiceException e) {
            
            // Creamos el espacio en memoria para una Mascota nueva / existente 
            Mascota mascota = new Mascota();
            // Setteamos la Mascota con los atributos en caso de que salte al catch y el usuario no tenga que escribirlos de nuevo
            mascota.setId(id);
            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setTipo(tipo);
            // Inyectamos los datos de la Mascota
            model.put("perfil", mascota);
            
            // Si saltó alguna excepción, inyectamos nuevamente los combos
            model.put("tipos", Tipo.values());
            model.put("sexos", Sexo.values());
            
            // Inyectamos el mensaje de error
            model.put("error", e.getMessage());
            
            // Devolvemos la vista del formulario
            return "mascota.html";

        }
    }

}
