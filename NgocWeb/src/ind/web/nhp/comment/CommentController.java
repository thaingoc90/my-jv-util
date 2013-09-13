package ind.web.nhp.comment;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.utils.JsonUtils;
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

	private String getCurrentUser() {
		return "Ngoc";
	}

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

		if (!checkToken(token, url)) {
			return VIEW_NAME_ERROR;
		}

		if (StringUtils.isBlank(target)) {
			target = url;
		}
		Long targetId = null;
		int rows = 0;
		try {
			Map<String, Object> targetObj = cmDao.getTargetByTarget(target, token);
			if (targetObj == null) {
				targetId = cmDao.createTarget(target, url, token);
			} else {
				targetId = Utils.getValue(targetObj, CommentDao.FIELD_TARGET_ID, Long.class);
			}
			if (targetId == null) {
				return VIEW_NAME_ERROR;
			}
			rows = cmDao.getNumberOfCommentsByTarget(targetId, token,
					CommentConstants.COMMENT_STATUS_VALID);
		} catch (Exception e) {
			return VIEW_NAME_ERROR;
		}

		int maxPage = rows % limit == 0 ? rows / limit : rows / limit + 1;
		String accountName = getCurrentUser();
		model.addAttribute("isLogin", StringUtils.isEmpty(accountName) ? 0 : 1);
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
	 * Checks user logged?
	 * 
	 * @return
	 */
	@RequestMapping(value = "/comment/checkLogin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkLogin() {
		String accountName = getCurrentUser();
		return createAjaxOk(StringUtils.isEmpty(accountName) ? 0 : 1);
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
		Long parentCommentId = form.getParentCommentId();
		String content = form.getContent();
		try {
			Map<String, Object> tokenInfo = cmDao.getToken(token);
			int cmtType = Utils.getInt(tokenInfo, CommentDao.FIELD_COMMENT_TYPE);
			int status = CommentConstants.COMMENT_STATUS_VALID;
			if (cmtType == CommentConstants.COMMENT_TYPE_CHECK) {
				status = CommentConstants.COMMENT_STATUS_NOT_CHECKED;
				msg = "Please, wait for aprroval.";
			}
			cmDao.addComment(accountName, content, targetId, token, parentCommentId, status);
		} catch (Exception e) {
			msg = "Error while posting.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(msg);
	}

	/* ---- PRIVATE FUNCTION ------- */

	private boolean validateForm(CommentForm form) {
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean checkToken(String token, String url) {
		Map<String, Object> tokenInfo = cmDao.getToken(token);
		if (tokenInfo == null) {
			return false;
		}
		String targetDomainsObj = Utils.getString(tokenInfo, CommentDao.FIELD_TARGET_DOMAINS);
		List<String> targetDomains = JsonUtils.fromJson(targetDomainsObj, List.class);
		url = getDomain(url);
		return targetDomains.contains(url);
	}

	private String getDomain(String url) {
		int index = url.indexOf("?");
		url = (index != -1) ? url.substring(0, index) : url;
		index = url.indexOf("://");
		url = (index != -1) ? url.substring(index + 3) : url;
		index = url.indexOf("/");
		url = (index != -1) ? url.substring(0, index) : url;
		return url;
	}

	/* ---- END PRIVATE FUNCTION ---- */

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
			msg = "Error while processing.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	@RequestMapping(value = "/comment/countChildComment", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> countChildComment(@ModelAttribute CommentForm form) {
		int result = 0;
		String token = form.getToken();
		Long targetId = form.getTargetId();
		Long parentCommentId = form.getParentCommentId();
		try {
			result = cmDao.getNumberOfChildComments(parentCommentId, targetId, token,
					CommentConstants.COMMENT_STATUS_VALID);
		} catch (Exception e) {
			String msg = "Error while processing.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	@RequestMapping(value = "/comment/getChildComments", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getChildComments(@ModelAttribute CommentForm form) {
		Map<String, Object> result = new HashMap<String, Object>();
		String token = form.getToken();
		Long targetId = form.getTargetId();
		Long parentCommentId = form.getParentCommentId();
		try {
			int rows = cmDao.getNumberOfChildComments(parentCommentId, targetId, token,
					CommentConstants.COMMENT_STATUS_VALID);
			List<Map<String, Object>> listChildsCmt = cmDao.getChildComments(parentCommentId,
					targetId, token, CommentConstants.COMMENT_STATUS_VALID);
			result.put("numChildComments", rows);
			result.put("listChildComments", listChildsCmt);
		} catch (Exception e) {
			String msg = "Error while processing.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	/* -------------------------------------------------------------------------- */
	/* --------------------------- LIKE ----------------------------------------- */
	/**
	 * Counts the number of like. And check current-user liked or not?
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment/countLikes", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> countLikes(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			int totalLikes = likeDao.countLikes(targetId, commentId);
			Map<String, Object> checkUser = likeDao.getLike(accountName, targetId, commentId);
			result.put("totalLikes", totalLikes);
			result.put("user", checkUser);
			result.put("flagUser", checkUser != null && checkUser.size() > 0 ? 1 : 0);
		} catch (Exception e) {
			String msg = "Error while processing.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	/**
	 * Like comment.
	 * 
	 * @param form
	 * @return
	 */
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

	/**
	 * Unlike comment.
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment/unlike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unlike(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		boolean result = likeDao.unlike(accountName, targetId, commentId);
		if (result) {
			return createAjaxOk(result);
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, result);
		}
	}

	/**
	 * Counts the number of dislike. And check current-user disliked or not?
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment/countDislikes", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> countDislikes(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			int totalDislikes = likeDao.countDislikes(targetId, commentId);
			Map<String, Object> checkUser = likeDao.getDislike(accountName, targetId, commentId);
			result.put("totalDislikes", totalDislikes);
			result.put("user", checkUser);
			result.put("flagUser", checkUser != null && checkUser.size() > 0 ? 1 : 0);
		} catch (Exception e) {
			String msg = "Error while processing.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(result);
	}

	/**
	 * Dislike comment.
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment/dislike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> dislike(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		boolean result = likeDao.dislike(accountName, targetId, commentId);
		if (result) {
			return createAjaxOk(result);
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, result);
		}
	}

	/**
	 * Undislike comment.
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/comment/unDislike", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> unDislike(@ModelAttribute LikeForm form) {
		Long targetId = form.getTargetId();
		Long commentId = form.getCommentId();
		String accountName = getCurrentUser();
		boolean result = likeDao.unDislike(accountName, targetId, commentId);
		if (result) {
			return createAjaxOk(result);
		} else {
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, result);
		}
	}
}
