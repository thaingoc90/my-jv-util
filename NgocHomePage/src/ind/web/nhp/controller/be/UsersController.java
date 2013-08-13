package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.encrypt.IPasswordEncoder;
import ind.web.nhp.encrypt.ISaltGenerator;
import ind.web.nhp.form.UserBEForm;
import ind.web.nhp.form.UserBEFormValidator;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.us.IGroup;
import ind.web.nhp.us.IUser;
import ind.web.nhp.us.impl.UserBo;
import ind.web.nhp.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsersController extends BaseBackendController {
	private static final String VIEW_NAME = "be_users";

	@Autowired
	private ISaltGenerator saltGenerator;

	@Autowired
	private IPasswordEncoder passwordEncoder;

	@Autowired
	private UserBEFormValidator userFormValidator;

	/**
	 * Manage users
	 * 
	 * @param errorObj
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/admin/users", "/admin/users/*" })
	public String get(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj,
	        HttpServletRequest request, Model model) {
		IUser[] listUsers = usManager.getAllUsers();
		IGroup[] listGroups = usManager.getAllGroups();
		Map<String, String> groupMapping = new HashMap<String, String>();
		for (IGroup group : listGroups) {
			groupMapping.put(String.valueOf(group.getId()), group.getName());
		}

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		} else if (errorObj.getErrorCode() != 0) {
			model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		}

		// PAGINATION
		// int page = Utils.toInt(request.getParameter("p"), 1);
		// int pageSize = Constants.DEFAULT_PAGESIZE;
		// int numUsers = listUsers.length;
		// String urlFormat = "abc?p={PAGE}";
		// PaginationModel pagniation = new PaginationModel(urlFormat, numUsers, pageSize, page);
		// model.addAttribute("pagination", pagniation);

		model.addAttribute("USERS", listUsers);
		model.addAttribute("GROUPS", listGroups);
		model.addAttribute("GROUP_MAPPING", groupMapping);
		return VIEW_NAME;
	}

	/**
	 * Create a new user.
	 * 
	 * @param form
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/users/add", method = RequestMethod.POST)
	public String addUser(@ModelAttribute UserBEForm form, BindingResult result,
	        RedirectAttributes redirectAttributes) {
		String email = form.getEmail();
		String loginName = form.getLoginName();
		String rawPassword = form.getPassword();
		String succMsg = this.lang.getMessage("msg.user.create.success");
		String errMsg = null;

		userFormValidator.validate(form, result);

		if (result.hasErrors()) {
			errMsg = this.lang.getMessage("error.field.required");
		} else if (!Utils.isValidEmail(email)) {
			errMsg = this.lang.getMessage("error.field.email.invalid");
		} else if (usManager.getUserByEmail(email) != null) {
			errMsg = this.lang.getMessage("error.user.email.existed");
		} else if (usManager.getUser(loginName) != null) {
			errMsg = this.lang.getMessage("error.user.name.existed");
		}

		if (errMsg == null) {
			UserBo user = new UserBo();
			user.setEmail(email);
			user.setLoginName(loginName);
			user.setLocked(false);
			user.setDisplayName(form.getDisplayName());
			user.setGroupId(form.getGroupId());
			String password = passwordEncoder.encodePassword(rawPassword,
			        saltGenerator.generateSalt(user));
			user.setPassword(password);
			usManager.createUser(user);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.CREATE_ERROR, errMsg, succMsg));
		return "redirect:/admin/users";
	}

	/**
	 * Edit user.
	 * 
	 * @param form
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/users/edit", method = RequestMethod.POST)
	public String editUser(@ModelAttribute UserBEForm form, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.user.edit.success");
		String errMsg = null;
		int userId = form.getId();
		String rawPassword = form.getPassword();

		UserBo user = (UserBo) usManager.getUser(userId);
		if (user == null) {
			errMsg = this.lang.getMessage("error.user.notExist");
		} else if (user.getId() == 1) {
			errMsg = this.lang.getMessage("error.user.notModify");
		}

		if (errMsg == null) {
			user.setDisplayName(form.getDisplayName());
			user.setGroupId(form.getGroupId());
			if (!StringUtils.isEmpty(rawPassword)) {
				String password = passwordEncoder.encodePassword(rawPassword,
				        saltGenerator.generateSalt(user));
				user.setPassword(password);
			}
			usManager.updateUser(user);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:/admin/users";
	}

	/**
	 * Delete user by id (except admin user)
	 * 
	 * @param userId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/users/delete/{userId}")
	public String deleteUser(@PathVariable int userId, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.user.delete.success");
		String errMsg = null;
		IUser user = usManager.getUser(userId);
		if (user == null) {
			errMsg = this.lang.getMessage("error.user.notExist");
		} else if (user.getId() == 1) {
			errMsg = this.lang.getMessage("error.user.notModify");
		}

		if (errMsg == null) {
			usManager.deleteUser(user);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:/admin/users";
	}

	/**
	 * Lock/Unlock user by id (except admin user)
	 * 
	 * @param userId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/admin/users/lock/{userId}")
	public String lockUser(@PathVariable int userId, RedirectAttributes redirectAttributes) {
		String errMsg = null;
		String succMsg = "";
		IUser user = usManager.getUser(userId);
		if (user == null) {
			errMsg = this.lang.getMessage("error.user.notExist");
		} else if (user.getId() == 1) {
			errMsg = this.lang.getMessage("error.user.notModify");
		}

		if (errMsg == null) {
			boolean locked = user.isLocked();
			((UserBo) user).setLocked(!locked);
			usManager.updateUser(user);
			succMsg = locked ? "msg.user.unlock.success" : "msg.user.lock.success";
			succMsg = this.lang.getMessage(succMsg);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
		        buildErrorObject(ErrorConstants.LOCK_ERROR, errMsg, succMsg));
		return "redirect:/admin/users";
	}
}
