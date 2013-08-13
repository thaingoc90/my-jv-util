package ind.web.nhp.controller.be;

import ind.web.nhp.us.IMenu;
import ind.web.nhp.us.impl.MenuBo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BuildMenu {

	public static List<IMenu> createMainMenu() {
		List<IMenu> mainMenu = new LinkedList<IMenu>();

		MenuBo menuManage = new MenuBo();
		menuManage.setId(1);
		menuManage.setName("<i class='icon-group icon-white'></i> Manage");
		menuManage.setUrl("");
		menuManage.setPosition(1);
		menuManage.setParentId(0);
		menuManage.setPermission("");
		mainMenu.add(menuManage);

		menuManage = new MenuBo();
		menuManage.setId(4);
		menuManage.setName("<i class='icon-gear icon-white'></i> Setting ");
		menuManage.setUrl("");
		menuManage.setPosition(1);
		menuManage.setParentId(0);
		menuManage.setPermission("");

		mainMenu.add(menuManage);

		MenuBo menuItem = new MenuBo();
		menuItem.setId(2);
		menuItem.setName("Users");
		menuItem.setUrl("manage/users");
		menuItem.setPosition(1);
		menuItem.setParentId(1);
		menuItem.setPermission("");
		mainMenu.add(menuItem);

		menuItem = new MenuBo();
		menuItem.setId(3);
		menuItem.setName("Groups");
		menuItem.setUrl("manage/groups");
		menuItem.setPosition(2);
		menuItem.setParentId(1);
		menuItem.setPermission("");
		mainMenu.add(menuItem);

		menuItem = new MenuBo();
		menuItem.setId(3);
		menuItem.setName("Permissions");
		menuItem.setUrl("manage/permissions");
		menuItem.setPosition(3);
		menuItem.setParentId(1);
		menuItem.setPermission("");
		mainMenu.add(menuItem);

		menuItem = new MenuBo();
		menuItem.setId(5);
		menuItem.setName("Accounts");
		menuItem.setUrl("setting/account");
		menuItem.setPosition(1);
		menuItem.setParentId(4);
		menuItem.setPermission("");
		mainMenu.add(menuItem);

		menuItem = new MenuBo();
		menuItem.setId(6);
		menuItem.setName("Primacy");
		menuItem.setUrl("setting/primacy");
		menuItem.setPosition(1);
		menuItem.setParentId(4);
		menuItem.setPermission("");
		mainMenu.add(menuItem);

		return mainMenu;
	}

	public static List<IMenu> buildMainMenu(List<String> permissions) {
		List<IMenu> mainMenu = new LinkedList<IMenu>();
		List<IMenu> rawMenu = createMainMenu();
		for (IMenu menu : rawMenu) {
			if (menu.getParentId() > 0) {
				break;
			}
			String menuPerm = menu.getPermission();
			if (StringUtils.isEmpty(menuPerm) || permissions.contains(menuPerm)) {
				for (IMenu subMenu : rawMenu) {
					if (subMenu.getParentId() != menu.getId()) {
						continue;
					}
					String subPerm = subMenu.getPermission();
					if (StringUtils.isEmpty(subPerm) || permissions.contains(subPerm)) {
						menu.addChild(subMenu.clone());
					}
				}
				mainMenu.add(menu);
			}
		}
		return mainMenu;
	}

}
