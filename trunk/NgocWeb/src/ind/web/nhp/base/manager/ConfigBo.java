package ind.web.nhp.base.manager;

import ind.web.nhp.base.BaseBo;

import java.util.HashMap;
import java.util.Map;

public class ConfigBo extends BaseBo implements IConfig {
	public static final String COL_KEY = "conf_key";
	public static final String COL_VALUE = "conf_value";

	private String key, value;

	public ConfigBo() {}

	public ConfigBo(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public Map<String, Object[]> getFieldMap() {
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		result.put(COL_KEY, new Object[] { "key", String.class });
		result.put(COL_VALUE, new Object[] { "value", String.class });
		return result;
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
