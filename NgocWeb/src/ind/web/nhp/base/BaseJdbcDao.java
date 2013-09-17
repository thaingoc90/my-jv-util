package ind.web.nhp.base;

import ind.web.nhp.utils.PropsUtils;
import ind.web.nhp.utils.RegexUtils;
import ind.web.nhp.utils.SqlProperties;

import java.io.InputStream;
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
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

public class BaseJdbcDao extends CacheDao {

	public static final String EXC_NOT_CONNECT_DB = "Can not make connection to database!";
	private final static int NUM_PROCESSORS = Runtime.getRuntime().availableProcessors();

	private static Logger LOGGER = LoggerFactory.getLogger(BaseJdbcDao.class);
	private DataSource ds;
	public static String PATTERN_PARAM = "\\@\\{([^\\}]+)\\}";
	public static String PATTERN_TABLE = "\\@\\{_([^\\}]+)\\}";
	private String driver, connUrl, username, password;
	private String sqlPropsLocation;
	private Properties sqlProps = new SqlProperties();
	private ConcurrentMap<String, String> cacheSqlProps = new MapMaker().concurrencyLevel(
			NUM_PROCESSORS).makeMap();

	public void init() {
		super.init();
		if (ds == null) {
			ds = buildDataSource(driver, connUrl, username, password);
		}
		loadSqlProps();
	}

	public void destroy() {
		ds = null;
	}

