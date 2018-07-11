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

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import es.sidi.common.Interfaz;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioSrOperadorInterface;
import es.sidi.common.Utils;

public class Distribuidor {

	// lista de carpeta que se van a ir creando en la repo, hace falta agregar
	// persistencia
	// la visibilidad es ladel paquete
	public static List<String> listaCarpetas;// = new ArrayList<String>();

	// atributos para buscar el servicio de autentificacion del servidor
	private static int estado = 0;
	private static int puerto = 7791;
	private static ServicioAutenticacionInterface servicioAutenticacionInterface;
	private static String direccion = "localhost";

	// aqui se van a guardar las URL rmi
	private static String autenticador;
	private static String sroperador;
	private static String cloperador;

	// atributos para levantar los servicios Servidor-Operador y Cliente-Operador
	private static int puertoServicio = 7792;
	private static Registry registryServicio;
	private static String direccionServicio = "localhost";

	private static String nombreRepo;
	// main

	public static void main(String[] args) throws Exception {

		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";
		sroperador = "rmi://" + direccionServicio + ":" + puertoServicio + "/sroperador/";
		cloperador = "rmi://" + direccionServicio + ":" + puertoServicio + "/cloperador/";

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
			String URLRegistro = autenticador;
			servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming.lookup(URLRegistro);

			// mostramos el menu
			int opcion = 0;
			do {
				opcion = Interfaz.menu("Acceso a Repositorio",
						new String[] { "Registrar nuevo Repositorio", "Autenticar Repositorio (login)" });
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
		estado = servicioAutenticacionInterface.autenticarDistribuidor(nombre, password);
		switch (estado) {

		case 1:
			Interfaz.imprime("Distribuidor " + nombre + " logueado correctamente");
			System.out.println("El path de la repo es: " + System.getProperty("user.dir"));
			nombreRepo = nombre;// memorizamos el nombre de la repo autenticada
			// levantarServicios();// levanta los servicios Servidor-Operador y
			// Cliente-Operador
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
	private void levantarServicios() throws Exception {

		String URLRegistro;

		arrancarRegistro(puertoServicio);
		// cuidado con la linea siguiente
		Utils.setCodeBase(ServicioSrOperadorInterface.class);

		// Levantar SrOperador en sroperador
		ServicioSrOperadorImpl objetoSrOperador = new ServicioSrOperadorImpl();
		URLRegistro = sroperador + estado;// RMI
		Naming.rebind(URLRegistro, objetoSrOperador);
		System.out.println("Operacion: Servicio Servidor Operador preparado con exito");

		// Levantar SrOperador en sroperador
		ServicioClOperadorImpl objetoClOperador = new ServicioClOperadorImpl();
		URLRegistro = cloperador + estado;// RMI
		Naming.rebind(URLRegistro, objetoClOperador);
		System.out.println("Operacion: Servicio Cliente Operador preparado con exito");

		listRegistry("rmi://" + direccionServicio + ":" + puertoServicio);// mostramos los servicios colgados
		// menu una vez autenticado el servicio
		int opcion = 0;
		do {
			opcion = Interfaz.menu("Operaciones de Distribuidor",
					new String[] { "Listar clientes", "Listar ficheros de clientes" });
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
			case 5:
				salir();
				break;

			}
		} while (opcion != 6);
		desconectar();// si pulsa salir aqui desconectar los servicios
		try {
			// eliminar Servidor-Operador
			System.out.println("Operacion: Servicio Servidor Operador cerrandose...");
			URLRegistro = sroperador + estado;// RMI
			Naming.unbind(URLRegistro);
			System.out.println("Operacion: Servicio Servidor Operador cerrado con exito");

			// eliminar Cliente-Operador
			System.out.println("Operacion: Servicio Cliente Operador cerrandose...");
			URLRegistro = cloperador + estado;// RMI;
			Naming.unbind(URLRegistro);
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
		estado = 0;

	}

	private void salir() {
		// TODO Auto-generated method stub

	}

	private void darDeBaja() {
		// TODO Auto-generated method stub

	}

	private void mostrarVentas() {
		// TODO Auto-generated method stub

	}

	/**
	 * registra una repo
	 * 
	 * @throws RemoteException
	 */
	private void registrar() throws RemoteException {
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

		servicioAutenticacionInterface.desconectarDistribuidor(estado);
		// miSesion=0;

		// Tambien hay que eliminar servicios

	}

	/**
	 * imprime la lista de ficheros de un cliente, pide el dato por teclado
	 */
	private void quitarOferta() {
		if (listaCarpetas.isEmpty()) {
			System.out.println("No hay nada que montrar de momento");

		} else {
			introducirOferta();
			String carpeta = Interfaz.pideDato("Introduzca nombre carpeta del idCliente que quiera explorar");
			File dir = new File(carpeta);
			if (dir.exists()) {
				String[] ficheros = dir.list();
				for (String s : ficheros)
					System.out.println(s);
			} else {
				System.out.println("La carpeta no existe");
			}
		}
	}

	/**
	 * imprime la lista de carpetas, los id unico de los clientes que mantiene en la
	 * estructura logica listaCarpetas leer las carpetas es mas lento
	 */
	private void introducirOferta() {
		System.out.println(listaCarpetas);

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
