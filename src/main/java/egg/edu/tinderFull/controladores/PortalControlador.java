package egg.edu.tinderFull.controladores;

import egg.edu.tinderFull.entidades.Zona;
import egg.edu.tinderFull.excepciones.UsuarioServiceException;
import egg.edu.tinderFull.servicios.UsuarioService;
import egg.edu.tinderFull.servicios.ZonaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller // Le indica a Spring que es un componente 'Controlador'
@RequestMapping("/") // Indica cual es la url que va a escuchar este controlador (desde la raíz, en este caso)
public class PortalControlador {

    //ATRIBUTO - Usuario Service
    @Autowired
    private UsuarioService usuarioService;

    //ATRIBUTO - Service de Zonas
    @Autowired
    private ZonaService zonaService;

    //Método que devolverá el index.html cuando se ingrese a la url raíz de la aplicación
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    /**
     * Método que devolverá inicio.html cuando el Usuario se loguee correctamente
     * La etiqueta @PreAuthorize hara que solo usuarios registrados y logueados puedan acceder
     * @return 
     */
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }

    /**
     * Método que devolverá el login.html cuando se ingrese a la url raíz/login 
     * @param error Le indicamos que puede recibir este argumento pero no es obligatorio
     * @param logout Si el usuario se ha deslogueado, mostramos un mensaje de salida
     * @param model
     * @return 
     */
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
            @RequestParam(required = false) String logout, ModelMap model) {
        
        //Si el parámetro error existe en la url, inyectamos el siguiente mensaje en el ModelMap
        if (error != null) {
            model.put("error", "El e-mail y/o contraseña son incorrectos.");
        }
        
        //Si el parámetro logout existe, el usuario se ha deslogueado y mostramos un mensaje de salida
        if (logout != null) {
            model.put("logout", "Ha cerrado sesión.");
        }
        return "login.html";
    }

    //Método que devolverá el registro.html cuando se ingrese a la url raíz/registro
    @GetMapping("/registro")
    public String registro(ModelMap modelo) {

        //Nos traemos todas las zonas de la base de datos
        List<Zona> zonas = zonaService.listarZonas();

        //Inyectamos en el ModelMap el listado con el key 'zonas'
        modelo.put("zonas", zonas);

        //Devolvemos el html
        return "registro.html";
    }

    //Método que responderá a una petición POST solicitada en la url raíz/registrar y recibirá una serie de argumentos
    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, MultipartFile archivo,
            @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String clave1,
            @RequestParam String clave2, @RequestParam String idZona) {

        //Llamamos al método registrar de UsuarioService y le pasamos los parámetros recibidos por el controlador
        try {
            usuarioService.registrar(archivo, nombre, apellido, email, clave1, clave2, idZona);
        } catch (UsuarioServiceException ex) {

            //Añadimos el Objeto ModelMap en los parámetros y usamos su método .put() para insertar un valor por pantalla.
            modelo.put("error", ex.getMessage()); // 1ro Nombre de la variable - 2do Valor contenido

            //Seteamos los mismos valores recibidos como argumentos dentro de los inputs del HTML
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            //Nos traemos todas las zonas de la base de datos
            List<Zona> zonas = zonaService.listarZonas();
            //Inyectamos en el ModelMap el listado con el key 'zonas'
            modelo.put("zonas", zonas);

            //Página que va a retornar si algo sale mal
            return "registro.html";
        }

        //Inyectamos textos a los campos de exito.html
        modelo.put("titulo", "¡Bienvenido al Tinder de Mascotas!");
        modelo.put("descripcion", "Tu usuario ha sido registrado con éxito.");

        //Página que va a retornar si todo sale todo bien
        return "exito.html";
    }
}
