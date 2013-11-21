package vng.wmb.activity;

public class InfoService {

	static {
		System.loadLibrary("infoManage");
	}

	public native InfoType getInfo(String name);

	public native void setInfo(String name, InfoType info);

	public native void deleteInfo(String name);

}
