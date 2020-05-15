package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import model.data_structures.Node;
import model.logic.Maps;
import model.logic.Modelo;
import view.View;

public class Controller {

	private Modelo modelo;
	private View view;
	
	public final static String VERTIBOB = "./data/bogota_vertices.txt";
	public final static String ARCAJUANJO = "./data/bogota_arcos.txt";
	public final static String ESTACIONES = "./data/estacionpolicia.geojson.json";
	public final static String CREADO="./data/grafoCreado.json";

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

				modelo.leerTxtVertix(VERTIBOB);
				modelo.leerTxtArc(ARCAJUANJO);
				modelo.leerGeoJsonEstaciones(ESTACIONES); 
				System.out.println("El total de estaciones de policia es: " + modelo.darEstaciones().darTamanio() + "\n----------------------");
				
				break;
			
			case 2:
				
				modelo.imprimirLaCosaBienHecha();
				System.out.println("Se creo el Json del grafo de forma exitosa. \n----------------");

				break;
				
			case 3:
				
				modelo.leerJsonGrafo(CREADO);
				
				int totalVertices = modelo.darGrafo().darV()-1;
				System.out.println("Total de Vertices: " + totalVertices + "\n--------------");
				System.out.println("Numero de arcos: " + modelo.darGrafo().darE() + "\n---------------");
				
				modelo.leerGeoJsonEstaciones(ESTACIONES); 
				System.out.println("El total de estaciones de policia es: " + modelo.darEstaciones().darTamanio() + "\n----------------------");
				
				break;
				
			case 4:
				
				Maps mapa = new Maps(modelo);
				System.out.println("Es billísimo");
				mapa.initFrame("Es billísimo");
				
				break;
				
			case 5:

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
