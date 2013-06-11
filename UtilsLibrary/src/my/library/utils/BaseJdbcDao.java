package my.library.utils;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;

/**
 * commons-lang3-3.1.jar, commons-dbcp-1.4.jar
 * 
 * @author ngoctdn
 *
 */
public class BaseJdbcDao {

	private DataSource ds;
	public static String PATTERN_PARAM = "\\@\\{([^\\}]+)\\}";
	public static String PATTERN_TABLE = "\\@\\{_([^\\}]+)\\}";

	public BaseJdbcDao(String driver, String connUrl, String username, String password) {
		ds = buildDataSource(driver, connUrl, username, password);
	}

	public DataSource buildDataSource(String driver, String connUrl, String username,
			String password) {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driver);
		bds.setUrl(connUrl);
		bds.setUsername(username);
		bds.setPassword(password);
		return bds;
	}

	public BaseJdbcDao(DataSource dts) {
		this.ds = dts;
	}

	public Connection getConnection() throws SQLException {
		Connection conn = ds.getConnection();
		return conn;
	}

	public void releaseConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {

			} finally {
				conn = null;
			}
		}
	}

	public void closeResources(Connection conn, Statement stm, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			} finally {
				rs = null;
			}
		}
		if (stm != null) {
			try {
				stm.close();
			} catch (Exception e) {
			} finally {
				stm = null;
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
			} finally {
				conn = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public PreparedStatement preparedStatement(Connection conn, String sql, Object params)
			throws SQLException {
		if (params == null) {
			return buildStatement(conn, sql, (Object[]) params);
		} else if (params instanceof Object[]) {
			return buildStatement(conn, sql, (Object[]) params);
		} else if (params instanceof Map<?, ?>) {
			return buildStatement(conn, sql, (Map<String, Object>) params);
		}
		return null;
	}

	public PreparedStatement buildStatement(Connection conn, String sql, Map<String, Object> params)
			throws SQLException {
		String cleanSql = RegexUtils.replaceRegexToValue(sql, PATTERN_TABLE, params);
		String[] listParams = RegexUtils.extractParams(cleanSql, PATTERN_PARAM);
		List<Object> listValues = new ArrayList<Object>();
		for (String param : listParams) {
			if (params.containsKey(param)) {
				listValues.add(params.get(param));
			} else {
				throw new SQLException("Missing parameter " + param);
			}
		}
		return buildStatement(conn, cleanSql, listValues.toArray());
	}

	public PreparedStatement buildStatement(Connection conn, String sql, Object[] params)
			throws SQLException {
		String cleanSql = sql.replaceAll(PATTERN_PARAM, "?");
		PreparedStatement stmt = conn.prepareStatement(cleanSql);
		int numNeedParam = StringUtils.countMatches(cleanSql, "?");
		if (params != null && params.length > 0) {
			Object param;
			for (int index = 1; index <= numNeedParam; index++) {
				try {
					param = params[index - 1];
				} catch (Exception e) {
					throw new SQLException("Missing parameter " + index);
				}
				if (param instanceof String) {
					stmt.setString(index, (String) param);
				} else if (param instanceof Integer) {
					stmt.setInt(index, (Integer) param);
				} else if (param instanceof Long) {
					stmt.setLong(index, (Long) param);
				} else if (param instanceof Float) {
					stmt.setFloat(index, (Float) param);
				} else if (param instanceof Double) {
					stmt.setDouble(index, (Double) param);
				} else {
					stmt.setObject(index, param);
				}
			}
		}
		return stmt;
	}

	public List<PreparedStatement> getListStatements(Connection conn, String[] sqlCommands,
			Object params) throws SQLException {
		List<PreparedStatement> listStates = new ArrayList<PreparedStatement>();
		for (String command : sqlCommands) {
			PreparedStatement preState = preparedStatement(conn, command, params);
			listStates.add(preState);
		}
		return listStates;
	}

	public long executeNonSelect(String sqlCommand, Object params) throws SQLException {
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		PreparedStatement stmt = null;
		try {
			stmt = preparedStatement(conn, sqlCommand, params);
			long result = stmt.executeUpdate();
			return result;
		} finally {
			closeResources(conn, stmt, null);
		}
	}

	public long executeCount(String sqlCommand, Object params) throws SQLException {
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		long result = 0;
		try {
			stmt = preparedStatement(conn, sqlCommand, params);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getLong(1);
			}
			return result;
		} finally {
			closeResources(conn, stmt, rs);
		}
	}

	public List<Map<String, Object>> executeSelect(String sqlCommand, Object params)
			throws SQLException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = preparedStatement(conn, sqlCommand, params);
			rs = stmt.executeQuery();
			ResultSetMetaData rsMetaData = rs != null ? rs.getMetaData() : null;
			while (rs.next()) {
				Map<String, Object> obj = new HashMap<String, Object>();
				for (int i = 1, n = rsMetaData.getColumnCount(); i <= n; i++) {
					String colName = rsMetaData.getColumnName(i);
					Object value = rs.getObject(colName);
					obj.put(colName, value);
				}
				result.add(obj);
			}
			return result;
		} finally {
			closeResources(conn, stmt, rs);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseBo> T[] executeSelect(String sqlCommand, Object params, Class<T> clazz)
			throws SQLException {
		List<T> result = new ArrayList<T>();
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = preparedStatement(conn, sqlCommand, params);
			rs = stmt.executeQuery();
			while (rs.next()) {
				T obj = null;
				try {
					obj = clazz.newInstance();
					obj.populate(rs);
					result.add(obj);
				} catch (Exception e) {
					throw new SQLException(e);
				}
			}
			return result.toArray((T[]) Array.newInstance(clazz, 0));
		} finally {
			closeResources(conn, stmt, rs);
		}

	}

	public void executeMultiCommands(String[] sqlCommand, Object params) throws SQLException {
		Boolean autoCommitOldValue = true;
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException("Can not make connection to database!");
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			autoCommitOldValue = conn.getAutoCommit();
			conn.setAutoCommit(false);
			List<PreparedStatement> statements = getListStatements(conn, sqlCommand, params);
			for (PreparedStatement ps : statements) {
				ps.execute();
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (Exception e1) {
				}
			}
		} finally {
			closeResources(null, stmt, rs);
			if (conn != null) {
				try {
					conn.setAutoCommit(autoCommitOldValue);
					conn.close();
				} catch (Exception e) {
				} finally {
					conn = null;
				}
			}
		}
	}
}
