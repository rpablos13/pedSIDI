/**
 * Esta interface permite realizar una abstraccion del almacenamiento de los datos, publicando los metodos
 * lo que nos permitira usar un motor de base de datos o bien mas sencillamente
 * en esta practica las estructuras List y HashMap de java
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170227
 */
package es.sidi.common;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioMercanciasInterface extends Remote {

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
	public int autenticarCliente(String nombre, int id) throws RemoteException;

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
	public int registrarCliente(String nombre, int id) throws RemoteException, MalformedURLException, NotBoundException;

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
	 * autentica una repo
	 * 
	 * @param nombre
	 *            String el nombre del Distribuidor
	 * @param id
	 *            int id sesion de la repo
	 * @return -1 la repo no esta registrada, 0 la repo ya esta autenticada, id
	 *         sesion de la repo en otro caso
	 * @throws RemoteException
	 */
	public int autenticarDistribuidor(String nombre, int sesion, String password) throws RemoteException;

	/**
	 * registra una repo
	 * 
	 * @param nombre
	 *            String el nombre de la repo
	 * @param id
	 *            int el id unico de la repo a registrar
	 * @return int 0 si la repo ya esta registrada, id unico de la repo en otro caso
	 * @throws RemoteException
	 */
	public int registrarDistribuidor(String nombre, int sesion, String password) throws RemoteException;

	/**
	 * desconecta la repo, borra la sesion actual de la repo
	 * 
	 * @param sesion
	 *            int el id seseion de la repo a cerrar
	 * @return String el nombre de la repo de la sesion que se ha cerrado
	 * @throws RemoteException
	 */
	public String desconectarDistribuidor(int sesion) throws RemoteException;

	/**
	 * devuvle la lista de cliente , todos los registrado indicando cual de ellos
	 * esta online y cual offline
	 * 
	 * @return String la lista de cliente registrado indicando cual esta online u
	 *         offline
	 * @throws RemoteException
	 */
	public String listaClientes() throws RemoteException;

	/**
	 * devuelve la lsita de repos, todas las registradas indicando cual de ellas
	 * esta online y cual offline
	 * 
	 * @return String la lista de repos registradas indicando cual esta online u
	 *         offline
	 * @throws RemoteException
	 */
	public String listaDistribuidors() throws RemoteException;

	/**
	 * devuelve la lista de los emparejamientos cliente y Distribuidor
	 * 
	 * @return String la lista para imprimir en formato simple de los emparejamiento
	 *         cliente y Distribuidor
	 * @throws RemoteException
	 */
	public String listaClientesDistribuidors() throws RemoteException;

	/**
	 * devulve el Distribuidor al que pertenece el cliente
	 * 
	 * @param idCliente
	 *            int el id unico del cliente
	 * @return int el id unico de la repo a la que pertenece
	 * @throws RemoteException
	 */
	public int dimeDistribuidor(int idCliente) throws RemoteException;

	/**
	 * devuelve el id unico a partir del id sesion del cliente
	 * 
	 * @param idsesion
	 *            itn el id sesion del cliente
	 * @return int id unico del cliente
	 * @throws RemoteException
	 */
	public int sesion2id(int idsesion) throws RemoteException;

}
