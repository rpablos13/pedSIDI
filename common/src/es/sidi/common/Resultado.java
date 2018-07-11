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
