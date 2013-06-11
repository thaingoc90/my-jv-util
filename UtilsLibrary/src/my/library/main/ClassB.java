package my.library.main;

public class ClassB extends ClassA {

	public static String b = "b";

	static {
		System.out.println(2);
	}

	public static class X {
		static String x = "x";

		static {
			System.out.println(4);
		}
	}

	public final static String t = "t";
}
