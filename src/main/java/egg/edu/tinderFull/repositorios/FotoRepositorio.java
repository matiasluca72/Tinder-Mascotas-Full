package egg.edu.tinderFull.repositorios;

import egg.edu.tinderFull.entidades.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */
@Repository
public interface FotoRepositorio extends JpaRepository<Foto, String> {

}
