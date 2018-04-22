package riskybank.persistence.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "UEBERWEISUNG")
public class Ueberweisung implements Serializable {

	private static final Logger LOG = LoggerFactory.getLogger(Ueberweisung.class);

	private static final long serialVersionUID = 1L;

	private Long id;
	private Date datum;
	private BigDecimal betrag;
	private Konto quellkonto;
	private String zielIban;
	private String text;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DATUM")
	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	@Column(name = "BETRAG")
	public BigDecimal getBetrag() {
		return betrag;
	}

	public void setBetrag(BigDecimal betrag) {
		this.betrag = betrag;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "quellkonto")
	public Konto getQuellkonto() {
		LOG.debug("getQuellkonto: " + quellkonto.toString());
		return quellkonto;
	}

	public void setQuellkonto(Konto quellkonto) {
		this.quellkonto = quellkonto;
	}

	@Column(name = "ZIELIBAN")
	public String getZielIban() {
		return zielIban;
	}

	public void setZielIban(String zielIban) {
		this.zielIban = zielIban;
	}

	@Column(name = "TEXT")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Transient
	public String getFormatiertesDatum(){
		return new SimpleDateFormat("yyyy-MM-dd").format(getDatum());
	}

}
