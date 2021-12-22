package egg.edu.tinderFull.controladores;

import egg.edu.tinderFull.servicios.UsuarioService;
import egg.edu.tinderFull.servicios.ZonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Matias Luca Soto
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {
    
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ZonaService zonaService;

}
