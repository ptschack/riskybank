package riskybank.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/login", method = RequestMethod.GET)
public class LoginController extends AbstractController {

    @GetMapping
    public String login() {
        return "login";
    }

    @GetMapping("/error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        System.out.println("reached login?error");
        return "login";
    }

}
