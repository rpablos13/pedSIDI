/**
 * Esta clase interfaz se utiliza para mostar si un resultado es satisfactorio o no
 * 
 * @autor Raúl Pablos de la Prieta, rpablos13@alumno.uned.es
 */
package es.sidi.common;

public enum Resultado {

	FAIL("ERROR"), SUCCESSFUL("CORRECTO");

	private String resultado;

	private Resultado(String resultado) {

		this.resultado = resultado;
	}

	public String getResultado() {
		return resultado;
	}
}
