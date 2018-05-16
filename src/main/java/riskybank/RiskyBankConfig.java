package riskybank;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import riskybank.services.IpBlockService;
import riskybank.services.UserService;

/**
 * Konfigurationsklasse für RiskyBank-Anwendung
 * 
 * <br />
 * Anmerkung: einige der mit {@link Bean} annotierten Methoden liefern per
 * Lambda generierte anonyme Klassen zurück, z.B. {@link #failureListener()}
 * 
 * @author Patrick Tschackert / Acando GmbH
 * 
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RiskyBankConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(RiskyBankConfig.class);

	/** akzeptierte Session-Tracking Mechanismen */
	public static final Set<SessionTrackingMode> VALID_TRACKING_MODES = Collections.unmodifiableSet(Stream.of( //
			SessionTrackingMode.COOKIE //
	).collect(Collectors.toSet()));

	@Autowired
	private UserService userService;

	@Autowired
	private IpBlockService ipBlocker;

	@Bean
	public WebMvcConfigurer webMvcKonfigurieren() {
		return new WebMvcConfigurer() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addRedirectViewController("/", "/welcome");
			}
		};
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http // Hier ist die Reihenfolge wichtig!
				.csrf().disable() // CSRF-Schutz deaktivieren
				// .csrf().ignoringAntMatchers("/h2_console/**")
				// .authorizeRequests().antMatchers("/**").hasAnyAuthority("KUNDE", "SERVICE",
				// "ADMIN").and() //
				.formLogin() // Login-Formular. Customizable mittels .loginPage(loginPage)
				.and().logout() // Logout. Customizable durch .logoutUrl(logoutUrl)
				.and().headers().frameOptions().sameOrigin() // Frames für H2 Console erlauben
		;
		http //
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //
				.and().sessionManagement().maximumSessions(1) //
				.and().sessionFixation().migrateSession() //
		;
	}

	/**
	 * setzt die Komponente, die Authentifizierung behandelt
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService);
		// auth.authenticationProvider(authService); // Custom-Authentifizierung
	}

	/**
	 * diese Methode existiert, damit das Framework bei der Invalidierung von
	 * Sessions benachrichtigt wird
	 * 
	 * @return HttpSessionEventPublisher
	 */
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	/**
	 * konfiguriert die zul&auml;ssigen Session-Tracking-Modi
	 * 
	 * @return ServletContextInitializer
	 */
	@Bean
	public ServletContextInitializer initializer() {
		return (ServletContext servletContext) -> {
			servletContext.setSessionTrackingModes(VALID_TRACKING_MODES);
		};
	}

	/**
	 * @return einen Listener, der auf erfolgreiche Loginversuche reagiert
	 */
	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> successListener() {
		return (AuthenticationSuccessEvent event) -> {
			String details = readDetails(event);
			LOG.debug("Login erfolgreich von " + details);
			ipBlocker.loginErfolgreich(details);
		};
	}

	/**
	 * @return einen Listener, der auf fehlgeschlagene Loginversuche reagiert
	 */
	@Bean
	public ApplicationListener<AuthenticationFailureBadCredentialsEvent> failureListener() {
		return (AuthenticationFailureBadCredentialsEvent event) -> {
			String details = readDetails(event);
			LOG.debug("Login fehlgeschlagen von " + details);
			ipBlocker.loginNichtErfolgreich(details);
		};
	}

	/**
	 * liest Details aus einem AbstractAuthenticationEvent, hoffentlich den Host
	 * 
	 * @param event
	 * @return
	 */
	private static String readDetails(AbstractAuthenticationEvent event) {
		return Optional.ofNullable(event) //
				.map(AbstractAuthenticationEvent::getAuthentication) //
				.map(Authentication::getDetails) //
				.map(d -> d.getClass().isAssignableFrom(WebAuthenticationDetails.class)
						? ((WebAuthenticationDetails) d).getRemoteAddress()
						: d.toString())
				.orElse("");
	}

}
