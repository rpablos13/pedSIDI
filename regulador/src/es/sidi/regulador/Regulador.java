/**
 * Funciones del Regulador (Servidor):

 * 
 * 1. Listar ofertas actuales
 * 2. Listar demandas actuales
 * 3. Listar clientes
 * 4. Listar distribuidores
 * 5. Salir
 * 
 * @autor rpablos13@alumno.uned.es
 */
package es.sidi.regulador;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import es.sidi.common.Interfaz;
import es.sidi.common.ServicioAutenticacionInterface;
import es.sidi.common.Utils;

public class Regulador {
	
	private static int puerto = 7791;
	private static Registry registry;
	private static String direccion = "localhost";
	
	/**
	 * main
	 * Arranca el registry
	 * Bindea los servicios de datos,autenticacion y el gestor
	 * imprime el menu de opciones
	 * cuando sale del menu debe guardar los datos persistentes, eliminar los servicios y el registry
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{

		String URLRegistro;
		//levantamos el registro es como rmiregistry en la shell
		//Registry registry = LocateRegistry.createRegistry(puerto);
		arrancarRegistro(puerto);
		
		//ubicacion de la clase
		Utils.setCodeBase(ServicioAutenticacionInterface.class);

		
		//Levantar Datos, el almacen, el constructor mantendra la persistencia
		ServicioDatosImpl objetoDatos = new ServicioDatosImpl();
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/almacen";
		Naming.rebind(URLRegistro, objetoDatos);
		System.out.println("Operacion: Servicio Datos preparado con exito");
		
		
		
		//Levantar Autenticador
		ServicioAutenticacionImpl objetoAutenticador = new ServicioAutenticacionImpl();
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/autenticador";
		Naming.rebind(URLRegistro, objetoAutenticador);
		System.out.println("Operacion: Servicio Autenticador preparado con exito");

		//Levantar Gestor
		ServicioGestorImpl objetoGestor = new ServicioGestorImpl();
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/gestor";
		Naming.rebind(URLRegistro, objetoGestor);
		System.out.println("Operacion: Servicio Gestor preparado con exito");
		
		listRegistry("rmi://" + direccion + ":" + puerto );
		//menu
		int opcion = 0;
		do{			
			opcion = Interfaz.menu("Servidor",new String[]{
					"Listar Clientes","Listar Repositorios","Listar Parejas Repositorio-Cliente"});
			switch(opcion){
				case 1: System.out.println(objetoDatos.listaClientes());break;
				case 2: System.out.println(objetoDatos.listaRepositorios());break;
				case 3: System.out.println(objetoDatos.listaClientesRepositorios());break;			
			}
		}while (opcion!=4);
		
		//mantenerPersistencia antes de cerrar
		objetoDatos.guardarDatosPersistentes();
		
		//eliminar Autenticador
		System.out.println("Operacion: Servicio Autenticador cerrandose...");
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/autenticador";
		Naming.unbind(URLRegistro);	
		System.out.println("Operacion: Servicio Autenticador cerrado con exito");
		
		//antes de tirar el almacen de Datos hay que indicarle que lo vamos a tirar
		//para que serialize los datos y los guarde en un fichero
		//eliminar almacen Datos
		System.out.println("Operacion: Servicio Datos cerrandose...");
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/almacen";
		Naming.unbind(URLRegistro);
		System.out.println("Operacion: Servicio Datos cerrado con exito");
		
		//eliminar Gestor
		System.out.println("Operacion: Servicio Gestor cerrandose...");
		URLRegistro = "rmi://" + direccion + ":" + puerto + "/gestor";
		Naming.unbind(URLRegistro);
		System.out.println("Operacion: Servicio Gestor cerrado con exito");
		
		//cerrar rmiregistry del objeto registry unico
		try{
			UnicastRemoteObject.unexportObject(registry,true);
			System.out.println("Operacion: Registry cerrado con exito");
		} catch (java.rmi.NoSuchObjectException e){
			System.err.println("Operacion: Registry no se ha cerrado");
		}
		//return;
		System.exit(0);//queda feo, pero es que sino no se cierra el servicio
	}
	
	/**
	 * arranca el registry en el puerto indicado
	 * @param numPuertoRMI int el puerto de escucha
	 * @throws RemoteException
	 */
	private static void arrancarRegistro(int numPuertoRMI) throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry(numPuertoRMI);
			registry.list(); // Esta llamada lanza
			// una excepcion si el registro no existe
		}
		catch (RemoteException e) {
			// Registro no valido en este puerto
			System.out.println("El registro RMI no se puede localizar en el puerto "+ numPuertoRMI);
			registry =	LocateRegistry.createRegistry(numPuertoRMI);
			System.out.println("Registro RMI creado en el puerto " + numPuertoRMI);
		}
	}
	
	/**
	 * lista los servicios colgados
	 * @param registryURL String la url que queremos mirar
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */
	private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException{
		System.out.println("Registry " + registryURL + " contains: ");
		String[] names =Naming.list(registryURL);
		for  (int i=0; i< names.length; i++)
		{
			System.out.println(names[i]);
		}
	}
	
}