package CarpetaCliente;


import java.security.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



/**
 * clase de cliente 
 * 
 * @author je.oliverosf
 *
 */
public class Cliente {

	private int puerto2 =50000;
	public final static int LONGITUD_MAXIMA=60000;//6KB

	//Host del servidor
	//private final String HOST = "192.168.97.112";
	private final String HOST = "localhost";

	//Puerto del servidor
	private final int PUERTO =61101;

	//Id del cliente
	private String id;

	
	public Cliente(String pId) {

		this.id= pId;

	}


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
			System.out.println("El archivo " + file.getName() + " tiene " + bytesCount +" bytes.");

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


	public void EmpiezaEjecucion() {

		//Meodos para ecribir y leer
		PrintWriter out = null; 
		BufferedReader in = null; 



		try {

			// Se crea el socket y conecta a la ip y puerto 
			Socket sc = new Socket(HOST, PUERTO);

			// Escribir a el servidor
			out = new PrintWriter( sc.getOutputStream(), true); 

			// Leer del servidor 
			in = new BufferedReader(new InputStreamReader( sc.getInputStream())); 


			//			//Se pregunta y envia a el servidor el id del cliente
			//			System.out.println("Escriba el id del cliente (numero)");
			//			id = scaner.nextLine();
			out.println(id);


			// ciclo hasta que escriba la palabra Listo
			boolean listo=true;
			while(listo) {
				System.out.println("Indique cuando este listo para la empezar la recepcion del archivo escribiendo: Listo");

				String ComprbanteDeEnvio= "Listo";//scaner.nextLine();//
				if(ComprbanteDeEnvio.equals("Listo")) 
					listo=false;
			}
			out.println("Listo");

			long startTime = System.currentTimeMillis();


			//Comienza Transferencia de Archivos

			//recibe el hash
			String line = in.readLine();
			String hashRecibido = line;
			System.out.println("recibo Hash: "+hashRecibido);

			//recibe el path
			line= in.readLine();
			String path = line;
			String[] split =  path.split("\\.");
			String tipoDeArchivo =  split[1]; 
			System.out.println("recibo path: "+ path);
			System.out.println("recibido Tipo Archivo: "+tipoDeArchivo);

			//recibe el numero de conexiones
			line= in.readLine();
			int numeroDeConexiones = Integer.parseInt(line);
			System.out.println("recibo numeroDeConexiones: "+ numeroDeConexiones);

			//recibe el tamanoArchvio
			line= in.readLine();
			int tamanoArchvio = Integer.parseInt(line);
			System.out.println("recibo tamanoArchvio: "+ tamanoArchvio);


			String pathNuevoArchvio = "/Users/julianoliveros/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;

			//String pathNuevoArchvio ="/home/infracom/Lab3-infracom/lab3-sockets/ArchivosRecibidos/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;



			try 
			{
				this.puerto2=this.puerto2+Integer.parseInt(id);
				System.out.println("El puerto es:"+this.puerto2);

				//Se establece la ip de la maquina
				InetAddress IP = InetAddress.getByName(HOST);

				//Se crea una conexion UDP
				DatagramSocket clientSocket = new DatagramSocket();
				
				System.out.println("DS En el puerto: "+ clientSocket.getPort());

				
				byte[] bufferEnviar = new byte[LONGITUD_MAXIMA];
				byte[] bufferRecibir = new byte[LONGITUD_MAXIMA];


				System.out.print("Establecer conexion CLIENTE "+"\n");

				String clientData = id;
				
				
				bufferEnviar = clientData.getBytes();    
				DatagramPacket sendPacket = new DatagramPacket(bufferEnviar, bufferEnviar.length, IP, this.puerto2);
				clientSocket.send(sendPacket);

				System.out.print("Comienza transferencia de Archivo Cliente"+"\n");

				//File output = new File("/Users/julianoliveros/documentoCOPIA.pdf");
				
				File output = new File(pathNuevoArchvio);
				
				FileOutputStream fos = new FileOutputStream(output);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				int data;

				System.out.print("Entro a while"+"\n");
				int a=0;
				while(true) 
				{
					
					DatagramPacket recvdpkt = new DatagramPacket(bufferRecibir, bufferRecibir.length);
					clientSocket.receive(recvdpkt);
					data = recvdpkt.getLength();
					System.out.println("Puerto"+recvdpkt.getPort());
					

//					String clientdata = new String(recvdpkt.getData());
//					System.out.println("Recibi los bytes: "+ clientdata+"\n");
					//System.out.print("data "+a+" : "+data+"\n");
					a++;

					

					if(data != -1)
					{
						if(data==LONGITUD_MAXIMA) {
							
//							System.out.println("BUUFER COMIENZO : "+"\n");
//							for (int i = 0; i < bufferRecibir.length; i++) {System.out.println(bufferRecibir[i]);}
//							System.out.println("BUUFER FINAL : "+"\n");
//							
//							System.out.println("1 if "+"\n");
							bos.write(bufferRecibir, 0, LONGITUD_MAXIMA);
						}
						else {
							
							byte[] newBuffer = new byte[data];
							
							for (int i = 0; i < newBuffer.length; i++) {
								newBuffer[i]= bufferRecibir[i];
							}
							
//							System.out.println("BUUFER COMIENZO : ");
//							for (int i = 0; i < newBuffer.length; i++) {System.out.println(newBuffer[i]);}
//							System.out.println("BUUFER FINAL : "+"\n");			
//							System.out.println("2 if "+"\n");
							
							bos.write(newBuffer, 0, data);
							
							System.out.println("Se termino de enviar el archivo");
							bos.close();
							break;
						}
					}
				}
				
				System.out.println("el a es"+a);
				System.out.println("salio cliente");
				clientSocket.close();


				String resp = VerificarHash(hashRecibido, pathNuevoArchvio);

				long endTime = System.currentTimeMillis() - startTime;
				System.out.println("Se tardo:"+endTime+" milisegundos");


				sc.close(); 
				
			} 
			catch (SocketException ex) {
				//Logger.getLogger(Clientecopy.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {

			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
	}


	/**
	 * Metodo que verifica el funcionamiento correcto de la app
	 * @param hashRecibido
	 * @param pathNuevoArchvio
	 */
	public String VerificarHash(String hashRecibido, String pathNuevoArchvio) {

		String resp ="";
		File fichero = new File(pathNuevoArchvio);

		System.out.println("Tamano Fichero Transferido: "+fichero.length());
		System.out.println("path Fichero Transferido: "+fichero.getPath());

		//Se verifica que el hash sea el mismo
		String hashArchivoNuevo =  getHash(fichero);
		System.out.println("Hash archivo recibido: "+hashArchivoNuevo);

		if(!hashArchivoNuevo.equals(hashRecibido)) {
			System.out.println("EL ARHCIVO NO ES CORRECTO!!!!");
			resp="Error";
		}
		else {
			System.out.println("\n"+"EL VALOR CALCULADO PARA EL HASH DEL ARHCIVO ES CORRECTO"+"\n");
			resp="Correcto";
		}
		System.out.println("Envio Respuesta Hash: "+resp);

		return resp;

	}



	/**
	 * Main de la clase cliente 
	 * @param args
	 */
	public static void main(String[] args) {

		Scanner scaner = new Scanner(System.in);

		//Se pregunta y envia a el servidor el id del cliente
		System.out.println("Escriba el id del cliente (numero)");
		String ClienteId = scaner.nextLine();


		Cliente cliente = new Cliente(ClienteId);
		cliente.EmpiezaEjecucion();;


	}

}


