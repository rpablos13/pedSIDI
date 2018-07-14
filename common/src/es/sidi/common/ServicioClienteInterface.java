/**
 * Interface del Sercicio Disco Cliente
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170623
 */
package es.sidi.common;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServicioClienteInterface extends Remote {

	/**
	 * autentica a un cliente
	 * 
	 * @param nombre
	 *            String el nombre del cliente
	 * @param id
	 *            int el id sesion del cliente
	 * @return int -2 si el cliente ya esta registrado, -1 si su repo no esta
	 *         online, 0 si ya esta autentica, id sesion del cliente en caso
	 *         contrario
	 * @throws RemoteException
	 */
	public int autenticarCliente(String nombre, int id, String password) throws RemoteException;

	/**
	 * registra a un cliente
	 * 
	 * @param nombre
	 *            String el nombre del cliente
	 * @param id
	 *            int el id unico del cliente a registrar
	 * @return int -1 si no hay repos online, 0 si ya esta registrado, id unico en
	 *         otro caso
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int registrarCliente(String nombre, int id, String password)
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * desconecta a un cliente, borra la sesion actual del cliente
	 * 
	 * @param sesion
	 *            int el id sesion del cliente a cerrar
	 * @return String el nombre del cliente de la sesion que se ha cerrado
	 * @throws RemoteException
	 */
	public String desconectarCliente(int sesion) throws RemoteException;

	/**
	 * devuvle la lista de cliente , todos los registrado indicando cual de ellos
	 * esta online y cual offline
	 * 
	 * @return String la lista de cliente registrado indicando cual esta online u
	 *         offline
	 * @throws RemoteException
	 */
	public int listarClientes() throws RemoteException;

	/**
	 * devuelve el id unico a partir del id sesion del cliente
	 * 
	 * @param idsesion
	 *            itn el id sesion del cliente
	 * @return int id unico del cliente
	 * @throws RemoteException
	 */
	public int sesion2id(int idsesion) throws RemoteException;

	public Map<Integer, String> getMapClientes() throws RemoteException;

}
