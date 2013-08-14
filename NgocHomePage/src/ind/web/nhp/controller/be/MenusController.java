package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IMenu;
import ind.web.nhp.us.IPermission;
import ind.web.nhp.us.impl.MenuBo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MenusController extends BaseBackendController {

	private static final String VIEW_NAME = "be_menus";

	@RequestMapping(value = { "/admin/manage/menus", "/admin/manage/menus/**" })
	public String get(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj, Model model) {
		List<IMenu> menus = buildAllMainMenu();
		IPermission[] permissions = usManager.getAllPermissions();

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		} else if (errorObj.getErrorCode() != 0) {
			model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		}

		model.addAttribute("MENUS", menus);
		model.addAttribute("PERMISSIONS", permissions);
		return VIEW_NAME;
	}

	@RequestMapping(value = "/admin/manage/menus/add", method = RequestMethod.POST)
	public String addMenu(@ModelAttribute MenuBo form, RedirectAttributes redirectAttributes) {
		String name = form.getName();
		String succMsg = this.lang.getMessage("msg.menu.create.success");
		String errMsg = null;

		if (StringUtils.isEmpty(name)) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		if (errMsg == null) {
			MenuBo menu = new MenuBo();
			menu.setName(name);
			menu.setUrl(form.getUrl() == null ? "" : form.getUrl());
			menu.setPosition(form.getPosition());
			menu.setParentId(form.getParentId());
			menu.setPermission("0".equals(form.getPermission()) ? null : form.getPermission());
			usManager.createMenu(menu);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.CREATE_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/menus";
	}

	@RequestMapping(value = "/admin/manage/menus/edit", method = RequestMethod.POST)
	public String editMenu(@ModelAttribute MenuBo form, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.menu.edit.success");
		String errMsg = null;

		String name = form.getName();
		if (StringUtils.isEmpty(name)) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		int menuId = form.getId();
		MenuBo menu = (MenuBo) usManager.getMenuById(menuId);
		if (menu == null) {
			errMsg = this.lang.getMessage("error.menu.notExist");
		}

		if (errMsg == null) {
			menu.setName(name);
			menu.setUrl(form.getUrl() == null ? "" : form.getUrl());
			menu.setPosition(form.getPosition());
			menu.setParentId(form.getParentId());
			menu.setPermission("0".equals(form.getPermission()) ? null : form.getPermission());
			usManager.updateMenu(menu);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/menus";
	}

	@RequestMapping(value = "/admin/manage/menus/delete/{menuId}")
	public String deleteMenu(@PathVariable int menuId, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.menu.delete.success");
		String errMsg = null;
		IMenu menu = usManager.getMenuById(menuId);
		if (menu == null) {
			errMsg = this.lang.getMessage("error.menu.notExist");
		}

		if (errMsg == null) {
			usManager.deleteMenu(menu);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/menus";
	}

	private List<IMenu> buildAllMainMenu() {
		List<IMenu> mainMenu = new LinkedList<IMenu>();
		IMenu[] rawMenu = usManager.getAllMenus();
		for (IMenu menu : rawMenu) {
			if (menu.getParentId() > 0) {
				break;
			}
			for (IMenu subMenu : rawMenu) {
				if (subMenu.getParentId() == menu.getId()) {
					menu.addChild(subMenu.clone());
				}
			}
			mainMenu.add(menu);
		}
		return mainMenu;
	}
}
