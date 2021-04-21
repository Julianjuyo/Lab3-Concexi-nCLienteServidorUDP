package CarpetaServidor;



import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException; 


/**
 * Clase que representa un servidor
 * 
 * @author julianoliveros
 *
 */
public class Servidor {

	//private final static String RUTA1="H:/Desktop/Laboratorio3TCP.pdf";

	//private final static String RUTA1="/home/infracom/Lab3-infracom/Archivos/250MB.zip";
	private final static String RUTA2="/home/infracom/Lab3-infracom/Archivos/100MB.zip";
	private final static String RUTA1="/Users/julianoliveros/lab3.pdf";
	//private final static String RUTA1="/Users/julianoliveros/Public/matricula.pdf";
	//private final static String RUTA2="/Users/julianoliveros/100MBcopy.zip";
	private static File fichero;
	private static CarpetaServidor.Logger logger;



	/**
	 * Convierte en un arreglo de bits un archivo 
	 * @param file
	 * @return
	 */	
	public static byte[] getArray(File file){

		byte[] byteArray = new byte[(int) file.length()];


		try {
			FileInputStream fis = new FileInputStream(file);
			int bytesCount = 0; 
			bytesCount = fis.read(byteArray);
			fis.close();
			System.out.println("El archivo " + file.getName() +" tiene " + bytesCount +" bytes."+"\n");

		} catch (Exception e) {
			System.out.println("Problemas al convertir el archivo a bytes: "+ e.getMessage());
		}
		return byteArray;
	}



	/**
	 * Funacion que crea un hash a partir de un archivo 
	 * @param file
	 * @return
	 */
	public static String getHash(File input) 
	{ 
		try { 
			MessageDigest md = MessageDigest.getInstance("MD5"); 

			byte[] messageDigest = md.digest(getArray(input)); 

			BigInteger no = new BigInteger(1, messageDigest); 

			String hashtext = no.toString(16); 
			while (hashtext.length() < 32) { 
				hashtext = "0" + hashtext; 
			} 
			return hashtext; 
		} 

		catch (NoSuchAlgorithmException e) { 
			throw new RuntimeException(e); 
		} 
	} 

	
	/**
	 * Main del metodo.
	 * @param args
	 */
	public static void main(String[] args) {

		ServerSocket servidor = null;
		
		final int PUERTO =61101;
		String ruta=" ";
		int numeroConexiones=0;


		try {

			boolean CargoDatos=false;

			while(CargoDatos==false) {

				//Seleccionar que archivo quiere enviar
				Scanner scaner = new Scanner(System.in);

				System.out.println("\n"+"Indique el numero de clientes a los que archivo quiere enviar el archivo \n");

				numeroConexiones= 2;//Integer.parseInt(scaner.nextLine());

				System.out.println(
						"Indique que archivo quiere enviar (ESCRIBA EL NUMERO 1,2,3) \n"+
								"1: archivo de 100 MB \n"+
								"2: Archivo de 250 MB \n"+
								"3: Otro (pasar ruta por parametro) \n"
						);

				String Archivo ="1"; // scaner.nextLine();

				//Transferir archivo de 100MB
				if(Archivo.equals("1")) {
					ruta= RUTA1;
					fichero = new File(ruta);
					if(fichero.exists()) { 
						CargoDatos=true;
						System.out.println("Se cargo el archivo correctamente \n");
					}			
				}

				//Transferir archivo de 250MB
				else if(Archivo.equals("2")) {
					ruta= RUTA2;
					fichero = new File(ruta);
					if(fichero.exists()) { 
						CargoDatos=true;
						System.out.println("Se cargo el archivo correctamente \n");
					}
				}
				//Transferir otro archivo
				else if(Archivo.equals("3")) {
					boolean c=true;
					while(c) {
						System.out.println("Escriba la ruta del archivo \n");
						ruta = scaner.nextLine();
						fichero = new File(ruta);

						if(fichero.exists()) { 
							CargoDatos=true;
							c=false;
							System.out.println("Se cargo el archivo correctamente \n");
						}
						else {
							System.out.println("Ruta inavlida \n");							
						}
					}
				}
			}


			//Creamos el socket del servidor
			servidor = new ServerSocket(PUERTO);

			//Permite que se se pueda utilizar el mismo puerto
			servidor.setReuseAddress(true);
			System.out.println("---------------- \n" +"Servidor iniciado \n");


			String path = fichero.getPath();
			
			String hash = getHash(fichero);
			int tamanoArchivo= (int) fichero.length();
			System.out.println("tam"+tamanoArchivo);
			
//			int division= tamanoArchivo/19;
//			System.out.println(division);

			//alamceno info de cada socket			
			ArrayList<Peticion> Clientes = new ArrayList<>();
			
			int estado=0;
			//logger = new CarpetaServidor.Logger(numeroConexiones, fichero.getName(), fichero.length());


			//While que se queda esperando a que lleguen clientes.
			while (true) {

				//Espero a que un cliente se conecte
				Socket clienteSC = servidor.accept();

				PrintWriter out = new PrintWriter( clienteSC.getOutputStream(), true); 
				BufferedReader in = new BufferedReader(new InputStreamReader( clienteSC.getInputStream())); 

				String  idCliente = in.readLine();

				String  estadoActual = in.readLine();
				System.out.println("Cliente conectado con ID: "+idCliente +" y con IP: "+ clienteSC.getInetAddress().getHostAddress());

				if(estadoActual.equals("Listo")){
					estado++;					
				}

				Peticion threadCliente = new Peticion(clienteSC, idCliente, logger, hash, path, numeroConexiones, tamanoArchivo);

				Clientes.add(threadCliente); 

				if(Clientes.size()== numeroConexiones && estado== numeroConexiones){
					for (int i = 0; i < Clientes.size(); i++) {
						
						Clientes.get(i).start();
					}
				}
			}

			//imprime log
		}
		catch (IOException ex)
		{
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			if(servidor!=null) {
				try {
					servidor.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}


