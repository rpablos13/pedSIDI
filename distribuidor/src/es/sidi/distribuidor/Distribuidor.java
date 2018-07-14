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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import es.sidi.common.Interfaz;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioMercanciasInterface;

public class Distribuidor {

	// URL rmi
	private static String autenticador;
	private static String mercancia;
	private static String cliente;

	private static int sesion = 0;
	private static int puerto = 7791;
	private static ServicioAutenticacionInterface servicioAutenticacionInterface;
	private static String direccion = "localhost";

	private static Registry registryServicio;

	public static void main(String[] args) throws Exception {

		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";
		mercancia = "rmi://" + direccion + ":" + puerto + "/mercancia";
		cliente = "rmi://" + direccion + ":" + puerto + "/cliente";

		ServicioVentasImpl servicioVentasImpl = new ServicioVentasImpl();
		Naming.rebind(cliente, servicioVentasImpl);
		System.out.println("Servicio Ventas levantado con éxito");

		new Distribuidor().iniciar();
	}

	private Integer idSesionCliente;

	private String nombre;

	private Integer idOferta;

	private int idNuevaOferta;

	/**
	 * inicia el menú
	 * 
	 * @throws Exception
	 */
	private void iniciar() throws Exception {

		// Arrancamos registry
		arrancarRegistro(puerto);

		try {
			String autenticadorURL = autenticador;
			servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming.lookup(autenticadorURL);

			int opcion = 0;
			do {
				opcion = Interfaz.menu("Menu Distribuidor", new String[] { "Registrar un nuevo usuario",
						"Autenticar en el sistema (hacer login)", "Salir" });
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
			System.out.println("Conectando con el servidor " + Resultado.FAIL.getResultado());
			String st = Interfaz.preguntaUsuario("Pulse enter para finalizar...");
			System.exit(0);
		}
	}

	/**
	 * Autenticar Distribuidor
	 * 
	 * @throws Exception
	 */
	private void autenticar() throws Exception {
		String nombre = Interfaz.preguntaUsuario("Nombre: ");
		String password = Interfaz.preguntaUsuario("Password: ");
		sesion = servicioAutenticacionInterface.autenticarDistribuidor(nombre, password);

		switch (sesion) {
		case 1:
			System.out.println("Distribuidor " + nombre + " logueado correctamente");
			nuevoMenu();// Se genera un nuevo menu
			break;
		case 0:
			System.out.println("Usuario o contraseña no válidos");
			break;
		default:
			System.out.println("El distribuidor " + nombre + " ya se encuentra logueado");
		}
	}

	/**
	 * Carga del nuevo menú con las funcionalidades pedidas
	 * 
	 * @throws Exception
	 */
	private void nuevoMenu() throws Exception {

		int opcion = 0;
		do {
			opcion = Interfaz.menu("Operaciones de Distribuidor", new String[] { "Introducir Oferta", "Quitar oferta",
					"Mostrar ventas", "Darse de baja en el sistema", "Salir" });
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
		System.exit(0);

	}

	/**
	 * Dar de baja, simplemente elimina el registro de la lista
	 * 
	 * @throws Exception
	 */
	private void darDeBaja() throws Exception {
		String autenticadorURL = autenticador;
		ServicioAutenticacionInterface servicioAutenticacionInterface = (ServicioAutenticacionInterface) Naming
				.lookup(autenticadorURL);

		int idSesionDistribuidor = servicioAutenticacionInterface.getIdSesionDistribuidor();

		String nombre = servicioAutenticacionInterface.darDeBajaDistribuidor(idSesionDistribuidor);

		if (nombre != null) {
			System.out.println("El distribuidor " + nombre + " ha sido dado de baja correctamente");
			iniciar();
		} else {
			System.out.println("Ha ocurrido un error");
		}

	}

	/**
	 * Muestra las ventas que se han realizado por distribuidor
	 * 
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void mostrarVentas() throws MalformedURLException, RemoteException, NotBoundException {

		Integer idNuevaDemanda = null;
		Integer idNuevaCompra = null;
		float dineroTotalRecaudado = 0;

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		if (!servicioMercanciasInterface.getListaCompras().isEmpty()) {

			try {
				System.out.println(
						"==================================\n********VENTAS REALIZADAS*********\n==================================\n");

				for (Map.Entry<Integer, Integer> listaVentas : servicioMercanciasInterface.getListaCompras()
						.entrySet()) {

					idNuevaCompra = listaVentas.getKey();
					idNuevaDemanda = listaVentas.getValue();

					for (Map.Entry<Integer, Integer> relacionClienteCompra : servicioMercanciasInterface
							.getListaCompraSesionCliente().entrySet()) {

						if (relacionClienteCompra.getKey().equals(idNuevaCompra))
							idSesionCliente = relacionClienteCompra.getValue();

					}

					// NOMBRE DEL CLIENTE
					String nombreCliente = null;
					for (Map.Entry<Integer, String> nombreClientes : servicioMercanciasInterface.getMapNombresClientes()
							.entrySet()) {
						if (nombreClientes.getKey().equals(idSesionCliente)) {
							nombreCliente = nombreClientes.getValue();
						}
					}

					// NOMBRE DISTRIBUIDOR
					String nombreDistribuidor = null;
					for (Map.Entry<Integer, String> nombreDistribuidores : servicioMercanciasInterface
							.getDistribuidoresAutenticados().entrySet()) {
						if (nombreDistribuidores.getKey()
								.equals(servicioMercanciasInterface.getIdSesionDistribuidor())) {
							nombreDistribuidor = nombreDistribuidores.getValue();
						}
					}

					// TIPO DE PRODUCTO COMPRADO
					for (Map.Entry<Integer, Integer> tipoOferta : servicioMercanciasInterface.getListaOfertasCompra()
							.entrySet()) {
						if (tipoOferta.getKey().equals(idNuevaCompra)) {
							idNuevaOferta = Integer.parseInt(tipoOferta.getValue().toString());
						}

					}

					String tipo = null;
					for (Map.Entry<Integer, String> tipoProductos : servicioMercanciasInterface.getTipoMercanciaOferta()
							.entrySet()) {
						if (tipoProductos.getKey().equals(idNuevaOferta)) {
							tipo = tipoProductos.getValue();
						}
					}

					// KILOS DEMANDADOS
					Float kilos = null;
					for (Map.Entry<Integer, Float> kilosDemandados : servicioMercanciasInterface
							.getKilosMercanciaDemanda().entrySet()) {
						if (kilosDemandados.getKey().equals(idNuevaDemanda)) {
							kilos = kilosDemandados.getValue();
						}
					}

					// NOMBRE OFERTA
					String nombreOferta = null;
					for (Map.Entry<String, Integer> nombreOfertas : servicioMercanciasInterface.getListaNombreOfertas()
							.entrySet()) {
						if (nombreOfertas.getValue().equals(idNuevaOferta)) {
							nombreOferta = nombreOfertas.getKey();
						}
					}

					// PRECIO OFERTA
					Float precioOferta = null;
					for (Map.Entry<Integer, Float> precioOfertas : servicioMercanciasInterface
							.getPrecioMercanciaOferta().entrySet()) {
						if (precioOfertas.getKey().equals(idNuevaOferta)) {
							precioOferta = precioOfertas.getValue();
						}
					}

					float dineroReaudado = precioOferta * kilos;
					dineroTotalRecaudado += dineroReaudado;

					System.out.println("Cliente: " + nombreCliente + ": " + "\n" + "\tTipo mercancia -> " + tipo
							+ "\n\tKilos demandados: " + kilos + "\n\tNombre del Distribuidor: " + nombreDistribuidor
							+ "\n\tOferta realizada: " + nombreOferta + " (" + precioOferta + " €/Kg" + ")"
							+ "\n\tDinero recaudado: " + dineroReaudado + "€");
					System.out.println("\n********************************\n");
				}

				System.out.println("Dinero Total Recaudado: " + dineroTotalRecaudado + "€");

				System.out.println(
						"\n==================================\n**********************************\n==================================\n");

			} catch (

			Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No se ha registrado ninguna venta aún");
		}

	}

	/**
	 * Registra un distribuidor
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void registrar() throws RemoteException, MalformedURLException, NotBoundException {
		String nombreDistribuidor = Interfaz.preguntaUsuario("Nombre: ");
		String passwordDistribuidor = Interfaz.preguntaUsuario("Contraseña: ");

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
			registryServicio.list();
		} catch (RemoteException e) {
			System.out.println("El registro RMI no se puede localizar en el puerto " + numPuertoRMI);
			registryServicio = LocateRegistry.createRegistry(numPuertoRMI);
			System.out.println("Registro RMI creado en el puerto " + numPuertoRMI);
		}
	}

	/**
	 * Quitar oferta
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void quitarOferta() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		Map<String, Integer> listarOfertasPorSesion = servicioMercanciasInterface.getListarOfertasPorSesion();

		for (Map.Entry<String, Integer> entry : listarOfertasPorSesion.entrySet()) {
			nombre = entry.getKey();
			idOferta = entry.getValue();
		}

		if (!listarOfertasPorSesion.isEmpty()) {

			System.out.println("Ofertas del distribuidor:");
			System.out.println("Oferta: " + nombre + " -> " + idOferta);

			String id = Interfaz.preguntaUsuario("Introduce el ID de la oferta que desea eliminar: ");

			int resultado = servicioMercanciasInterface.quitarOferta(Integer.parseInt(id));

			switch (resultado) {
			case 0:
				System.out.println(
						"No se reconoce el tipo de Mercancía, por favor verifique que ha introducido alguno de los siguientes productos");
				// TODO: Listar productos
				break;
			case 1:
				System.out.println("Oferta eliminada correctamente");
				break;
			default:
				break;
			}
		} else {
			System.out.println("No hay ofertas por el momento");
		}

	}

	/**
	 * Inroducir oferta
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	private void introducirOferta() throws RemoteException, MalformedURLException, NotBoundException {

		String URLRegistro = mercancia;// RMI
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(URLRegistro);

		String nombre = Interfaz.preguntaUsuario("Nombre de la oferta: ");
		String tipo = Interfaz.preguntaUsuario("Tipo: ");
		String precio = Interfaz.preguntaUsuario("Precio: ");
		String kilos = Interfaz.preguntaUsuario("Kilos: ");

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

}
