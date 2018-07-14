/**
 * Esta clase se encarga de llamar a los métodos que luego serán implementados en ServicioMercanciasImpl
 * 
 * @autor Raúl Pablos de la Prieta, rpablos13@alumno.uned.es
 */
package es.sidi.common;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ServicioMercanciasInterface extends Remote {

	/**
	 * Método para autenticar a un Distribuidor
	 * 
	 * @param nombre
	 * @param sesion
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	public int autenticarDistribuidor(String nombre, int sesion, String password) throws RemoteException;

	/**
	 * Método para registrar un Distribuidor
	 * 
	 * @param nombre
	 * @param sesion
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	public int registrarDistribuidor(String nombre, int sesion, String password) throws RemoteException;

	/**
	 * Método para generar la lista de productos
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public String listaTipoProductos() throws RemoteException;

	/**
	 * Método para registrar una oferta
	 * 
	 * @param tipo
	 * @param precio
	 * @param kilos
	 * @param nombre
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int registrarOferta(String tipo, String precio, String kilos, String nombre)
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Quita una oferta de los registros
	 * 
	 * @param idOferta
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int quitarOferta(int idOferta) throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Lista las ofertas por sesisión, en algunos casos no nos interesa mostrar
	 * todas
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public Map<String, Integer> getListarOfertasPorSesion()
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Lista de los distribuidores
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public void listarDistribuidores() throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Lista de las ofertas
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public void listarOfertas() throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Registra las demandas
	 * 
	 * @param tipo
	 * @param kilos
	 * @param nombre
	 * @param idSesionCliente
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	public int registrarDemanda(String tipo, String kilos, String nombre, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Lista las demandas
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	void listarDemandas() throws RemoteException, MalformedURLException, NotBoundException;

	/**
	 * Pregunta por el id de sesión del distribuidor actual
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public int getIdSesionDistribuidor() throws MalformedURLException, RemoteException, NotBoundException;

	/**
	 * Compra una determinada mercancía
	 * 
	 * @param idSesionCliente
	 * @param idDemanda
	 * @param idOferta
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public int comprarMercancia(int idSesionCliente, int idDemanda, int idOferta)
			throws MalformedURLException, RemoteException, NotBoundException;

	/**
	 * Da de baja un Distribuidor
	 * 
	 * @param sesionDistribuidor
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public String darDeBajaDistribuidor(int sesionDistribuidor)
			throws MalformedURLException, RemoteException, NotBoundException;

	// Todos estos métdodos de abajo son simplemente para llamar a esas listas desde
	// clases que son inaccesibles

	public Map<Integer, Integer> getListaCompras() throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, String> getTipoMercanciaOferta()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, Integer> getListaOfertasCompra()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<String, Integer> getListaNombreOfertas()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, Float> getPrecioMercanciaOferta()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, Float> getKilosMercanciaDemanda()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, String> getMapNombresClientes()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, String> getDistribuidoresAutenticados()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, Float> getKilosMercanciaOferta()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<String, Integer> getListaDemandas() throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, String> getTipoMercanciaDemanda()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<String, Integer> getDistribuidorNombre()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<String, String> getDistribuidorPassword()
			throws MalformedURLException, RemoteException, NotBoundException;

	public Map<Integer, Integer> getListaCompraSesionCliente()
			throws MalformedURLException, RemoteException, NotBoundException;

}
