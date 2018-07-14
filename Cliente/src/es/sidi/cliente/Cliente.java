/**
 * Funciones del Cliente:


 * 
 * 1. Introducir demanda
 * 2. Recibir ofertas
 * 3. Comprar mercancía
 * 4. Darse de baja en el sitema
 * 5. Salir 
 * 
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.cliente;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import es.sidi.common.Interfaz;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioClienteInterface;
import es.sidi.common.ServicioGestorInterface;
import es.sidi.common.ServicioMercanciasInterface;
import es.sidi.common.ServicioVentasInterface;
import es.sidi.common.Utils;

public class Cliente {

	private static int estado = 0;

	// atributos para buscar los servicios del servidor
	private static int puertoAutenticador = 7791;
	private static Registry registryServicio;
	private static ServicioAutenticacionInterface servicioAutenticacionInterface;
	private static String direccion = "localhost";

	// aqui se van a guardar las URL rmi
	private static String autenticador;
	private static String mercancias;
	private static String cliente;

	private static String discocliente;

	@SuppressWarnings("unused")
	private static String nombre = ""; // el nombre del cliente de autenticado

	// atributos para levantar el servicio DiscoCliente
	// es de suponer que esto es necesario ya que puede ejecutarse
	// en otra maquina tendriamos que saber la ip nuestra
	// y cambiarla por la direccionServicio
	private static int puertoMercancias = 7791;
	private static ServicioMercanciasInterface servicioMercanciasInterface;

	private static int puertoCliente = 7791;
	private static ServicioClienteInterface servicioClienteInterface;

	/**
	 * main del cliente, generas las URL usadas en el programa
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// vamos a crear aqui las direcciones, que por supuesto podriamos leerlas por
		// teclado, desde un fichero de configuracion
		// o bien pasarselas al jar
		autenticador = "rmi://" + direccion + ":" + puertoAutenticador + "/autenticador";
		mercancias = "rmi://" + direccion + ":" + puertoMercancias + "/mercancia";
		cliente = "rmi://" + direccion + ":" + puertoCliente + "/cliente";

		new Cliente().iniciar();
		System.exit(0);// fin del programa,return o nada deja abierto el programa
	}

	/**
	 * inicia el registro de un cliente nuevo en el sistema, solicita los datos
	 * desde teclado
	 * 
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private static void registrar() throws RemoteException, MalformedURLException, NotBoundException {
		String nombre = Interfaz.pideDato("Nombre: ");
		String password = Interfaz.pideDato("Contraseña: ");

		if (servicioAutenticacionInterface.registrarCliente(nombre, password) != 0)
			System.out.println("Registro del Cliente. Resultado: " + Resultado.SUCCESSFUL.getResultado());
		else
			System.out.println("Registro del Cliente. Resultado: " + Resultado.FAIL.getResultado()
					+ "\nEste Cliente ya se encuentra en el sistema");

	}

	/**
	 * conecta con el utenticador y muestra el menu principal
	 * 
	 * @throws Exception
	 */
	private void iniciar() throws Exception {

		// buscamos el objeto en el servidor cliente para autenticarnos
		String URLRegistro = autenticador;// RMI

		// si el servidor no esta disponible, cerramos informando de ello
		try {
			servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming.lookup(URLRegistro);

			// mostramos menu de acceso
			int opcion = 0;
			do {
				opcion = Interfaz.menu("Acceso de Cliente",
						new String[] { "Registrar un nuevo usuario", "Autenticarse en el sistema(hacer login)" });

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
	 * inicia la autenticacion de un cliente en el sistema, solicita datos y entra
	 * en un bucle de peticion de opciones con un menu
	 * 
	 * @throws Exception
	 */
	private void autenticar() throws Exception {

		String nombre = Interfaz.pideDato("Nombre: ");
		String password = Interfaz.pideDato("Password: ");
		estado = servicioAutenticacionInterface.autenticarCliente(nombre, password);
		switch (estado) {

		case 1:
			Interfaz.imprime("Cliente " + nombre + " logueado correctamente");
			nuevoMenuClientes();
			break;
		case 0:
			Interfaz.imprime("Usuario o contraseña no válidos");
			break;
		default:
			Interfaz.imprime("El cliente " + nombre + " ya se encuentra logueado");
		}
	}

	/**
	 * solicita la desconexion
	 * 
	 * @throws RemoteException
	 */
	private void desconectar() throws RemoteException {
		servicioAutenticacionInterface.desconectarCliente(estado);
		// si ponemos a 0 la sesion aqui el unbind fallara
		// miSesion=0;
	}

	/**
	 * levanta el Registry y el servicio Discocliente con el id sesion actual
	 * muestra los servicios colgados (los discocliente de todos los clientes) tiene
	 * que cerrar tambien los servicios cuando se sale del menu de Servicio
	 * 
	 * @throws Exception
	 */
	private void nuevoMenuClientes() throws Exception {
		String URLRegistro = cliente;
		arrancarRegistro(puertoCliente);
		// cuidado con la linea siguiente
		Utils.setCodeBase(ServicioVentasInterface.class);

		menuServicio();

		// // eliminar Servidor-Operador
		// System.out.println("Operacion: Servicio Disco Cliente cerrandose...");
		// try {
		// URLRegistro = discocliente + estado;// RMI
		// Naming.unbind(URLRegistro);
		// System.out.println("Operacion: Servicio Disco Cliente cerrado con exito");
		//
		// // cerrar rmiregistry del objeto registry unico
		// if (estaVacioRegistry(discocliente)) { // RMI
		// UnicastRemoteObject.unexportObject(registryServicio, true);
		// System.out.println("Operacion: Registry cerrado con exito");
		// } else
		// System.out.println("Operacion: Registry todavia esta abierto porque quedan
		// clientes conectados");
		//
		// } catch (NoSuchObjectException e) {
		// System.out.println("No se ha podido cerrar el registro, se ha forzado el
		// cierre");
		// }
		// estado = 0;

	}

	/**
	 * Proporciona un menu mediante un bucle de espera
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void menuServicio() throws RemoteException, MalformedURLException, NotBoundException {
		int opcion = 0;
		do {
			opcion = Interfaz.menu("Operaciones de Cliente",
					new String[] { "Introducir demanda", "Recibir ofertas", "Comprar mercancía", "Dar de baja" });
			switch (opcion) {

			case 1:
				introducirDemanda();
				break;
			case 2:
				recibirOfertas();
				break;
			case 3:
				comprarMercancia();
				break;
			case 4:
				darDeBaja();
				break;
			case 5:
				salir();
				break;
			}
		} while (opcion != 6);

		desconectar();
	}

	/**
	 * Muestra por pantalla la lista de los clientes registrados y cuales de ellos
	 * estan online
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void listarClientes() throws MalformedURLException, RemoteException, NotBoundException {
		String URLRegistro = cliente;// RMI
		ServicioGestorInterface servicioGestor = (ServicioGestorInterface) Naming.lookup(URLRegistro);

		String lista = servicioGestor.listarClientes();
		System.out.println(lista);

	}

	/**
	 * Muestra los ficheros(compartidos incluidos) que tiene este cliente
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void salir() throws MalformedURLException, RemoteException, NotBoundException {

		String URLRegistro = cliente;// RMI
		ServicioGestorInterface servicioGestor = (ServicioGestorInterface) Naming.lookup(URLRegistro);

		// el servicio Gestor solo necesita el id de la sesion del cliente para saber
		// quien es
		List<String> lista = servicioGestor.listarFicherosCliente(estado);
		System.out.println(lista);

	}

	/**
	 * comparte un fichero, pidiendo el id del fichero y el nombre a quien se
	 * compartira
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void darDeBaja() throws MalformedURLException, RemoteException, NotBoundException {
		salir();
		String s = Interfaz
				.pideDato("Introduzca el IDENTIFICADOR del fichero, p.e. X.- nombre.Fichero, el identificador es X");
		int idFichero = Integer.parseInt(s);

		listarClientes();
		String nombreCliente = Interfaz.pideDato("Introduzca nombre del cliente");

		String URLRegistro = cliente; // RMI
		ServicioGestorInterface servicioGestor = (ServicioGestorInterface) Naming.lookup(URLRegistro);

		boolean compartido = servicioGestor.compartirFichero(idFichero, nombreCliente, estado);

		if (compartido)
			System.out.println("El fichero se ha compartido correctamente");
		else
			System.out.println("El fichero no se ha podido compartir");
	}

	/**
	 * borrar un fichero, pide el borrado al servicio Gestor quien devuelve la url
	 * del servicio del respositorio
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void comprarMercancia() throws MalformedURLException, RemoteException, NotBoundException {

		String URLRegistro = mercancias;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		// Listamos las demandas para que el usuario pueda ver con claridad que demanda
		// quiere procesar
		servicioMercanciasInterface.listarDemandas();

		// Listamos las ofertas para que el usuario pueda ver con claridad que oferta
		// quiere procesar, tenemos en cuenta que puede haber varias oferta del mismo
		// tipo, por tanto el cliente deberá elegir una, será necesario entonces mostrar
		// su id
		servicioMercanciasInterface.listarOfertas();

		String idDemanda = Interfaz.pideDato("Introduce el id de la demanda: ");
		String idOferta = Interfaz.pideDato("Introduce el id de la oferta: ");

		// Sacamos el id del cliente para procesar la demanda
		int idSesionCliente = servicioAutenticacionInterface.getIdSesioncliente();

		int resultado = servicioMercanciasInterface.comprarMercancia(Integer.parseInt(idDemanda),
				Integer.parseInt(idOferta), idSesionCliente);

		switch (resultado) {
		case 0:
			System.out.println("No hay suficiente cantidad de productos en la oferta, estas son las ofertas:");
			servicioMercanciasInterface.listarOfertas();
			break;
		case 1:
			System.out.println("Compra realizada con éxito");
			break;
		default:
			System.out.println("Ha ocurrido un error desconocido");
			break;
		}

	}

	/**
	 * solicita la descarga de un fichero pidiendo el id de fichero(compartidos
	 * incluidos)
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void recibirOfertas() throws MalformedURLException, RemoteException, NotBoundException {
		String URLRegistro = mercancias;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		servicioMercanciasInterface.listarOfertas();

	}

	/**
	 * solicita la subida de un fichero a su repo
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */

	/**
	 * Se usa para levantar el servicio del Disco Cliente
	 * 
	 * @param numPuertoRMI
	 *            numero de puerto del servicio DiscoCliente
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
	 * lista los servicios de un puerto
	 * 
	 * @param registryURL
	 *            la direccion del puerto a comprobar
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
	 * comprueba si tiene colgados servicios en el la URL
	 * 
	 * @param URL
	 * @return true si no hay servicios en la URL
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

	private void introducirDemanda() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancias;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		String nombre = Interfaz.pideDato("Nombre demanda: ");
		String tipo = Interfaz.pideDato("Tipo de mercancía: ");
		String kilos = Interfaz.pideDato("Kilos que se desean: ");

		// Sacamos el id del cliente para registrar la demanda
		int idSesionCliente = servicioAutenticacionInterface.getIdSesioncliente();

		int resultado = servicioMercanciasInterface.registrarDemanda(tipo, kilos, nombre, idSesionCliente);

		switch (resultado) {
		case 0:
			System.out.println(
					"No se reconoce el tipo de Mercancía, por favor verifique que ha introducido alguno de los siguientes productos");
			// TODO: Listar tipos de productos
			break;
		case 1:
			System.out.println("Demanda registrada  con éxito");
			break;
		default:
			break;
		}

	}

}
