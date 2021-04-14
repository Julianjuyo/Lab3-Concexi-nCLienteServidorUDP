package CarpetaServidor;


import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

public class Logger {
	
	private String PATH="/home/infracom/Lab3-infracom/lab3-sockets/logs/";
	//private String PATH="/Users/julianoliveros/Public";
	private boolean append_to_file = true;
	private String name  ;

	
	public Logger(int numRequest, String name, long l ) {
		long millis= System.currentTimeMillis();
		Date date= new Date(millis);
		this.name= date.getYear() + "-" ;
		this.name += date.getMonth() + "-";
		this.name += date.getDay() +"-";
		this.name += date.getHours() +"-";
		this.name += date.getMinutes() +"-";
		this.name += date.getSeconds() +".txt";
		PrintWriter pw = null;
		FileWriter archivo = null;
		try{
			File fichero = new File (PATH + this.name);
			fichero.createNewFile();
			archivo = new FileWriter(PATH + this.name ,true);
			pw = new PrintWriter(archivo);
			pw.println("Log iniciado el " + this.name);
			pw.println("Archivo a pasar " + name);
			pw.println("Tamanio del archivo " + l);
			pw.println("Numero de clientes: " + numRequest);
			pw.close();

		}
		catch(Exception e){
			System.out.println("problemas al hacer el log.");
		}
		

	}
	
	public void log(String msg) throws IOException{
		
		FileWriter write = new FileWriter(PATH + this.name , append_to_file);
		PrintWriter print_line= new PrintWriter(write);
		
		long millis=System.currentTimeMillis();
		Date date= new Date(millis);
		
		print_line.println("/ "+ date);
		print_line.println(msg);
		
		print_line.close();
	}

}
