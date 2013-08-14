package ind.web.nhp.us.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ind.web.nhp.base.BaseBo;
import ind.web.nhp.us.IMenu;

public class MenuBo extends BaseBo implements IMenu {

	public static final String COL_ID = "mId";
	public static final String COL_NAME = "mName";
	public static final String COL_URL = "mUrl";
	public static final String COL_POSITION = "mPosition";
	public static final String COL_PERMISSION = "mPermission";
	public static final String COL_PARENT_ID = "mParentId";

	private int id, parentId;
	private Integer position;
	private String name, url;
	private String permission;
	private List<IMenu> childs;

	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_ID, new Object[] { "id", int.class });
		result.put(COL_NAME, new Object[] { "name", String.class });
		result.put(COL_URL, new Object[] { "url", String.class });
		result.put(COL_POSITION, new Object[] { "position", Integer.class });
		result.put(COL_PERMISSION, new Object[] { "permission", String.class });
		result.put(COL_PARENT_ID, new Object[] { "parentId", Integer.class });
		return result;
	}

	@Override
	public IMenu clone() {
		MenuBo cloneObj = new MenuBo();
		cloneObj.setId(id);
		cloneObj.setName(name);
		cloneObj.setUrl(url);
		cloneObj.setParentId(parentId);
		cloneObj.setPosition(position);
		cloneObj.setPermission(permission);
		return cloneObj;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position == null ? 0 : position;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	@Override
	public int getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId == null ? 0 : parentId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void addChild(IMenu child) {
		if (childs == null) {
			childs = new LinkedList<IMenu>();
		}
		childs.add(child);
	}

	public List<IMenu> getChilds() {
		return childs;
	}

	public void setChilds(List<IMenu> childs) {
		this.childs = childs;
	}

}
