package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.form.PermissionBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IPermission;
import ind.web.nhp.us.impl.PermissionBo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PermissionsController extends BaseBackendController {

	private static final String VIEW_NAME = "be_permissions";

	@RequestMapping(value = { "/admin/manage/permissions", "/admin/manage/permissions/**" })
	public String get(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj, Model model) {
		IPermission[] permisions = usManager.getAllPermissions();
		model.addAttribute("PERMISSIONS", permisions);
		return VIEW_NAME;
	}

	/**
	 * 
	 * @param form
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/manage/permissions/add", method = RequestMethod.POST)
	public String addPerm(@ModelAttribute PermissionBEForm form,
	        RedirectAttributes redirectAttributes) {
		String id = form.getId();
		String succMsg = this.lang.getMessage("msg.permission.create.success");
		String errMsg = null;

		if (StringUtils.isEmpty(id)) {
			errMsg = this.lang.getMessage("error.field.required");
		} else if (usManager.getPermission(id) != null) {
			errMsg = this.lang.getMessage("error.permission.id.existed");
		}

		if (errMsg == null) {
			PermissionBo perm = new PermissionBo();
			perm.setId(id);
			perm.setDesc(form.getDesc());
			if (!"0".equals(form.getPid())) {
				perm.setPid(form.getPid());
			}
			usManager.addPermission(perm);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.CREATE_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/permissions";
	}

	@RequestMapping(value = "/admin/manage/permissions/edit", method = RequestMethod.POST)
	public String editPerm(@ModelAttribute PermissionBEForm form,
	        RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.permission.edit.success");
		String errMsg = null;

		String id = form.getId();
		PermissionBo perm = (PermissionBo) usManager.getPermission(id);
		if (perm == null) {
			errMsg = this.lang.getMessage("error.permission.notExist");
		}

		if (errMsg == null) {
			perm.setDesc(form.getDesc());
			if (!"0".equals(form.getPid())) {
				perm.setPid(form.getPid());
			} else {
				perm.setPid(null);
			}
			usManager.updatePermission(perm);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/permissions";
	}

	/**
	 * 
	 * @param permId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/manage/permissions/delete/{permId}")
	public String deletePerm(@PathVariable String permId, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.permission.delete.success");
		String errMsg = null;
		IPermission perm = usManager.getPermission(permId);
		if (perm == null) {
			errMsg = this.lang.getMessage("error.permission.notExist");
		}

		if (errMsg == null) {
			usManager.deletePermission(perm);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:/admin/manage/permissions";
	}
}
