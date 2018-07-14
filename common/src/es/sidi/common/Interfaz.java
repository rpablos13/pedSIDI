/**
 * Esta clase interfaz se crea para no mostrar siempre el mismo código cada vez que queramos preguntar un dato por pantalla o simplemente mostar el menú
 * 
 * @autor Raúl Pablos de la Prieta, rpablos13@alumno.uned.es
 */
package es.sidi.common;

import java.util.Scanner;

public class Interfaz {

	/**
	 * muestra un menu y recoge una opcion
	 * 
	 * @param titulo
	 *            el titulo del menu
	 * @param opciones
	 *            un vector de strings con las opciones a mostrar
	 * @return int con el nÃºmero de opcion elegido
	 */
	public static int menu(String titulo, String[] opciones) {
		System.out.println("\n\t" + titulo.toUpperCase());
		System.out.println("-----------------------------------------");
		for (int i = 0; i < opciones.length; i++) {
			System.out.println((i + 1) + ".- " + opciones[i]);
		}
		System.out.println("Selecione una opción: ");
		Scanner opcion = new Scanner(System.in);
		return opcion.nextInt();
	}

	/**
	 * Este método se utiliza para las preguntas que se hagan al usuario. Es mucho
	 * mejor sacar este método a una interfaz que declararlo en cada clase
	 * 
	 * @param texto
	 * @return
	 */
	public static String preguntaUsuario(String texto) {
		System.out.println("\n " + texto);
		Scanner reader = new Scanner(System.in);
		return reader.nextLine();
	}

}