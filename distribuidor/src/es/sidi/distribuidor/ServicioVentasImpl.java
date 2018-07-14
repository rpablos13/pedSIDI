/**
 * Clase ServicioSrOperadorImpl que implementa la interface ServicioSrOperadorInterface
 * implementa los metodos crear carpeta y bajar un fichero
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170628
 */
package es.sidi.distribuidor;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import es.sidi.common.Fichero;
import es.sidi.common.ServicioClienteInterface;
import es.sidi.common.ServicioVentasInterface;

public class ServicioVentasImpl extends UnicastRemoteObject implements ServicioVentasInterface {

	private static final long serialVersionUID = 1L;

	private Map<Integer, String> tipoMercanciaMap = new HashMap<>();
	private Map<String, Integer> tipoMercancia = new HashMap<String, Integer>();
	private Map<Float, Integer> precioMercancia = new HashMap<Float, Integer>();
	private Map<Float, Integer> kilosMercancia = new HashMap<Float, Integer>();

	/**
	 * constructor por defecto
	 * 
	 * @throws RemoteException
	 */
	protected ServicioVentasImpl() throws RemoteException {
		super();
	}

	/**
	 * crea la carpeta del cliente, no creamos carpeta de la repo, ya que no tiene
	 * sentido puesto que el servicio Datos es quien conoce la relacion entre repos
	 * clientes si hay varias repos registradas nos da igual, incluso si hay varias
	 * repos autenticadas a si que las carpetas de los clientes se crean en la
	 * carpeta actual
	 * 
	 * @param int
	 *            idCliente el identificardor del cliente que sera el nombre de la
	 *            carpeta
	 * @return true si la carpeta de cliente se ha creado con exito, false en otro
	 *         caso
	 */
	@Override
	public boolean crearCarpetaRepositorio(int idCliente) throws RemoteException {

		File carpeta = new File("" + idCliente);
		boolean creada = carpeta.mkdir(); // creada sera true si se ha creado.
		if (creada) {
			System.out.println(
					"Se ha creado la carpeta: " + idCliente + " en el path: " + System.getProperty("user.dir"));
			Distribuidor.listaCarpetas.add("" + idCliente);
		} else
			System.out.println("No se ha podido crear la carpeta: " + idCliente + " en el path: "
					+ System.getProperty("user.dir"));
		return creada;
	}

	/**
	 * envia un fichero del repositorio en la url enviada
	 * 
	 * @param URLdiscoCliente
	 *            String la url a la que se envia el fichero
	 * @param nombreFichero
	 *            String el fichero que vamos a descargar
	 * @param idCliente
	 *            int el id unico de cliente que es la carpeta de la repo
	 */
	@Override
	public void bajarFichero(String URLdiscoCliente, String nombreFichero, int idCliente)
			throws MalformedURLException, RemoteException, NotBoundException {
		// conversion implicita a cadena ""+idCliente
		Fichero fichero = new Fichero("" + idCliente, nombreFichero, "" + idCliente);
		ServicioClienteInterface servicioDiscoCliente = (ServicioClienteInterface) Naming.lookup(URLdiscoCliente);

		// if (servicioDiscoCliente.bajarFichero(fichero, idCliente) == false) {
		// System.out.println("Error en el envío (Checksum failed), intenta de nuevo");
		// } else {
		// System.out.println("Fichero: " + nombreFichero + " enviado");
		// }

	}

	@Override
	public int registrarOferta(String tipo, String precio, String kilos) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
}
