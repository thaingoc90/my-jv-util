package ind.web.nhp.base.manager;

import java.util.Date;

public interface IPaperclip {

	public String getId();

	public String getOriginalName();

	public String getStatus();

	public String getType();

	public String getOwner();

	public Date getCreateTime();

	public long getSize();

	public String getMeta();

	public String getDiskName();

	public String getDir();

	public boolean isExternalStorage();

	public String getUrl(String prefix);

}