	/**
	 * Builds a DataSource.
	 * 
	 * @param driver
	 * @param connUrl
	 * @param username
	 * @param password
	 * @return
	 */
	public DataSource buildDataSource(String driver, String connUrl, String username,
			String password) {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driver);
		bds.setUrl(connUrl);
		bds.setUsername(username);
		bds.setPassword(password);
		return bds;
	}

	/**
	 * Loads SQL properties.
	 */
	protected void loadSqlProps() {
		this.sqlProps.clear();
		this.cacheSqlProps.clear();

		String sqlLocation = getSqlPropsLocation();
		if (sqlLocation != null) {
			InputStream is = getClass().getResourceAsStream(sqlLocation);
			if (is != null) {
				Properties props = PropsUtils.loadProperties(is, sqlLocation.endsWith(".xml"));
				if (props != null) {
					this.sqlProps.putAll(props);
				} else {
					String msg = "Can not load SQL properties from [" + sqlLocation + "]!";
					LOGGER.warn(msg);
				}
			} else {
				String msg = "Location of sql is invalid!";
				LOGGER.warn(msg);
			}
		} else {
			String msg = "Location of sql is invalid!";
			LOGGER.warn(msg);
		}
	}

	protected String getSqlProps(String name) {
		String result = cacheSqlProps.get(name);
		if (result == null) {
			result = sqlProps.getProperty(name);
			if (!StringUtils.isBlank(result)) {
				try {
					cacheSqlProps.put(name, result);
				} catch (Exception e) {
					LOGGER.warn(e.getMessage(), e);
					result = null;
				}
			}
		}
		return result;
	}

	/**
	 * Obtains and builds the sql
	 * 
	 * @param sqkKey
	 * @return
	 */
	protected String buildSqlProps(final Object sqlKey) {
		final String finalKey = (sqlKey instanceof Object[]) ? ((Object[]) sqlKey)[0].toString()
				: sqlKey.toString();
		String sql = getSqlProps(finalKey);
		if (sql != null && sqlKey instanceof Object[]) {
			Object[] temp = (Object[]) sqlKey;
			for (int i = 1; i < temp.length; i++) {
				sql = sql.replaceAll("\\{" + i + "\\}", temp[i] != null ? temp[i].toString() : "");
			}
		}
		return sql;
	}

	/**
	 * Obtain a database connection from the JDBC datasource.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = ds.getConnection();
		return conn;
	}

	/**
	 * Releases an opening database connection.
	 * 
	 * @param conn
	 * @param stm
	 * @param rs
	 */
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

	/**
	 * Builds a PreparedStatement.
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * * Builds a PreparedStatement which params is a map.
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Builds a PreparedStatement which params is an array.
	 * 
	 * @param conn
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * 
	 * Builds a list of PreparedStatements from multi sqlCommands
	 * 
	 * @param conn
	 * @param sqlCommands
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<PreparedStatement> getListStatements(Connection conn, String[] sqlCommands,
			Object params) throws SQLException {
		List<PreparedStatement> listStates = new ArrayList<PreparedStatement>();
		for (String command : sqlCommands) {
			PreparedStatement preState = preparedStatement(conn, command, params);
			listStates.add(preState);
		}
		return listStates;
	}

	/**
	 * Executes a non-SELECT query and returns the number of affected rows.
	 * 
	 * @param sqlKey
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public long executeNonSelect(String sqlKey, Object params) throws SQLException {
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException(EXC_NOT_CONNECT_DB);
		}
		PreparedStatement stmt = null;
		try {
			String sql = buildSqlProps(sqlKey);
			if (sql == null) {
				throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
			}
			stmt = preparedStatement(conn, sql, params);
			long result = stmt.executeUpdate();
			return result;
		} finally {
			closeResources(conn, stmt, null);
		}
	}

	/**
	 * Executes a COUNT query and returns the result. Don't use cache.
	 * 
	 * @param sqlKey
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public long executeCount(String sqlKey, Object params) throws SQLException {
		return executeCount(sqlKey, params, (String) null);
	}

	/**
	 * Executes a COUNT query and returns the result.
	 * 
	 * @param sqlKey
	 * @param params
	 * @param cacheKey
	 * @return
	 * @throws SQLException
	 */
	public long executeCount(String sqlKey, Object params, String cacheKey) throws SQLException {
		Long result = null;
		if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
			Object temp = getFromCache(cacheKey);
			if (temp instanceof Long) {
				result = (Long) temp;
			} else if (temp instanceof Number) {
				result = ((Number) temp).longValue();
			} else {
				result = null;
			}
		}

		if (result == null) {
			Connection conn = getConnection();
			if (conn == null) {
				throw new RuntimeException(EXC_NOT_CONNECT_DB);
			}
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				String sql = buildSqlProps(sqlKey);
				if (sql == null) {
					throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
				}
				stmt = preparedStatement(conn, sql, params);
				rs = stmt.executeQuery();
				if (rs.next()) {
					result = rs.getLong(1);
				}
			} finally {
				closeResources(conn, stmt, rs);
			}
		}

		if (!StringUtils.isBlank(cacheKey) && cacheEnabled() && result != null) {
			putToCache(cacheKey, result);
		}
		return result;

	}

	/**
	 * Executes a SELECT query and returns the result as an array of records, each record is a
	 * Map<String, Object>. Don't use cache.
	 * 
	 * @param sqlKey
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> executeSelect(String sqlKey, Object params)
			throws SQLException {
		return executeSelect(sqlKey, params, (String) null);
	}

	/**
	 * Executes a SELECT query and returns the result as an array of records, each record is a
	 * Map<String, Object>.
	 * 
	 * @param sqlKey
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> executeSelect(String sqlKey, Object params, String cacheKey)
			throws SQLException {
		List<Map<String, Object>> result = null;
		// GET FROM CACHE
		if (!StringUtils.isBlank(cacheKey) && cacheEnabled()) {
			Object temp = getFromCache(cacheKey);
			if (temp != null) {
				try {
					result = (List<Map<String, Object>>) temp;
				} catch (ClassCastException e) {
					result = null;
				}
			}
		}

		if (result == null) {
			result = new ArrayList<Map<String, Object>>();
			Connection conn = getConnection();
			if (conn == null) {
				throw new RuntimeException(EXC_NOT_CONNECT_DB);
			}
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				String sql = buildSqlProps(sqlKey);
				if (sql == null) {
					throw new SQLException("Can not retrieve SQL [" + sqlKey + "]!");
				}
				stmt = preparedStatement(conn, sql, params);
				rs = stmt.executeQuery();
				ResultSetMetaData rsMetaData = rs != null ? rs.getMetaData() : null;
				while (rs.next()) {
					Map<String, Object> obj = new HashMap<String, Object>();
					for (int i = 1, n = rsMetaData.getColumnCount(); i <= n; i++) {
						String colName = rsMetaData.getColumnLabel(i);
						Object value = rs.getObject(colName);
						obj.put(colName, value);
					}
					result.add(obj);
				}
			} finally {
				closeResources(conn, stmt, rs);
			}
		}

		// PUT TO CACHE
		if (!StringUtils.isBlank(cacheKey) && cacheEnabled() && result != null && result.size() > 0) {
			putToCache(cacheKey, result);
		}

		return result;
	}

	/**
	 * Executes a SELECT query and returns the result as an array of result, each result is an
	 * instance of type {@link BaseBo}. Don't use cache.
	 * 
	 * @param sqlKey
	 * @param params
	 * @param clazz
	 * @return
	 * @throws SQLException
	 */
	public <T extends BaseBo> T[] executeSelect(String sqlKey, Object params, Class<T> clazz)
			throws SQLException {
		return executeSelect(sqlKey, params, clazz, (String) null);
	}

	/**
	 * Executes a SELECT query and returns the result as an array of result, each result is an
	 * instance of type {@link BaseBo}.
	 * 
	 * @param sqlKey
	 * @param params
	 * @param clazz
	 * @param cacheKey
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	protected <T extends BaseBo> T[] executeSelect(String sqlKey, Object params, Class<T> clazz,
			String cacheKey) throws SQLException {
		List<Map<String, Object>> dbResult = executeSelect(sqlKey, params, cacheKey);
		if (dbResult == null || dbResult.size() <= 0) {
			return null;
		}

		List<T> result = new ArrayList<T>();
		T obj = null;
		for (Map<String, Object> data : dbResult) {
			try {
				obj = clazz.newInstance();
				obj.populate(data);
				result.add(obj);
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		return result.toArray((T[]) Array.newInstance(clazz, 0));
	}

	/**
	 * Executes multi queries. Nothing returns.
	 * 
	 * @param sqlKey
	 * @param params
	 * @throws SQLException
	 */
	public void executeMultiCommands(String[] sqlKey, Object params) throws SQLException {
		Boolean autoCommitOldValue = true;
		String[] sql = new String[sqlKey.length];
		for (int i = 0; i < sqlKey.length; i++) {
			String sqlCommand = buildSqlProps(sqlKey[i]);
			if (sqlCommand == null) {
				throw new SQLException("Can not retrieve SQL [" + sqlKey[i] + "]!");
			}
			sql[i] = sqlCommand;
		}
		Connection conn = getConnection();
		if (conn == null) {
			throw new RuntimeException(EXC_NOT_CONNECT_DB);
		}
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			autoCommitOldValue = conn.getAutoCommit();
			conn.setAutoCommit(false);
			List<PreparedStatement> statements = getListStatements(conn, sql, params);
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

	public void setDs(DataSource ds) {
		this.ds = ds;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setConnUrl(String connUrl) {
		this.connUrl = connUrl;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSqlPropsLocation() {
		return sqlPropsLocation;
	}

	public void setSqlPropsLocation(String sqlPropsLocation) {
		this.sqlPropsLocation = sqlPropsLocation;
	}

}
