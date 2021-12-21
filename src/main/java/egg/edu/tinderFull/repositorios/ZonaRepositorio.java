
package egg.edu.tinderFull.repositorios;

import egg.edu.tinderFull.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Matias Luca Soto
 */@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, String>{
    
}
