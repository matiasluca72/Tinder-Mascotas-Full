package egg.edu.tinderFull.servicios;

import egg.edu.tinderFull.entidades.Zona;
import egg.edu.tinderFull.repositorios.ZonaRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Matias Luca Soto
 */
@Service
public class ZonaService {

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    public List<Zona> listarZonas() {
        return zonaRepositorio.findAll();
    }

}
