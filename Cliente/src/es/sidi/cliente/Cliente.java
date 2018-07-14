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
import java.rmi.registry.Registry;
import java.util.Map;

import es.sidi.common.Interfaz;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioMercanciasInterface;
import es.sidi.common.ServicioVentasInterface;

public class Cliente {

	private static int estado = 0;

	private static Registry registryServicio;
	private static ServicioAutenticacionInterface servicioAutenticacionInterface;
	private static String direccion = "localhost";

	private static String autenticador;
	private static String mercancias;
	private static String cliente;

	private static int puerto = 7791;

	/**
	 * este método se ejecuta al iniciar esta clase
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Leemos e iniciamos los servicios pertinentes
		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";
		mercancias = "rmi://" + direccion + ":" + puerto + "/mercancia";
		cliente = "rmi://" + direccion + ":" + puerto + "/cliente";

		new Cliente().iniciar();
	}

	/**
	 * inicia el registro de un cliente nuevo en el sistema, solicita los datos
	 * desde teclado
	 * 
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	private static void registrar() throws RemoteException, MalformedURLException, NotBoundException {
		String nombre = Interfaz.preguntaUsuario("Nombre: ");
		String password = Interfaz.preguntaUsuario("Contraseña: ");

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
				opcion = Interfaz.menu("Acceso de Cliente", new String[] { "Registrar un nuevo usuario",
						"Autenticarse en el sistema(hacer login)", "Salir" });

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
			String st = Interfaz.preguntaUsuario("Pulse enter para finalizar...");
		}

	}

	/**
	 * inicia la autenticacion de un cliente en el sistema, solicita datos y entra
	 * en un bucle de peticion de opciones con un menu
	 * 
	 * @throws Exception
	 */
	private void autenticar() throws Exception {

		String nombre = Interfaz.preguntaUsuario("Nombre: ");
		String password = Interfaz.preguntaUsuario("Password: ");
		estado = servicioAutenticacionInterface.autenticarCliente(nombre, password);
		switch (estado) {

		case 1:
			System.out.println("Cliente " + nombre + " logueado correctamente");
			nuevoMenu();
			break;
		case 0:
			System.out.println("Usuario o contraseña no válidos");
			break;
		default:
			System.out.println("El cliente " + nombre + " ya se encuentra logueado");
		}
	}

	/**
	 * Proporciona un menu mediante un bucle de espera
	 * 
	 * @throws Exception
	 */
	private void nuevoMenu() throws Exception {
		int opcion = 0;
		do {
			opcion = Interfaz.menu("Operaciones de Cliente", new String[] { "Introducir demanda", "Recibir ofertas",
					"Comprar mercancía", "Dar de baja", "Salir" });
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
			}
		} while (opcion != 5);
		System.exit(0);
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

	}

	/**
	 * comparte un fichero, pidiendo el id del fichero y el nombre a quien se
	 * compartira
	 * 
	 * @throws Exception
	 */
	private void darDeBaja() throws Exception {
		String autenticadorURL = autenticador;
		ServicioAutenticacionInterface servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming
				.lookup(autenticadorURL);

		int idSesioncliente = servicioAutenticacionInterface.getIdSesioncliente();

		String nombre = servicioAutenticacionInterface.darDeBajaCliente(idSesioncliente);

		if (nombre != null) {
			System.out.println("El cliente " + nombre + " ha sido dado de baja correctamente");
		} else {
			System.out.println("Ha ocurrido un error");

		}

		// Iniciamos el menú
		new Cliente().iniciar();
	}

	/**
	 * Método para comprar la mercancía, este método preguntará por el id de la
	 * demanda y el id de la oferta para asi registrar la compra
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
		try {
			System.out.println("Demandas:\n");

			for (Map.Entry<String, Integer> entry : servicioMercanciasInterface.getListaDemandas().entrySet()) {

				String nombre = entry.getKey();
				Integer id = entry.getValue();

				System.out.println(nombre + " -> " + id);
			}
			System.out.println("\n**********************************\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Listamos las ofertas para que el usuario pueda ver con claridad que oferta
		// quiere procesar, tenemos en cuenta que puede haber varias oferta del mismo
		// tipo, por tanto el cliente deberá elegir una, será necesario entonces mostrar
		// su id
		try {
			System.out.println("Ofertas:\n");

			for (Map.Entry<String, Integer> entry : servicioMercanciasInterface.getListaNombreOfertas().entrySet()) {
				String nombre = entry.getKey();
				Integer id = entry.getValue();

				System.out.println(nombre + " -> " + id);

			}
			System.out.println("\n**********************************\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String idDemanda = Interfaz.preguntaUsuario("Introduce el id de la demanda: ");
		String idOferta = Interfaz.preguntaUsuario("Introduce el id de la oferta: ");

		// Sacamos el id del cliente para procesar la demanda
		int idSesionCliente = servicioAutenticacionInterface.getIdSesioncliente();

		String ventasURL = cliente;// RMI
		ServicioVentasInterface servicioVentasInterface = (ServicioVentasInterface) Naming.lookup(ventasURL);

		int resultado = servicioVentasInterface.comprarMercancia(Integer.parseInt(idDemanda),
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
	 * Método para recibir las ofertas, simplemente llama al servicio y lista las
	 * ofertas disponibles
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void recibirOfertas() throws MalformedURLException, RemoteException, NotBoundException {
		String URLRegistro = mercancias;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		Map<String, Integer> listaNombreOfertas = servicioMercanciasInterface.getListaNombreOfertas();
		if (!listaNombreOfertas.isEmpty()) {
			try {
				System.out.println("********OFERTAS DISPONIBLES*********\n");

				for (Map.Entry<String, Integer> entry : listaNombreOfertas.entrySet()) {
					String nombre = entry.getKey();
					Integer id = entry.getValue();

					String tipo = null;
					for (Map.Entry<Integer, String> tipoMercancia : servicioMercanciasInterface.getTipoMercanciaOferta()
							.entrySet()) {
						if (tipoMercancia.getKey().equals(id)) {
							tipo = tipoMercancia.getValue();
						}
					}

					float kilos = 0;
					for (Map.Entry<Integer, Float> kilosMercancia : servicioMercanciasInterface
							.getKilosMercanciaOferta().entrySet()) {
						if (kilosMercancia.getKey().equals(id)) {
							kilos = Float.parseFloat(kilosMercancia.getValue().toString());
						}
					}

					System.out.println(nombre + ": " + "\n" + "\tTipo mercancia -> " + tipo + "\n\tKilos ofertados: "
							+ kilos + "\n\tId -> " + id);

				}
				System.out.println("\n**********************************\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("\nNo hay ofertas disponibles");

		}

	}

	/**
	 * Método para introducir la demanda, preguntará por un nombre para la demanda,
	 * el tipo de mercancía y los kilos que se quieren pedir
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void introducirDemanda() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancias;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		String nombre = Interfaz.preguntaUsuario("Nombre demanda: ");
		String tipo = Interfaz.preguntaUsuario("Tipo de mercancía: ");
		String kilos = Interfaz.preguntaUsuario("Kilos que se desean: ");

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
