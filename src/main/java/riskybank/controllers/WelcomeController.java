package riskybank.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

public class WelcomeController extends AbstractController {

	private static final Logger LOG = LoggerFactory.getLogger(WelcomeController.class);

//	@RequestMapping(value = "/welcome")
//	@PreAuthorize("isAuthenticated()")
//	public String welcome(Model model) {
//		model.addAttribute("konten", currentUser().getKonten());
//		model.addAttribute("name", currentUser().getVorname() + "" + currentUser().getNachname());
//		return "welcome";
//	}

}
