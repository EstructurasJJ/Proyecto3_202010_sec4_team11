package controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import model.data_structures.Arco;
import model.data_structures.Graph;
import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.MaxHeapCP;
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;
import model.logic.EstPol;
import model.logic.MapMST;
import model.logic.MapZonas;
import model.logic.MapitaSP;
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

				Graph mapi = modelo.darGrafo();
				Maps mapa = new Maps(mapi);
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

					MapitaSP Spam = new MapitaSP(mapita);
					System.out.println("Quiero perico");
					Spam.initFrame("Quiero perico");
				}
				else
				{
					System.out.println("No son validas las coordenadas.");
				}


				break;

			case 5:
				
				System.out.println("Digite el n�mero de lugares para la red");
				int josh = lector.nextInt();
				
				Graph mapita = (Graph) modelo.montarRedDeTombos(josh);

				MapMST Spam = new MapMST(mapita);
				System.out.println("Quiero perico de calidad");
				Spam.initFrame("Quiero perico de calidad");

				break;

			case 6:

				boolean vale=true;

				System.out.println("Digite la latitud inicial:");
				double latI = Double.parseDouble(lector.next());

				System.out.println("Digite la longitud inicial:");
				double lonI = Double.parseDouble(lector.next());

				System.out.println("Digite la latitud de destino");
				double latF = Double.parseDouble(lector.next());

				System.out.println("Digite la longitud de destino:");
				double lonF = Double.parseDouble(lector.next());


				if(latI > MAX_LAT || latI < MIN_LAT) vale = false;
				if(latF > MAX_LAT || latF < MIN_LAT) vale = false;

				if(lonI > MAX_LON || lonI < MIN_LON) vale = false;
				if(lonF > MAX_LON || lonF < MIN_LON) vale = false;


				if(vale)
				{
					Graph soyElMapa = modelo.SPPorComparendos(latI, lonI, latF, lonF);		

					MapitaSP diomedes = new MapitaSP(soyElMapa);
					System.out.println("Quiero g�eler");
					diomedes.initFrame("Quiero g�eler");
				}
				else
				{
					System.out.println("No son validas las coordenadas.");
				}

				break;
				
			case 7: 

				System.out.println("Digite el n�mero de lugares para la red");
				int red=lector.nextInt();


				Graph soyElMapa = modelo.redPorNumComparendos(red); 

				MapMST Zeke = new MapMST(soyElMapa);
				System.out.println("Am�n");
				Zeke.initFrame("Am�n");


				break;
				
			case 8:
				
				System.out.println("Digite el n�mero de comparendos graves que desea atender");
				int graves=lector.nextInt();
				
				Graph yaNoMasSemestre =modelo.atenderMComparendosGraves(graves);
				
				MapitaSP yaNoSeMeOcurrenNombres = new MapitaSP(yaNoMasSemestre);
				System.out.println("Ya el semestre vali�");
				yaNoSeMeOcurrenNombres.initFrame("Ya el semestre vali�");
				
				break;
				
			case 9:

				System.out.println("Digite el n�mero de comparendos que desea mostrar por estaci�n.");
				int numCai = lector.nextInt();
				
				System.out.println("--------------------------");
				System.out.println("Empezamos :)");
				System.out.println("--------------------------");
				
				Graph LosHechiceros = modelo.zonasDeImpacto();
				MaxHeapCP<EstPol> hola = modelo.darCais();
				
				MapZonas queBuenosNombres = new MapZonas(LosHechiceros, hola, numCai);
				System.out.println("Arriba vacaciones");
				queBuenosNombres.initFrame("Arriba vacaciones");
				
				break;
			
			case 10:


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
