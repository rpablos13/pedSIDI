/**
 * La clase ServicioDatosImpl implementa la interface ServicioDatosInterface
 * Almacena y gestiona los datos como si de una base de datos se tratase
 * De esta forma el acoplamiento es minimo, y podemos rescribirla utilizando
 * cualquier otro mecanismo de almacenamiento
 * 
 * Recordemos operaciones con HashMap: put y containsKey
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170228
 */
package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.sidi.common.Resultado;
import es.sidi.common.ServicioMercanciasInterface;

public class ServicioMercanciasImpl extends UnicastRemoteObject implements ServicioMercanciasInterface {
	private static final long serialVersionUID = 123711131719L;

	// atributos para buscar el servicio Servidor Operador del Distribuidor
	private static int puertoSrOperador = 7792;
	// private static ServicioSrOperadorInterface servidorSrOperador;
	private static String direccionSrOperador = "localhost";
	private static String nombreSrOperador = "sroperador";

	// Estructuras que mantienen las autenticaciones VOLATILES
	private Map<Integer, String> sesionCliente = new HashMap<Integer, String>();
	private Map<Integer, String> clienteSesion = new HashMap<Integer, String>();
	private Map<Integer, String> sesionDistribuidor = new HashMap<Integer, String>();
	private Map<Integer, String> distribuidorSesion = new HashMap<Integer, String>();

	// Estructuras que mantiene el almacen de Clientes y Distribuidors registrados
	// PERSISTENTES
	private Map<Integer, String> almacenIdCliente = new HashMap<Integer, String>();
	private Map<String, Integer> clienteNombre = new HashMap<String, Integer>();
	private Map<String, String> clientePassword = new HashMap<String, String>();
	private Map<String, Integer> distribuidorNombre = new HashMap<String, Integer>();
	private Map<String, String> distribuidorPassword = new HashMap<String, String>();
	private Map<Integer, Integer> almacenClienteDistribuidor = new HashMap<Integer, Integer>();

	// Metodos
	/**
	 * Contructor necesario al extender UnicastRemoteOBject y poder utilizar Naming
	 * 
	 * @throws RemoteException
	 */

	protected ServicioMercanciasImpl() throws RemoteException {
		super();
		inicializarTablas();
		cargarTiposMercanciaInicial();
		cargarBaseDatosMercancia();
	}

	/**
	 * Cargamos un nuevo hashmap con los tipos de mercancía que se van a utilizar,
	 * de esta manera no hará falta registarlos manualmente.
	 */
	public void cargarTiposMercanciaInicial() {

		Map<Integer, String> tipoMercanciaMap = new HashMap<>();

		tipoMercanciaMap.put(1, "Tomates");
		tipoMercanciaMap.put(2, "Limones");
		tipoMercanciaMap.put(3, "Naranjas");
		tipoMercanciaMap.put(4, "Fresas");
		tipoMercanciaMap.put(5, "Plátanos");
		tipoMercanciaMap.put(6, "Melones");
		tipoMercanciaMap.put(7, "Sandías");

		System.out.println("Cargar tipo de mercancías. Resultado: " + Resultado.SUCCESSFUL.getResultado());
	}

	public void cargarBaseDatosMercancia() {

		Map<Integer, String> mercanciaMap = new HashMap<>();
		System.out.println("Cargado de tablas. Resultado: " + Resultado.SUCCESSFUL.getResultado());

	}

