package ind.web.nhp.comment;

import java.util.List;
import java.util.Map;

public interface ICommentDao {

	public Long addComment(String accountName, String content, Long targetId, String token,
			Long parentCommentId, int status);

	public boolean deleteComment(Long commentId, Long targetId, String token);

	public Long updateComment(Long commentId, Long targetId, String token, String content,
			String accountUpdate);

	public Long approveComment(Long commentId, Long targetId, String token, String approver,
			Integer status);

	public Map<String, Object> getComment(Long commentId, Long targetId, String token);

	public List<Map<String, Object>> getChildComments(Long parentCommentId, Long targetId,
			String token);

	public List<Map<String, Object>> getCommentsByTarget(Long targetId, String token,
			Integer status, Integer page, Integer pageSize);
}
