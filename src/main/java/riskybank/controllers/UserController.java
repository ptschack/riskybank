package riskybank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import riskybank.persistence.dao.UserDao;
import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.KontoRepository;

@Controller
public class UserController extends AbstractController {

	@Autowired
	private KontoRepository kontoRepo;

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/welcome")
	public String welcome(Model model) {
		User currentUser = currentUser();
		model.addAttribute("name", currentUser.getVorname() + " " + currentUser.getNachname());
		model.addAttribute("konten", kontoRepo.findByOwner(currentUser));
		return "welcome";
	}

	@RequestMapping(value = "/listAll", method = { RequestMethod.GET, RequestMethod.POST })
	@PreAuthorize("hasRole('ROLE_BENUTZER_AUFLISTEN')")
	@ResponseBody
	public String listAll(@RequestParam("query") String query) {
		return userDao.listAll(query);
	}

}
