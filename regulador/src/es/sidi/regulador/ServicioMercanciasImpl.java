/**
 * La clase ServicioDatosImpl implementa la interface ServicioDatosInterface
 * Almacena y gestiona los datos como si de una base de datos se tratase
 * De esta forma el acoplamiento es minimo, y podemos rescribirla utilizando
 * cualquier otro mecanismo de almacenamiento
 * 
 * Recordemos operaciones con HashMap: put y containsKey
 * 
 * @autor Buenaventura Salcedo Santos-Olmo, xpressmoviles@gmail.com
 * @version v1.20170228
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
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.ServicioClienteInterface;
import es.sidi.common.ServicioMercanciasInterface;

public class ServicioMercanciasImpl extends UnicastRemoteObject implements ServicioMercanciasInterface {

	private static final long serialVersionUID = 1L;

	private ServicioAutenticacionInterface servicioAutenticacionInterface;

	// atributos para buscar el servicio Servidor Operador del Distribuidor
	private static int puerto = 7791;
	// private static ServicioSrOperadorInterface servidorSrOperador;
	private static String direccion = "localhost";
	private static String nombreSrOperador = "sroperador";
	private static String autenticador;

	// Estructuras que mantienen las autenticaciones VOLATILES
	private Map<Integer, String> sesionDistribuidorAutenticado = new HashMap<Integer, String>();
	private Map<Integer, String> distribuidorSesionAutenticado = new HashMap<Integer, String>();

	// Estructuras que mantiene el almacen de Clientes y Distribuidors registrados
	// PERSISTENTES
	private Map<Integer, String> almacenIdCliente = new HashMap<Integer, String>();
	private Map<String, Integer> distribuidorNombre = new HashMap<String, Integer>();
	private Map<String, String> distribuidorPassword = new HashMap<String, String>();
	private Map<Integer, Integer> almacenClienteDistribuidor = new HashMap<Integer, Integer>();

	private List<String> listMercancias = new ArrayList<>();

	private Map<Integer, Integer> listaOfertaSesion = new HashMap<Integer, Integer>();
	private Map<String, Integer> listaNombreOfertas = new HashMap<String, Integer>();
	private Map<Integer, String> tipoMercanciaOferta = new HashMap<Integer, String>();
	private Map<Integer, Float> precioMercanciaOferta = new HashMap<Integer, Float>();
	private Map<Integer, Float> kilosMercanciaOferta = new HashMap<Integer, Float>();

	private Map<Integer, Integer> listaDemandaSesion = new HashMap<Integer, Integer>();
	private Map<String, Integer> listaDemandas = new HashMap<String, Integer>();
	private Map<Integer, String> tipoMercanciaDemanda = new HashMap<Integer, String>();
	private Map<Integer, Float> kilosMercanciaDemanda = new HashMap<Integer, Float>();

	private Map<Integer, Integer> listaCompras = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> listaCompraSesionCliente = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> listaOfertaSesionCliente = new HashMap<Integer, Integer>();

	private int idSesionDistribuidor;

	private float kilosOfertados;

	private float kilosDemandados;

	private String nombreCliente;

	private String tipo;

	private float kilos;

	private String nombreDistribuidor;

	private String nombreOferta;

	private float precioOferta;

	private Integer idSesionCliente;

	// Metodos
	/**
	 * Contructor necesario al extender UnicastRemoteOBject y poder utilizar Naming
	 * 
	 * @throws RemoteException
	 */

	protected ServicioMercanciasImpl() throws RemoteException {
		super();
		autenticador = "rmi://" + direccion + ":" + puerto + "/autenticador";

		cargarTiposMercanciaInicial();
		cargarBaseDatosMercancia();
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

	public void cargarBaseDatosMercancia() {

		Map<Integer, String> mercanciaMap = new HashMap<>();
		System.out.println("Cargado de tablas. Resultado: " + Resultado.SUCCESSFUL.getResultado());

	}

	/**
	 * autentica un Distribuidor
	 * 
	 * @param String
	 *            el nombre del Distribuidor
	 * @param int
	 *            el idsesion que le pasamos
	 * @return int -1 si la repo no estra registrada, 0 si ya esta autenticado, el
	 *         idsesion si es correcto
	 */
	@Override
	public int autenticarDistribuidor(String nombre, int idSesion, String password) throws RemoteException {

		if (distribuidorSesionAutenticado.containsKey(nombre)) {
			return -1; // ya esta autenticado
		}

		else { // Se comprueba que se ha logueado correctamente el usuario
			String passwordAlmacenada = distribuidorPassword.get(nombre);
			if (distribuidorNombre.containsKey(nombre) && password.equals(passwordAlmacenada)) {

				// No tenemos por que cambiar la sesión basta con recuerar la que ya tenía
				// cuando se registró por primera vez
				Integer idSesionRegistrada = distribuidorNombre.get(nombre);
				idSesionDistribuidor = idSesionRegistrada;
				distribuidorSesionAutenticado.put(idSesionDistribuidor, nombre);
				sesionDistribuidorAutenticado.put(idSesionDistribuidor, nombre);

				return 1;
			} else
				return 0;// No se ha introducido un usuario o contraseña correctos
		}
	}

	private int getIdSesionDistribuidor() {

		return idSesionDistribuidor;

	}

	/**
	 * registra un repositiorio
	 * 
	 * @param String
	 *            el nombre del Distribuidor
	 * @param int
	 *            el id sesion del Distribuidor
	 * @return int 0 si ya esta registrada con ese nombre, el id sesion en caso
	 *         contrario
	 */
	@Override
	public int registrarDistribuidor(String nombre, int id, String password) throws RemoteException {
		// Nos basta con saber si se encuentra en nombre en nuestra base de datos
		if (distribuidorNombre.containsKey(nombre))
			return 0;
		else {
			distribuidorNombre.put(nombre, id);
			distribuidorPassword.put(nombre, password);
			sesionDistribuidorAutenticado.put(id, nombre);
		}
		return id;
	}

	/**
	 * devuelve un String con los emparejameintos entre clientes y repos recordemos
	 * un lciente solo esta en una repo se devulve el ide cliente el id repositiorio
	 * el nombre cliente y el nombre de la repo
	 * 
	 * @return la lsita de las parejas
	 */
	@Override
	public String listaTipoProductos() {
		String lista = "";
		System.out.println(listMercancias);
		return lista;
	}

	/**
	 * borra la entrada de sesion de una repo, es decir desconecta la repo
	 * 
	 * @param int
	 *            el id sesion de la repo
	 * @return String el nombre de la repo desconectada
	 */
	@Override
	public String desconectarDistribuidor(int sesion) throws RemoteException {
		String repo = sesionDistribuidorAutenticado.get(sesion);
		sesionDistribuidorAutenticado.remove(sesion);
		distribuidorSesionAutenticado.remove(repo);
		return repo;
	}

	/**
	 * busca la priemra repo online que encuentra y devulve su id unico
	 * 
	 * @return int el id unico de la primera repo online encontrada
	 */
	public int dameDistribuidor() {
		if (distribuidorSesionAutenticado.isEmpty()) {
			return 0;
		} else {
			Iterator it = distribuidorSesionAutenticado.entrySet().iterator();
			String nombre = "";
			if (it.hasNext()) {
				Map.Entry e = (Map.Entry) it.next();
				nombre = (String) e.getKey();
			}
			return distribuidorNombre.get(nombre);
		}
	}

	/**
	 * devuelve el Distribuidor de un cliente
	 * 
	 * @param int
	 *            el id unico del cliente
	 * @return int el id sesion de la repo
	 */
	@Override
	public int dimeDistribuidor(int idCliente) {
		return Integer.parseInt(distribuidorSesionAutenticado
				.get(sesionDistribuidorAutenticado.get(almacenClienteDistribuidor.get(idCliente))));
	}

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
			Integer idSesion = Integer.parseInt(entry.getValue().toString());

			if (idSesion.equals(idOferta))
				tipoMercanciaIterator.remove();

		}
		// ITERATOR PRECIO MERCANCÍA
		while (precioMercanciaIterator.hasNext()) {
			Entry entry = (Entry) precioMercanciaIterator.next();
			Integer idSesion = Integer.parseInt(entry.getValue().toString());

			if (idSesion.equals(idOferta))
				precioMercanciaIterator.remove();

		}

		// ITERATOR KILOS MERCANCÍA
		while (kilosMercanciaIterator.hasNext()) {
			Entry entry = (Entry) kilosMercanciaIterator.next();
			Integer idSesion = Integer.parseInt(entry.getValue().toString());

			if (idSesion.equals(idOferta))
				kilosMercanciaIterator.remove();

		}

		return 1;

	}
	// // Nos basta con saber si se encuentra en nombre en nuestra base de datos
	// if (!listMercancias.contains(tipo))
	// return 0; // Si el producto escrito no corresponde con los tipos registrados
	// no regista la
	// // oferta
	// else {
	// tipoMercancia.put(tipo, idOferta);
	// precioMercancia.put(Float.parseFloat(precio), idOferta);
	// kilosMercancia.put(Float.parseFloat(kilos), idOferta);

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

	@Override
	public int listarDistribuidores() throws RemoteException, MalformedURLException, NotBoundException {
		try {
			System.out.println("********LISTA DISTRIBUIDORES*********\n");

			for (Map.Entry<String, Integer> entry : distribuidorNombre.entrySet()) {
				String nombre = entry.getKey();
				Integer id = entry.getValue();

				System.out.println("Cliente: " + nombre + " -> " + id);

			}
			System.out.println("\n****************************************\n");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int listarOfertas() throws RemoteException, MalformedURLException, NotBoundException {
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
			System.out.println("\n**********************************\n");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int listarDemandas() throws RemoteException, MalformedURLException, NotBoundException {

		try {
			System.out.println("********DEMANDAS DISPONIBLES*********\n");

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

				System.out.println(nombre + ": " + "\n" + "\tTipo mercancia -> " + tipo + "\n\tKilos pedidos: " + kilos
						+ "\n\tId -> " + id);
			}
			System.out.println("\n**********************************\n");
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

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

	@Override
	public int comprarMercancia(int idDemanda, int idOferta, int idSesionCliente)
			throws RemoteException, MalformedURLException, NotBoundException {
		// Generamos un nuevo id para la compra
		int idCompra = RandomSessionNumber.generateSessionId();

		// Primero comprobamos que el cliente puede ejecutar la demanda, hay que
		// comprobar si existe un numero suficiente de kilos en la oferta

		for (Map.Entry<Integer, Float> kilosMercanciaOfertados : kilosMercanciaOferta.entrySet()) {
			if (kilosMercanciaOfertados.getKey().equals(idOferta)) {
				kilosOfertados = kilosMercanciaOfertados.getValue();
			}

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
				listaCompraSesionCliente.put(idDemanda, idSesionCliente);
				// Añadimos un nuevo map con la oferta que ha registrado el cliente
				listaOfertaSesionCliente.put(idOferta, idSesionCliente);

				// Añadimos un nuevo registro de una venta
				listaCompras.put(idOferta, idDemanda);

				// Actualizamos los kilos en la oferta elegida
				kilosMercanciaOferta.put(idOferta, (kilosOfertados - kilosDemandados));
				return 1;
			} else
				return -1;
		}
		return idCompra;
	}

	@Override
	public int listarVentasPorDistribuidor() throws MalformedURLException, RemoteException, NotBoundException {

		Integer idNuevaDemanda = null;
		Integer idNuevaOferta = null;
		float dineroTotalRecaudado = 0;

		try {
			System.out.println(
					"==================================\n********VENTAS REALIZADAS*********\n==================================\n");

			for (Map.Entry<Integer, Integer> listaVentas : listaCompras.entrySet()) {

				idNuevaOferta = listaVentas.getKey();
				idNuevaDemanda = listaVentas.getValue();

				for (Map.Entry<Integer, Integer> relacionClienteOferta : listaOfertaSesionCliente.entrySet()) {

					if (relacionClienteOferta.getKey().equals(idNuevaOferta))
						idSesionCliente = relacionClienteOferta.getValue();

				}

				// NOMBRE DEL CLIENTE
				for (Map.Entry<Integer, String> nombreClientes : getMapNombresClientes().entrySet()) {
					if (nombreClientes.getKey().equals(idSesionCliente)) {
						nombreCliente = nombreClientes.getValue();
					}
				}

				// NOMBRE DISTRIBUIDOR
				for (Map.Entry<Integer, String> nombreDistribuidores : distribuidorSesionAutenticado.entrySet()) {
					if (nombreDistribuidores.getKey().equals(getIdSesionDistribuidor())) {
						nombreDistribuidor = nombreDistribuidores.getValue();
					}
				}

				// TIPO DE PRODUCTO COMPRADO
				for (Map.Entry<Integer, String> tipoProductos : tipoMercanciaOferta.entrySet()) {
					if (tipoProductos.getKey().equals(idNuevaOferta)) {
						tipo = tipoProductos.getValue();
					}
				}

				// KILOS DEMANDADOS
				for (Map.Entry<Integer, Float> kilosDemandados : kilosMercanciaDemanda.entrySet()) {
					if (kilosDemandados.getKey().equals(idNuevaDemanda)) {
						kilos = kilosDemandados.getValue();
					}
				}

				// NOMBRE OFERTA
				for (Map.Entry<String, Integer> nombreOfertas : listaNombreOfertas.entrySet()) {
					if (nombreOfertas.getValue().equals(idNuevaOferta)) {
						nombreOferta = nombreOfertas.getKey();
					}
				}

				// PRECIO OFERTA
				for (Map.Entry<Integer, Float> precioOfertas : precioMercanciaOferta.entrySet()) {
					if (precioOfertas.getKey().equals(idNuevaOferta)) {
						precioOferta = precioOfertas.getValue();
					}
				}

				float dineroReaudado = precioOferta * kilosDemandados;
				dineroTotalRecaudado += dineroReaudado;

				System.out.println("Cliente: " + nombreCliente + ": " + "\n" + "\tTipo mercancia -> " + tipo
						+ "\n\tKilos demandados: " + kilos + "\n\tNombre del Distribuidor: " + nombreDistribuidor
						+ "\n\tOferta realizada: " + nombreOferta + " (" + precioOferta + " €/Kg" + ")"
						+ "\n\tDinero recaudado: " + dineroReaudado + "€");
				System.out.println("\n********************************\n");
			}

			System.out.println("Dinero Total Recaudado: " + dineroTotalRecaudado + "€");

			System.out.println(
					"\n==================================\n**********************************\n==================================\n");

			return 1;

		} catch (

		Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private Map<Integer, String> getMapNombresClientes()
			throws MalformedURLException, RemoteException, NotBoundException {
		String URLRegistro = "rmi://localhost:7791/cliente";// RMI
		ServicioClienteInterface servicioClienteInterface = (ServicioClienteInterface) Naming.lookup(URLRegistro);

		return servicioClienteInterface.getMapClientes();
	}
}
