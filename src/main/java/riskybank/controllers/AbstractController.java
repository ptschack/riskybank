package riskybank.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.UserRepository;

public abstract class AbstractController {

	@Autowired
	private UserRepository userRepo;

	protected User currentUser() {
		return Optional.ofNullable(SecurityContextHolder.getContext()) //
				.map(SecurityContext::getAuthentication) //
				.map(Authentication::getName) //
				.map(userRepo::findByUsername) //
				.orElse(null);
	}

}
