package ind.web.nhp.us.impl;

import ind.web.nhp.base.BaseBo;
import ind.web.nhp.us.IPermission;

import java.util.HashMap;
import java.util.Map;

public class PermissionBo extends BaseBo implements IPermission {

	public static final String COL_ID = "perId";
	public static final String COL_DESCRIPTION = "perDesc";
	public static final String COL_PARENTID = "parentId";
	public static final int NO_PERMISSION = 0;
	public static final int PERMISSION = 1;
	public static final int GLOBAL_PERMISSION = 2;

	private String id, desc, pid;

	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_ID, new Object[] { "id", String.class });
		result.put(COL_DESCRIPTION, new Object[] { "desc", String.class });
		result.put(COL_PARENTID, new Object[] { "pid", String.class });
		return result;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}