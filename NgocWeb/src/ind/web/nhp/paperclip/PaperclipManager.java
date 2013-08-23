package ind.web.nhp.paperclip;

import ind.web.nhp.base.BaseJdbcDao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaperclipManager extends BaseJdbcDao implements IPaperclipManager {

    private Logger LOGGER = LoggerFactory.getLogger(PaperclipManager.class);

    public void invalidateCachePaperclip(IPaperclip pc) {
	if (pc != null) {
	    deleteFromCache(cacheKeyPaperclipById(pc.getId()));
	}
    }

    public static String cacheKeyPaperclipById(String id) {
	return "PAPERCLIP_ID_" + id;
    }

    @Override
    public String createAttachment(IPaperclip paperclip) {
	final String sqlKey = "sql.createPaperclip";
	Map<String, Object> params = _buildParamPaperclip(paperclip);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result > 0) ? paperclip.getId() : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePaperclip(paperclip);
	}
    }

    @Override
    public IPaperclip updateAttachment(IPaperclip paperclip) {
	final String sqlKey = "sql.updatePaperclip";
	Map<String, Object> params = _buildParamPaperclip(paperclip);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result > 0) ? paperclip : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePaperclip(paperclip);
	}
    }

    @Override
    public void deleteAttachment(IPaperclip paperclip) {
	final String sqlKey = "sql.deletePaperclip";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PaperclipBo.COL_ID, paperclip.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePaperclip(paperclip);
	}
    }

    @Override
    public IPaperclip getAttachment(String id) {
	final String sqlKey = "sql.getPaperclipById";
	String cacheKey = cacheKeyPaperclipById(id);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PaperclipBo.COL_ID, id);
	try {
	    IPaperclip[] pcs = executeSelect(sqlKey, params, PaperclipBo.class, cacheKey);
	    return pcs != null && pcs.length > 0 ? pcs[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    private static Map<String, Object> _buildParamPaperclip(IPaperclip pc) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PaperclipBo.COL_ID, pc.getId());
	params.put(PaperclipBo.COL_ORIGINAL_NAME, pc.getOriginalName());
	params.put(PaperclipBo.COL_FILESIZE, pc.getSize());
	params.put(PaperclipBo.COL_FILESTATUS, pc.getStatus());
	params.put(PaperclipBo.COL_MIMETYPE, pc.getType());
	params.put(PaperclipBo.COL_OWNER, pc.getOwner());
	params.put(PaperclipBo.COL_CREATE_TIME, pc.getCreateTime());
	params.put(PaperclipBo.COL_METADATA, pc.getMeta());
	return params;
    }

}
