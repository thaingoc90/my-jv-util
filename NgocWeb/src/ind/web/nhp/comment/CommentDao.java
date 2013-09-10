package ind.web.nhp.comment;

import ind.web.nhp.base.BaseJdbcDao;
import ind.web.nhp.utils.Utils;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentDao extends BaseJdbcDao implements ICommentDao {

	public final static String FIELD_TARGET_ID = "target_id";
	public final static String FIELD_CONTENT = "content";
	public final static String FIELD_TOKEN = "token";
	public final static String FIELD_ACCOUNT_NAME = "account_name";
	public final static String FIELD_COMMENT_ID = "comment_id";
	public final static String FIELD_PARENT_ID = "parent_comment_id";
	public final static String FIELD_CREATED = "created";
	public final static String FIELD_UPDATED = "updated";
	public final static String FIELD_UPDATED_BY = "updated_by";
	public final static String FIELD_STATUS = "status";
	public final static String FIELD_APPROVED_BY = "approved_by";
	public final static String FIELD_TOTAL_LIKES = "total_likes";

	public final static String FIELD_START_INDEX = "start_index";
	public final static String FIELD_PAGE_SIZE = "page_size";

	private Logger LOGGER = LoggerFactory.getLogger(CommentDao.class);

	@Override
	public Long addComment(String accountName, String content, Long targetId, String token,
			Long parentCommentId, int status) {
		final String sqlKey = "sql.addComment";
		long commentId = Utils.generateLongId64();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_ACCOUNT_NAME, accountName);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_CONTENT, content);
		params.put(FIELD_STATUS, status);
		params.put(FIELD_TOKEN, token);
		params.put(FIELD_PARENT_ID, parentCommentId);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? commentId : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public boolean deleteComment(Long commentId, Long targetId, String token) {
		final String sqlKey = "sql.deleteComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_TOKEN, token);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? true : false;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public Long updateComment(Long commentId, Long targetId, String token, String content,
			String accountUpdate) {
		final String sqlKey = "sql.updateComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_UPDATED_BY, accountUpdate);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_CONTENT, content);
		params.put(FIELD_TOKEN, token);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? commentId : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public Long approveComment(Long commentId, Long targetId, String token, String approver,
			Integer status) {
		final String sqlKey = "sql.approveComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_TOKEN, token);
		params.put(FIELD_APPROVED_BY, approver);
		params.put(FIELD_UPDATED, new Date());
		params.put(FIELD_STATUS, status);
		try {
			long result = executeNonSelect(sqlKey, params);
			return (result > 0) ? commentId : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public Map<String, Object> getComment(Long commentId, Long targetId, String token) {
		final String sqlKey = "sql.getComment";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_COMMENT_ID, commentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_TOKEN, token);
		try {
			List<Map<String, Object>> cms = executeSelect(sqlKey, params);
			return cms != null && cms.size() > 0 ? cms.get(0) : null;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public List<Map<String, Object>> getChildComments(Long parentCommentId, Long targetId,
			String token) {
		final String sqlKey = "sql.getChildComments";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_PARENT_ID, parentCommentId);
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_TOKEN, token);
		try {
			return executeSelect(sqlKey, params);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}

	@Override
	public List<Map<String, Object>> getCommentsByTarget(Long targetId, String token,
			Integer status, Integer page, Integer pageSize) {
		final String sqlKey = "sql.getCommentsByTarget";
		int ignoreStatus = status == null ? 1 : 0;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(FIELD_TARGET_ID, targetId);
		params.put(FIELD_TOKEN, token);
		params.put("ignore_status", ignoreStatus);
		params.put(FIELD_STATUS, status);
		params.put(FIELD_START_INDEX, (page - 1) * pageSize);
		params.put(FIELD_PAGE_SIZE, pageSize);
		try {
			return executeSelect(sqlKey, params);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException();
		} finally {
		}
	}
}
