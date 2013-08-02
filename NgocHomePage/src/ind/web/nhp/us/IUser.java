package ind.web.nhp.us;

import java.util.Date;

public interface IUser {

	public int getId();

	public String getEmail();

	public boolean isLocked();

	public String getLoginName();

	public String getDisplayName();

	public String getPassword();

	public Date getBirthday();

	public String getToken();

	public Date getCreatedDate();

	public Date getTimeLogin();

	public Date getLastLogin();

	public int getGroupId();
}