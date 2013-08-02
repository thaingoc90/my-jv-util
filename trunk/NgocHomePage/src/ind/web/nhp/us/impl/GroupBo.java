package ind.web.nhp.us.impl;

import ind.web.nhp.base.BaseBo;
import ind.web.nhp.us.IGroup;

import java.util.HashMap;
import java.util.Map;

public class GroupBo extends BaseBo implements IGroup {

	public static final String COL_ID = "groupId";
	public static final String COL_SYSTEM = "groupIsSystem";
	public static final String COL_NAME = "groupName";
	public static final String COL_DESCRIPTION = "groupDesc";

	private int id;
	private boolean isSystem;
	private String name, desc;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_ID, new Object[] { "id", int.class });
		result.put(COL_SYSTEM, new Object[] { "isSystem", boolean.class });
		result.put(COL_NAME, new Object[] { "name", String.class });
		result.put(COL_DESCRIPTION, new Object[] { "desc", String.class });
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}
