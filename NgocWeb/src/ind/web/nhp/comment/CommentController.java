package ind.web.nhp.comment;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.utils.Utils;

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
	private final static int DEFAULT_LIMIT = 10;

	@Autowired
	private ICommentDao cmDao;

	@RequestMapping(value = { "/comment", "/comment/*" }, method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		String token = request.getParameter("token");
		long targetId = Utils.toInt(request.getParameter("target"));
		int limit = Utils.toInt(request.getParameter("limit"));
		limit = limit > 0 ? limit : DEFAULT_LIMIT;
		int page = Utils.toInt(request.getParameter("page"));
		page = page > 0 ? page : 1;

		List<Map<String, Object>> listComments = null;
		listComments = cmDao.getCommentsByTarget(targetId, token, 1, page, limit);
		if (listComments == null || listComments.size() == 0) {
			listComments = cmDao.getCommentsByTarget(123l, "ngoc", 1, page, limit);
		}

		model.addAttribute("LIST_COMMENTS", listComments);
		model.addAttribute("cm_box_width", DEFAULT_WIDTH);
		model.addAttribute("cm_box_height", DEFAULT_HEIGHT);
		model.addAttribute("limit", limit);

		return VIEW_NAME;
	}

	@RequestMapping(value = "/comment/post", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> comment(@ModelAttribute CommentForm form, HttpServletRequest request) {
		String msg = "Post successfully.";
		if (!validate(form)) {
			msg = "Error: input's invalid";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		try {
			cmDao.addComment("Ngoc Thai", form.getContent(), form.getTargetId(), form.getToken(),
					null, CommentConstants.COMMENT_STATUS_VALID);
		} catch (Exception e) {
			msg = "Error while posting.";
			return createAjaxResult(Constants.AJAX_STATUS_ERROR, msg);
		}
		return createAjaxOk(msg);
	}

	private boolean validate(CommentForm form) {
		return true;
	}
}
