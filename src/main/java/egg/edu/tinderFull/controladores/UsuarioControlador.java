package egg.edu.tinderFull.controladores;

import egg.edu.tinderFull.entidades.Usuario;
import egg.edu.tinderFull.entidades.Zona;
import egg.edu.tinderFull.excepciones.UsuarioServiceException;
import egg.edu.tinderFull.servicios.UsuarioService;
import egg.edu.tinderFull.servicios.ZonaService;
import java.util.List;
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
@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    // ATRIBUTOS - Services
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ZonaService zonaService;

    /**
     * Controller que responde a la vista de editar el perfil de un usuario
     *
     * @param session
     * @param id Del Usuario a cargar sus atributos (no enviado como PathVariable)
     * @param model
     * @return
     */
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        //Inyección de las Zonas al formulario
        List<Zona> zonas = zonaService.listarZonas();
        model.put("zonas", zonas);
        
        // Verificación de que el Usuario logueado coincida con el id del Usuario a editar su perfil
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {
            //Traemos al Usuario logueado con su id e inyectamos sus datos al ModelMap
            Usuario usuario = usuarioService.buscarPorId(id);
            model.addAttribute("perfil", usuario);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        // Devolvemos el HTML de "Configurar Perfil"
        return "perfil.html";
    }

    /**
     * Controlador para actualizar los datos de un Usuario ya logueado.
     * @param model
     * @param session
     * @param archivo
     * @param id
     * @param nombre
     * @param apellido
     * @param email
     * @param idZona
     * @param clave1
     * @param clave2
     * @return 
     */
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String actualizar(ModelMap model, HttpSession session,
            MultipartFile archivo, @RequestParam String id,
            @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String idZona,
            @RequestParam String clave1, @RequestParam String clave2) {
        
        // Verificación de que el Usuario logueado coincida con el id del Usuario a editar su perfil
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        try {
            // Buscamos al Usuario por su id, lo modificamos y redireccionamos al inicio
            usuarioService.modificar(archivo, id, nombre, apellido, email, clave1, clave2, idZona);
            
            // Si la modificación fue exitosa, pisamos los atributos antiguos de la sesión con los nuevos
            session.setAttribute("usuariosession", usuarioService.buscarPorId(id));
            
            return "redirect:/inicio";
        } catch (UsuarioServiceException e) {
            
            // Volvemos a inyectar las zonas, el perfil del usuario logueado y el mensaje de error
            List<Zona> zonas = zonaService.listarZonas();
            model.put("zonas", zonas);
            model.put("perfil", usuarioService.buscarPorId(id));
            model.put("error", e.getMessage());
            
            // Volvemos al formulario de modificar datos
            return "perfil";
        }
    }

}
