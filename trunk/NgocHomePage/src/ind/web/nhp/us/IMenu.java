package ind.web.nhp.us;

import java.util.List;

public interface IMenu {

	public int getId();

	public int getPosition();

	public int getParentId();

	public String getName();

	public String getUrl();

	public String getPermission();

	public void addChild(IMenu child);

	public List<IMenu> getChilds();
	
	public IMenu clone();
	
}
