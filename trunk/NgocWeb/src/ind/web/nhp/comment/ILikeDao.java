package ind.web.nhp.comment;

import java.util.List;
import java.util.Map;

public interface ILikeDao {

	public boolean like(String accountName, Long targetId, Long commentId);

	public boolean unlike(String accountName, Long targetId, Long commentId);

	public Map<String, Object> getLike(String accountName, Long targetId, Long commentId);

	public List<Map<String, Object>> getLikes(Long targetId, Long commentId);

	public int countLikes(Long targetId, Long commentId);

}
