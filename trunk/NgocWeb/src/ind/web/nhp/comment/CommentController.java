package ind.web.nhp.comment;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
	private final static int DEFAULT_WIDTH = 500;
	private final static int DEFAULT_HEIGHT = 300;
	private final static int DEFAULT_LIMIT = 5;

	@Autowired
	private ICommentDao cmDao;

	@RequestMapping(value = { "/comment", "/comment/*" }, method = RequestMethod.GET)
	public String handle(HttpServletRequest request, Model model) {

		String token = request.getParameter("token");
		long targetId = Utils.toInt(request.getParameter("target"));
		int limit = Utils.toInt(request.getParameter("limit"));
		limit = limit > 0 ? limit : DEFAULT_LIMIT;
		int page = Utils.toInt(request.getParameter("page"));
		page = page > 0 ? page : 1;
		targetId = 123l;
		token = "ngoc";

		int rows = cmDao.getNumberOfCommentsByTarget(targetId, token,
				CommentConstants.COMMENT_STATUS_VALID);
		int maxPage = rows % limit == 0 ? rows / limit : rows / limit + 1;

		model.addAttribute("cm_box_width", DEFAULT_WIDTH);
		model.addAttribute("cm_box_height", DEFAULT_HEIGHT);
		model.addAttribute("limit", limit);
		model.addAttribute("numComments", rows);
		model.addAttribute("maxPage", maxPage);
		model.addAttribute("curPage", page);

		return VIEW_NAME;
	}

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
		try {
			cmDao.addComment("Ngoc Thai", form.getContent(), targetId, token, null,
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

	@RequestMapping(value = "/comment/getComments")
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
}
