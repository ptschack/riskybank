package riskybank.services;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.UserRepository;

@Service("riskybank.services.authenticationService")
public class AuthenticationService implements AuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance(); // PasswordEncoderFactories.createDelegatingPasswordEncoder();

	public static final Set<Class<?>> SUPPORTED_AUTHENTICATION_TYPES = Collections.unmodifiableSet(Stream.of( //
			UsernamePasswordAuthenticationToken.class //
	).collect(Collectors.toSet()));

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getPrincipal().toString();
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Es gibt keinen User mit dem Namen \"" + username + "\"");
		}
		if (!passwordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
			throw new BadCredentialsException("Falsches Passwort");
		}
		return new UsernamePasswordAuthenticationToken( //
				user.getUsername(), //
				user.getPassword(), //
				user.getAuthorities() //
		);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return SUPPORTED_AUTHENTICATION_TYPES.contains(authentication);
	}

}
