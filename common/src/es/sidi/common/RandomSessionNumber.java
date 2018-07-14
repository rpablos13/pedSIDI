package es.sidi.common;

import java.util.Random;

public class RandomSessionNumber {

	static Random random = new Random();

	public static int generateSessionId() {
		return Math.abs(random.nextInt());
	}

}
