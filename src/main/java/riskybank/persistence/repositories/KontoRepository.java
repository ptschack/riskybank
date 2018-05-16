package riskybank.persistence.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import riskybank.persistence.entities.Konto;
import riskybank.persistence.entities.User;

public interface KontoRepository extends CrudRepository<Konto, Long> {

	List<Konto> findByOwner(User owner);

}
