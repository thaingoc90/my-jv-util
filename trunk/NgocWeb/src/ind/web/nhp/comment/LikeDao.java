package ind.web.nhp.comment;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ind.web.nhp.base.BaseJdbcDao;

public class LikeDao extends BaseJdbcDao implements ILikeDao {

	public final static String FIELD_ACCOUNT_NAME = "account_name";
	public final static String FIELD_COMMENT_ID = "comment_id";
	public final static String FIELD_TARGET_ID = "target_id";

	private Logger LOGGER = LoggerFactory.getLogger(LikeDao.class);

	@Override
	public boolean like(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.like";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? true : false;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
		}
	}

	@Override
	public boolean unlike(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.unlike";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? true : false;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
		}
	}

	@Override
	public Map<String, Object> getLike(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.getLike";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			List<Map<String, Object>> dbResults = executeSelect(sqlKey, params);
			return dbResults != null && dbResults.size() > 0 ? dbResults.get(0) : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public List<Map<String, Object>> getLikes(Long targetId, Long commentId) {
		final String sqlKey = "sql.getLikes";
		int ignoreCommemt = commentId == null ? 1 : 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put("ignore_comment", ignoreCommemt);
		try {
			List<Map<String, Object>> dbResults = executeSelect(sqlKey, params);
			return dbResults != null && dbResults.size() > 0 ? dbResults : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public int countLikes(Long targetId, Long commentId) {
		final String sqlKey = "sql.countLikes";
		int ignoreCommemt = commentId == null ? 1 : 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put("ignore_comment", ignoreCommemt);
		try {
			long result = executeCount(sqlKey, params);
			return (int) result;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}
	
	@Override
	public boolean dislike(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.dislike";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? true : false;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
		}
	}

	@Override
	public boolean unDislike(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.unDislike";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? true : false;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {
		}
	}

	@Override
	public Map<String, Object> getDislike(String accountName, Long targetId, Long commentId) {
		final String sqlKey = "sql.getDislike";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		try {
			List<Map<String, Object>> dbResults = executeSelect(sqlKey, params);
			return dbResults != null && dbResults.size() > 0 ? dbResults.get(0) : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public List<Map<String, Object>> getDislikes(Long targetId, Long commentId) {
		final String sqlKey = "sql.getDislikes";
		int ignoreCommemt = commentId == null ? 1 : 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put("ignore_comment", ignoreCommemt);
		try {
			List<Map<String, Object>> dbResults = executeSelect(sqlKey, params);
			return dbResults != null && dbResults.size() > 0 ? dbResults : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public int countDislikes(Long targetId, Long commentId) {
		final String sqlKey = "sql.countDislikes";
		int ignoreCommemt = commentId == null ? 1 : 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put("ignore_comment", ignoreCommemt);
		try {
			long result = executeCount(sqlKey, params);
			return (int) result;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

}
