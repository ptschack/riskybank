package riskybank.persistence.entities;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "ROLE")
public class Role implements Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(Role.class);
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Set<Privilege> privileges;

	public Role() {

	}

	public Role(String name) {
		this();
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable( //
			name = "role_privilege", //
			joinColumns = @JoinColumn(name = "roleid", referencedColumnName = "id"), //
			inverseJoinColumns = @JoinColumn(name = "privilegeid", referencedColumnName = "id") //
	)
	public Set<Privilege> getPrivileges() {
		LOG.debug("getPrivileges: " + privileges.toString());
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

}
