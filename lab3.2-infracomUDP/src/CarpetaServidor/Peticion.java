package CarpetaServidor;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Clase que crear un thread por cada peticion
 * @author julianoliveros
 *
 */
public class Peticion extends Thread{

	//Longitud de bytes por paquete
	public final static int LONGITUD_MAXIMA=1460;//1460;


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
			System.out.println("envio el hash: "+hash);

			//Se envia el path
			out.println(path);
			System.out.println("envio el path: "+path);

			//Se envia el numero de conexciones
			out.println(numeroDeConexciones);
			System.out.println("envio el numero de conecciones: "+ numeroDeConexciones);

			//Se envia el tamano archivo
			out.println(tamanoArchvio);
			System.out.println("envio el tamanoArchvio: "+ tamanoArchvio);
		

			FileInputStream fis;
			OutputStream os;
			
			try 
			{
				File input = new File(path);
				fis = new FileInputStream(input);

				os = clienteSC.getOutputStream();
				
				BufferedInputStream bis = new BufferedInputStream(fis);
				BufferedOutputStream bos = new BufferedOutputStream(os);
				
				byte[] buffer = new byte[LONGITUD_MAXIMA];

				int data;

				while(true)
				{
					data = bis.read(buffer);
					//System.out.println("data: "+data);

					if(data != -1)
					{
						if(data==LONGITUD_MAXIMA) {
							//  	System.out.println("1");
							bos.write(buffer, 0, LONGITUD_MAXIMA);
						}
						else {
							//	System.out.println("2");
							bos.write(buffer, 0, data);

						}


					}
					else
					{
						//System.out.println("3");
						// Se cierra el ObjectOutputStream
						bis.close();
						bos.close();

//
//
//
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

						
						break;
					}
				}


			} 
			catch (FileNotFoundException ex) 
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
