package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.data_structures.Node;
import model.data_structures.Vertice;
import model.logic.Maps;
import model.logic.Modelo;
import view.View;

public class Controller {

	private Modelo modelo;
	private View view;
	
	public final static String ESTACIONES = "./data/estacionpolicia.geojson";
	public final static String CREADO="./data/grafoCreado.json";
	
	public final static String RUTAGEOJASON = "./data/Comparendos_DEI_2018_Bogotá_D.C.geojson";
	public final static String JUEGUEMOS = "./data/Comparendos_DEI_2018_Bogotá_D.C_small.geojson";
	public final static String COTEJO = "./data/Comparendos_DEI_2018_Bogotá_D.C_50000_.geojson";

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
				System.out.println("La asiganción de los comparendos fue un exito." + "\n---------------");
				
				break;
				
			case 2: 
				
				System.out.println("Ingrese la latitud deseada:");
				double lati = Double.parseDouble(lector.next());
				
				System.out.println("Ingrese la longitud deseada:");
				double longi = Double.parseDouble(lector.next());
				
				Vertice ganador = modelo.idMinimoAVerti(lati, longi);
				System.out.println("----------------------\n" + "El vertice más cercano es: " + ganador.darId() + "\n----------------------" );
				
				break;
				
			case 3:
				
				Maps mapa = new Maps(modelo);
				System.out.println("Es billísimo");
				mapa.initFrame("Es billísimo");
				
				break;
				
			case 4:

				view.printMessage("--------- \n Hasta pronto !! \n---------"); 
				lector.close();
				fin = true;

				break;	

			default: 

				view.printMessage("--------- \n Opción Invalida !! \n---------");

				break;

			}
		}

	}	
}
