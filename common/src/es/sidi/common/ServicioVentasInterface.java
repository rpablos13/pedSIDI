/**
 * Esta clase se encarga de llamar a los métodos que luego serán implementados en ServiciosVentasImpl
 * 
 * @autor Raúl Pablos de la Prieta, rpablos13@alumno.uned.es
 */
package es.sidi.common;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServicioVentasInterface extends Remote {

	/**
	 * Autentica a un cliente
	 * 
	 * @param nombre
	 * @param id
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	public int autenticarCliente(String nombre, int id, String password) throws RemoteException;

	/**
	 * Registra a un cliente
	 * 
	 * @param nombre
	 * @param id
	 * @param password
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int registrarCliente(String nombre, int id, String password)
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Lista los clientes
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public int listarClientes() throws RemoteException;

	/**
	 * Compra la mercancía
	 * 
	 * @param idDemanda
	 * @param idOferta
	 * @param idSesionCliente
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int comprarMercancia(int idDemanda, int idOferta, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Pregunta por el id de sesión del distribuidor
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int getIdSesionDistribuidor() throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Da de baja a un cliente
	 * 
	 * @param idSesionCliente
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public String darDeBajaCliente(int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException;

	public Map<Integer, String> getClientesRegistrados()
			throws RemoteException, MalformedURLException, NotBoundException;

	public Map<Integer, String> getClientesAutenticados()
			throws RemoteException, MalformedURLException, NotBoundException;

	public Map<String, Integer> getClienteNombre() throws RemoteException, MalformedURLException, NotBoundException;

	public Map<String, String> getClientePassword() throws RemoteException, MalformedURLException, NotBoundException;

}
