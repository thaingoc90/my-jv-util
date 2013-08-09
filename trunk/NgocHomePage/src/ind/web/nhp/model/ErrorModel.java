package ind.web.nhp.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ErrorModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int errorCode;
	private List<String> msgErrors;
	private String msgSuccess;

	public ErrorModel() {
		this.errorCode = 0;
		msgErrors = new LinkedList<String>();
		msgSuccess = "";
	}

	public ErrorModel(int errCode, String msg, String msgSuccess) {
		this.errorCode = errCode;
		msgErrors = new LinkedList<String>();
		if (msg != null) {
			msgErrors.add(msg);
		}
		this.msgSuccess = msgSuccess;
	}

	public boolean isError() {
		return errorCode != 0 && msgErrors.size() > 0;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void addMsgError(String msg) {
		this.msgErrors.add(msg);
	}

	public List<String> getMsgErrors() {
		return msgErrors;
	}

	public void setMsgErrors(List<String> msgErrors) {
		this.msgErrors = msgErrors;
	}

	public String getMsgSuccess() {
		return msgSuccess;
	}

	public void setMsgSuccess(String msgSuccess) {
		this.msgSuccess = msgSuccess;
	}

}
