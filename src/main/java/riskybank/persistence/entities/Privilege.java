package riskybank.persistence.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "PRIVILEGE")
public class Privilege implements GrantedAuthority, Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(Privilege.class);
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;

	public Privilege() {

	}

	public Privilege(String name) {
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

	@Override
	@Transient
	public String getAuthority() {
		return getName();
	}

}
