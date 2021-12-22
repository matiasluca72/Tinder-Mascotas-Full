package egg.edu.tinderFull.servicios;

import egg.edu.tinderFull.entidades.Foto;
import egg.edu.tinderFull.repositorios.FotoRepositorio;
import java.io.IOException;
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
public class FotoService {

    //ATRIBUTO REPOSITORIO
    @Autowired
    private FotoRepositorio fotoRepositorio;

    /**
     * Método para guardar una foto
     *
     * @param archivo De tipo MultipartFile que nos servirá para almacenar archivos
     * @return Un Objeto Foto creado y persistido
     */
    @Transactional
    public Foto guardar(MultipartFile archivo) {

        //Verificación de que el archivo no esté nulo. Si lo está, se devuelve null
        if (archivo != null) {

            try {
                //Creamos el Objeto Foto
                Foto foto = new Foto();

                //Seteamos el mime de la Foto haciendo uso de un getter del archivo recibido
                foto.setMime(archivo.getContentType());

                //Seteamos el nombre que lleva el archivo recibido
                foto.setNombre(archivo.getName());

                //Seteamos el contenido de la foto obteniendo los bytes del archivo
                foto.setContenido(archivo.getBytes());
                
                //Persistimos y devolvemos la Foto creada
                return fotoRepositorio.save(foto);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        
        //Si la foto está null o salta un error, se retorna null
        return null;
    }
    
    /**
     * Actualizar la foto de la Mascota de un Usuario
     * @param idFoto De la foto anterior a reemplazar
     * @param archivo Nueva foto a actualizar
     * @return Foto actualizada
     */
    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) {
        
        if (archivo != null) {
            try {
                Foto foto = new Foto();

                //Si el Id de la foto anterior no es nulo, lo busco en el repo
                if (idFoto != null) {
                    
                    //Buscamos la foto en la base de datos
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    
                    //Si encontré la foto, la traigo y la piso con los atributos nuevos
                    if (respuesta.isPresent()) {
                        foto = respuesta.get();
                    }
                }
                
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        //Si la foto está null o salta un error, se retorna null
        return null;
    }
}
