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
import java.util.Map;

public interface ServicioMercanciasInterface extends Remote {

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
	 * devuelve la lsita de repos, todas las registradas indicando cual de ellas
	 * esta online y cual offline
	 * 
	 * @return String la lista de repos registradas indicando cual esta online u
	 *         offline
	 * @throws RemoteException
	 */
	public String listaTipoProductos() throws RemoteException;

	/**
	 * devulve el Distribuidor al que pertenece el cliente
	 * 
	 * @param idCliente
	 *            int el id unico del cliente
	 * @return int el id unico de la repo a la que pertenece
	 * @throws RemoteException
	 */
	public int dimeDistribuidor(int idCliente) throws RemoteException;

	public int registrarOferta(String tipo, String precio, String kilos, String nombre)
			throws RemoteException, MalformedURLException, NotBoundException;

	public int quitarOferta(int idOferta) throws RemoteException, MalformedURLException, NotBoundException;

	public Map<String, Integer> getListarOfertasPorSesion()
			throws RemoteException, MalformedURLException, NotBoundException;

	public int listarDistribuidores() throws RemoteException, MalformedURLException, NotBoundException;

	public int listarOfertas() throws RemoteException, MalformedURLException, NotBoundException;

	int registrarDemanda(String tipo, String kilos, String nombre, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException;

	int listarDemandas() throws RemoteException, MalformedURLException, NotBoundException;

	public int comprarMercancia(int idDemanda, int idOferta, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException;

	public int listarVentasPorDistribuidor() throws MalformedURLException, RemoteException, NotBoundException;

}