	/**
	 * inicializa las tablas para ser guardadas
	 */
	public void inicializarTablas() {
		almacenIdCliente = new HashMap<Integer, String>();
		clienteSesion = new HashMap<Integer, String>();
		sesionDistribuidor = new HashMap<Integer, String>();
		distribuidorNombre = new HashMap<String, Integer>();
		distribuidorPassword = new HashMap<String, String>();
		clienteNombre = new HashMap<String, Integer>();
		clientePassword = new HashMap<String, String>();
		almacenClienteDistribuidor = new HashMap<Integer, Integer>();
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
		if (clienteSesion.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = clientePassword.get(nombre);
			if (clienteNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {
				clienteSesion.put(id, nombre);
				sesionCliente.put(id, nombre);
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
			clienteNombre.put(nombre, id);
			clientePassword.put(nombre, password);
			sesionCliente.put(id, nombre);
		}
		return id;
	}

	/**
	 * autentica un Distribuidor
	 * 
	 * @param String
	 *            el nombre del Distribuidor
	 * @param int
	 *            el idsesion que le pasamos
	 * @return int -1 si la repo no estra registrada, 0 si ya esta autenticado, el
	 *         idsesion si es correcto
	 */
	@Override
	public int autenticarDistribuidor(String nombre, int id, String password) throws RemoteException {

		if (distribuidorSesion.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = distribuidorPassword.get(nombre);
			if (distribuidorNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {
				distribuidorSesion.put(id, nombre);
				sesionDistribuidor.put(id, nombre);
				return 1;
			} else
				return 0;// No se ha introducido un usuario o contraseña correctos
		}
	}

	/**
	 * registra un repositiorio
	 * 
	 * @param String
	 *            el nombre del Distribuidor
	 * @param int
	 *            el id sesion del Distribuidor
	 * @return int 0 si ya esta registrada con ese nombre, el id sesion en caso
	 *         contrario
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public int registrarDistribuidor(String nombre, int id, String password) throws RemoteException {
		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (distribuidorNombre.containsKey(nombre))
			return 0;
		else {
			distribuidorNombre.put(nombre, id);
			distribuidorPassword.put(nombre, password);
			sesionDistribuidor.put(id, nombre);
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
	public String listaClientes() throws RemoteException {
		String clientes = "";
		for (String nombre : almacenIdCliente.values()) {
			int id = Integer.parseInt(clienteSesion.get(nombre));
			int estado = 0;
			if (clienteSesion.containsKey(nombre))
				estado = 1;
			String s = "";
			if (estado == 0)
				s = "[OFFLINE]";
			else
				s = "[ ONLINE]";
			clientes = clientes + "Cliente [" + s + " id=" + id + ", nombre=" + nombre + "] ";
		}
		return clientes;
	}

	/**
	 * devuelve la lista de Distribuidors con un formato basico de presentacion se
	 * muestran todas las repos registradas y si estan online o no
	 * 
	 * @return String la lista de las repos formateada
	 */
	@Override
	public String listaDistribuidors() throws RemoteException {
		String lista = "";

		Iterator it = sesionDistribuidor.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			int id = (Integer) e.getKey();
			String nombre = (String) e.getValue();
			int estado = 0;
			if (distribuidorSesion.containsKey(nombre))
				estado = 1;
			String s = "";
			if (estado == 0)
				s = "[OFFLINE]";
			else
				s = "[ ONLINE]";
			lista = lista + "Distribuidor [" + s + " id=" + id + ", nombre=" + nombre + "] ";
		}
		return lista;

	}

	/**
	 * devuelve un String con los emparejameintos entre clientes y repos recordemos
	 * un lciente solo esta en una repo se devulve el ide cliente el id repositiorio
	 * el nombre cliente y el nombre de la repo
	 * 
	 * @return la lsita de las parejas
	 */
	@Override
	public String listaClientesDistribuidors() {
		String lista = "";
		// System.out.println(almacenIdDistribuidor);
		// System.out.println(almacenClienteDistribuidor);
		Iterator it = almacenClienteDistribuidor.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			int idCliente = (Integer) e.getKey();
			int idDistribuidor = (Integer) e.getValue();
			String nombreCliente = almacenIdCliente.get(idCliente);
			String nombreDistribuidor = sesionDistribuidor.get(idDistribuidor);
			lista = lista + "Pareja Cliente - Distribuidor [cliente=" + idCliente + ", Distribuidor=" + idDistribuidor
					+ ", nombreCliente=" + nombreCliente + ", nombreDistribuidor=" + nombreDistribuidor + "] ";
		}
		// System.out.println("Todavia listaClientesDistribuidor: " + lista);
		return lista;
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
		String cliente = sesionCliente.get(sesion);
		sesionCliente.remove(sesion);
		clienteSesion.remove(cliente);
		return cliente;
	}

	/**
	 * borra la entrada de sesion de una repo, es decir desconecta la repo
	 * 
	 * @param int
	 *            el id sesion de la repo
	 * @return String el nombre de la repo desconectada
	 */
	@Override
	public String desconectarDistribuidor(int sesion) throws RemoteException {
		String repo = sesionDistribuidor.get(sesion);
		sesionDistribuidor.remove(sesion);
		distribuidorSesion.remove(repo);
		return repo;
	}

	/**
	 * busca la priemra repo online que encuentra y devulve su id unico
	 * 
	 * @return int el id unico de la primera repo online encontrada
	 */
	public int dameDistribuidor() {
		if (distribuidorSesion.isEmpty()) {
			return 0;
		} else {
			Iterator it = distribuidorSesion.entrySet().iterator();
			String nombre = "";
			if (it.hasNext()) {
				Map.Entry e = (Map.Entry) it.next();
				nombre = (String) e.getKey();
			}
			return distribuidorNombre.get(nombre);
		}
	}

	/**
	 * devuelve el Distribuidor de un cliente
	 * 
	 * @param int
	 *            el id unico del cliente
	 * @return int el id sesion de la repo
	 */
	@Override
	public int dimeDistribuidor(int idCliente) {
		return Integer
				.parseInt(distribuidorSesion.get(sesionDistribuidor.get(almacenClienteDistribuidor.get(idCliente))));
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
		return Integer.parseInt(clienteSesion.get(sesionCliente.get(idsesion)));
	}

}