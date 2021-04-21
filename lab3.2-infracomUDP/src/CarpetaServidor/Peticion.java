package CarpetaServidor;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



/**
 * Clase que crear un thread por cada peticion
 * @author julianoliveros
 *
 */
public class Peticion extends Thread{

	//Longitud de bytes por paquete
	private int puerto =50000;
	public final static int LONGITUD_MAXIMA=60000;//6KB


	private final Socket clienteSC;
	private String  idCliente;
	private final CarpetaServidor.Logger log;
	private final String hash;
	private final String path;
	private final int tamanoArchvio;
	private final int  numeroDeConexciones;





	public Peticion(Socket sc, String pidCliente, CarpetaServidor.Logger plog,String phash ,String ppath, int  pnumeroDeConexciones, int ptamanoArchvio ) {
		this.clienteSC= sc;
		this.idCliente=pidCliente;
		this.log = plog;
		this.hash= phash;
		this.path= ppath;
		this.numeroDeConexciones =  pnumeroDeConexciones;
		this.tamanoArchvio= ptamanoArchvio;
	}

	public void run() 
	{
		PrintWriter out = null; 
		BufferedReader in = null; 

		try { 

			// Ecribir a el cliente
			out = new PrintWriter( clienteSC.getOutputStream(), true); 

			// Leer del cliente 
			in = new BufferedReader( new InputStreamReader(clienteSC.getInputStream())); 


			System.out.println("Comenzo Thread: "+idCliente);

			//Se enviua el hash del archvio
			out.println(hash);
			//System.out.println("envio el hash: "+hash);

			//Se envia el path
			out.println(path);
			//System.out.println("envio el path: "+path);

			//Se envia el numero de conexciones
			out.println(numeroDeConexciones);
			//System.out.println("envio el numero de conecciones: "+ numeroDeConexciones);

			//Se envia el tamano archivo
			out.println(tamanoArchvio);
			//System.out.println("envio el tamanoArchvio: "+ tamanoArchvio);
			
			try 
			{
				
				System.out.print("Establecer conexion  SERVIDOR "+"\n");

				this.puerto=this.puerto+Integer.parseInt(idCliente);
				// Se realiza la conexion TCP
				
				DatagramSocket serverSocket = new DatagramSocket(this.puerto);
				
				System.out.println("Cliente con ID: "+ idCliente+ " En el puerto: "+this.puerto);
				System.out.println("DS En el puerto: "+ serverSocket.getPort());

				//Se crear dos buffer para recibir y enviar datos
				byte[] bufferRecibir = new byte[LONGITUD_MAXIMA];
				byte[] bufferEnviar  = new byte[LONGITUD_MAXIMA];



				// recibe info del cliente que se conecta
				DatagramPacket recvdpkt = new DatagramPacket(bufferRecibir, bufferRecibir.length);
				serverSocket.receive(recvdpkt);
				InetAddress IP = recvdpkt.getAddress();
				int portno = recvdpkt.getPort();
				String clientdata = new String(recvdpkt.getData());
				
				System.out.println("con ID: "+ clientdata+ " En el puerto: "+portno);

				System.out.println("Puerto"+recvdpkt.getPort());

				System.out.print("Comienza transferencia de Archivo Servidor "+"\n");
				
				
				//File fichero = new File("/Users/julianoliveros/documento.pdf");
				File fichero = new File(path);
				FileInputStream fis;


				fis = new FileInputStream(fichero);
				BufferedInputStream bis = new BufferedInputStream(fis);
				int data;
				
				System.out.println("tamano archivo: "+fichero.length());

				System.out.print("Entro a while"+"\n");
				
				//Se comienza a enviar el archvio 
				int a=0;
				while(true) 
				{
					data = bis.read(bufferEnviar);
					
					//System.out.print("data "+a+" : "+data+"\n");
					a++;
					
					if(data != -1)
					{
						if(data==LONGITUD_MAXIMA) {
							//System.out.println("1 if"+"\n");
							DatagramPacket sendPacket = new DatagramPacket(bufferEnviar, bufferEnviar.length, IP,portno);
							serverSocket.send(sendPacket);
							System.out.println("Puerto"+recvdpkt.getPort());
							
//							System.out.println("BUUFER COMIENZO : "+"\n");
//							for (int i = 0; i < bufferEnviar.length; i++) {System.out.println(bufferEnviar[i]);}
//							System.out.println("BUUFER FINAL : "+"\n");
						}
						else {
//							System.out.println("2 if"+"\n");
							
							byte[] newBuffer = new byte[data];
							
							for (int i = 0; i < newBuffer.length; i++) {
								newBuffer[i]= bufferEnviar[i];
							}
							
//							System.out.println("BUUFER COMIENZO : "+"\n");
//							for (int i = 0; i < newBuffer.length; i++) {System.out.println(newBuffer[i]);}
//							System.out.println("BUUFER FINAL : "+"\n");
							
							
							DatagramPacket sendPacket = new DatagramPacket(newBuffer, newBuffer.length, IP,portno);
							serverSocket.send(sendPacket); 
							System.out.println("Puerto"+recvdpkt.getPort());
						}
					}
					else
					{
						System.out.println("Se termino de enviar el archivo");
						break;

					}
				}
				
				System.out.println("el a es"+a);
				
				System.out.println("salio servidor");
				serverSocket.close();
		

//						String ResultadoHash ="";
//						System.out.println("paso por aqui");
//						ResultadoHash = in.readLine();
//						System.out.println("Resultado hash: "+ResultadoHash);
//						if(ResultadoHash.equals("Correcto")){
//							this.log.log("El archivo se envio correctamente al cliente " +idCliente );
//						}
//						else{
//							this.log.log("El archivo no se envio correctamente al cliente " + idCliente);
//						}
//						String tiempoEjecucion = in.readLine();
//						System.out.println("Resultado tiempoEjecucion: "+tiempoEjecucion);
//						this.log.log("La petición del cliente " + idCliente + " se proceso en " + tiempoEjecucion );



			} 
			catch (SocketException ex) 
			{
				//Logger.getLogger(Servidorcopy.class.getName()).log(Level.SEVERE, null, ex);
			} 
			catch (IOException ex) 
			{
				//Logger.getLogger(Servidorcopy.class.getName()).log(Level.SEVERE, null, ex);
			}


			clienteSC.close();
			System.out.println("Cliente desconectado");	




		} 
		catch (IOException e) { 
			e.printStackTrace(); 
		} 

		finally { 
			try { 
				if (out != null) { 
					out.close(); 
				} 
				if (in != null) { 
					in.close();  
				} 
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 
	} 
} 
