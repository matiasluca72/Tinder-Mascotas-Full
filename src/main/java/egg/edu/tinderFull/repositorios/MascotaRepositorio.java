package egg.edu.tinderFull.repositorios;

import egg.edu.tinderFull.entidades.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */
@Repository
public interface MascotaRepositorio extends JpaRepository<Mascota, String> {

    /**
     * Trae una Lista de Mascotas relacionadas al id del Usuario enviado como parámetro
     * @param id del Usuario a buscar sus mascotas
     * @return Las Mascotas relacionadas a ese Usuario
     */
    @Query("SELECT m FROM Mascota m WHERE m.usuario.id = :id")
    public List<Mascota> buscarMascotasPorUsuario(@Param("id") String id);

}
