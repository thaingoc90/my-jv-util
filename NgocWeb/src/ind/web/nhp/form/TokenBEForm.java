package ind.web.nhp.form;

public class TokenBEForm {

	private String token;
	private int commentType;
	private String[] targetDomains;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	public String[] getTargetDomains() {
		return targetDomains;
	}

	public void setTargetDomains(String[] targetDomains) {
		this.targetDomains = targetDomains;
	}

}
