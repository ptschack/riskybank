package riskybank.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IpBlockService {

	private static final Logger LOG = LoggerFactory.getLogger(IpBlockService.class);

	public static final long ZEITRAUM_IN_STUNDEN = 24;
	public static final long MAX_VERSUCHE = 5;
	private Map<String, List<LocalDateTime>> versuche = new ConcurrentHashMap<>();

	public void loginErfolgreich(String host) {
		LOG.debug("Setze Anzahl Versuche zurück für " + host);
		versuche.put(host, new ArrayList<>());
	}

	public void loginNichtErfolgreich(String host) {
		versuchslisteErmitteln(host).add(LocalDateTime.now());
	}

	public int fehlgeschlageneVersuche(String host) {
		List<LocalDateTime> l = versuchslisteErmitteln(host);
		aufraeumen(l);
		LOG.debug("Für " + host + " gab es in den letzten " + ZEITRAUM_IN_STUNDEN + " Stunden " + l.size()
				+ " fehlgeschlagene Loginversuche");
		return l.size();
	}

	private void aufraeumen(List<LocalDateTime> versuche) {
		LOG.debug("Räume Versuchsliste auf. Alte Größe: " + versuche.size());
		Iterator<LocalDateTime> i = versuche.iterator();
		while (i.hasNext()) {
			if (i.next().isBefore(LocalDateTime.now().minusHours(ZEITRAUM_IN_STUNDEN))) {
				i.remove();
			}
		}
		LOG.debug("Versuchsliste aufgeräumt. Neue Größe: " + versuche.size());
	}

	private List<LocalDateTime> versuchslisteErmitteln(String host) {
		LOG.debug("ermittle Versuchsliste für " + host);
		if (!versuche.containsKey(host)) {
			LOG.debug("Keine Versuchsliste für " + host + " vorhanden, lege eine neue an");
			versuche.put(host, new ArrayList<>());
		}
		return versuche.get(host);
	}

	public boolean istBlockiert(String host) {
		boolean ergebnis = fehlgeschlageneVersuche(host) > MAX_VERSUCHE;
		LOG.debug(host + (ergebnis ? " ist blockiert" : " ist nicht blockiert"));
		return ergebnis;
	}

}
