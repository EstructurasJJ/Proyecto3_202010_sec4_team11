package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import model.data_structures.Graph;
import model.data_structures.Node;
import model.data_structures.Vertice;
import model.logic.Mapita;
import model.logic.Maps;
import model.logic.Modelo;
import view.View;

public class Controller {

	private Modelo modelo;
	private View view;
	
	public final static String ESTACIONES = "./data/estacionpolicia.geojson";
	public final static String CREADO="./data/grafoCreado.json";
	
	public final static String RUTAGEOJASON = "./data/Comparendos_DEI_2018_Bogot�_D.C.geojson";
	public final static String JUEGUEMOS = "./data/Comparendos_DEI_2018_Bogot�_D.C_small.geojson";
	public final static String COTEJO = "./data/Comparendos_DEI_2018_Bogot�_D.C_50000_.geojson";

	
	public final static double MIN_LAT = 3.819966340000008;
	public final static double MAX_LAT = 4.836643219999985;
	public final static double MIN_LON = -74.39470032000003;
	public final static double MAX_LON = -73.9546749999999;
	
	public Controller ()
	{
		view = new View();
		modelo = new Modelo();
	}

	public void run() throws FileNotFoundException, IOException 
	{
		Scanner lector = new Scanner(System.in);
		boolean fin = false;
		
		String dato = "";
		String respuesta = "";

		while( !fin )
		{
			view.printMenu();

			int option = lector.nextInt();

			int nHeap;


			int capIni;
			switch(option)
			
			{
			case 1:

				modelo.leerGeoJsonComparendos(COTEJO);
				System.out.println("El numero de comparendo es: " + modelo.darComaprendo().darTamanio() + "\n---------------");
				
				modelo.leerJsonGrafo(CREADO);
				
				int totalVertices = modelo.darGrafo().darV()-1;
				System.out.println("Total de Vertices: " + totalVertices + "\n--------------");
				System.out.println("Numero de arcos: " + modelo.darGrafo().darE() + "\n---------------");
				
				modelo.leerGeoJsonEstaciones(ESTACIONES); 
				System.out.println("El total de estaciones de policia es: " + modelo.darEstaciones().darTamanio() + "\n----------------------");
				
				modelo.crearDivisionMapa();
				modelo.asigancionComparendos();
				System.out.println("La asiganci�n de los comparendos fue un exito." + "\n---------------");
				
				break;
				
			case 2: 
				
				System.out.println("Ingrese la latitud deseada:");
				double lati = Double.parseDouble(lector.next());
				
				System.out.println("Ingrese la longitud deseada:");
				double longi = Double.parseDouble(lector.next());
				
				Vertice ganador = modelo.idMinimoAVerti(lati, longi);
				System.out.println("----------------------\n" + "El vertice m�s cercano es: " + ganador.darId() + "\n----------------------" );
				
				break;
				
			case 3:
				
				Maps mapa = new Maps(modelo);
				System.out.println("Es bill�simo");
				mapa.initFrame("Es bill�simo");
				
				break;
				
			case 4:
				
				boolean valido = true;
				
				System.out.println("Ingrese la latitud inicial: ");
				Double lat1=Double.parseDouble(lector.next());
				
				System.out.println("Ingrese la longitud inicial: ");
				Double lon1=Double.parseDouble(lector.next());
				
				System.out.println("Ingrese la latitud final: ");
				Double lat2=Double.parseDouble(lector.next());
				
				System.out.println("Ingrese la longitud final: ");
				Double lon2=Double.parseDouble(lector.next());
				
				if(lat1 > MAX_LAT || lat1 < MIN_LAT) valido = false;
				if(lat2 > MAX_LAT || lat2 < MIN_LAT) valido = false;
				
				if(lon1 > MAX_LON || lon1 < MIN_LON) valido = false;
				if(lon2 > MAX_LON || lon2 < MIN_LON) valido = false;
				
				if(valido)
				{
					Graph mapita = modelo.SPDosUbicaciones(lat1, lon1, lat2, lon2);		
					
					Mapita Spam = new Mapita(mapita);
					System.out.println("Quiero perico");
					Spam.initFrame("Quiero perico");
				}
				else
				{
					System.out.println("No son validas las coordenadas.");
				}

				
				break;
				
			case 5:

				view.printMessage("--------- \n Hasta pronto !! \n---------"); 
				lector.close();
				fin = true;

				break;	

			default: 

				view.printMessage("--------- \n Opci�n Invalida !! \n---------");

				break;

			}
		}

	}	
}
