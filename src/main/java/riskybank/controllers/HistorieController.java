package riskybank.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HistorieController extends AbstractController {

	@RequestMapping(value = "/historie")
	@PreAuthorize("isAuthenticated()")
	public String historie(Model model) {
		historie.addAktion("Historie-Seite aufgerufen");
		model.addAttribute("name", currentUser().getVorname() + " " + currentUser().getNachname());
		model.addAttribute("aktionen", historie.aktionenAuslesen());
		return "historie";
	}

}
