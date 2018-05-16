package riskybank.api;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import riskybank.persistence.entities.Ueberweisung;
import riskybank.persistence.repositories.UeberweisungRepository;

@RestController
@RequestMapping("/api/ueberweisung")
public class RestUeberweisungController {

	private static final String UEBERWEISUNG_FILTERN_NACH_AUFTRAGGEBER = "filterObject.quellkonto.owner.username eq authentication.name";

	@Autowired
	private UeberweisungRepository ueberweisungRepo;

	@RequestMapping("/{id}")
	@PostAuthorize("returnObject.quellkonto.owner.username eq authentication.name")
	public Ueberweisung find(@PathVariable Long id) {
		return ueberweisungRepo.findById(id).orElse(null);
	}

	@RequestMapping("/")
	@PostFilter(UEBERWEISUNG_FILTERN_NACH_AUFTRAGGEBER)
	public Collection<Ueberweisung> listAll() {
		return StreamSupport.stream(ueberweisungRepo.findAll().spliterator(), false) //
				.collect(Collectors.toSet());
	}

}
