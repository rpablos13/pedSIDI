/**
 * Esta clase interfaz se crea para generar un número positivo aleatorio, se utilizará para guardar las sesiones
 * 
 * @autor Raúl Pablos de la Prieta, rpablos13@alumno.uned.es
 */
package es.sidi.common;

import java.util.Random;

public class RandomSessionNumber {

	static Random random = new Random();

	public static int generateSessionId() {
		return Math.abs(random.nextInt());
	}

}
