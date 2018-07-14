/**
 * Esta clase gestiona todo lo relacionado con los distribuidores
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.regulador;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import es.sidi.common.RandomSessionNumber;
import es.sidi.common.Resultado;
import es.sidi.common.ServicioMercanciasInterface;
import es.sidi.common.ServicioVentasInterface;

public class ServicioMercanciasImpl extends UnicastRemoteObject implements ServicioMercanciasInterface {

	private static final long serialVersionUID = 1L;

	private static int puerto = 7791;
	private static String direccion = "localhost";

	// Listas Distribuidores
	private Map<Integer, String> distribuidoresRegistrados = new HashMap<Integer, String>();
	private Map<Integer, String> distribuidoresAutenticados = new HashMap<Integer, String>();
	private Map<String, Integer> distribuidorNombre = new HashMap<String, Integer>();
	private Map<String, String> distribuidorPassword = new HashMap<String, String>();

	private List<String> listMercancias = new ArrayList<>();

	// Listas ofertas
	private Map<Integer, Integer> listaOfertaSesion = new HashMap<Integer, Integer>();
	private Map<String, Integer> listaNombreOfertas = new HashMap<String, Integer>();
	private Map<Integer, String> tipoMercanciaOferta = new HashMap<Integer, String>();
	private Map<Integer, Float> precioMercanciaOferta = new HashMap<Integer, Float>();
	private Map<Integer, Float> kilosMercanciaOferta = new HashMap<Integer, Float>();

	// Listas Demandas
	private Map<Integer, Integer> listaDemandaSesion = new HashMap<Integer, Integer>();
	private Map<String, Integer> listaDemandas = new HashMap<String, Integer>();
	private Map<Integer, String> tipoMercanciaDemanda = new HashMap<Integer, String>();
	private Map<Integer, Float> kilosMercanciaDemanda = new HashMap<Integer, Float>();

	// Listas Compras
	private Map<Integer, Integer> listaCompras = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> listaCompraSesionCliente = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> listaOfertasCompra = new HashMap<Integer, Integer>();

	private int idSesionDistribuidor;

	private String nombreDistribuidor;

	private String autenticador;

	/**
	 * Constructor por defecto
	 * 
	 * @throws RemoteException
	 */
	protected ServicioMercanciasImpl() throws RemoteException {
		super();
		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";

		cargarTiposMercanciaInicial();
	}

	/**
	 * Cargamos un nuevo hashmap con los tipos de mercancía que se van a utilizar,
	 * de esta manera no hará falta registarlos manualmente.
	 */
	public void cargarTiposMercanciaInicial() {

		listMercancias.add("Tomates");
		listMercancias.add("Limones");
		listMercancias.add("Naranjas");
		listMercancias.add("Fresas");
		listMercancias.add("Plátanos");
		listMercancias.add("Melones");
		listMercancias.add("Sandías");

		System.out.println("Cargar tipo de mercancías. Resultado: " + Resultado.SUCCESSFUL.getResultado());
	}

	/**
	 * Método para autenticar a un Distribuidor
	 * 
	 * @param nombre
	 * @param sesion
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public int autenticarDistribuidor(String nombre, int idSesion, String password) throws RemoteException {

		if (distribuidoresAutenticados.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = distribuidorPassword.get(nombre);
			if (distribuidorNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {
				distribuidoresAutenticados.put(idSesion, nombre);
				return 1;
			} else
				return 0;// No se ha introducido un usuario o contraseña correctos
		}
	}

	/**
	 * Pregunta por el id de sesión del distribuidor actual
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	@Override
	public int getIdSesionDistribuidor() {

		return idSesionDistribuidor;

	}

	/**
	 * Método para registrar un Distribuidor
	 * 
	 * @param nombre
	 * @param sesion
	 * @param password
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public int registrarDistribuidor(String nombre, int id, String password) throws RemoteException {
		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (distribuidorNombre.containsKey(nombre))
			return 0;
		else {
			distribuidorNombre.put(nombre, id);
			distribuidorPassword.put(nombre, password);
			distribuidoresRegistrados.put(id, nombre);
		}
		return id;
	}

	/**
	 * Método para generar la lista de productos
	 * 
	 * @return
	 * @throws RemoteException
	 */
	@Override
	public String listaTipoProductos() {
		String lista = "";
		System.out.println(listMercancias);
		return lista;
	}

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
	@Override
	public int registrarOferta(String tipo, String precio, String kilos, String nombre)
			throws RemoteException, MalformedURLException, NotBoundException {

		int idOferta = RandomSessionNumber.generateSessionId();
		idSesionDistribuidor = getIdSesionDistribuidor();
		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (!listMercancias.contains(tipo))
			return 0; // Si el producto escrito no corresponde con los tipos registrados no regista la
						// oferta
		else {
			listaOfertaSesion.put(idOferta, idSesionDistribuidor);
			listaNombreOfertas.put(nombre, idOferta);
			tipoMercanciaOferta.put(idOferta, tipo);
			precioMercanciaOferta.put(idOferta, Float.parseFloat(precio));
			kilosMercanciaOferta.put(idOferta, Float.parseFloat(kilos));
			return 1;
		}
	}

	/**
	 * Quita una oferta de los registros
	 * 
	 * @param idOferta
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public int quitarOferta(int idOferta) throws RemoteException, MalformedURLException, NotBoundException {

		// Se almacenan varios maps y en cada uno hay diferentes registros, pero siempre
		// con el id de sesión con el que se registró, por lo tanto eliminas únicamente
		// todos los relacionados con el id que pedimos
		Iterator listaOfertasIterator = listaNombreOfertas.entrySet().iterator();
		Iterator listaOfertasSesionIterator = listaOfertaSesion.entrySet().iterator();
		Iterator tipoMercanciaIterator = tipoMercanciaOferta.entrySet().iterator();
		Iterator precioMercanciaIterator = precioMercanciaOferta.entrySet().iterator();
		Iterator kilosMercanciaIterator = kilosMercanciaOferta.entrySet().iterator();

		// ITERATOR LISTA OFERTAS
		while (listaOfertasIterator.hasNext()) {
			Entry entry = (Entry) listaOfertasIterator.next();
			Integer idSesion = Integer.parseInt(entry.getValue().toString());

			if (idSesion.equals(idOferta))
				listaOfertasIterator.remove();
		}

		// ITERATOR OFERTAS - SESION
		while (listaOfertasSesionIterator.hasNext()) {
			Entry entry = (Entry) listaOfertasSesionIterator.next();
			Integer idSesion = Integer.parseInt(entry.getKey().toString());

			if (idSesion.equals(idOferta))
				listaOfertasSesionIterator.remove();

		}

		// ITERATOR TIPO MERCANCÍA
		while (tipoMercanciaIterator.hasNext()) {
			Entry entry = (Entry) tipoMercanciaIterator.next();
			Integer idSesion = Integer.parseInt(entry.getKey().toString());

			if (idSesion.equals(idOferta))
				tipoMercanciaIterator.remove();

		}
		// ITERATOR PRECIO MERCANCÍA
		while (precioMercanciaIterator.hasNext()) {
			Entry entry = (Entry) precioMercanciaIterator.next();
			Integer idSesion = Integer.parseInt(entry.getKey().toString());

			if (idSesion.equals(idOferta))
				precioMercanciaIterator.remove();

		}

		// ITERATOR KILOS MERCANCÍA
		while (kilosMercanciaIterator.hasNext()) {
			Entry entry = (Entry) kilosMercanciaIterator.next();
			Integer idSesion = Integer.parseInt(entry.getKey().toString());

			if (idSesion.equals(idOferta))
				kilosMercanciaIterator.remove();

		}

		return 1;
	}

	/**
	 * Lista las ofertas por sesisión, en algunos casos no nos interesa mostrar
	 * todas
	 * 
	 * @return
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public Map<String, Integer> getListarOfertasPorSesion()
			throws RemoteException, MalformedURLException, NotBoundException {

		Map<String, Integer> nuevaListaOfertas = new HashMap<String, Integer>();

		for (Map.Entry<String, Integer> entry : listaNombreOfertas.entrySet()) {
			String nombreOferta = entry.getKey();
			Integer idOferta = entry.getValue();
			if (listaOfertaSesion.get(idOferta).equals(getIdSesionDistribuidor())) {
				nuevaListaOfertas.put(nombreOferta, idOferta);
			}
		}
		return nuevaListaOfertas;
	}

	/**
	 * Lista de los distribuidores
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public void listarDistribuidores() throws RemoteException, MalformedURLException, NotBoundException {

		if (!distribuidorNombre.isEmpty()) {
			try {
				System.out.println(
						"==================================\n********LISTA DISTRIBUIDORES*********\n==================================\n");
				for (Map.Entry<String, Integer> entry : distribuidorNombre.entrySet()) {
					String nombre = entry.getKey();
					Integer id = entry.getValue();

					System.out.println(nombre + " -> " + id);

				}
				System.out.println(
						"\n==================================\n**********************************\n==================================\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No se encuentras distribuidores registrados actualmente");
		}
	}

	/**
	 * Lista de las ofertas
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public void listarOfertas() throws RemoteException, MalformedURLException, NotBoundException {

		if (!listaNombreOfertas.isEmpty()) {
			try {
				System.out.println("********OFERTAS DISPONIBLES*********\n");

				for (Map.Entry<String, Integer> entry : listaNombreOfertas.entrySet()) {
					String nombre = entry.getKey();
					Integer id = entry.getValue();

					String tipo = null;
					for (Map.Entry<Integer, String> tipoMercancia : tipoMercanciaOferta.entrySet()) {
						if (tipoMercancia.getKey().equals(id)) {
							tipo = tipoMercancia.getValue();
						}
					}

					float kilos = 0;
					for (Map.Entry<Integer, Float> kilosMercancia : kilosMercanciaOferta.entrySet()) {
						if (kilosMercancia.getKey().equals(id)) {
							kilos = Float.parseFloat(kilosMercancia.getValue().toString());
						}
					}

					System.out.println(nombre + ": " + "\n" + "\tTipo mercancia -> " + tipo + "\n\tKilos ofertados: "
							+ kilos + "\n\tId -> " + id);

				}
				System.out.println(
						"\n==================================\n**********************************\n==================================\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No se encuentran ofertas actualmente");
		}
	}

	/**
	 * Lista las demandas
	 * 
	 * @throws RemoteException
	 * @throws MalformedURLException
	 * @throws NotBoundException
	 */
	@Override
	public void listarDemandas() throws RemoteException, MalformedURLException, NotBoundException {

		if (!listaDemandas.isEmpty()) {
			try {
				System.out.println(
						"==================================\n********DEMANDAS DISPONIBLES******\n==================================\n");
				for (Map.Entry<String, Integer> entry : listaDemandas.entrySet()) {

					String nombre = entry.getKey();
					Integer id = entry.getValue();

					String tipo = null;
					for (Map.Entry<Integer, String> tipoMercancia : tipoMercanciaDemanda.entrySet()) {
						if (tipoMercancia.getKey().equals(id)) {
							tipo = tipoMercancia.getValue();
						}
					}

					float kilos = 0;
					for (Map.Entry<Integer, Float> kilosMercancia : kilosMercanciaDemanda.entrySet()) {
						if (kilosMercancia.getKey().equals(id)) {
							kilos = Float.parseFloat(kilosMercancia.getValue().toString());
						}
					}

					System.out.println(nombre + ": " + "\n" + "\tTipo mercancia -> " + tipo + "\n\tKilos pedidos: "
							+ kilos + "\n\tId -> " + id);
				}
				System.out.println(
						"\n==================================\n**********************************\n==================================\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No se encuentran demandas actualmente");
		}
	}

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
	@Override
	public int registrarDemanda(String tipo, String kilos, String nombre, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException {

		int idDemanda = RandomSessionNumber.generateSessionId();
		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (!listMercancias.contains(tipo))
			return 0; // Si el producto escrito no corresponde con los tipos registrados no regista la
						// oferta
		else {
			listaDemandaSesion.put(idDemanda, idSesionCliente);
			listaDemandas.put(nombre, idDemanda);
			tipoMercanciaDemanda.put(idDemanda, tipo);
			kilosMercanciaDemanda.put(idDemanda, Float.parseFloat(kilos));
			return 1;
		}
	}

	/**
	 * Nombre de los clientes
	 */
	@Override
	public Map<Integer, String> getMapNombresClientes()
			throws MalformedURLException, RemoteException, NotBoundException {
		String URLRegistro = "rmi://localhost:7791/cliente";// RMI
		ServicioVentasInterface servicioClienteInterface = (ServicioVentasInterface) Naming.lookup(URLRegistro);

		return servicioClienteInterface.getClientesAutenticados();
	}

	@Override
	public int comprarMercancia(int idSesionCliente, int idDemanda, int idOferta)
			throws MalformedURLException, RemoteException, NotBoundException {

		// Generamos un nuevo id para la compra
		int idCompra = RandomSessionNumber.generateSessionId();

		// Primero comprobamos que el cliente puede ejecutar la demanda, hay que
		// comprobar si existe un numero suficiente de kilos en la oferta
		for (Map.Entry<Integer, Float> kilosMercanciaOfertados : kilosMercanciaOferta.entrySet()) {
			Float kilosOfertados = null;
			if (kilosMercanciaOfertados.getKey().equals(idOferta)) {
				kilosOfertados = kilosMercanciaOfertados.getValue();
			}

			Float kilosDemandados = null;
			for (Map.Entry<Integer, Float> kilosMercanciaDemandada : kilosMercanciaDemanda.entrySet()) {
				if (kilosMercanciaDemandada.getKey().equals(idDemanda)) {
					kilosDemandados = kilosMercanciaDemandada.getValue();
				}
			}
			// Si no hay suficientes kilos
			if (kilosOfertados < kilosDemandados) {
				return 0;
			} else if (kilosOfertados >= kilosDemandados) {
				// Añadimos un nuevo map con el registro de la compra
				listaCompraSesionCliente.put(idCompra, idSesionCliente);

				// Añadimos un nuevo registro de una venta
				listaCompras.put(idCompra, idDemanda);

				listaOfertasCompra.put(idCompra, idOferta);

				// Actualizamos los kilos en la oferta elegida
				kilosMercanciaOferta.put(idOferta, (kilosOfertados - kilosDemandados));
				return 1;
			} else
				return -1;
		}
		return idCompra;

	}

	/**
	 * Da de baja un Distribuidor
	 * 
	 * @param sesionDistribuidor
	 * @return
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	@Override
	public String darDeBajaDistribuidor(int sesionDistribuidor)
			throws MalformedURLException, RemoteException, NotBoundException {

		// ITERATOR DISTRIBUIDORES AUTENTICADOS
		for (Iterator<Map.Entry<Integer, String>> it = distribuidoresAutenticados.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<Integer, String> entry = it.next();
			if (entry.getKey().equals(sesionDistribuidor)) {
				nombreDistribuidor = entry.getValue();
				it.remove();
			}
		}

		// ITERATOR DISTRIBUIDORES NOMBRE
		for (Iterator<Map.Entry<String, Integer>> it = distribuidorNombre.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			if (entry.getValue().equals(sesionDistribuidor)) {
				it.remove();
			}
		}

		// ITERATOR DISTRIBUIDORES PASSWORD
		for (Iterator<Map.Entry<String, String>> it = distribuidorPassword.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			if (entry.getKey().equals(nombreDistribuidor)) {
				it.remove();
			}
		}

		// ITERATOR DISTRIBUIDORES REGISTRADOS
		for (Iterator<Map.Entry<Integer, String>> it = distribuidoresRegistrados.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, String> entry = it.next();
			if (entry.getKey().equals(sesionDistribuidor)) {
				it.remove();
			}
		}

		return nombreDistribuidor;
	}

	@Override
	public Map<Integer, String> getDistribuidoresAutenticados() {
		return distribuidoresAutenticados;
	}

	@Override
	public Map<Integer, String> getTipoMercanciaOferta() {
		return tipoMercanciaOferta;
	}

	@Override
	public Map<Integer, Integer> getListaCompras() {
		return listaCompras;
	}

	@Override
	public Map<Integer, Integer> getListaOfertasCompra() {
		return listaOfertasCompra;
	}

	@Override
	public Map<String, Integer> getListaNombreOfertas() {
		return listaNombreOfertas;
	}

	@Override
	public Map<Integer, Float> getPrecioMercanciaOferta() {
		return precioMercanciaOferta;
	}

	@Override
	public Map<Integer, Float> getKilosMercanciaDemanda() {
		return kilosMercanciaDemanda;
	}

	@Override
	public Map<Integer, Float> getKilosMercanciaOferta() {
		return kilosMercanciaOferta;
	}

	@Override
	public Map<String, Integer> getListaDemandas() {
		return listaDemandas;
	}

	@Override
	public Map<Integer, String> getTipoMercanciaDemanda() {
		return tipoMercanciaDemanda;
	}

	@Override
	public Map<String, Integer> getDistribuidorNombre() {
		return distribuidorNombre;
	}

	@Override
	public Map<String, String> getDistribuidorPassword() {
		return distribuidorPassword;
	}

	@Override
	public Map<Integer, Integer> getListaCompraSesionCliente() {
		return listaCompraSesionCliente;
	}

}
