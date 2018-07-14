/**
 * Funciones del Distribuidor:


 * 
 * 1. Introducir oferta
 * 2. Quitar oferta
 * 3. Mostrar ventas
 * 4. Darse de baja en el sistema
 * 5. Salir
 * 
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.distribuidor;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.sidi.common.Interfaz;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioMercanciasInterface;

public class Distribuidor {

	// lista de carpeta que se van a ir creando en la repo, hace falta agregar
	// persistencia
	// la visibilidad es ladel paquete
	public static List<String> listaCarpetas;// = new ArrayList<String>();

	// atributos para buscar el servicio de autentificacion del servidor
	private static int sesion = 0;
	private static int puerto = 7791;
	private static ServicioAutenticacionInterface servicioAutenticacionInterface;
	private static String direccion = "localhost";

	// aqui se van a guardar las URL rmi
	private static String autenticador;
	private static String mercancia;
	private static String cloperador;

	// atributos para levantar los servicios Mercancias
	private static Registry registryServicio;
	private static int puertoMercancia = 7792;
	private static String nombreRepo;
	// main

	public static void main(String[] args) throws Exception {

		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";
		mercancia = "rmi://" + direccion + ":" + puerto + "/mercancia";

		new Distribuidor().iniciar();
		System.exit(0);// fin del programa,return o nada deja abierto el programa
	}

	/**
	 * inicializa la tabla de la estructura logica de las carpetas
	 */
	public void inicializarTablas() {
		listaCarpetas = new ArrayList<String>();

	}

	/**
	 * pone en funcionamiento cada repositorio, trata la excepcion de conexion al
	 * servidor intentando acceder para registrar o autenticar
	 * 
	 * @throws Exception
	 */
	private void iniciar() throws Exception {

		// si el servidor no esta disponible, cerramos informando de ello
		try {
			String autenticadorURL = autenticador;

			servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming.lookup(autenticadorURL);

			// mostramos el menu
			int opcion = 0;
			do {
				opcion = Interfaz.menu("Menu Distribuidor",
						new String[] { "Registrar un nuevo usuario", "Autenticar en el sistema (hacer login)" });
				switch (opcion) {
				case 1:
					registrar();
					break;
				case 2:
					autenticar();
					break;
				}
			} while (opcion != 3);
		} catch (ConnectException e) {
			System.out.println("Error de conexion, el servidor no esta disponible, vuelva a intentarlo mas tarde");
			String st = Interfaz.pideDato("Pulse enter para finalizar...");
		}
	}

	/**
	 * inicia la autenticacion de una repo en el sistema
	 * 
	 * @throws Exception
	 */
	private void autenticar() throws Exception {
		String nombre = Interfaz.pideDato("Nombre: ");
		String password = Interfaz.pideDato("Password: ");
		sesion = servicioAutenticacionInterface.autenticarDistribuidor(nombre, password);

		switch (sesion) {
		case 1:
			Interfaz.imprime("Distribuidor " + nombre + " logueado correctamente");
			nuevoMenu();// Se genera un nuevo menu
			break;
		case 0:
			Interfaz.imprime("Usuario o contraseña no válidos");
			break;
		default:
			Interfaz.imprime("El distribuidor " + nombre + " ya se encuentra logueado");
		}
	}

	/**
	 * realiza la carga de los datos persistentes bindea los servicios una vez que
	 * la repo se ha autenticado muestra el menu y cuando se sale del menu elimina
	 * los servicios y elimina el registry cada repo levanta dos servicios con su id
	 * sesion
	 * 
	 * @throws Exception
	 */
	private void nuevoMenu() throws Exception {

		String mercanciaURL = mercancia;

		// arrancarRegistro(puertoMercancia);
		// cuidado con la linea siguiente
		// Utils.setCodeBase(ServicioVentasInterface.class);

		// ServicioVentasImpl servicioVentasImpl = new ServicioVentasImpl();
		// mercanciaURL = mercancia + sesion;// RMI
		// Naming.rebind(mercanciaURL, servicioVentasImpl);
		// System.out.println("Levantado Servicio ventas, Resultado: " +
		// Resultado.SUCCESSFUL);

		// // Levantar SrOperador en sroperador
		// ServicioClOperadorImpl objetoClOperador = new ServicioClOperadorImpl();
		// URLRegistro = cloperador + estado;// RMI
		// Naming.rebind(URLRegistro, objetoClOperador);
		// System.out.println("Operacion: Servicio Cliente Operador preparado con
		// exito");

		// listRegistry("rmi://" + direccionServicio + ":" + puertoServicio);//
		// mostramos los servicios colgados
		// menu una vez autenticado el servicio

		int opcion = 0;
		do {
			opcion = Interfaz.menu("Operaciones de Distribuidor", new String[] { "Introducir Oferta", "Quitar oferta",
					"Mostrar ventas", "Darse de baja en el sistema" });
			switch (opcion) {

			case 1:
				introducirOferta();
				break;
			case 2:
				quitarOferta();
				break;
			case 3:
				mostrarVentas();
				break;
			case 4:
				darDeBaja();
				break;

			}
		} while (opcion != 5);
		desconectar();// si pulsa salir aqui desconectar los servicios
		try {
			// eliminar Servidor-Operador
			System.out.println("Operacion: Servicio Servidor Operador cerrandose...");
			// URLRegistro = sroperador + estado;// RMI
			// Naming.unbind(URLRegistro);
			System.out.println("Operacion: Servicio Servidor Operador cerrado con exito");

			// eliminar Cliente-Operador
			System.out.println("Operacion: Servicio Cliente Operador cerrandose...");
			mercanciaURL = cloperador + sesion;// RMI;
			Naming.unbind(mercanciaURL);
			System.out.println("Operacion: Servicio Cliente Operador cerrado con exito");

			// cerrar rmiregistry del objeto registry unico
			if (estaVacioRegistry(cloperador)) { // RMI
				UnicastRemoteObject.unexportObject(registryServicio, true);// true aunque haya pendiente cosas, false
																			// solo sin pendientes
				System.out.println("Operacion: Registry cerrado con exito");
			} else
				System.out.println("Operacion: Registry todavia esta abierto porque quedan repositorios conectados");

		} catch (NoSuchObjectException e) {
			System.out.println("No se ha podido cerrar el registro, se ha forzado el cierre");
		}
		sesion = 0;

	}

	private void salir() {
		// TODO Auto-generated method stub

	}

	private void darDeBaja() {
		// TODO Auto-generated method stub

	}

	private void mostrarVentas() throws MalformedURLException, RemoteException, NotBoundException {

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		int resultado = servicioMercanciasInterface.listarVentasPorDistribuidor();
		switch (resultado) {
		case 0:
			System.out.println(
					"No se reconoce el tipo de Mercancía, por favor verifique que ha introducido alguno de los siguientes productos");
			break;
		case 1:
			System.out.println("Oferta registrada con éxito");
			break;
		default:
			break;
		}

	}

	/**
	 * registra una repo
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private void registrar() throws RemoteException, MalformedURLException, NotBoundException {
		String nombreDistribuidor = Interfaz.pideDato("Nombre: ");
		String passwordDistribuidor = Interfaz.pideDato("Contraseña: ");

		if (servicioAutenticacionInterface.registrarDistribuidor(nombreDistribuidor, passwordDistribuidor) != 0)
			System.out.println("Registro del Distribuidor. Resultado: " + Resultado.SUCCESSFUL.getResultado());
		else
			System.out.println("Registro del Distribuidor. Resultado: " + Resultado.FAIL.getResultado()
					+ "\nEste Distribuidor ya se encuentra en el sistema");

	}

	/**
	 * levanta el registry
	 * 
	 * @param numPuertoRMI
	 *            int el puerto de escucha
	 * @throws RemoteException
	 */
	private void arrancarRegistro(int numPuertoRMI) throws RemoteException {
		try {
			registryServicio = LocateRegistry.getRegistry(numPuertoRMI);
			registryServicio.list(); // Esta llamada lanza
			// una excepcion si el registro no existe
		} catch (RemoteException e) {
			// Registro no valido en este puerto
			System.out.println("El registro RMI no se puede localizar en el puerto " + numPuertoRMI);
			registryServicio = LocateRegistry.createRegistry(numPuertoRMI);
			System.out.println("Registro RMI creado en el puerto " + numPuertoRMI);
		}
	}

	/**
	 * desconecta una repo
	 * 
	 * @throws RemoteException
	 */
	private void desconectar() throws RemoteException {

		servicioAutenticacionInterface.desconectarDistribuidor(sesion);
		// miSesion=0;

		// Tambien hay que eliminar servicios

	}

	/**
	 * imprime la lista de ficheros de un cliente, pide el dato por teclado
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private void quitarOferta() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		// String idOferta = Interfaz.pideDato("Introduce el Id de la oferta que desea
		// eliminar: ");
		Map<String, Integer> listarOfertasPorSesion = servicioMercanciasInterface.getListarOfertasPorSesion();

		System.out.println("Ofertas del distribuidor:");

		for (Map.Entry<String, Integer> entry : listarOfertasPorSesion.entrySet()) {
			String nombre = entry.getKey();
			Integer idOferta = entry.getValue();

			System.out.println("Oferta: " + nombre + " -> " + idOferta);
		}

		String id = Interfaz.pideDato("Introduce el ID de la oferta que desea eliminar: ");

		int resultado = servicioMercanciasInterface.quitarOferta(Integer.parseInt(id));

		// int resultado = servicioMercanciasInterface.quitarOferta();
		switch (resultado) {
		case 0:
			System.out.println(
					"No se reconoce el tipo de Mercancía, por favor verifique que ha introducido alguno de los siguientes productos");
			break;
		case 1:
			System.out.println("Oferta eliminada correctamente");
			break;
		default:
			break;
		}

	}

	/**
	 * imprime la lista de carpetas, los id unico de los clientes que mantiene en la
	 * estructura logica listaCarpetas leer las carpetas es mas lento
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private void introducirOferta() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		String nombre = Interfaz.pideDato("Nombre de la oferta: ");
		String tipo = Interfaz.pideDato("Tipo: ");
		String precio = Interfaz.pideDato("Precio: ");
		String kilos = Interfaz.pideDato("Kilos: ");

		int resultado = servicioMercanciasInterface.registrarOferta(tipo, precio, kilos, nombre);
		switch (resultado) {
		case 0:
			System.out.println(
					"No se reconoce el tipo de Mercancía, por favor verifique que ha introducido alguno de los siguientes productos");
			break;
		case 1:
			System.out.println("Oferta registrada con éxito");
			break;
		default:
			break;
		}

	}

	/**
	 * lista los servicios que hay bindeados en la url
	 * 
	 * @param registryURL
	 *            la url de los servicios que queremos mostrar
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
		System.out.println("Registry " + registryURL + " contiene: ");
		String[] names = Naming.list(registryURL);
		for (int i = 0; i < names.length; i++) {
			System.out.println(names[i]);
		}
	}

	/**
	 * comprueba si esta vacia la lista de servicios de la url
	 * 
	 * @param URL
	 *            String la url a mostrar
	 * @return boolean true si esta vacia, false en caso contrario
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private static boolean estaVacioRegistry(String URL) throws RemoteException, MalformedURLException {
		String[] names = Naming.list(URL);
		if (names.length == 0)
			return true;
		else
			return false;
	}

}
