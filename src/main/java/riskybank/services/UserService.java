package riskybank.services;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.UserRepository;

@Service("riskybank.services.userService")
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IpBlockService ipBlocker;

	@Autowired
	private HttpServletRequest request;

	private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance(); // PasswordEncoderFactories.createDelegatingPasswordEncoder();

	public void neuenUserRegistrieren(String vorname, String nachname, String username, String password,
			String telefonnummer) {
		User neu = new User();
		neu.setVorname(vorname);
		neu.setNachname(nachname);
		neu.setUsername(username);
		neu.setPassword(password);
		neu.setPassword(passwordEncoder.encode(password));
		neu.setTelefonnummer(telefonnummer);
		userRepo.save(neu);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (ipBlocker.istBlockiert(clientIpErmitteln())) {
			throw new RuntimeException("Host blockiert");
		}
		return userRepo.findByUsername(username);
	}

	private String clientIpErmitteln() {
		return Optional.ofNullable(request.getHeader("X-Forwarded-For")) // wenn der Client Proxies benutzt ...
				.map(s -> s.split(",")) // ... IPs auslesen ...
				.map(array -> array.length > 0 ? array[0] : null) // ... und ursprüngliche IP nehmen
				.orElse(request.getRemoteAddr()); // ansonsten normal die remote-Adresse verwenden 
	}

}
