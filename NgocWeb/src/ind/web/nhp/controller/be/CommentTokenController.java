package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.comment.CommentConstants;
import ind.web.nhp.comment.CommentDao;
import ind.web.nhp.comment.ICommentDao;
import ind.web.nhp.form.TokenBEForm;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.utils.JsonUtils;
import ind.web.nhp.utils.Utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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
public class CommentTokenController extends BaseBackendController {

	private static final String VIEW_NAME = "be_cm_token";
	private static final String URL_MAPPING = "/admin/comment/token";

	@PostConstruct
	public void init() {
		setUrlMapping(URL_MAPPING);
	}

	@Autowired
	private ICommentDao cmtDao;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = { URL_MAPPING, URL_MAPPING + "/**" })
	public String handle(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj,
			HttpServletRequest request, Model model) {
		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		} else if (errorObj.getErrorCode() != 0) {
			model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		}

		List<Map<String, Object>> listTokens = cmtDao.getAllTokens();
		for (Map<String, Object> token : listTokens) {
			String domainObj = Utils.getString(token, CommentDao.FIELD_TARGET_DOMAINS);
			List<String> targetDomains = JsonUtils.fromJson(domainObj, List.class);
			token.put("list_domains", targetDomains);
		}

		Map<String, String> commentTypes = new LinkedHashMap<String, String>();
		commentTypes.put(String.valueOf(CommentConstants.COMMENT_TYPE_NO_CHECK),
				lang.getMessage("msg.token.type.no_check"));
		commentTypes.put(String.valueOf(CommentConstants.COMMENT_TYPE_CHECK),
				lang.getMessage("msg.token.type.check"));

		model.addAttribute("TOKENS", listTokens);
		model.addAttribute("commentTypes", commentTypes);
		return VIEW_NAME;
	}

	/**
	 * Deletes a token.
	 * 
	 * @param token
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/delete/{token}")
	public String deleteToken(@PathVariable String token, RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.token.delete.success");
		String errMsg = null;
		Map<String, Object> tokenObj = cmtDao.getToken(token);
		if (tokenObj == null) {
			errMsg = this.lang.getMessage("error.token.notExist");
		}

		if (errMsg == null) {
			cmtDao.deleteToken(token);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING;
	}

	@RequestMapping(value = URL_MAPPING + "/add", method = RequestMethod.POST)
	public String addToken(@ModelAttribute TokenBEForm form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.token.create.success");
		String errMsg = null;

		String token = form.getToken();
		if (StringUtils.isEmpty(token)) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		if (errMsg == null) {
			int cmtType = form.getCommentType();
			String targetDomains = JsonUtils.toJson(form.getTargetDomains());
			cmtDao.createToken(token, cmtType, targetDomains);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.CREATE_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING;
	}

	@RequestMapping(value = URL_MAPPING + "/edit", method = RequestMethod.POST)
	public String editToken(@ModelAttribute TokenBEForm form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.token.edit.success");
		String errMsg = null;

		String token = form.getToken();
		if (StringUtils.isEmpty(token)) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		Map<String, Object> tokenObj = cmtDao.getToken(token);
		if (tokenObj == null) {
			errMsg = this.lang.getMessage("error.token.notExist");
		}

		if (errMsg == null) {
			int cmtType = form.getCommentType();
			String targetDomains = JsonUtils.toJson(form.getTargetDomains());
			cmtDao.updateToken(token, cmtType, targetDomains);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING;
	}
}
