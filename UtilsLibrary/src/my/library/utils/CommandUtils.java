package my.library.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandUtils {

	public static void main(String[] args) throws Exception {
		String dosCommand = "dir D:\\VNG";
		try {
			InputStream is = CommandUtils.executeDOSCommand(dosCommand);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String in;
			while ((in = br.readLine()) != null) {
				System.out.println(in);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static InputStream executeDOSCommand(String command) throws Exception {
		String dosCommand = "cmd /c " + command;
		try {
			Process p = Runtime.getRuntime().exec(dosCommand);
			return p.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Can not execute");
		}
	}

	public static InputStream executeLinuxCommand(String command) throws Exception {
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);
			return p.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Can not execute");
		}
	}
}
