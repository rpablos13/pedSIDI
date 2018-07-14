/**
 * Clase que autentica a los diferentes usuarios, tanto distribuidores como clientes, tambi�n se encarga de las bajas
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import es.sidi.common.RandomSessionNumber;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioMercanciasInterface;
import es.sidi.common.ServicioVentasInterface;

public class ServicioAutenticacionImpl extends UnicastRemoteObject implements ServicioAutenticacionInterface {

	private static final long serialVersionUID = 1L;

	private int puerto = 7791;
	private int sesionDistribuidor;
	private int sesionCliente;

	/**
	 * Constructor por defecto
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	protected ServicioAutenticacionImpl() throws RemoteException, MalformedURLException, NotBoundException {
		super();
	}

	/**
	 * M�todo para autenticar el cliente
	 * 
	 * @param nombre
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public int autenticarCliente(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesionAleatoria = getSesion();

		sesionCliente = sesionAleatoria;

		String clienteURL = "rmi://localhost:" + puerto + "/cliente";
		ServicioVentasInterface servicioClienteInterface = (ServicioVentasInterface) Naming.lookup(clienteURL);

		int id = servicioClienteInterface.autenticarCliente(nombre, sesionCliente, password);
		switch (id) {
		case 1:
			System.out.println("Cliente " + nombre + " logueado correctamente");
			break;
		case 0:
			System.out.println("Usuario o contrase�a no v�lidos");
			break;
		default:
			System.out.println("El Cliente " + nombre + " ya se encuentra logueado");
			break;
		}
		return id;
	}

	/**
	 * M�todo para registar un cliente
	 * 
	 * @param nombre
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public int registrarCliente(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesion = getSesion();

		String clienteURL = "rmi://localhost:" + puerto + "/cliente";
		ServicioVentasInterface servicioClienteInterface = (ServicioVentasInterface) Naming.lookup(clienteURL);

		int id = servicioClienteInterface.registrarCliente(nombre, sesion, password);
		if (id != 0)
			System.out.println("El usuario " + nombre + " se ha registrado Correctamente");
		else
			System.out.println("Ya existe el usuario " + nombre + " en el sistema");
		return id;
	}

	/**
	 * autentica un repositorio
	 * 
	 * @param String
	 *            el nombre del repositorio
	 * @return int el id sesion de la repo
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	@Override
	public int autenticarDistribuidor(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		sesionDistribuidor = getSesion();

		String mercanciasURL = "rmi://localhost:" + puerto + "/mercancia";
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(mercanciasURL);

		int idSesion = servicioMercanciasInterface.autenticarDistribuidor(nombre, sesionDistribuidor, password);

		switch (idSesion) {
		case 1:
			System.out.println("Distribuidor " + nombre + " logueado correctamente");
			break;
		case 0:
			System.out.println("Usuario o contrase�a no v�lidos");
			break;
		default:
			System.out.println("El distribuidor " + nombre + " ya se encuentra logueado");
			break;
		}
		return idSesion;
	}

	/**
	 * M�todo para regisrar un distribuidor
	 * 
	 * @param nombre
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public int registrarDistribuidor(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesion = getSesion();

		String mercanciasURL = "rmi://localhost:" + puerto + "/mercancia";
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(mercanciasURL);

		int id = servicioMercanciasInterface.registrarDistribuidor(nombre, sesion, password);
		if (id != 0)
			System.out.println("El usuario " + nombre + " se ha registrado Correctamente");
		else
			System.out.println("Ya existe el usuario " + nombre + " en el sistema");
		return id;
	}

	/**
	 * Devuelve el un n�mero aleatorio para la sesi�n
	 * 
	 * @return
	 */
	public int getSesion() {
		return RandomSessionNumber.generateSessionId();
	}

	/**
	 * Simplemente llama a la sesi�n del cliente para saber cual es
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public int getIdSesioncliente() throws RemoteException {
		return sesionCliente;
	}

	/**
	 * M�todo para preguntar por el id de sesi�n de un distribuidor
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public int getIdSesionDistribuidor() throws RemoteException {
		return sesionDistribuidor;
	}

	/**
	 * M�todo para dar de baja a un distribuidor
	 * 
	 * @param sesionDistribuidor
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public String darDeBajaDistribuidor(int sesionDistribuidor)
			throws RemoteException, MalformedURLException, NotBoundException {

		String distribuidorUrl = "rmi://localhost:" + puerto + "/mercancia";
		ServicioMercanciasInterface servicioMercanciasInterface = (ServicioMercanciasInterface) Naming
				.lookup(distribuidorUrl);

		String nombreDistribuidor = servicioMercanciasInterface.darDeBajaDistribuidor(sesionDistribuidor);

		System.out.println("El cliente " + nombreDistribuidor + " se ha dado de baja correctamente");

		return nombreDistribuidor;

	}

	/**
	 * M�todo para dar de baja a un distribuidor
	 * 
	 * @param sesionDistribuidor
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public String darDeBajaCliente(int sesionCliente) throws RemoteException, MalformedURLException, NotBoundException {

		String clienteUrl = "rmi://localhost:" + puerto + "/cliente";
		ServicioVentasInterface servicioVentasInterface = (ServicioVentasInterface) Naming.lookup(clienteUrl);

		String nombreCliente = servicioVentasInterface.darDeBajaCliente(sesionCliente);

		System.out.println("El cliente " + nombreCliente + " se ha dado de baja correctamente");

		return nombreCliente;
	}

}
