package ind.web.nhp.controller.be;

import ind.web.nhp.base.Constants;
import ind.web.nhp.base.ErrorConstants;
import ind.web.nhp.comment.CommentConstants;
import ind.web.nhp.comment.CommentDao;
import ind.web.nhp.comment.CommentForm;
import ind.web.nhp.comment.ICommentDao;
import ind.web.nhp.model.ErrorModel;
import ind.web.nhp.model.PaginationModel;
import ind.web.nhp.us.IUser;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CommentBEController extends BaseBackendController {
	private static final String VIEW_NAME = "be_cm_comment";
	private static final String VIEW_NAME_SEARCH = "be_cm_search";
	private static final String URL_MAPPING = "/admin/comment/list";

	@PostConstruct
	public void init() {
		setUrlMapping(URL_MAPPING);
	}

	@Autowired
	private ICommentDao cmtDao;

	@RequestMapping(value = URL_MAPPING)
	public String handle(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj,
			HttpServletRequest request, Model model) {
		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		}
		return VIEW_NAME_SEARCH;
	}

	/**
	 * List comments of a token
	 * 
	 * @param errorObj
	 * @param token
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/{token}")
	public String list(@ModelAttribute(Constants.ERROR_PARAM) ErrorModel errorObj,
			@PathVariable String token, HttpServletRequest request, Model model,
			RedirectAttributes redirectAttributes) {
		Map<String, Object> tokenObj = cmtDao.getToken(token);
		if (tokenObj == null) {
			String errMsg = this.lang.getMessage("error.token.notExist");
			redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
					buildErrorObject(ErrorConstants.ERROR_CODE_DEFAULT, errMsg, ""));
			return "redirect:" + URL_MAPPING;
		}

		// PAGINATION
		int page = Utils.toInt(request.getParameter("p"), 1);
		int pageSize = Constants.DEFAULT_PAGESIZE;
		int numUsers = cmtDao.getNumberOfCommentsByToken(token, null);
		String urlFormat = URL_MAPPING + "/" + token + "?p={PAGE}";
		PaginationModel pagniation = new PaginationModel(urlFormat, numUsers, pageSize, page);
		model.addAttribute("pagination", pagniation);

		List<Map<String, Object>> listComments = cmtDao.getCommentsByToken(token, null, page,
				pageSize);
		for (Map<String, Object> comment : listComments) {
			Long targetId = Utils.getValue(comment, CommentDao.FIELD_TARGET_ID, Long.class);
			Map<String, Object> targetObj = cmtDao.getTargetById(targetId);
			String target = Utils.getString(targetObj, CommentDao.FIELD_TARGET);
			String targetUrl = Utils.getString(targetObj, CommentDao.FIELD_TARGET_URL);
			comment.put(CommentDao.FIELD_TARGET, target);
			comment.put(CommentDao.FIELD_TARGET_URL, targetUrl);
		}

		Map<String, String> statusMap = new LinkedHashMap<>();
		statusMap.put(String.valueOf(CommentConstants.COMMENT_STATUS_NOT_CHECKED),
				lang.getMessage("msg.comment.status.notChecked"));
		statusMap.put(String.valueOf(CommentConstants.COMMENT_STATUS_VALID),
				lang.getMessage("msg.comment.status.valid"));
		statusMap.put(String.valueOf(CommentConstants.COMMENT_STATUS_REJECT),
				lang.getMessage("msg.comment.status.reject"));

		if (errorObj.isError()) {
			model.addAttribute(ErrorConstants.MODEL_ERRORS, errorObj.getMsgErrors());
		} else if (errorObj.getErrorCode() != 0) {
			model.addAttribute(ErrorConstants.MODEL_SUCCESS, errorObj.getMsgSuccess());
		}

		model.addAttribute("statusMap", statusMap);
		model.addAttribute("COMMENTS", listComments);
		model.addAttribute("token", Utils.getString(tokenObj, CommentDao.FIELD_TOKEN));
		return VIEW_NAME;
	}

	/**
	 * Edits a comment.
	 * 
	 * @param form
	 * @param result
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/edit", method = RequestMethod.POST)
	public String editComment(@ModelAttribute CommentForm form, BindingResult result,
			RedirectAttributes redirectAttributes) {
		String succMsg = this.lang.getMessage("msg.comment.edit.success");
		String errMsg = null;

		String token = form.getToken();
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();

		if (StringUtils.isEmpty(token) || targetId == 0 || commentId == 0) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		Map<String, Object> commentObj = cmtDao.getComment(commentId, targetId, token);
		if (commentObj == null) {
			errMsg = this.lang.getMessage("error.comment.notExist");
		}

		if (errMsg == null) {
			int oldStatus = Utils.getInt(commentObj, CommentDao.FIELD_STATUS);
			int status = form.getStatus();
			String content = form.getContent();
			IUser user = getCurrentUser();
			String userUpdate = user.getLoginName();
			if (status != oldStatus) {
				cmtDao.updateCommentStatus(commentId, targetId, token, userUpdate, status);
			}
			cmtDao.updateComment(commentId, targetId, token, content, userUpdate);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING + "/" + token;
	}

	/**
	 * Deletes comment
	 * 
	 * @param redirectAttributes
	 * @param request
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/delete")
	public String deleteComment(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		String succMsg = this.lang.getMessage("msg.comment.delete.success");
		String errMsg = null;

		String token = request.getParameter("token");
		String targetIdParam = request.getParameter("target_id");
		String commentIdParam = request.getParameter("comment_id");
		Long targetId = Utils.toLong(targetIdParam, 0l);
		Long commentId = Utils.toLong(commentIdParam, 0l);

		if (StringUtils.isEmpty(token) || targetId == 0 || commentId == 0) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		Map<String, Object> commentObj = cmtDao.getComment(commentId, targetId, token);
		if (commentObj == null) {
			errMsg = this.lang.getMessage("error.comment.notExist");
		}

		if (errMsg == null) {
			cmtDao.deleteComment(commentId, targetId, token);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.DELETE_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING + "/" + token;
	}

	/**
	 * Approves a comment.
	 * 
	 * @param redirectAttributes
	 * @param request
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/approve")
	public String approveComment(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		String succMsg = this.lang.getMessage("msg.comment.approve.success");
		String errMsg = null;

		String token = request.getParameter("token");
		String targetIdParam = request.getParameter("target_id");
		String commentIdParam = request.getParameter("comment_id");
		Long targetId = Utils.toLong(targetIdParam, 0l);
		Long commentId = Utils.toLong(commentIdParam, 0l);

		if (StringUtils.isEmpty(token) || targetId == 0 || commentId == 0) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		Map<String, Object> commentObj = cmtDao.getComment(commentId, targetId, token);
		if (commentObj == null) {
			errMsg = this.lang.getMessage("error.comment.notExist");
		}

		if (errMsg == null) {
			IUser user = getCurrentUser();
			String userUpdate = user.getLoginName();
			cmtDao.updateCommentStatus(commentId, targetId, token, userUpdate,
					CommentConstants.COMMENT_STATUS_VALID);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING + "/" + token;
	}

	/**
	 * Rejects a comment.
	 * 
	 * @param redirectAttributes
	 * @param request
	 * @return
	 */
	@RequestMapping(value = URL_MAPPING + "/reject")
	public String rejectComment(RedirectAttributes redirectAttributes, HttpServletRequest request) {
		String succMsg = this.lang.getMessage("msg.comment.reject.success");
		String errMsg = null;

		String token = request.getParameter("token");
		String targetIdParam = request.getParameter("target_id");
		String commentIdParam = request.getParameter("comment_id");
		Long targetId = Utils.toLong(targetIdParam, 0l);
		Long commentId = Utils.toLong(commentIdParam, 0l);

		if (StringUtils.isEmpty(token) || targetId == 0 || commentId == 0) {
			errMsg = this.lang.getMessage("error.field.required");
		}

		Map<String, Object> commentObj = cmtDao.getComment(commentId, targetId, token);
		if (commentObj == null) {
			errMsg = this.lang.getMessage("error.comment.notExist");
		}

		if (errMsg == null) {
			IUser user = getCurrentUser();
			String userUpdate = user.getLoginName();
			cmtDao.updateCommentStatus(commentId, targetId, token, userUpdate,
					CommentConstants.COMMENT_STATUS_REJECT);
		}

		redirectAttributes.addFlashAttribute(Constants.ERROR_PARAM,
				buildErrorObject(ErrorConstants.EDIT_ERROR, errMsg, succMsg));
		return "redirect:" + URL_MAPPING + "/" + token;
	}

	@RequestMapping(value = URL_MAPPING + "/detail", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> detailComment(@ModelAttribute CommentForm form) {
		String token = form.getToken();
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();

		if (StringUtils.isEmpty(token) || targetId == 0 || commentId == 0) {
			String errMsg = this.lang.getMessage("error.field.required");
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, errMsg);
		}

		Map<String, Object> commentObj = cmtDao.getComment(commentId, targetId, token);
		if (commentObj == null) {
			String errMsg = this.lang.getMessage("error.comment.notExist");
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, errMsg);
		}

		int status = Utils.getInt(commentObj, CommentDao.FIELD_STATUS);
		String statusString = "";
		if (status == CommentConstants.COMMENT_STATUS_VALID) {
			statusString = lang.getMessage("msg.comment.status.valid");
		} else if (status == CommentConstants.COMMENT_STATUS_REJECT) {
			statusString = lang.getMessage("msg.comment.status.reject");
		} else {
			statusString = lang.getMessage("msg.comment.status.notChecked");
		}
		commentObj.put("status_str", statusString);
		return createAjaxOk(commentObj);
	}
}
