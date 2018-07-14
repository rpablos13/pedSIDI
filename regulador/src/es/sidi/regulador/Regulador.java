/**
 * Funciones del Regulador (Servidor):

 * 
 * 1. Listar ofertas actuales
 * 2. Listar demandas actuales
 * 3. Listar clientes
 * 4. Listar distribuidores
 * 5. Salir
 * 
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import es.sidi.common.Interfaz;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioVentasInterface;
import es.sidi.common.Utils;

public class Regulador {

	private static int puerto = 7791;
	private static Registry registry;
	private static String direccion = "localhost";
	private static ServicioAutenticacionImpl servicioAutenticacionImpl;
	private static ServicioMercanciasImpl servicioMercanciasImpl;
	private static ServicioVentasInterface servicioVentasInterface;
	private static String url;

	/**
	 * Clase main principal
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Iniciamos los servicios
		iniciarServidor();

		// menu
		int opcion = 0;
		do {
			opcion = Interfaz.menu("Servidor", new String[] { "Listar ofertas actuales", "Listar demandas actuales",
					"Listar clientes", "Listar distribuidores", "Salir" });
			switch (opcion) {
			case 1:
				servicioMercanciasImpl.listarOfertas();
				break;
			case 2:
				servicioMercanciasImpl.listarDemandas();
				break;
			case 3:
				String ventasURL = "rmi://" + direccion + ":" + puerto + "/cliente";
				servicioVentasInterface = (ServicioVentasInterface) Naming.lookup(ventasURL);

				if (!servicioVentasInterface.getClientesRegistrados().isEmpty()) {

					try {
						System.out.println("********LISTA CLIENTES*********\n");

						for (Map.Entry<Integer, String> entry : servicioVentasInterface.getClientesRegistrados()
								.entrySet()) {
							Integer id = entry.getKey();
							String nombre = entry.getValue();

							System.out.println(nombre + " -> " + id);

						}
						System.out.println("\n*******************************\n");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("No se encuentran clientes registrados actualmente");

				}
				break;
			case 4:
				servicioMercanciasImpl.listarDistribuidores();
				break;
			}
		} while (opcion != 5);

		// NO es lo correcto, hay que cerrar los servicios primero, queda pendiente
		System.exit(0);
	}

	/**
	 * Inicia los servicios
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public static void iniciarServidor() throws RemoteException, MalformedURLException, NotBoundException {
		arrancarRegistro(puerto);

		Utils.setCodeBase(ServicioAutenticacionInterface.class);

		servicioAutenticacionImpl = new ServicioAutenticacionImpl();
		url = "rmi://" + direccion + ":" + puerto + "/autenticador";
		Naming.rebind(url, servicioAutenticacionImpl);
		System.out.println("Servicio Autentificador levantado con éxito");

		servicioMercanciasImpl = new ServicioMercanciasImpl();
		url = "rmi://" + direccion + ":" + puerto + "/mercancia";
		Naming.rebind(url, servicioMercanciasImpl);
		System.out.println("Servicio Mercancias levantado con éxito");

	}

	// Arrancamos el registro
	private static void arrancarRegistro(int puertoRmi) throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry(puertoRmi);
			registry.list();

		} catch (RemoteException e) {
			System.out.println("El registro RMI no se encuentar en el puerto " + puertoRmi);
			registry = LocateRegistry.createRegistry(puertoRmi);
			System.out.println("Registro RMI creado en el puerto " + puertoRmi);
		}
	}

}