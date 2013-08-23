package ind.web.nhp.us.impl;

import ind.web.nhp.base.BaseJdbcDao;
import ind.web.nhp.us.IGroup;
import ind.web.nhp.us.IMenu;
import ind.web.nhp.us.IPermission;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUsManager extends BaseJdbcDao implements IUsManager {

    private Logger LOGGER = LoggerFactory.getLogger(JdbcUsManager.class);

    private void invalidateCacheUser(IUser user) {
	if (user != null) {
	    deleteFromCache(cacheKeyUserById(user.getId()));
	    deleteFromCache(cacheKeyUserByEmail(user.getEmail()));
	}
	deleteFromCache(cacheKeyAllUsers());
    }

    private void invalidateCacheUserInGroup(IGroup group) {
	IUser[] listUsers = getAllUsers();
	for (IUser user : listUsers) {
	    if (user.getGroupId() == group.getId()) {
		deleteFromCache(cacheKeyUserById(user.getId()));
		deleteFromCache(cacheKeyUserByEmail(user.getEmail()));
	    }
	}
	deleteFromCache(cacheKeyAllUsers());
    }

    public static String cacheKeyAllUsers() {
	return "ALL_USERS";
    }

    public static String cacheKeyUserById(int id) {
	return "USER_ID_" + id;
    }

    public static String cacheKeyUserByEmail(String email) {
	return "USER_EMAIL_" + email;
    }

    @Override
    public IUser getUser(int id) {
	final String sqlKey = "sql.getUserById";
	String cacheKey = cacheKeyUserById(id);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_ID, id);
	try {
	    IUser[] users = executeSelect(sqlKey, params, UserBo.class, cacheKey);
	    return users != null && users.length > 0 ? users[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IUser getUser(String loginName) {
	final String sqlKey = "sql.getUserByLoginName";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_LOGIN_NAME, loginName);
	try {
	    IUser[] users = executeSelect(sqlKey, params, UserBo.class);
	    return users != null && users.length > 0 ? users[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IUser getUserByEmail(String email) {
	final String sqlKey = "sql.getUserByEmail";
	String cacheKey = cacheKeyUserByEmail(email);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_EMAIL, email);
	try {
	    IUser[] users = executeSelect(sqlKey, params, UserBo.class, cacheKey);
	    return users != null && users.length > 0 ? users[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IUser createUser(IUser user) {
	final String sqlKey = "sql.createUser";
	Map<String, Object> params = _buildParamUser(user);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result == 1) ? user : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheUser(user);
	}
    }

    @Override
    public IUser updateUser(IUser user) {
	final String sqlKey = "sql.updateUser";
	Map<String, Object> params = _buildParamUser(user);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result > 0) ? user : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheUser(user);
	}
    }

    @Override
    public void deleteUser(IUser user) {
	final String sqlKey = "sql.deleteUser";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_ID, user.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheUser(user);
	}
    }

    @Override
    public IUser updatePasswordUser(IUser user) {
	final String sqlKey = "sql.updatePasswordUser";
	Map<String, Object> params = _buildParamUser(user);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result > 0) ? user : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheUser(user);
	}
    }

    @Override
    public IUser updateLastLoginUser(IUser user) {
	final String sqlKey = "sql.updateLastLoginUser";
	Map<String, Object> params = _buildParamUser(user);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result > 0) ? user : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheUser(user);
	}
    }

    @Override
    public IUser[] getAllUsers() {
	final String sqlKey = "sql.getAllUsers";
	String cacheKey = cacheKeyAllUsers();
	Map<String, Object> params = new HashMap<String, Object>();
	try {
	    IUser[] users = executeSelect(sqlKey, params, UserBo.class, cacheKey);
	    return users != null && users.length > 0 ? users : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IUser[] getAllUsersByGroupId(int groupId) {
	final String sqlKey = "sql.getAllUsersByGroupId";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_ID, groupId);
	try {
	    IUser[] users = executeSelect(sqlKey, params, UserBo.class);
	    return users != null && users.length > 0 ? users : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    private static Map<String, Object> _buildParamUser(IUser user) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(UserBo.COL_ID, user.getId());
	params.put(UserBo.COL_EMAIL, user.getEmail());
	params.put(UserBo.COL_LOGIN_NAME, user.getLoginName());
	params.put(UserBo.COL_DISPLAY_NAME, user.getDisplayName());
	params.put(UserBo.COL_GROUP_ID, user.getGroupId());
	params.put(UserBo.COL_LOCKED, user.isLocked() ? 1 : 0);
	params.put(UserBo.COL_TIME_LOGIN, user.getTimeLogin());
	params.put(UserBo.COL_LAST_LOGIN, user.getLastLogin());
	params.put(UserBo.COL_BIRTHDAY, user.getBirthday());
	params.put(UserBo.COL_TOKEN, user.getToken());
	params.put(UserBo.COL_PASSWORD, user.getPassword());
	params.put(UserBo.COL_CREATED_DATE, user.getCreatedDate());
	return params;
    }

    /*
     * -------------FUNCTION OF GROUP ----------------------------------
     */

    private void invalidateCacheGroup(IGroup group) {
	if (group != null) {
	    deleteFromCache(cacheKeyGroupById(group.getId()));
	}
	deleteFromCache(cacheKeyAllGroups());
    }

    public static String cacheKeyAllGroups() {
	return "ALL_GROUPS";
    }

    public static String cacheKeyGroupById(int id) {
	return "GROUP_" + id;
    }

    @Override
    public IGroup createGroup(IGroup group) {
	final String sqlKey = "sql.createGroup";
	Map<String, Object> params = _buildParamGroup(group);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result != 0) ? group : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheGroup(group);
	}
    }

    @Override
    public void deleteGroup(IGroup group) {
	final String sqlKey = "sql.deleteGroup";
	Map<String, Object> params = _buildParamGroup(group);
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheGroup(group);
	    invalidateCacheUserInGroup(group);
	}
    }

    @Override
    public IGroup[] getAllGroups() {
	final String sqlKey = "sql.getAllGroups";
	String cacheKey = cacheKeyAllGroups();
	Map<String, Object> params = new HashMap<String, Object>();
	try {
	    IGroup[] groups = executeSelect(sqlKey, params, GroupBo.class, cacheKey);
	    return (groups != null && groups.length > 0) ? groups : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IGroup getGroup(int id) {
	final String sqlKey = "sql.getGroupById";
	String cacheKey = cacheKeyGroupById(id);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_ID, id);
	try {
	    IGroup[] groups = executeSelect(sqlKey, params, GroupBo.class, cacheKey);
	    return groups != null && groups.length > 0 ? groups[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IGroup getGroup(String name) {
	final String sqlKey = "sql.getGroupByName";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_NAME, name);
	try {
	    IGroup[] groups = executeSelect(sqlKey, params, GroupBo.class);
	    return groups != null && groups.length > 0 ? groups[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	}
    }

    @Override
    public IGroup updateGroup(IGroup group) {
	final String sqlKey = "sql.updateGroup";
	Map<String, Object> params = _buildParamGroup(group);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result != 0) ? group : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException(e);
	} finally {
	    invalidateCacheGroup(group);
	}
    }

    private static Map<String, Object> _buildParamGroup(IGroup group) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_ID, group.getId());
	params.put(GroupBo.COL_NAME, group.getName());
	params.put(GroupBo.COL_DESCRIPTION, group.getDesc());
	params.put(GroupBo.COL_SYSTEM, group.isSystem() ? 1 : 0);
	return params;
    }

    /*-------------------------------------------------------------*/

    private void invalidateCachePermission(IPermission permission) {
	if (permission != null) {
	    deleteFromCache(cacheKeyPermisionById(permission.getId()));
	}
	deleteFromCache(cacheKeyAllPermissions());
    }

    public static String cacheKeyAllPermissions() {
	return "ALL_PERMISSIONS";
    }

    public static String cacheKeyPermisionById(String pid) {
	return "PERMISSION_" + pid;
    }

    @Override
    public IPermission getPermission(String pid) {
	final String sqlKey = "sql.getPermission";
	String cacheKey = cacheKeyPermisionById(pid);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PermissionBo.COL_ID, pid);
	try {
	    IPermission[] permissions = executeSelect(sqlKey, params, PermissionBo.class, cacheKey);
	    return (permissions != null && permissions.length > 0) ? permissions[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

    @Override
    public IPermission[] getAllPermissions() {
	final String sqlKey = "sql.getAllPermissions";
	Map<String, Object> params = new HashMap<String, Object>();
	try {
	    IPermission[] permissions = executeSelect(sqlKey, params, PermissionBo.class);
	    if (permissions == null || permissions.length == 0) {
		return null;
	    }
	    return orderPermission(permissions);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

    private IPermission[] orderPermission(IPermission[] orgPerms) {
	List<IPermission> result = new LinkedList<IPermission>();
	for (IPermission perm : orgPerms) {
	    if (perm.getPid() != null) {
		break;
	    }
	    result.add(perm);
	    String permId = perm.getId();
	    for (IPermission subPerm : orgPerms) {
		if (permId.equals(subPerm.getPid())) {
		    result.add(subPerm);
		}
	    }
	}
	return result.toArray(new IPermission[0]);
    }

    @Override
    public void deletePermission(IPermission permission) {
	final String sqlKey = "sql.deletePermission";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PermissionBo.COL_ID, permission.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePermission(permission);
	}
    }

    @Override
    public IPermission updatePermission(IPermission permission) {
	final String sqlKey = "sql.updatePermission";
	Map<String, Object> params = _buildParamPermission(permission);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result == 1) ? permission : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePermission(permission);
	}
    }

    @Override
    public IPermission addPermission(IPermission permission) {
	final String sqlKey = "sql.addPermission";
	Map<String, Object> params = _buildParamPermission(permission);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result == 1) ? permission : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCachePermission(permission);
	}
    }

    private static Map<String, Object> _buildParamPermission(IPermission permission) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(PermissionBo.COL_ID, permission.getId());
	params.put(PermissionBo.COL_DESCRIPTION, permission.getDesc());
	params.put(PermissionBo.COL_PARENTID, permission.getPid());
	return params;
    }

    /* ---------------------------------- ------------------------ */

    private void invalidateCacheGroupPermission(IGroup group) {
	if (group != null) {
	    deleteFromCache(cacheKeyAllPermissionsOfGroup(group.getId()));
	}
    }

    private static String cacheKeyAllPermissionsOfGroup(int groupId) {
	return "ALL_PERMISSIONS_OF_GROUP" + "_" + groupId;
    }

    @Override
    public IPermission[] getAllPermisionsOfGroup(IGroup group) {
	if (group.isSystem()) {
	    return getAllPermissions();
	}
	final String sqlKey = "sql.getAllPermissionsOfGroup";
	String cacheKey = cacheKeyAllPermissionsOfGroup(group.getId());
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_ID, group.getId());
	try {
	    IPermission[] permissions = executeSelect(sqlKey, params, PermissionBo.class, cacheKey);
	    return (permissions == null || permissions.length == 0) ? null : permissions;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

    @Override
    public IPermission[] getAllPermisionsOfGroupId(int groupId) {
	IGroup group = getGroup(groupId);
	return group == null ? null : getAllPermisionsOfGroup(group);
    }

    @Override
    public void deletePermisionsOfGroup(IGroup group) {
	final String sqlKey = "sql.deletePermisionsOfGroup";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_ID, group.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheGroupPermission(group);
	}

    }

    @Override
    public void addMultiPermisionsForGroup(IGroup group, IPermission[] permissions) {
	for (IPermission permission : permissions) {
	    addPermisionForGroup(group, permission);
	}
    }

    @Override
    public void addPermisionForGroup(IGroup group, IPermission permission) {
	final String sqlKey = "sql.addPermisionForGroup";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(GroupBo.COL_ID, group.getId());
	params.put(PermissionBo.COL_ID, permission.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheGroupPermission(group);
	}
    }

    /*----------------------MENU---------------------------*/

    public static String cacheKeyAllMenus() {
	return "ALL_MENUS";
    }

    public static String cacheKeyPermissionOfUrl(String url) {
	return "MENUS_BY_PERM_" + url;
    }

    public static String cacheKeyMenuById(int munuId) {
	return "MENU_" + munuId;
    }

    private void invalidateCacheMenu(IMenu menu) {
	if (menu != null) {
	    deleteFromCache(cacheKeyMenuById(menu.getId()));
	    deleteFromCache(cacheKeyPermissionOfUrl(menu.getUrl()));
	}
	deleteFromCache(cacheKeyAllMenus());
    }

    @Override
    public IMenu[] getAllMenus() {
	final String sqlKey = "sql.getAllMenus";
	String cacheKey = cacheKeyAllMenus();
	Map<String, Object> params = new HashMap<String, Object>();
	try {
	    IMenu[] menus = executeSelect(sqlKey, params, MenuBo.class, cacheKey);
	    if (menus == null || menus.length == 0) {
		return null;
	    }
	    return menus;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

    @Override
    public IMenu getMenuById(int menuId) {
	final String sqlKey = "sql.getMenuById";
	String cacheKey = cacheKeyMenuById(menuId);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(MenuBo.COL_ID, menuId);
	try {
	    IMenu[] menus = executeSelect(sqlKey, params, MenuBo.class, cacheKey);
	    return (menus != null && menus.length > 0) ? menus[0] : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

    @Override
    public IMenu updateMenu(IMenu menu) {
	final String sqlKey = "sql.updateMenu";
	Map<String, Object> params = _buildParamMenu(menu);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result == 1) ? menu : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheMenu(menu);
	}
    }

    @Override
    public IMenu createMenu(IMenu menu) {
	final String sqlKey = "sql.createMenu";
	Map<String, Object> params = _buildParamMenu(menu);
	try {
	    long result = executeNonSelect(sqlKey, params);
	    return (result == 1) ? menu : null;
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheMenu(menu);
	}
    }

    @Override
    public void deleteMenu(IMenu menu) {
	final String sqlKey = "sql.deleteMenu";
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(MenuBo.COL_ID, menu.getId());
	try {
	    executeNonSelect(sqlKey, params);
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	} finally {
	    invalidateCacheMenu(menu);
	}
    }

    private static Map<String, Object> _buildParamMenu(IMenu menu) {
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(MenuBo.COL_ID, menu.getId());
	params.put(MenuBo.COL_NAME, menu.getName());
	params.put(MenuBo.COL_PARENT_ID, menu.getParentId() == 0 ? null : menu.getParentId());
	params.put(MenuBo.COL_URL, menu.getUrl());
	params.put(MenuBo.COL_POSITION, menu.getPosition());
	params.put(MenuBo.COL_PERMISSION, menu.getPermission());
	return params;
    }

    /* ------------------------------------------- */
    @Override
    public String getPermissionOfUrl(String url) {
	String sqlKey = "sql.getPermissionOfUrl";
	String cacheKey = cacheKeyPermissionOfUrl(url);
	Map<String, Object> params = new HashMap<String, Object>();
	params.put(MenuBo.COL_URL, url);

	try {
	    List<Map<String, Object>> dbResults = executeSelect(sqlKey, params, cacheKey);
	    if (dbResults == null || dbResults.size() == 0) {
		return "";
	    }
	    Object permission = dbResults.get(0).get(MenuBo.COL_PERMISSION);
	    return permission == null ? "" : permission.toString().trim();
	} catch (SQLException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new RuntimeException();
	}
    }

}
