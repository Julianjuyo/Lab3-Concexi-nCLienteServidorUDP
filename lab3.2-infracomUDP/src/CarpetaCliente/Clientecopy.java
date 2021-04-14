package CarpetaCliente;


import java.security.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;



/**
 * clase de cliente 
 * 
 * @author je.oliverosf
 *
 */
public class Clientecopy {

	public final static int LONGITUD_MAXIMA=1460;

	//Host del servidor
	private final String HOST = "192.168.97.112";
	//private final String HOST = "localhost";

	//Puerto del servidor
	private final int PUERTO =61101;

	//Id del cliente
	private String id;
	



	public Clientecopy(String pId) {

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

			
			
			
			

			String pathNuevoArchvio = "/Users/julianoliveros/ArchivosRecibidos/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;

			//String pathNuevoArchvio ="/home/infracom/Lab3-infracom/lab3-sockets/ArchivosRecibidos/Cliente"+id+"-Prueba"+numeroDeConexiones+"."+tipoDeArchivo;
			
			InputStream is;
			BufferedInputStream bis;
			FileOutputStream fos   ;
			BufferedOutputStream bos;


			try 
			{
				File output = new File(pathNuevoArchvio);
				is = sc.getInputStream();

				bis = new BufferedInputStream(is);
				fos = new FileOutputStream(output);
				bos = new BufferedOutputStream(fos);
				byte[] buffer = new byte[LONGITUD_MAXIMA];
				int data;

				while(true)
				{
					data = bis.read(buffer);
					
					
					//System.out.println("data: "+data);

					if(data != -1)
					{
						if(data==LONGITUD_MAXIMA) {
							//System.out.println("1");
							bos.write(buffer, 0, LONGITUD_MAXIMA);
						}
						else {
							//System.out.println("2");
							bos.write(buffer, 0, data);
						}
					}
					else
					{
						//System.out.println("3");
						
						bis.close();
						bos.close();
						
//						out = new PrintWriter( sc.getOutputStream(), true);
//						in = new BufferedReader(new InputStreamReader( sc.getInputStream())); 
//						
						String resp = VerificarHash(hashRecibido, pathNuevoArchvio);
//						out.println(resp);
//						
//						out.println(endTime);
//						System.out.println("Se demoro: "+endTime+" milisegundos en enviar el archivo");

						
						break;
					}
				}
				
				
				long endTime = System.currentTimeMillis() - startTime;
				System.out.println("Se tardo:"+endTime);
				
				
				sc.close(); 
			} 
			catch (IOException ex) 
			{
				//Logger.getLogger(Clientecopy.class.getName()).log(Level.SEVERE, null, ex);
			}





		} catch (IOException ex) {

			Logger.getLogger(Clientecopy.class.getName()).log(Level.SEVERE, null, ex);
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
		System.out.println("Tamano Fichero Transferido: "+fichero.getPath());
		
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
		
		
		Clientecopy cliente = new Clientecopy(ClienteId);
		cliente.EmpiezaEjecucion();;


	}

}


