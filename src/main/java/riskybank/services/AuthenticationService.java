package riskybank.services;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import riskybank.persistence.entities.Host;
import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.UserRepository;

@Service("riskybank.services.authenticationService")
@Transactional
public class AuthenticationService implements AuthenticationProvider {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HttpServletRequest request;

	private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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
		if (CollectionUtils.isNotEmpty(user.getErlaubteHosts())) {
			String host = clientIpErmitteln();
			boolean erlaubt = user.getErlaubteHosts().stream() //
					.map(Host::getName) //
					.filter(h -> host.equalsIgnoreCase(h)) //
					.findAny() //
					.isPresent();
			if (!erlaubt) {
				throw new InsufficientAuthenticationException("Host nicht erlaubt");
			}
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

	private String clientIpErmitteln() {
		return Optional.ofNullable(request.getHeader("X-Forwarded-For")) // wenn der Client Proxies benutzt ...
				.map(s -> s.split(",")) // ... IPs auslesen ...
				.map(array -> array.length > 0 ? array[0] : null) // ... und ursprüngliche IP nehmen
				.orElse(request.getRemoteAddr()); // ansonsten normal die remote-Adresse verwenden
	}

}
