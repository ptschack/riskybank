package riskybank.persistence.entities;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "USER")
public class User implements UserDetails {

	private static final Logger LOG = LoggerFactory.getLogger(User.class);

	private static final long serialVersionUID = 1L;

	private Long id;
	private String vorname;
	private String nachname;
	private String username;
	private String password;
	private String telefonnummer;
	private Long treuepunkte;
	private Boolean aktiv;
	private Boolean gesperrt;
	private Set<Role> roles;
	private Set<Host> erlaubteHosts;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "VORNAME")
	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	@Column(name = "NACHNAME")
	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	@Column(name = "USERNAME")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "PASSWORD")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "TELEFONNUMMER")
	public String getTelefonnummer() {
		return telefonnummer;
	}

	public void setTelefonnummer(String telefonnummer) {
		this.telefonnummer = telefonnummer;
	}

	@Column(name = "TREUEPUNKTE")
	public Long getTreuepunkte() {
		return treuepunkte;
	}

	public void setTreuepunkte(Long treuepunkte) {
		this.treuepunkte = treuepunkte;
	}

	@Column(name = "AKTIV")
	public boolean isEnabled() {
		return aktiv;
	}

	public void setEnabled(Boolean enabled) {
		this.aktiv = enabled;
	}

	@Column(name = "GESPERRT")
	public Boolean getGesperrt() {
		return gesperrt;
	}

	public void setGesperrt(Boolean gesperrt) {
		this.gesperrt = gesperrt;
	}

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable( //
			name = "USER_ROLE", //
			joinColumns = { @JoinColumn(name = "USERID", referencedColumnName = "ID") }, //
			inverseJoinColumns = { @JoinColumn(name = "ROLEID", referencedColumnName = "ID", unique = true) } //
	)
	public Set<Role> getRoles() {
		LOG.debug("getRoles: " + roles.toString());
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	@Transient
	public Set<Privilege> getAuthorities() {
		return getRoles().stream() //
				.map(Role::getPrivileges) //
				.flatMap(Set::stream) //
				.filter(Objects::nonNull) //
				.collect(Collectors.toSet());
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return !getGesperrt();
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable( //
			name = "ERLAUBTE_HOSTS", //
			joinColumns = { @JoinColumn(name = "USERID", referencedColumnName = "ID") }, //
			inverseJoinColumns = { @JoinColumn(name = "HOSTID", referencedColumnName = "ID", unique = true) } //
	)
	public Set<Host> getErlaubteHosts() {
		return erlaubteHosts;
	}

	public void setErlaubteHosts(Set<Host> erlaubteHosts) {
		this.erlaubteHosts = erlaubteHosts;
	}

}
