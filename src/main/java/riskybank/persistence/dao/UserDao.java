package riskybank.persistence.dao;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("riskybank.persistence.userDao")
public class UserDao extends AbstractDao {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);

	public String listAll(String username) {
		String result = "";
		List<Map<String, String>> list = query("SELECT * FROM USER u WHERE u.username='" + username + "'");
		for (Map<String, String> m : list) {
			for (String key : m.keySet()) {
				result += "(" + key + " : " + m.get(key) + ")";
			}
			result += "\n";
		}
		return result;
	}

}
