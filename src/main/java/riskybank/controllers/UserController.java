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
import org.springframework.web.bind.annotation.ResponseBody;
import riskybank.persistence.dao.UserDao;
import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.KontoRepository;

@Controller
public class UserController extends AbstractController {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private KontoRepository kontoRepo;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/welcome")
    @PreAuthorize("isAuthenticated()")
    public String welcome(Model model) {
        User currentUser = currentUser();
        model.addAttribute("name", currentUser.getVorname() + " " + currentUser.getNachname());
        if (currentUser.getRoles().stream().filter(r -> "ADMIN".equals(r.getName())).findAny().isPresent()) {
            historie.addAktion("Welcome-Seite fuer Admin aufgerufen");
            return "welcome-admin";
        }
        if (currentUser.getRoles().stream().filter(r -> "KUNDE".equals(r.getName())).findAny().isPresent()) {
            historie.addAktion("Welcome-Seite fuer Kunde aufgerufen");
            model.addAttribute("konten", kontoRepo.findByOwner(currentUser));
            return "welcome-kunde";
        }
        if (currentUser.getRoles().stream().filter(r -> "SERVICE".equals(r.getName())).findAny().isPresent()) {
            historie.addAktion("Welcome-Seite fuer Service aufgerufen");
            return "welcome-service";
        }
        historie.addAktion("Default Welcome-Seite fuer aufgerufen");
        return "welcome";
    }

    @RequestMapping(value = "/findUser", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("hasRole('ROLE_BENUTZER_AUFLISTEN')")
    @ResponseBody
    public String listAll(@RequestParam("username") String username) {
        historie.addAktion("Benutzer Suchseite aufgerufen mit parameter " + username);
        return userDao.listAll(username);
    }

}
