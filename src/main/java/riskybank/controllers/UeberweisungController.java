package riskybank.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import riskybank.persistence.entities.Ueberweisung;
import riskybank.persistence.repositories.KontoRepository;
import riskybank.services.UeberweisungService;

import javax.servlet.http.HttpSession;

@Controller
public class UeberweisungController extends AbstractController {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(UeberweisungController.class);

	@Autowired
	private UeberweisungService ueberweisungService;
	
	@Autowired
	private KontoRepository kontoRepo;

	@RequestMapping(value = "/ueberweisung")
	@PreAuthorize("hasRole('ROLE_UEBERWEISUNG_TAETIGEN')")
	public String ueberweisung(Model model) {
		historie.addAktion("Ueberweisungs-Seite aufgerufen");
        model.addAttribute("name", currentUser().getVorname() + " " + currentUser().getNachname());
		model.addAttribute("konten", kontoRepo.findByOwner(currentUser()));
		return "ueberweisung";
	}

	@RequestMapping(value = "/doUeberweisung", method = { RequestMethod.GET, RequestMethod.POST })
	@PreAuthorize("hasRole('ROLE_UEBERWEISUNG_TAETIGEN')")
	public String doUeberweisung(@RequestParam("quellkonto") Long quellkonto, @RequestParam("iban") String iban,
			@RequestParam("betrag") final Double betrag, @RequestParam("betrag") String text, Model model,
			HttpSession session) {
		historie.addAktion("Versuch, Ueberweisung durchzufuehren");
        model.addAttribute("name", currentUser().getVorname() + " " + currentUser().getNachname());
        try {
			Ueberweisung ueberweisung = ueberweisungService.ueberweisen(currentUser(), quellkonto, betrag, iban, text);
			model.addAttribute("ueberweisung", ueberweisung);
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "ueberweisungFehler";
		}
		return "bestaetigung";
	}

	@RequestMapping(value = "/ueberweisungenAnzeigen")
	@PreAuthorize("hasRole('ROLE_UEBERWEISUNGEN_ANZEIGEN')")
	public String ueberweisungenAnzeigen(Model model) {
		historie.addAktion("Ueberweisungen anzeigen");
        model.addAttribute("name", currentUser().getVorname() + " " + currentUser().getNachname());
        model.addAttribute("ueberweisungen", ueberweisungService.ermittleUeberweisungenFuerKunden(currentUser()));
		return "umsaetze";
	}

}
