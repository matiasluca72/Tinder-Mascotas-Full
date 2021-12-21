package egg.edu.tinderFull.repositorios;

import egg.edu.tinderFull.entidades.Voto;
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
public interface VotoRepositorio extends JpaRepository<Voto, String> {

    //Buscar los votos que mi Mascota generó o envió
    @Query("SELECT c FROM Voto c WHERE c.mascotaOrigin.id = :id ORDER BY c.fecha DESC")
    public List<Voto> buscarVotosPropios(@Param("id") String id);

    //Buscar los votos que mi Mascota recibió
    @Query("SELECT c FROM Voto c WHERE c.mascotaDestiny.id = :id ORDER BY c.fecha DESC")
    public List<Voto> buscarVotosRecibidos(@Param("id") String id);
}
