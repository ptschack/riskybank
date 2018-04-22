package riskybank.sessionbeans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Historie {

	private List<String> protokoll = new ArrayList<>();

	public void addAktion(String aktion) {
		protokoll.add(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).concat(": ").concat(aktion));
	}

	public List<String> aktionenAuslesen() {
		return Collections.unmodifiableList(protokoll);
	}

}
