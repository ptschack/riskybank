package riskybank.services;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import riskybank.persistence.entities.Konto;
import riskybank.persistence.entities.Ueberweisung;
import riskybank.persistence.entities.User;
import riskybank.persistence.repositories.KontoRepository;
import riskybank.persistence.repositories.UeberweisungRepository;

@Service("riskybank.services.ueberweisungService")
public class UeberweisungService {

	@Autowired
	private UeberweisungRepository ueberweisungRepo;

	@Autowired
	private KontoRepository kontoRepo;

	public Ueberweisung ueberweisen(User auftraggeber, Long quellkontoId, Double betrag, String zielIban, String text) {
		Ueberweisung ueberweisung = ueberweisungErstellen(quellkontoId, betrag, zielIban, text);
		ueberweisungPruefen(auftraggeber, ueberweisung);
		ueberweisungDurchfuehren(auftraggeber, ueberweisung);
		return ueberweisung;
	}

	private Ueberweisung ueberweisungErstellen(Long quellkontoId, Double betrag, String zielIban, String text) {
		Konto quellkonto = kontoRepo.findById(quellkontoId).orElseThrow(() -> new RuntimeException("Konto mit ID " + quellkontoId + "nicht gefunden"));
		Ueberweisung u = new Ueberweisung();
		u.setQuellkonto(quellkonto);
		u.setDatum(new Date());
		if (betrag == null) {
			throw new RuntimeException("Ueberweisung erfordert Betrag > 0");
		}
		u.setBetrag(BigDecimal.valueOf(betrag));
		u.setZielIban(zielIban);
		u.setText(text);
		return u;
	}

	private void ueberweisungPruefen(User user, Ueberweisung ueberweisung) {
		// Kontoeigentümer prüfen
		if (ueberweisung.getQuellkonto().getOwner() != user) {
			throw new RuntimeException("Quellkonto passt nicht zu angemeldetem Kunden");
		}
		// Deckung prüfen
		if (ueberweisung.getBetrag().compareTo(ueberweisung.getQuellkonto().getSaldo()) > 0) {
			throw new RuntimeException("Deckung nicht ausreichend");
		}
		// Validieren
		if (ueberweisung.getBetrag().compareTo(BigDecimal.ZERO) < 0) {
			throw new RuntimeException("Ueberweisung erfordert Betrag > 0");
		}
		if (StringUtils.isBlank(ueberweisung.getText())) {
			throw new RuntimeException("Bitte Verwendungszweck angeben");
		}
		if (StringUtils.isBlank(ueberweisung.getZielIban())) {
			throw new RuntimeException("Bitte zielIban angeben");
		}
	}

	private void ueberweisungDurchfuehren(User angemeldeterUser, Ueberweisung ueberweisung) {
		Konto quellkonto = ueberweisung.getQuellkonto();
		BigDecimal alterSaldo = quellkonto.getSaldo();
		BigDecimal neuerSaldo = alterSaldo.subtract(ueberweisung.getBetrag());
		ueberweisungRepo.save(ueberweisung);
		quellkonto.setSaldo(neuerSaldo);
		kontoRepo.save(quellkonto);
	}
	
	public List<Ueberweisung> ermittleUeberweisungenFuerKunden(User u){
		return kontoRepo.findByOwner(u).stream().flatMap(k -> ueberweisungRepo.findByQuellkonto(k).stream()).collect(Collectors.toList());
	}

}
