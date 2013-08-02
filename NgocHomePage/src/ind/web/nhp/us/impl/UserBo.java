package ind.web.nhp.us.impl;

import ind.web.nhp.base.BaseBo;
import ind.web.nhp.us.IUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserBo extends BaseBo implements IUser {

	public static final String COL_ID = "id";
	public static final String COL_EMAIL = "email";
	public static final String COL_LOGIN_NAME = "loginName";
	public static final String COL_DISPLAY_NAME = "displayName";
	public static final String COL_PASSWORD = "password";
	public static final String COL_LOCKED = "locked";
	public static final String COL_BIRTHDAY = "birthday";
	public static final String COL_CREATED_DATE = "createdDate";
	public static final String COL_TIME_LOGIN = "timeLogin";
	public static final String COL_LAST_LOGIN = "lastLogin";
	public static final String COL_TOKEN = "token";
	public static final String COL_GROUP_ID = "groupId";

	private int id, groupId;
	private String email, loginName, displayName, password, token;
	private Date birthday, createdDate, timeLogin, lastLogin;
	private boolean locked;

	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_ID, new Object[] { "id", int.class });
		result.put(COL_EMAIL, new Object[] { "email", String.class });
		result.put(COL_LOGIN_NAME, new Object[] { "loginName", String.class });
		result.put(COL_DISPLAY_NAME,
				new Object[] { "displayName", String.class });
		result.put(COL_PASSWORD, new Object[] { "password", String.class });
		result.put(COL_LOCKED, new Object[] { "locked", boolean.class });
		result.put(COL_TOKEN, new Object[] { "token", String.class });
		result.put(COL_BIRTHDAY, new Object[] { "birthday", Date.class });
		result.put(COL_CREATED_DATE, new Object[] { "createdDate", Date.class });
		result.put(COL_TIME_LOGIN, new Object[] { "timeLogin", Date.class });
		result.put(COL_LAST_LOGIN, new Object[] { "lastLogin", Date.class });
		result.put(COL_GROUP_ID, new Object[] { "groupId", int.class });
		return result;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public Date getTimeLogin() {
		return timeLogin;
	}

	public void setTimeLogin(Date timeLogin) {
		this.timeLogin = timeLogin;
	}

	@Override
	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

}
