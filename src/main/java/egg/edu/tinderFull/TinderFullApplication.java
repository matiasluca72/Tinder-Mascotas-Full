package egg.edu.tinderFull;

import egg.edu.tinderFull.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TinderFullApplication extends WebSecurityConfigurerAdapter{

    //Traemos a la Clase Servicio que tiene el método para validar un Usuario
    @Autowired
    private UsuarioService usuarioService;
    
    public static void main(String[] args) {
        SpringApplication.run(TinderFullApplication.class, args);
    }
    
    /* Este método le indica a la configuración de Spring Security cual es el servicio que
    vamos a utilizar para autentifica al Usuario, y le setea un encriptador a ese Servicio */
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(usuarioService).passwordEncoder(new BCryptPasswordEncoder());
//    }
    
}
