package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.GroupBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IGroup;
import ind.web.nhp.us.IPermission;
import ind.web.nhp.us.impl.GroupBo;
import ind.web.nhp.us.impl.PermissionBo;
import ind.web.nhp.utils.JsonUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GroupsController extends BaseBackendController {

	private static final String VIEW_NAME = "be_groups";

	@RequestMapping(value = { "/admin/groups", "/admin/groups/*" })
	public String get(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj, Model model) {
		IGroup[] listGroups = usManager.getAllGroups();
		IPermission[] permisions = usManager.getAllPermissions();

		Map<Integer, IPermission[]> permGroupMapping = new HashMap<Integer, IPermission[]>();
		for (IGroup group : listGroups) {
			IPermission[] listPers = usManager.getAllPermisionsOfGroup(group);
			if (listPers != null) {
				permGroupMapping.put(group.getId(), listPers);
			}
		}

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		} else if (errorObj.getErrorCode() != 0) {
			model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		}

		model.addAttribute("GROUPS", listGroups);
		model.addAttribute("PERMISSIONS", permisions);
		model.addAttribute("GPMAPPING", JsonUtils.toJson(permGroupMapping));

		return VIEW_NAME;
	}

	/**
	 * Creates a group
	 * 
	 * @param form
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/groups/add", method = RequestMethod.POST)
	public String addGroup(@ModelAttribute GroupBEForm form, RedirectAttributes redirectAttributes) {
		String name = form.getName();
		String succMsg = this.lang.getMessage("msg.group.create.success");
		String errMsg = null;

		if (StringUtils.isEmpty(name)) {
			errMsg = this.lang.getMessage("error.field.required");
		} else if (usManager.getGroup(name) != null) {
			errMsg = this.lang.getMessage("error.group.name.existed");
		}

		if (errMsg == null) {
			GroupBo group = new GroupBo();
			group.setName(name);
			group.setDesc(form.getDesc());
			group.setSystem(form.isSystem());
			usManager.createGroup(group);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.CREATE_ERROR, errMsg, succMsg));
		return "redirect:/admin/groups";
	}

	/**
	 * Edits a group.
	 * 
	 * @param form
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/groups/edit", method = RequestMethod.POST)
	public String editGroup(@ModelAttribute GroupBEForm form, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.group.edit.success");
		String errMsg = null;
		int groupId = form.getId();
		String groupName = form.getName();

		GroupBo group = (GroupBo) usManager.getGroup(groupId);
		if (group == null) {
			errMsg = this.lang.getMessage("error.group.notExist");
		} else if (group.getId() == 1) {
			errMsg = this.lang.getMessage("error.group.notModify");
		} else if (!groupName.equals(group.getName()) && usManager.getGroup(groupName) != null) {
			errMsg = this.lang.getMessage("error.group.name.existed");
		}

		if (errMsg == null) {
			group.setName(groupName);
			group.setDesc(form.getDesc());
			group.setSystem(form.isSystem());
			usManager.updateGroup(group);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:/admin/groups";
	}

	/**
	 * Deletes a group by id.
	 * 
	 * @param groupId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/groups/delete/{groupId}")
	public String deleteGroup(@PathVariable int groupId, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.group.delete.success");
		String errMsg = null;
		IGroup group = usManager.getGroup(groupId);
		if (group == null) {
			errMsg = this.lang.getMessage("error.group.notExist");
		} else if (group.getId() == 1) {
			errMsg = this.lang.getMessage("error.group.notModify");
		}

		if (errMsg == null) {
			usManager.deleteGroup(group);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:/admin/groups";
	}

	@RequestMapping(value = "/admin/groups/permission", method = RequestMethod.POST)
	public String editPermission(@ModelAttribute GroupBEForm form,
	        RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.group.edit.success");
		String errMsg = null;
		int groupId = form.getId();
		String[] permissionIds = form.getPermissions();
		IGroup group = usManager.getGroup(groupId);
		if (group == null) {
			errMsg = this.lang.getMessage("error.group.notExist");
		} else if (group.getId() == 1) {
			errMsg = this.lang.getMessage("error.group.notModify");
		}

		List<IPermission> listPerms = new LinkedList<IPermission>();
		for (int i = 0; permissionIds != null && i < permissionIds.length; i++) {
			IPermission permission = usManager.getPermission(permissionIds[i]);
			if (permission == null) {
				errMsg = this.lang.getMessage("error.permission.notExist");
				break;
			}
			listPerms.add(permission);
		}

		if (errMsg == null) {
			usManager.deletePermisionsOfGroup(group);
			usManager.addMultiPermisionsForGroup(group, listPerms.toArray(new PermissionBo[0]));
		}
		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:/admin/groups";
	}
}
