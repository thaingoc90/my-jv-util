package ind.web.nhp.us;

public interface IUsManager {
	/**
	 * Gets a user account by id.
	 * 
	 * @param id
	 * @return
	 */
	public IUser getUser(int id);

	/**
	 * Gets a user account by login name.
	 * 
	 * @param loginName
	 * @return
	 */
	public IUser getUser(String loginName);

	/**
	 * Gets a user account by email.
	 * 
	 * @param loginName
	 * @return
	 */
	public IUser getUserByEmail(String email);

	/**
	 * Creates a user account
	 * 
	 * @param user
	 * @return
	 */
	public IUser createUser(IUser user);

	/**
	 * Updates a user account
	 * 
	 * @param user
	 * @return
	 */
	public IUser updateUser(IUser user);

	/**
	 * Delete a user account
	 * 
	 * @param user
	 * @return
	 */
	public void deleteUser(IUser user);

	/**
	 * Updates password a user account
	 * 
	 * @param user
	 * @return
	 */
	public IUser updatePasswordUser(IUser user);

	/**
	 * Updates last_login a user account
	 * 
	 * @param user
	 * @return
	 */
	public IUser updateLastLoginUser(IUser user);

	/**
	 * Gets a user account by email.
	 * 
	 * @param loginName
	 * @return
	 */
	public IUser[] getAllUsers();

	/**
	 * Gets all available user groups.
	 * 
	 * @return
	 */
	public IUser[] getAllUsersByGroupId(int groupId);

	/**
	 * Creates a new user group.
	 * 
	 * @param group
	 * @return
	 */
	public IGroup createGroup(IGroup group);

	/**
	 * Deletes a user group.
	 * 
	 * @param group
	 */
	public void deleteGroup(IGroup group);

	/**
	 * Gets a user group by id.
	 * 
	 * @param id
	 * @return
	 */
	public IGroup getGroup(int id);

	/**
	 * Gets a user group by name
	 * 
	 * @param name
	 * @return
	 */
	public IGroup getGroup(String name);

	/**
	 * Gets all available user groups.
	 * 
	 * @return
	 */
	public IGroup[] getAllGroups();

	/**
	 * Updates an existing user group.
	 * 
	 * @param group
	 * @return
	 */
	public IGroup updateGroup(IGroup group);

	/* ----------------------------------------------- */

	/**
	 * gets a Permission by pid
	 * 
	 * @param id
	 * @return
	 */
	public IPermission getPermission(String id);

	/**
	 * gets all Permissions
	 * 
	 * @param
	 * @return
	 */
	public IPermission[] getAllPermissions();

	/**
	 * Deletes a permission
	 * 
	 * @param permission
	 * @return
	 */
	public void deletePermission(IPermission permission);

	/**
	 * Updates a permission
	 * 
	 * @param permission
	 * @return
	 */
	public IPermission updatePermission(IPermission permission);

	/**
	 * Adds a permission
	 * 
	 * @param permission
	 * @return
	 */
	public IPermission addPermission(IPermission permission);

	/*---------------------- GROUP PERMISSION --------------------------------*/

	/**
	 * getAllPermisionsOfGroup
	 * 
	 * @param group
	 * @return
	 */
	public IPermission[] getAllPermisionsOfGroup(IGroup group);

	public IPermission[] getAllPermisionsOfGroupId(int groupId);

	/**
	 * deletePermisionsOfGroup
	 * 
	 * @param group
	 * @return
	 */
	public void deletePermisionsOfGroup(IGroup group);

	/**
	 * addPermisionForGroup
	 * 
	 * @param group
	 * @return
	 */
	public void addPermisionForGroup(IGroup group, IPermission permissions);

	/**
	 * addMultiPermisionsForGroup
	 * 
	 * @param group
	 * @return
	 */
	public void addMultiPermisionsForGroup(IGroup group, IPermission[] permissions);

	/*----------------MENU-------------------*/

	public IMenu[] getAllMenus();

	public IMenu getMenuById(int menuId);

	public IMenu updateMenu(IMenu menu);

	public IMenu createMenu(IMenu menu);

	public void deleteMenu(IMenu menu);
	
	public IMenu[] getMenusByPermission();

}
