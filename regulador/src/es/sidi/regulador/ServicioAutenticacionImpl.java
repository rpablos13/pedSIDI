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
import java.util.Random;

import es.sidi.common.Interfaz;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioMercanciasInterface;

public class ServicioAutenticacionImpl extends UnicastRemoteObject implements ServicioAutenticacionInterface {

	// necesitamos identificadores de sesion
	private static final long serialVersionUID = 123711131719L;
	private int sesion = Math.abs(new Random().nextInt()); // no quiero numeros negativos
	private int puerto = 7791;
	private ServicioMercanciasInterface servicioMercanciasInterface;

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
		String URLRegistro = "rmi://localhost:" + puerto + "/almacen";
		servicioMercanciasInterface = (ServicioMercanciasInterface) Naming.lookup(URLRegistro);
	}

	/**
	 * solicita al servicio de Datos la autenticacion de un cliente,
	 * 
	 * @param el
	 *            nombre del cliente que se quiere autenticar
	 * @return int el id sesion de cliente que se ha autenticado
	 */
	@Override
	public int autenticarCliente(String nombre, String password) throws RemoteException {
		int sesionCliente = getSesion();
		int id = servicioMercanciasInterface.autenticarCliente(nombre, sesionCliente, password);
		switch (id) {
		case 1:
			Interfaz.imprime("Cliente " + nombre + " logueado correctamente");
			break;
		case 0:
			Interfaz.imprime("Usuario o contrase�a no v�lidos");
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
	 *         el id unico del registro en caso de �xito
	 */
	@Override
	public int registrarCliente(String nombre, String password)
			throws RemoteException, MalformedURLException, NotBoundException {
		int sesion = getSesion();
		int id = servicioMercanciasInterface.registrarCliente(nombre, sesion, password);
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
	 */
	@Override
	public int autenticarDistribuidor(String nombre, String password) throws RemoteException {
		int sesionDistribuidor = getSesion();
		int id = servicioMercanciasInterface.autenticarDistribuidor(nombre, sesionDistribuidor, password);
		switch (id) {
		case 1:
			Interfaz.imprime("Distribuidor " + nombre + " logueado correctamente");
			break;
		case 0:
			Interfaz.imprime("Usuario o contrase�a no v�lidos");
			break;
		default:
			Interfaz.imprime("El distribuidor " + nombre + " ya se encuentra logueado");
			break;
		}
		return id;
	}

	/**
	 * registra un repositorio
	 * 
	 * @String el nombre del repositorio
	 * @return int el id sesion del repositorio
	 */
	@Override
	public int registrarDistribuidor(String nombre, String password) throws RemoteException {
		int sesion = getSesion();
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
		String cliente = servicioMercanciasInterface.desconectarCliente(sesion);
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
		return ++sesion;
	}
}
