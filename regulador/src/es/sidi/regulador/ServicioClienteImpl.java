/**
 * Clase ServicioDiscoClienteImpl que implementa la interface ServicioDiscoClienteInterface
 * se encarga de recibir un fichero
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20171007
 */
package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import es.sidi.common.ServicioClienteInterface;

public class ServicioClienteImpl extends UnicastRemoteObject implements ServicioClienteInterface {

	private static final long serialVersionUID = 1L;

	private Map<Integer, String> clientesRegistrados = new HashMap<Integer, String>();
	private Map<Integer, String> clientesEnSesion = new HashMap<Integer, String>();

	private Map<String, Integer> clienteNombre = new HashMap<String, Integer>();
	private Map<String, String> clientePassword = new HashMap<String, String>();

	/**
	 * Constructor por defecto
	 * 
	 * @throws RemoteException
	 */
	protected ServicioClienteImpl() throws RemoteException {
		super();
		inicializarMapas();
	}

	private void inicializarMapas() {
		clientesRegistrados = new HashMap<Integer, String>();
		clientesEnSesion = new HashMap<Integer, String>();
		clienteNombre = new HashMap<String, Integer>();
		clientePassword = new HashMap<String, String>();

	}

	/**
	 * autentica un cliente devolviendo el id
	 * 
	 * @param nombre
	 *            el nombre del cliente para autenticar
	 * @param id
	 *            el entero de sesion
	 * @return int entero con el id
	 */
	@Override
	public int autenticarCliente(String nombre, int id, String password) {
		if (clientesEnSesion.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = clientePassword.get(nombre);
			if (clienteNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {
				clientesEnSesion.put(id, nombre);
				return 1;
			} else
				return 0;// No se ha introducido un usuario o contraseña correctos
		}
	}

	/**
	 * registra un cliente devolviendo un id
	 * 
	 * @param nombre
	 *            el nombre del cliente a registrar
	 * @param id
	 *            la sesion
	 * @return int entero con el id
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	@Override
	public int registrarCliente(String nombre, int id, String password)
			throws RemoteException, MalformedURLException, NotBoundException {

		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (clienteNombre.containsKey(nombre))
			return 0;
		else {
			clientesRegistrados.put(id, nombre);
			clienteNombre.put(nombre, id);
			clientePassword.put(nombre, password);
		}
		return id;
	}

	/**
	 * devulve la lsita de lcientes con un formato basico de presentacion de datos
	 * similar a toString() se muestran todos los clientes registrado y se indican
	 * si estan online o no.
	 * 
	 * @return String la lista de los cliente formateada
	 */
	@Override
	public int listarClientes() throws RemoteException {
		try {
			System.out.println("********LISTA CLIENTES*********\n");

			for (Map.Entry<Integer, String> entry : clientesRegistrados.entrySet()) {
				Integer id = entry.getKey();
				String nombre = entry.getValue();

				System.out.println("Cliente: " + nombre + " -> " + id);

			}
			System.out.println("\n*******************************\n");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Elimina a un cliente de las sesion activas ojo!!!!! no lo borra del almacen
	 * de usuarios registrados, solo cierra la sesion podriamos devolver algun
	 * codigo de error, pero pasamos de momento
	 * 
	 * @param sesion
	 *            el identificador de la sesion actual
	 * @return int devuelve 0 sin error otro valor si hay error
	 */
	@Override
	public String desconectarCliente(int sesion) throws RemoteException {
		String cliente = clientesEnSesion.get(sesion);
		clientesEnSesion.remove(sesion);
		return cliente;
	}

	/**
	 * devuelve el id unico de un cliente a partir del id sesion
	 * 
	 * @param int
	 *            el id sesion del cliente
	 * @return int el id unico del cliente
	 */
	@Override
	public int sesion2id(int idsesion) {
		return Integer.parseInt(clientesEnSesion.get(idsesion));
	}

	@Override
	public Map<Integer, String> getMapClientes() throws RemoteException {
		return clientesEnSesion;
	}

}
