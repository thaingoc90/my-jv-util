package ind.web.nhp.base.manager;

public interface IConfigManager {

	public IConfig loadConfig(String key);

	public IConfig createConfig(IConfig config);

	public IConfig updateConfig(IConfig config);

	public void deleteConfig(IConfig config);
}
