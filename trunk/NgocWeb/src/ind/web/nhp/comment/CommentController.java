package ind.web.nhp.comment;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommentController extends BaseController {

	private final static String VIEW_NAME = "cm_frame";
	private final static String VIEW_NAME_ERROR = "cm_frame_error";
	private final static int DEFAULT_LIMIT = 2;

	@Autowired
	private ICommentDao cmDao;

	@Autowired
	private ILikeDao likeDao;

	/**
	 * Renders comment view.
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/comment", "/comment/*" }, method = RequestMethod.GET)
	public String handle(HttpServletRequest request, Model model) {

		String token = request.getParameter("token");
		String target = request.getParameter("target");
		String targetUrl = request.getParameter("target_url");
		String url = new String(Base64.decodeBase64(targetUrl));
		int limit = Utils.toInt(request.getParameter("limit"));
		limit = limit > 0 ? limit : DEFAULT_LIMIT;
		int page = Utils.toInt(request.getParameter("page"));
		page = page > 0 ? page : 1;

		// TODO: Check token.

		if (StringUtils.isBlank(target)) {
			target = url;
		}
		Long targetId = null;
		Map<String, Object> targetObj = cmDao.getTargetByTarget(target, token);
		if (targetObj == null) {
			targetId = cmDao.createTarget(target, url, token);
		} else {
			targetId = Utils.getValue(targetObj, "target_id", Long.class);
		}

		if (targetId == null) {
			return VIEW_NAME_ERROR;
		}

		int rows = cmDao.getNumberOfCommentsByTarget(targetId, token,
				CommentConstants.COMMENT_STATUS_VALID);
		int maxPage = rows % limit == 0 ? rows / limit : rows / limit + 1;

		model.addAttribute("url", url);
		model.addAttribute("limit", limit);
		model.addAttribute("token", token);
		model.addAttribute("targetId", targetId);
		model.addAttribute("numComments", rows);
		model.addAttribute("maxPage", maxPage);
		model.addAttribute("curPage", page);

		return VIEW_NAME;
	}

	/**
	 * Posts a comment.
	 * 
	 * @param form
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/comment/post", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> postComment(@ModelAttribute CommentForm form,
			HttpServletRequest request) {
		String msg = "Post successfully.";
		if (!validateForm(form)) {
			msg = "Error: input's invalid";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		String token = form.getToken();
		Long targetId = form.getTargetId();
		String accountName = getCurrentUser();
		try {
			cmDao.addComment(accountName, form.getContent(), targetId, token, null,
					CommentConstants.COMMENT_STATUS_VALID);
		} catch (Exception e) {
			msg = "Error while posting.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(msg);
	}

	private boolean validateForm(CommentForm form) {
		return true;
	}

	/**
	 * Gets list comments of a target.
	 * 
	 * @param form
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/comment/getComments", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getComments(@ModelAttribute CommentForm form,
			HttpServletRequest request) {
		String msg = "Gets successfully.";
		if (!validateForm(form)) {
			msg = "Error: input's invalid";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		String token = form.getToken();
		Long targetId = form.getTargetId();
		int page = form.getPage();
		page = page < 1 ? 1 : page;
		int limit = form.getLimit();
		limit = limit < 1 ? 1 : limit;
		List<Map<String, Object>> listComments = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			int rows = cmDao.getNumberOfCommentsByTarget(targetId, token,
					CommentConstants.COMMENT_STATUS_VALID);
			int maxPage = rows % limit == 0 ? rows / limit : rows / limit + 1;
			listComments = cmDao.getCommentsByTarget(targetId, token,
					CommentConstants.COMMENT_STATUS_VALID, page, limit);
			result.put("numComments", rows);
			result.put("maxPage", maxPage);
			result.put("listComments", listComments);
		} catch (Exception e) {
			msg = "Error while posting.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	@RequestMapping(value = "/comment/countLikes", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> countLikes(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		int totalLikes = likeDao.countLikes(targetId, commentId);
		Map<String, Object> checkUser = likeDao.getLike(accountName, targetId, commentId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalLikes", totalLikes);
		result.put("user", checkUser);
		result.put("flagUser", checkUser != null && checkUser.size() > 0 ? 1 : 0);
		return createAjaxOk(result);
	}

	@RequestMapping(value = "/comment/like", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> like(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		boolean result = likeDao.like(accountName, targetId, commentId);
		if (result) {
			return createAjaxOk(result);
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, result);
		}
	}

	@RequestMapping(value = "/comment/unlike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unlike(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = "Ngoc Thai";
		boolean result = likeDao.unlike(accountName, targetId, commentId);
		if (result) {
			return createAjaxOk(result);
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, result);
		}
	}

	private String getCurrentUser() {
		return "Ngoc Thai";
	}

}
