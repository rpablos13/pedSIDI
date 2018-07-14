/**
 *Clase que se encarga de lo relacionado con el cliente
 * 
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.distribuidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.sidi.common.ServicioMercanciasInterface;
import es.sidi.common.ServicioVentasInterface;

public class ServicioVentasImpl extends UnicastRemoteObject implements ServicioVentasInterface {

	private static final long serialVersionUID = 1L;

	// Se crean las listas
	private Map<Integer, String> clientesRegistrados;
	private Map<Integer, String> clientesAutenticados;
	private Map<String, Integer> clienteNombre;
	private Map<String, String> clientePassword;

	private String nombreCliente;

	protected ServicioVentasImpl() throws RemoteException {
		super();
		inicializarMapas();
	}

	/**
	 * cuando se inicia esta clase, se inician los mapas de nuevo, es un reseteo
	 */
	private void inicializarMapas() {
		clientesRegistrados = new HashMap<Integer, String>();
		clienteNombre = new HashMap<String, Integer>();
		clientePassword = new HashMap<String, String>();
		clientesAutenticados = new HashMap<Integer, String>();
	}

	/**
	 * Autentica a un cliente
	 * 
	 * @param nombre
	 * @param id
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public int autenticarCliente(String nombre, int id, String password) {
		if (clientesAutenticados.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = clientePassword.get(nombre);
			if (clienteNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {
				clientesAutenticados.put(id, nombre);
				return 1;
			} else
				return 0;// No se ha introducido un usuario o contraseña correctos
		}
	}

	/**
	 * Registra a un cliente
	 * 
	 * @param nombre
	 * @param id
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
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
	 * Lista los clientes
	 * 
	 * @return
	 * @throws RemoteException
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
	 * Compra la mercancía
	 * 
	 * @param idDemanda
	 * @param idOferta
	 * @param idSesionCliente
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public int comprarMercancia(int idDemanda, int idOferta, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException {

		String puerto = "7791";
		String mercanciaURL = "rmi://localhost:" + puerto + "/mercancia";
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(mercanciaURL);

		return servicioMercanciasInterface.comprarMercancia(idSesionCliente, idDemanda, idOferta);

	}

	/**
	 * Pregunta por el id de sesión del distribuidor
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public int getIdSesionDistribuidor() throws RemoteException, MalformedURLException, NotBoundException {
		String puerto = "7791";
		String mercanciaURL = "rmi://localhost:" + puerto + "/mercancia";
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(mercanciaURL);

		return servicioMercanciasInterface.getIdSesionDistribuidor();
	}

	/**
	 * Da de baja a un cliente
	 * 
	 * @param idSesionCliente
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public String darDeBajaCliente(int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException {

		// ITERATOR CLIENTES AUTENTICADOS
		for (Iterator<Map.Entry<Integer, String>> it = clientesAutenticados.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, String> entry = it.next();
			if (entry.getKey().equals(idSesionCliente)) {
				nombreCliente = entry.getValue();
				it.remove();
			}
		}

		// ITERATOR CLIENTES NOMBRE
		for (Iterator<Map.Entry<String, Integer>> it = clienteNombre.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			if (entry.getValue().equals(idSesionCliente)) {
				it.remove();
			}
		}

		// ITERATOR CLIENTES PASSWORD
		for (Iterator<Map.Entry<String, String>> it = clientePassword.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			if (entry.getKey().equals(nombreCliente)) {
				it.remove();
			}
		}

		// ITERATOR CLIENTES REGISTRADOS
		for (Iterator<Map.Entry<Integer, String>> it = clientesRegistrados.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, String> entry = it.next();
			if (entry.getKey().equals(idSesionCliente)) {
				it.remove();
			}
		}

		return nombreCliente;
	}

	@Override
	public Map<Integer, String> getClientesRegistrados() {
		return clientesRegistrados;
	}

	@Override
	public Map<Integer, String> getClientesAutenticados() {
		return clientesAutenticados;
	}

	@Override
	public Map<String, Integer> getClienteNombre() {
		return clienteNombre;
	}

	@Override
	public Map<String, String> getClientePassword() {
		return clientePassword;
	}

}
