package ind.web.nhp.base.manager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.web.nhp.base.BaseJdbcDao;

public class ConfigManager extends BaseJdbcDao implements IConfigManager {

	private Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);

	public void invalidateCacheConfig(IConfig config) {
		if (config != null) {
			deleteFromCache(cacheKeyConfigByKey(config.getKey()));
		}
	}

	public static String cacheKeyConfigByKey(String key) {
		return "CONFIG_KEY_" + key;
	}

	@Override
	public IConfig loadConfig(String key) {
		final String sqlKey = "sql.getConfig";
		String cacheKey = cacheKeyConfigByKey(key);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ConfigBo.COL_KEY, key);
		try {
			IConfig[] configs = executeSelect(sqlKey, params, ConfigBo.class, cacheKey);
			return configs != null && configs.length > 0 ? configs[0] : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public IConfig createConfig(IConfig config) {
		final String sqlKey = "sql.createConfig";
		Map<String, Object> params = _buildParamConfig(config);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? config : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
			invalidateCacheConfig(config);
		}
	}

	@Override
	public IConfig updateConfig(IConfig config) {
		IConfig configTemp = loadConfig(config.getKey());
		if (configTemp == null) {
			return createConfig(config);
		}
		final String sqlKey = "sql.updateConfig";
		Map<String, Object> params = _buildParamConfig(config);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? config : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
			invalidateCacheConfig(config);
		}
	}

	@Override
	public void deleteConfig(IConfig config) {
		final String sqlKey = "sql.deleteConfig";
		Map<String, Object> params = _buildParamConfig(config);
		try {
			executeNonSelect(sqlKey, params);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
			invalidateCacheConfig(config);
		}
	}

	private static Map<String, Object> _buildParamConfig(IConfig config) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ConfigBo.COL_KEY, config.getKey());
		params.put(ConfigBo.COL_VALUE, config.getValue());
		return params;
	}
}
