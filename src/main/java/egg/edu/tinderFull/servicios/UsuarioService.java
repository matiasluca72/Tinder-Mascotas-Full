package egg.edu.tinderFull.servicios;

import egg.edu.tinderFull.entidades.Foto;
import egg.edu.tinderFull.entidades.Usuario;
import egg.edu.tinderFull.entidades.Zona;
import egg.edu.tinderFull.excepciones.UsuarioServiceException;
import egg.edu.tinderFull.repositorios.UsuarioRepositorio;
import egg.edu.tinderFull.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class UsuarioService implements UserDetailsService {

    //La etiqueta sirve para indicar que se inicializa desde el servidor de aplicaciones 
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    //Atributo para subir y actualizar la foto de perfil
    @Autowired
    private FotoService fotoService;

    //Atributo para traer una Zona del repositorio según el Id
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    //Atributo para hacer uso del JavaMailSender
    @Autowired
    private NotificacionService notificacionService;

    /**
     * Registrar un nuevo Usuario desde cero
     *
     * @param archivo Foto de la Mascota
     * @param nombre
     * @param apellido
     * @param email
     * @param clave1 Ingreso de la contraseña
     * @param clave2 Repetir la misma contraseña
     * @param idZona Zona donde se ubica el Usuario (elegido de una lista)
     * @throws UsuarioServiceException
     */
    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String email, String clave1, String clave2, String idZona) throws UsuarioServiceException {

        //Validación de los parámetros
        validar(nombre, apellido, email, clave1, clave2, idZona, true);

        //Creamos el Objeto Usuario
        Usuario usuario = new Usuario();

        //Seteamos sus atributos con los parámetros recibidos
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(email);
        usuario.setClave(new BCryptPasswordEncoder().encode(clave1)); //Contraseña encriptada del Usuario
        usuario.setAlta(new Date());

        //Creamos el Objeto Foto haciendo uso de FotoService
        Foto foto = fotoService.guardar(archivo);

        //Seteamos la Foto al Usuario
        usuario.setFoto(foto);

        //Creamos el Objeto Zona con el Id recibido para setearlo al Usuario
        Zona zona = zonaRepositorio.getById(idZona);
        // Seteamos la zona
        usuario.setZona(zona);

        /* Invocamos al atributo repositorio (que es en realidad una interfaz que extiende
        de JpaRepository) y llamamos al método 'save' enviándole como argumento el Objeto
        Usuario para que lo guarde en la base de datos. */
        usuarioRepositorio.save(usuario);

        //Enviamos al Usuario recién registrado un correo de bienvenida a la aplicación
        //notificacionService.enviar("¡Bienvenidos al TinderFull de Mascotas!", "TinderFull de Mascotas", usuario.getMail());
    }

    //Modificar un Usuario ya existente en la base de datos
    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String email, String clave1, String clave2, String idZona) throws UsuarioServiceException {

        /* Llamamos a un método de UsuarioRepositorio para buscar un registro por el Id,
        atrapamos el resultado usando el método get() y lo guardo en mi Objeto Usuario */
        // Usuario usuario = usuarioRepositorio.findById(id).get();
        //
        //Validación de los parámetros
        validar(nombre, apellido, email, clave1, clave2, idZona, false);
        //Validación del mail
        validarMail(id, email);

        //Validamos que se encuentre un Usuario con el Id recibido
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        //Método que devuelve true si se encontró un registro en la base de datos
        if (respuesta.isPresent()) {

            //Guardamos el resultado de la búsqueda en el repositorio en el Objeto Usuario
            Usuario usuario = respuesta.get();

            //Seteamos los nuevos atributos al Objeto Usuario
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(email);
            usuario.setClave(new BCryptPasswordEncoder().encode(clave1)); //Clave encriptada

            //Creamos el Objeto Zona con el Id recibido para setearlo al Usuario
            Zona zona = zonaRepositorio.getById(idZona);
            // Seteamos la zona
            usuario.setZona(zona);

            //Para la foto, verificamos primero si ya existia una anterior para obtener su ID
            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            //Enviamos el id de la foto (o su valor nulo si no existia una anterior) y la foto nueva
            Foto foto = fotoService.actualizar(idFoto, archivo);
            usuario.setFoto(foto); // Seteamos el resultado del método actualizar

            //Actualizamos la entrada dentro del repositorio usando el mismo método save()
            usuarioRepositorio.save(usuario);

        } else {
            //Si el método .isPresent() da false es porque no se encontró ningún Usuario
            throw new UsuarioServiceException("No se encontró el usuario solicitado.");
        }
    }

    //Método para deshabilitar un Usuario
    @Transactional
    public void deshabilitar(String id) throws UsuarioServiceException {

        //Validamos que se encuentre un Usuario con el Id recibido
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        //Método que devuelve true si se encontró un registro en la base de datos
        if (respuesta.isPresent()) {

            //Guardamos el resultado de la búsqueda en el repositorio en el Objeto Usuario
            Usuario usuario = respuesta.get();

            //Seteamos la fecha actual de baja del Usuario
            usuario.setBaja(new Date());

            //Actualizamos la entrada dentro del repositorio usando el mismo método save()
            usuarioRepositorio.save(usuario);

        } else {
            //Si el método .isPresent() da false es porque no se encontró ningún Usuario
            throw new UsuarioServiceException("No se encontró el usuario solicitado.");
        }
    }

    //Método para rehabilitar un Usuario
    @Transactional
    public void rehabilitar(String id) throws UsuarioServiceException {

        //Validamos que se encuentre un Usuario con el Id recibido
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        //Método que devuelve true si se encontró un registro en la base de datos
        if (respuesta.isPresent()) {

            //Guardamos el resultado de la búsqueda en el repositorio en el Objeto Usuario
            Usuario usuario = respuesta.get();

            //Seteamos como null el atributo Baja del Usuario
            usuario.setBaja(null);

            //Actualizamos la entrada dentro del repositorio usando el mismo método save()
            usuarioRepositorio.save(usuario);

        } else {
            //Si el método .isPresent() da false es porque no se encontró ningún Usuario
            throw new UsuarioServiceException("No se encontró el usuario solicitado.");
        }
    }
    
    public Usuario buscarPorId(String id) {
        return usuarioRepositorio.getById(id);
    }

    //Método para delegar la tarea de validar los parámetros
    private void validar(String nombre, String apellido, String email, String clave1, String clave2, String idZona, boolean newUser) throws UsuarioServiceException {

        //Validaciones de los argumentos
        if (nombre == null || nombre.isEmpty()) {
            throw new UsuarioServiceException("El nombre del usuario no puede ser nulo.");
        }

        if (apellido == null || apellido.isEmpty()) {
            throw new UsuarioServiceException("El apellido del usuario no puede ser nulo.");
        }

        if (email == null || email.isEmpty()) {
            throw new UsuarioServiceException("El email del usuario no puede ser nulo.");
        } else if (!email.contains("@")) {
            throw new UsuarioServiceException("El email del usuario no es válido.");
        } else if (usuarioRepositorio.buscarPorMail(email) != null && newUser) {
            throw new UsuarioServiceException("El mail ya está en uso.");
        }

        if (clave1 == null || clave1.isEmpty()) {
            throw new UsuarioServiceException("La clave del usuario no puede ser nulo.");
        } else if (clave1.length() < 8) {
            throw new UsuarioServiceException("La clave del usuario debe contener 8 o más caracteres.");
        }

        //Validamos que las dos claves recibidas sean iguales
        if (!clave1.equals(clave2)) {
            throw new UsuarioServiceException("Las contraseñas no coinciden.");
        }

        //Validamos que el idZona sea válido
        Optional<Zona> respuesta = zonaRepositorio.findById(idZona);
        if (!respuesta.isPresent()) {
            throw new UsuarioServiceException("La zona indicada no es válida.");
        }
    }
    
    /**
     * Validamos que el nuevo mail ingresado de un usuario ya registrado sea nuevo o no pertenezca a otro usuario
     * @param id
     * @param email
     * @throws UsuarioServiceException 
     */
    private void validarMail(String id, String email) throws UsuarioServiceException {
        Usuario newUser = usuarioRepositorio.buscarPorMail(email);
        if (newUser != null && !id.equals(newUser.getId())) {
            throw new UsuarioServiceException("El nuevo mail ya está en uso.");
        }
    }

    /**
     * Este método nos ayudará a asignar permisos a los Usuarios que estén registrados en la base
     * de datos con su email. Si encuentra el Usuario, se crea un nuevo User con su email, su clave
     * y sus permisos concedidos como atributos y lo devuelve
     * @param email del Usuario (tiene que estar en la base de datos)
     * @return Un nuevo Objeto User con los atributos de Usuario y un List con permisos
     * @throws UsernameNotFoundException 
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Buscamos al Usuario en el repo por su email
        Usuario usuario = usuarioRepositorio.buscarPorMail(email);

        //Verificamos que hayamos encontrado un Usuario
        if (usuario != null) {

            //Creamos la Lista que contendrá los permisos necesarios para crear el Objeto User
            List<GrantedAuthority> permisos = new ArrayList();

            //Creamos cada permiso del tipo GrantedAuthority por cada módulo de nuestra aplicación
            GrantedAuthority permiso1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");

            //Añadimos cada permiso creado dentro de la lista...
            permisos.add(permiso1);
            
            /* Recuperamos los atributos del request HTTP, del request le hace un get a los datos de sesión y en esa sesión
            le seteamos un nuevo atributo con la key 'usuariosession' con los datos del Objeto Usuario en este scope */
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            //Creamos un Objeto del tipo User, propio de Spring Security
            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            //Si no se encuentra el Usuario indicado con el email, se retorna null
            return null;
        }
    }


}
