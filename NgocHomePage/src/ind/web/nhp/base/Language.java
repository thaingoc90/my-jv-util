package ind.web.nhp.base;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Language {

	private Map<String, String> allMessages;

	public Language() {
		allMessages = new ConcurrentHashMap<String, String>();
	}

	public Language(Map<String, String> messages) {
		setAllMessages(messages);
	}

	public String getMessage(String key) {
		String value = allMessages.get(key);
		return value != null ? value : "";
	}

	public String getMessage(String key, Object... replacement) {
		String value = getMessage(key);
		return MessageFormat.format(value, replacement);
	}

	public Map<String, String> getAllMessages() {
		return allMessages;
	}

	public void setAllMessages(Map<String, String> allMessages) {
		this.allMessages = allMessages;
	}

}
