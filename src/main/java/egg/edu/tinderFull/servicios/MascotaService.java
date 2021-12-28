package egg.edu.tinderFull.servicios;

import egg.edu.tinderFull.entidades.Foto;
import egg.edu.tinderFull.entidades.Mascota;
import egg.edu.tinderFull.entidades.Usuario;
import egg.edu.tinderFull.enumeraciones.Sexo;
import egg.edu.tinderFull.enumeraciones.Tipo;
import egg.edu.tinderFull.excepciones.MascotaServiceException;
import egg.edu.tinderFull.repositorios.MascotaRepositorio;
import egg.edu.tinderFull.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class MascotaService {

    //ATRIBUTOS REPOSITORIOS AUTO-INICIALIZADOS
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    //ATRIBUTO FOTO SERVICE
    @Autowired
    private FotoService fotoService;

    //Método para agregar una Mascota
    @Transactional
    public void agregarMascota(MultipartFile archivo, String idUsuario, String nombre, Sexo sexo, Tipo tipo) throws MascotaServiceException {

        //Traemos al Usuario de la base de datos buscandolo por su Id
        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();

        //Validamos que los datos de la Mascota sean correctos
        validar(nombre, sexo);

        //Creamos el Objeto Mascota y le seteamos los atributos recibidos
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setTipo(tipo);
        mascota.setAlta(new Date());
        mascota.setUsuario(usuario);

        //Persistimos la foto y la creamos usando el método de FotoService
        Foto foto = fotoService.guardar(archivo);
        mascota.setFoto(foto); //Seteamos el Objeto Foto en el Objeto Mascota

        //Guardamos la Mascota en la base de datos
        mascotaRepositorio.save(mascota);
    }

    //Modificar una Mascota existente en la base de datos
    @Transactional
    public Mascota modificarMascota(MultipartFile archivo, String idUsuario, String idMascota, String nombre, Sexo sexo, Tipo tipo) throws MascotaServiceException {

        //Validación de los parámetros recibidos
        validar(nombre, sexo);

        //Verificamos si existe la Mascota con el id recibido como parámetro
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()) {
            Mascota mascota = respuesta.get();

            /* Verificamos que el id del Usuario que solicita la modificación sea el mismo que tiene
            la Mascota en sus atributos */
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                mascota.setTipo(tipo);

                //Verificamos si la Mascota ya tenia una foto seteada en sus atributos
                String idFoto = null;
                if (mascota.getFoto() != null) {
                    idFoto = mascota.getFoto().getId();
                }
                //Llamamos al método de Foto Service y le seteamos a la Mascota el resultado del método actualizar()
                Foto foto = fotoService.actualizar(idFoto, archivo);
                mascota.setFoto(foto);

                return mascotaRepositorio.save(mascota);
            } else {
                throw new MascotaServiceException("No tiene permisos suficientes para realizar la operación.");
            }
        } else {
            throw new MascotaServiceException("No existe una mascota con el identificador solicitado.");
        }
    }

    //Método para eliminar una Mascota (darla de baja)
    @Transactional
    public void eliminarMascota(String idUsuario, String idMascota) throws MascotaServiceException {

        //Verificamos si existe la Mascota con el id recibido como parámetro
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        if (respuesta.isPresent()) {

            Mascota mascota = respuesta.get();

            /* Verificamos que el id del Usuario que solicita la modificación sea el mismo que tiene
            la Mascota en sus atributos */
            if (mascota.getUsuario().getId().equals(idUsuario)) {

                //Si todo está ok, damos de baja a la Mascota con la fecha actual
                mascota.setBaja(new Date());
                mascotaRepositorio.save(mascota);
            } else {
                throw new MascotaServiceException("No tiene permisos suficientes para realizar la operación.");
            }
        } else {
            throw new MascotaServiceException("No existe una mascota con el identificador solicitado.");
        }
    }

    /**
     * Buscar una Mascota en la DB según su ID
     *
     * @param id
     * @return
     */
    public Mascota buscarPorId(String id) throws MascotaServiceException {

        Optional<Mascota> resultado = mascotaRepositorio.findById(id);
        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new MascotaServiceException("La Mascota indicada no ha sido encontrada.");
        }

    }
    
    /**
     * Devuelve una Lista de Mascotas según un Usuario mandando su id como parámetro
     * @param id
     * @return 
     */
    public List<Mascota> buscarMascotasPorUsuario(String id) {
        return mascotaRepositorio.buscarMascotasPorUsuario(id);
    }

    //Método para validar los atributos de una Mascota
    private void validar(String nombre, Sexo sexo) throws MascotaServiceException {
        if (nombre == null || nombre.isEmpty()) {
            throw new MascotaServiceException("El nombre de la mascota no puede estar vacío.");
        }
        if (sexo == null) {
            throw new MascotaServiceException("El sexo de la mascota no puede estar vacío.");
        }
    }


}
