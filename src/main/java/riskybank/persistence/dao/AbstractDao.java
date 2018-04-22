package riskybank.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public abstract class AbstractDao {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Map<String, String>> query(String sql) {
		List<Map<String, String>> result = new ArrayList<>();
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		String[] columnNames = set.getMetaData().getColumnNames();
		while (set.next()) {
			Map<String, String> m = new HashMap<>();
			for (String s : columnNames) {
				m.put(s, set.getString(s));
			}
			result.add(m);
		}
		return result;
	}

}
