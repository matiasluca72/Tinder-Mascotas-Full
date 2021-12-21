package egg.edu.tinderFull.configuraciones;

import egg.edu.tinderFull.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class ConfiguracionesSeguridad extends WebSecurityConfigurerAdapter {

    //Instancia de UsuarioService para poder usarlo para acceder a la base de datos
    @Autowired
    public UsuarioService usuarioService;

    /* Configuración del manejador de seguridad de Spring Security que le indica que Servicio debe utilizar para autentificar un Usuario;
    y que Encoder debe utilizar para comparar las contraseñas */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(usuarioService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.headers().frameOptions().sameOrigin().and()
                .authorizeRequests()
                .antMatchers("/css/*", "/js/*", "/img/*", "/**").permitAll() // Permitir a cualquier persona acceder a los archivos estáticos
                .and().formLogin()
                .loginPage("/login") // En cual URL está mi formulario de login
                .loginProcessingUrl("/logincheck") // URL que va a usar Spring Security para validar / procesar un Usuario
                .usernameParameter("username") // Con cuales nombres viajan los datos del logueo
                .passwordParameter("password")// Con cuales nombres viajan los datos del logueo
                .defaultSuccessUrl("/inicio") // A que URL viaja el Usuario si todo sale bien
                .permitAll()
                .and().logout() // Aca configuro la salida
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // A que URL se redirige si el Usuario se desloguea
                .permitAll().and().csrf().disable();
    }
}
