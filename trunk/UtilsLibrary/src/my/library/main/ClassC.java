package my.library.main;

public class ClassC {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		search: for (int possiblePrime = 2; possiblePrime <= 11; possiblePrime++) {

			for (int divisor = 2; divisor < possiblePrime; divisor++)
				if (possiblePrime % divisor == 0 && possiblePrime != divisor)
					continue search;

			System.out.println(possiblePrime);
		}
		long endTime = System.currentTimeMillis();
		System.err.println(endTime - startTime);
	}

}
