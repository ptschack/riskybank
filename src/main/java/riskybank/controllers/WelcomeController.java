package riskybank.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

public class WelcomeController extends AbstractController {

	@RequestMapping(value = "/welcome")
	@PreAuthorize("isAuthenticated()")
	public String welcome(Model model) {
		model.addAttribute("konten", currentUser().getKonten());
		model.addAttribute("name", currentUser().getVorname() + "" + currentUser().getNachname());
		return "welcome";
	}

}
