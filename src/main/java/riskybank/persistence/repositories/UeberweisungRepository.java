package riskybank.persistence.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import riskybank.persistence.entities.Konto;
import riskybank.persistence.entities.Ueberweisung;

public interface UeberweisungRepository extends CrudRepository<Ueberweisung, Long> {
	
	List<Ueberweisung> findByQuellkonto(Konto k);

}
