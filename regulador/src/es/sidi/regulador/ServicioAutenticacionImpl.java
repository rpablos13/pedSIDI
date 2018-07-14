/**
 * Implementa la interface ServicioAutenticacionInterface
 * Se encarga de registrar, autenticar, desconectar a clientes y a repositorios
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170301
 */

package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import es.sidi.common.Interfaz;
import es.sidi.common.RandomSessionNumber;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioClienteInterface;
import es.sidi.common.ServicioMercanciasInterface;

public class ServicioAutenticacionImpl extends UnicastRemoteObject implements ServicioAutenticacionInterface {

	// necesitamos identificadores de sesion
	private static final long serialVersionUID = 1L;
	private int puerto = 7791;
	private ServicioMercanciasInterface servicioMercanciasInterface;
	private ServicioClienteInterface servicioClienteInterface;
	private int sesionDistribuidor;
	private int sesionCliente;

	/**
	 * Contructor necesario al extender UnicastRemoteOBject y poder utilizar Naming
	 * lo aprovecharemos tambien para buscar el almacen de datos
	 * 
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	protected ServicioAutenticacionImpl() throws RemoteException, MalformedURLException, NotBoundException {
		super();

		// buscamos el objeto en el servidor gestor para autenticarnos
		// String URLRegistro = "rmi://localhost:" + puerto + "/mercancia";
		// servicioMercanciasInterface = (ServicioMercanciasInterface)
		// Naming.lookup(URLRegistro);
	}

	/**
	 * solicita al servicio de Datos la autenticacion de un cliente,
	 * 
	 * @param el
	 *            nombre del cliente que se quiere autenticar
	 * @return int el id sesion de cliente que se ha autenticado
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	@Override
	public int autenticarCliente(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesionAleatoria = getSesion();

		sesionCliente = sesionAleatoria;

		String clienteURL = "rmi://localhost:" + puerto + "/cliente";
		ServicioClienteInterface servicioClienteInterface = (ServicioClienteInterface) Naming.lookup(clienteURL);

		int id = servicioClienteInterface.autenticarCliente(nombre, sesionCliente, password);
		switch (id) {
		case 1:
			Interfaz.imprime("Cliente " + nombre + " logueado correctamente");
			break;
		case 0:
			Interfaz.imprime("Usuario o contraseña no válidos");
			break;
		default:
			Interfaz.imprime("El Cliente " + nombre + " ya se encuentra logueado");
			break;
		}
		return id;
	}

	/**
	 * registra un cliente
	 * 
	 * @param String
	 *            el nombre del cliente a registrar
	 * @return int -1 si no hay repos online, 0 si el cliente ya esta registrado y
	 *         el id unico del registro en caso de éxito
	 */
	@Override
	public int registrarCliente(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesion = getSesion();

		String clienteURL = "rmi://localhost:" + puerto + "/cliente";
		ServicioClienteInterface servicioClienteInterface = (ServicioClienteInterface) Naming.lookup(clienteURL);

		int id = servicioClienteInterface.registrarCliente(nombre, sesion, password);
		if (id != 0)
			Interfaz.imprime("El usuario " + nombre + " se ha registrado Correctamente");
		else
			Interfaz.imprime("Ya existe el usuario " + nombre + " en el sistema");
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
			Interfaz.imprime("Distribuidor " + nombre + " logueado correctamente");
			break;
		case 0:
			Interfaz.imprime("Usuario o contraseña no válidos");
			break;
		default:
			Interfaz.imprime("El distribuidor " + nombre + " ya se encuentra logueado");
			break;
		}
		return idSesion;
	}

	/**
	 * registra un repositorio
	 * 
	 * @String el nombre del repositorio
	 * @return int el id sesion del repositorio
	 * @throws NotBoundException
	 * @throws MalformedURLException
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
			Interfaz.imprime("El usuario " + nombre + " se ha registrado Correctamente");
		else
			Interfaz.imprime("Ya existe el usuario " + nombre + " en el sistema");
		return id;
	}

	/**
	 * solicita al Gestor la desconexion de un cliente
	 * 
	 * @param sesion
	 *            int el id sesion del cliente a desconectar
	 */
	@Override
	public void desconectarCliente(int sesion) throws RemoteException {
		String cliente = servicioClienteInterface.desconectarCliente(sesion);
		Interfaz.imprime("El cliente " + cliente + " se ha desconectado del sistema");
	}

	/**
	 * solicita al Gestor la desconexion de una repo
	 * 
	 * @param sesion
	 *            int el id sesion de la repo a desconectar
	 */
	@Override
	public void desconectarDistribuidor(int sesion) throws RemoteException {
		String repo = servicioMercanciasInterface.desconectarDistribuidor(sesion);
		Interfaz.imprime("La repo " + repo + " se ha desconectado del sistema");
	}

	/**
	 * devulve un id de sesion
	 * 
	 * @return int un id sesion nuevo valido para cliente o repo
	 */
	// devuelve el contador de sesiones
	public int getSesion() {
		return RandomSessionNumber.generateSessionId();
	}

	@Override
	public int getIdSesioncliente() throws RemoteException {
		return sesionCliente;
	}

}
