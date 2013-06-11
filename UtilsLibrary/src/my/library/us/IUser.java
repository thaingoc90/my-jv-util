package my.library.us;

import java.util.Date;

public interface IUser {

	public int getId();

	public String getEmail();

	public boolean isLocked();

	public String getLoginName();

	public String getRealName();

	public String getPassword();

	public Date getBirthday();

	public String getToken();

	public Date getCreatedDate();

	public Date getTimeLogin();

	public Date getLastLogin();
}
