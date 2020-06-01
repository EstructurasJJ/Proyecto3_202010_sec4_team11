package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.teamdev.jxmaps.i;

import model.data_structures.Arco;
import model.data_structures.DijkstraSP;
import model.data_structures.Graph;
import model.data_structures.KruskalMST;
import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.ListaEnlazadaStack;
import model.data_structures.MaxHeapCP;
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;
import model.data_structures.VirgilVanDijkstra;


public class Modelo 
{

	//Constantes MAX MIN

	public final static double MIN_LAT = 3.819966340000008;
	public final static double MAX_LAT = 4.836643219999985;
	public final static double MIN_LON = -74.39470032000003;
	public final static double MAX_LON = -73.9546749999999;

	//Atributos necesarios para la lectura desde txt
	private Haversine costoHaversiano;

	//El grafo, su nombre lo dice todo
	private Graph cositaBienHecha = new Graph(1);

	//Atributos encesarios para la carga del JSON de las estaciones de policía
	private String parteDelaEstacion;
	private EstPol porAgregar; 
	private ListaEnlazadaQueue<EstPol> estaciones;
	private boolean coordenadas=false;

	//Atributos necesarios para la carga del JSON del grafo
	private String parteDelVerti;
	private Vertices_Bogota_Info infoPorAgregar;
	private Vertice vertiPorAgregar;
	private ArrayList<Vertice<Integer, Vertices_Bogota_Info>> listaVertices=new ArrayList<Vertice<Integer, Vertices_Bogota_Info>>();
	private boolean coordenadasGrafo=false;

	private int origenPorIngresar, destinoPorIngresar;
	private double costoPorAgregar;

	//Atributos necesarios para la carga de JSON de comparendos.
	private String parteDelComparendo;
	private ListaEnlazadaQueue<Comparendo> booty = new ListaEnlazadaQueue<Comparendo>();
	private boolean coordenadasCompi=false;
	private Comparendo compaAgregar;

	//Atributos necesarios para cargar los sectores

	private ListaEnlazadaQueue[] sectoresOrdenados;
	private int numIntervalos;

	// Reiterar la asignacion
	private int numReAsig = 1; 
	private MaxHeapCP<EstPol> caisOrdenados = new MaxHeapCP<EstPol>(1);

	///////////////////////////////////////////////////////Constructor

	public Modelo()
	{
		parteDelaEstacion = "";
		parteDelVerti="";
		parteDelComparendo = "";

		numIntervalos = 100;
	}

	//////////////////////////////////////////////////////Dar

	public Graph<Integer, Vertices_Bogota_Info> darGrafo()
	{
		return cositaBienHecha;
	}

	public ListaEnlazadaQueue<EstPol> darEstaciones()
	{
		return estaciones;
	}

	public ListaEnlazadaQueue<Comparendo> darComaprendo()
	{
		return booty;
	}
	
	public MaxHeapCP<EstPol> darCais()
	{
		caisOrdenados = new MaxHeapCP<EstPol>(1);
		ordenarEstacionesPorComparendos();
		return caisOrdenados;
	}

	/////////////////////////////////////////////////////////////////////////Lectura del JSON de las estaciones de policía

	public void leerGeoJsonEstaciones (String pRuta)
	{
		estaciones=new ListaEnlazadaQueue<EstPol>();
		JsonParser parser= new JsonParser();
		FileReader fr=null;

		try
		{
			fr= new FileReader(pRuta);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		JsonElement datos=parser.parse(fr);
		dumpJSONElement(datos);
	}

	private void dumpJSONElement(JsonElement elemento)
	{
		if (elemento.isJsonObject()) 
		{
			JsonObject obj = elemento.getAsJsonObject();
			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();

			while (iter.hasNext()) 
			{
				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
				componentesDelComparendo(entrada.getKey());	            
				dumpJSONElement(entrada.getValue());
			}
		}

		else if (elemento.isJsonArray()) 
		{			
			JsonArray array = elemento.getAsJsonArray();
			java.util.Iterator<JsonElement> iter = array.iterator();
			while (iter.hasNext()) 
			{
				JsonElement entrada = iter.next();
				dumpJSONElement(entrada);
			}
		} 
		else if (elemento.isJsonPrimitive()) 
		{
			JsonPrimitive valor = elemento.getAsJsonPrimitive();
			if(porAgregar == null)
			{
				porAgregar = new EstPol();
			}
			if(parteDelaEstacion.equals("OBJECTID"))
			{
				porAgregar.setobjetcID(valor.getAsInt());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPODESCRIP"))
			{
				porAgregar.setdescrip(valor.getAsString());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPODIR_SITIO"))
			{
				porAgregar.setdirSitio(valor.getAsString());
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOLATITUD"))
			{
				porAgregar.setlatitud(valor.getAsDouble());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOLONGITU"))
			{
				porAgregar.setlongitud(valor.getAsDouble());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOTELEFON"))
			{
				porAgregar.settel(valor.getAsString());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOCELECTR"))
			{
				porAgregar.setmail(valor.getAsString());
				//System.out.println(valor);
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPONOMBRE"))
			{				
				porAgregar.setnombre(valor.getAsString());
				//System.out.println(valor);	
				parteDelaEstacion = "";
			}
			else if (parteDelaEstacion.equals("EPOIDENTIF"))
			{				
				porAgregar.setidentificador(valor.getAsString());
				//System.out.println(valor);	
				parteDelaEstacion = "";
				//AGREGAR//

				coordenadas = false;
				parteDelaEstacion = "";

				estaciones.enqueue(porAgregar);
				//System.out.println(porAgregar.darobjetcID());
				porAgregar = null;
				//System.out.println("///AGREGADO///");
			}
			else if (parteDelaEstacion.equals("coordinates"))
			{
				agregarCoordenada(valor.getAsDouble());				
			}
			else
			{
				//Es algo que no nos interesa
			}

		} 
		else if (elemento.isJsonNull()) 
		{
			System.out.println("Es NULL");
		} 
		else 
		{
			System.out.println("Es otra cosa");
		}
	}

	private void componentesDelComparendo(String palabra)
	{
		if (palabra.equals("OBJECTID"))
		{
			parteDelaEstacion = "OBJECTID";
		}
		else if (palabra.equals("EPODESCRIP"))
		{
			parteDelaEstacion = "EPODESCRIP";
		}
		else if (palabra.equals("EPODIR_SITIO"))
		{
			parteDelaEstacion = "EPODIR_SITIO";
		}
		else if (palabra.equals("EPOLATITUD"))
		{
			parteDelaEstacion = "EPOLATITUD";
		}
		else if (palabra.equals("EPOLONGITU"))
		{
			parteDelaEstacion = "EPOLONGITU";
		}
		else if (palabra.equals("EPOTELEFON"))
		{
			parteDelaEstacion = "EPOTELEFON";
		}
		else if (palabra.equals("EPOCELECTR"))
		{
			parteDelaEstacion = "EPOCELECTR";
		}
		else if (palabra.equals("EPONOMBRE"))
		{
			parteDelaEstacion = "EPONOMBRE";
		}
		else if (palabra.equals("EPOIDENTIF"))
		{
			parteDelaEstacion = "EPOIDENTIF";
		}
		else if (palabra.equals("coordinates"))
		{
			parteDelaEstacion = "coordinates";
		}
	}

	private void agregarCoordenada(double pCor)
	{
		if(coordenadas == false)
		{
			porAgregar.setlongitud(pCor);
			//System.out.println("Longitud: " + pCor);
			coordenadas = true;
		}
		else
		{
			porAgregar.setlatitud(pCor);
		}
	}


	//////////////////////////////////////////////////////////////////////Lectura del JSON del grafo

	public void leerJsonGrafo(String pRuta)
	{
		cositaBienHecha = new Graph(1);

		JsonParser parser= new JsonParser();
		FileReader fr=null;

		try
		{
			fr= new FileReader(pRuta);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		JsonElement datos=parser.parse(fr);
		dumpJSONElementGrafo(datos);
	}

	private void dumpJSONElementGrafo(JsonElement elemento)
	{
		if (elemento.isJsonObject()) 
		{
			JsonObject obj = elemento.getAsJsonObject();

			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();

			while (iter.hasNext()) 
			{
				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
				componentesDelGrafo(entrada.getKey());	            

				dumpJSONElementGrafo(entrada.getValue());
			}
		}
		else if (elemento.isJsonArray()) 
		{			
			JsonArray array = elemento.getAsJsonArray();
			java.util.Iterator<JsonElement> iter = array.iterator();

			while (iter.hasNext()) 
			{
				JsonElement entrada = iter.next();
				dumpJSONElementGrafo(entrada);
			}

		}
		else if (elemento.isJsonPrimitive()) 
		{
			JsonPrimitive valor = elemento.getAsJsonPrimitive();

			if(infoPorAgregar == null)
			{
				infoPorAgregar=new Vertices_Bogota_Info (0,0);
			}
			if(parteDelVerti.equals("OBJECTID"))
			{
				vertiPorAgregar = new Vertice(valor.getAsInt(), infoPorAgregar);				
				infoPorAgregar.asignarId(valor.getAsInt());

				//System.out.println(valor);
				parteDelVerti = "";
			}
			else if (parteDelVerti.equals("LATITUD"))
			{
				infoPorAgregar.asignarLat(valor.getAsDouble());

				//System.out.println(compaAgregar.darFecha_Hora().toString());
				parteDelVerti = "";
			}
			else if (parteDelVerti.equals("LONGITUD"))
			{
				infoPorAgregar.asignarLon(valor.getAsDouble());
				vertiPorAgregar.cambiarInfo(infoPorAgregar);

				//Agregar VERTICE
				cositaBienHecha.addVertex(vertiPorAgregar.darId(), vertiPorAgregar);

				parteDelVerti = "";
				vertiPorAgregar=null;
				infoPorAgregar=null;

			}
			else if (parteDelVerti.equals("ORIGEN"))
			{
				origenPorIngresar = valor.getAsInt();
			}

			else if (parteDelVerti.equals("DESTINO"))
			{
				destinoPorIngresar = valor.getAsInt();
			}
			else if (parteDelVerti.equals("COSTO"))
			{
				costoPorAgregar = valor.getAsDouble();

				//Añadir Arco
				cositaBienHecha.addEdge(origenPorIngresar, destinoPorIngresar, costoPorAgregar);

				parteDelVerti = "";
				origenPorIngresar=0;
				destinoPorIngresar=0;
				costoPorAgregar=0;
			}
			else if (parteDelVerti.equals("coordinates"))
			{
				agregarCoordenadaGrafo(valor.getAsDouble());				
			}
			else
			{
				//Es algo que no nos interesa
			}
		} 
		else if (elemento.isJsonNull()) 
		{
			System.out.println("Es NULL");
		} 
		else 
		{
			System.out.println("Es otra cosa");
		}
	}

	private void componentesDelGrafo(String palabra)
	{
		if (palabra.equals("OBJECTID"))
		{
			parteDelVerti = "OBJECTID";
		}
		else if (palabra.equals("LATITUD"))
		{
			parteDelVerti = "LATITUD";
		}
		else if (palabra.equals("LONGITUD"))
		{
			parteDelVerti = "LONGITUD";
		}
		else if (palabra.equals("ORIGEN"))
		{
			parteDelVerti = "ORIGEN";
		}
		else if (palabra.equals("DESTINO"))
		{
			parteDelVerti = "DESTINO";
		}
		else if (palabra.equals("COSTO"))
		{
			parteDelVerti = "COSTO";
		}
		else if (palabra.equals("coordinates"))
		{
			parteDelVerti = "coordinates";
		}
	}

	private void agregarCoordenadaGrafo(double pCor)
	{
		if(coordenadasGrafo == false)
		{
			infoPorAgregar.asignarLat(pCor);
			//System.out.println("Longitud: " + pCor);
			coordenadasGrafo = true;
		}

		else
		{

			infoPorAgregar.asignarLon(pCor);
			//System.out.println("Latitud: " + pCor);

			coordenadasGrafo = false;
		}
	}

	//////////////////////////////////////////////////////////////////////Lectura del Comparendos

	public void leerGeoJsonComparendos(String pRuta) 
	{			
		JsonParser parser = new JsonParser();
		FileReader fr = null;

		try 
		{
			fr = new FileReader(pRuta);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		JsonElement datos = parser.parse(fr);
		dumpJSONElementCompa(datos);

	}

	private void dumpJSONElementCompa(JsonElement elemento) 
	{


		if (elemento.isJsonObject()) 
		{

			JsonObject obj = elemento.getAsJsonObject();

			java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
			java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();

			while (iter.hasNext()) 
			{
				java.util.Map.Entry<String,JsonElement> entrada = iter.next();
				componentesDelComparendoBob(entrada.getKey());	            

				dumpJSONElementCompa(entrada.getValue());
			}

		}
		else if (elemento.isJsonArray()) 
		{			
			JsonArray array = elemento.getAsJsonArray();
			java.util.Iterator<JsonElement> iter = array.iterator();

			while (iter.hasNext()) 
			{
				JsonElement entrada = iter.next();
				dumpJSONElementCompa(entrada);
			}

		} 
		else if (elemento.isJsonPrimitive()) 
		{
			JsonPrimitive valor = elemento.getAsJsonPrimitive();

			if(compaAgregar == null)
			{
				compaAgregar = new Comparendo();
			}

			if(parteDelComparendo.equals("OBJECTID"))
			{
				compaAgregar.asignarObjectid(valor.getAsInt());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("FECHA_HORA"))
			{
				compaAgregar.asignarFecha_Hora(valor.getAsString());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("MEDIO_DETECCION"))
			{
				compaAgregar.asignarMedio_Dete(valor.getAsString());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("CLASE_VEHICULO"))
			{
				compaAgregar.asignarClase_Vehi(valor.getAsString());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("TIPO_SERVICIO"))
			{
				compaAgregar.asignarTipo_Servicio(valor.getAsString());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("INFRACCION"))
			{
				compaAgregar.asignarInfraccion(valor.getAsString());
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("DES_INFRACCION"))
			{
				compaAgregar.asignarDes_Infrac(valor.getAsString());
				parteDelComparendo = "";

			}
			else if (parteDelComparendo.equals("LOCALIDAD"))
			{				
				compaAgregar.asignarLocalidad(valor.getAsString());	
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("MUNICIPIO"))
			{				
				compaAgregar.asignarMunicipio(valor.getAsString());	
				parteDelComparendo = "";
			}
			else if (parteDelComparendo.equals("coordinates"))
			{
				agregarCoordenadaComparendo(valor.getAsDouble());				
			}

		} 
		else if (elemento.isJsonNull()) 
		{
			System.out.println("Es NULL");
		} 
		else 
		{
			System.out.println("Es otra cosa");
		}

	}

	public void componentesDelComparendoBob(String palabra)
	{
		if (palabra.equals("OBJECTID"))
		{
			parteDelComparendo = "OBJECTID";
		}
		else if (palabra.equals("FECHA_HORA"))
		{
			parteDelComparendo = "FECHA_HORA";
		}
		else if (palabra.equals("MEDIO_DETECCION"))
		{
			parteDelComparendo = "MEDIO_DETECCION";
		}
		else if (palabra.equals("CLASE_VEHICULO"))
		{
			parteDelComparendo = "CLASE_VEHICULO";
		}
		else if (palabra.equals("TIPO_SERVICIO"))
		{
			parteDelComparendo = "TIPO_SERVICIO";
		}
		else if (palabra.equals("INFRACCION"))
		{
			parteDelComparendo = "INFRACCION";
		}
		else if (palabra.equals("DES_INFRACCION"))
		{
			parteDelComparendo = "DES_INFRACCION";
		}
		else if (palabra.equals("LOCALIDAD"))
		{
			parteDelComparendo = "LOCALIDAD";
		}
		else if (palabra.equals("MUNICIPIO"))
		{
			parteDelComparendo = "MUNICIPIO";
		}
		else if (palabra.equals("coordinates"))
		{
			parteDelComparendo = "coordinates";
		}
	}

	public void agregarCoordenadaComparendo(double pCor)
	{
		if(coordenadasCompi == false)
		{
			compaAgregar.asignarLongitud(pCor);
			coordenadasCompi = true;
		}

		else
		{
			compaAgregar.asignarLatitud(pCor);

			//AGREGAR//

			coordenadasCompi = false;
			parteDelComparendo = "";

			booty.enqueue(compaAgregar);

			compaAgregar = null;

		}
	}

	////////////////////////////////////////////////////////////////////////Distribución del mapa

	private void generarSectores()
	{
		sectoresOrdenados = new ListaEnlazadaQueue[numIntervalos];

		double tamIntervaloLat=(MAX_LAT-MIN_LAT)/numIntervalos;
		double tamIntervaloLon=(MAX_LON-MIN_LON)/numIntervalos;

		for (int i =1;i<=numIntervalos;i++)
		{
			sectoresOrdenados[i-1]=new ListaEnlazadaQueue();

			for (int j=1;j<=numIntervalos;j++)
			{
				double minLatAux = MIN_LAT + ((i-1)*tamIntervaloLat);
				double maxLatAux = MIN_LAT + (i*tamIntervaloLat);

				double minLonAux = MIN_LON + ((j-1)*tamIntervaloLon);
				double maxLonAux = MIN_LON+ (j*tamIntervaloLon);

				Sector porAgregar = new Sector (minLonAux,maxLonAux,minLatAux,maxLatAux);

				sectoresOrdenados[i-1].enqueue(porAgregar);
			}
		}
	}

	private void agregarVerticeACola(Vertices_Bogota_Info porAgregar)
	{
		double tamIntervaloLat = (MAX_LAT-MIN_LAT)/numIntervalos;
		double tamIntervaloLon = (MAX_LON-MIN_LON)/numIntervalos;


		double cocienteLat = (porAgregar.darLat()-MIN_LAT)/tamIntervaloLat;
		int posLat = (int)Math.floor(cocienteLat);

		double cocienteLon = (porAgregar.darLon()-MIN_LON)/tamIntervaloLon;
		int posLon = (int)Math.floor(cocienteLon);

		//Mitigo el error para maxLat y minLat
		if (posLat==numIntervalos)
			posLat=numIntervalos-1;

		if (posLon==numIntervalos)
			posLon=numIntervalos-1;

		//Recupero la posición del arreglo que contiene esa latitud
		ListaEnlazadaQueue porLat = sectoresOrdenados[posLat];

		//En esta cola porLat, el posLon -ésimo nodo tiene las coordenadas deseadas

		int contadorAux=0;
		Node actual = porLat.darPrimerElemento();

		while (contadorAux < posLon)
		{
			actual = actual.darSiguiente();
			contadorAux++;
		}

		Sector alQueAgrego = (Sector) actual.data;
		alQueAgrego.agregarVertice(porAgregar);


	}

	private void revisarAgregados()
	{
		for (ListaEnlazadaQueue a : sectoresOrdenados)
		{
			Node actual = a.darPrimerElemento();

			while (actual !=null)
			{

				Sector pres = (Sector) actual.data;

				double minlat = pres.darMinLat(), maxlat = pres.darMaxLat(), minlon = pres.darMinLon(), maxlon = pres.darMaxLon();
				ListaEnlazadaQueue vertices = pres.darVerticesAsignados();

				Node actualVertex = vertices.darPrimerElemento();

				while (actualVertex!=null)
				{
					Vertices_Bogota_Info actVer = (Vertices_Bogota_Info) actualVertex.data;
					double latAux = actVer.darLat(), lonAux = actVer.darLon();

					if (!(latAux <=maxlat && latAux >=minlat && lonAux<=maxlon && lonAux>=minlon))
					{
						System.out.println("Mal Agregado");
					}

					actualVertex=actualVertex.darSiguiente();
				}				

				actual=actual.darSiguiente();
			}
		}
	}

	public void crearDivisionMapa()
	{
		generarSectores();

		TablaHashSondeoLineal vertex = cositaBienHecha.vertis;

		for (int i=0;i<cositaBienHecha.darV()-1;i++)
		{
			Vertice aux = (Vertice) vertex.getSet(i);
			Vertices_Bogota_Info info = (Vertices_Bogota_Info) aux.darInfo();

			agregarVerticeACola(info);
		}

		revisarAgregados();

		///////////////////// PARA LAS ESTADISTICAS.

		for (ListaEnlazadaQueue a : sectoresOrdenados)
		{
			Node actual = a.darPrimerElemento();

			int bla = 0;
			while (actual !=null)
			{
				Sector pres = (Sector) actual.data;
				bla = pres.darVerticesAsignados().darTamanio();
				//System.out.println(bla);

				actual = actual.darSiguiente();
			}
		}



	}

	//////////////////////////////////////////////////////////////////////// UBICAR COMPARENDOS	

	private int idMinimoAsignado (Comparendo compi)
	{
		double tamIntervaloLat = (MAX_LAT-MIN_LAT)/numIntervalos;
		double tamIntervaloLon = (MAX_LON-MIN_LON)/numIntervalos;

		double cocienteLat = (compi.darLatitud()-MIN_LAT)/tamIntervaloLat;
		int posLat = (int)Math.floor(cocienteLat);

		double cocienteLon = (compi.darLongitud()-MIN_LON)/tamIntervaloLon;
		int posLon = (int)Math.floor(cocienteLon);

		//Mitigo el error para maxLat y minLat
		if (posLat==numIntervalos)
			posLat=numIntervalos-1;

		if (posLon==numIntervalos)
			posLon=numIntervalos-1;

		//Recupero la posición del arreglo que contiene esa latitud
		ListaEnlazadaQueue porLat = sectoresOrdenados[posLat];

		//En esta cola porLat, el posLon -ésimo nodo tiene las coordenadas deseadas

		int contadorAux=0;
		Node actual = porLat.darPrimerElemento();

		while (contadorAux < posLon && actual != null)
		{
			actual = actual.darSiguiente();
			contadorAux++;
		}

		if (actual == null)
		{
			System.out.println("Micos y tigrillos");
		}

		Sector alQueAgrego = (Sector) actual.data;

		//BUSCAR EL MINIMO DE LOS VERTICES.
		int idGANADOR = encontrarMInimo(alQueAgrego, compi);

		return idGANADOR;

	}

	private int encontrarMInimo (Sector match, Comparendo compi)
	{
		Node actual = match.darVerticesAsignados().darPrimerElemento();
		double max = 1000000;
		int idgana = 0;

		double latCompa = compi.darLatitud();
		double lonCompa = compi.darLongitud();

		while (actual != null)
		{
			Vertices_Bogota_Info infoActual = (Vertices_Bogota_Info) actual.darData();

			double latAux = infoActual.darLat();
			double lonAux = infoActual.darLon();

			double costoActu = costoHaversiano.distance(latAux, lonAux, latCompa, lonCompa);

			if(costoActu < max)
			{
				max = costoActu;
				idgana = infoActual.darId();
			}

			actual = actual.darSiguiente();
		}

		return idgana;

	}

	public void asigancionComparendos()
	{
		Node<Comparendo> actual = booty.darPrimerElemento();
		TablaHashSondeoLineal vertix = cositaBienHecha.vertis;

		//ASIGNAR LOS COMPARENDOs A LOS VERTICES.

		while (actual != null)
		{	
			Comparendo actuCompi = actual.darData();

			int idVerticeGanador = idMinimoAsignado(actuCompi);
			Vertice ganador = (Vertice) vertix.getSet(idVerticeGanador);

			ganador.aumentarMatch();

			actual = actual.darSiguiente();
		}

		//ASIGNAR LOS COMPARENDOS A LOS ARCOS.

		ListaEnlazadaQueue arquitos = cositaBienHecha.arcos;
		long cantidad = 0;

		Node arqui = arquitos.darPrimerElemento();

		while(arqui != null)
		{
			Arco actu = (Arco) arqui.darData();

			Vertice inicial = actu.darInicial();
			Vertice fin = actu.darFinal();

			cantidad = inicial.darMatch() + fin.darMatch();
			actu.asignarCantidad(cantidad);

			arqui = arqui.darSiguiente();
		}


	}

	////////////////////////////////////////////////////////////////////////ENCONTRAR VERTICE

	public Vertice idMinimoAVerti (double lati, double longi)
	{
		TablaHashSondeoLineal vertix = cositaBienHecha.vertis;

		double tamIntervaloLat = (MAX_LAT-MIN_LAT)/numIntervalos;
		double tamIntervaloLon = (MAX_LON-MIN_LON)/numIntervalos;

		double cocienteLat = (lati-MIN_LAT)/tamIntervaloLat;
		int posLat = (int)Math.floor(cocienteLat);

		double cocienteLon = (longi-MIN_LON)/tamIntervaloLon;
		int posLon = (int)Math.floor(cocienteLon);

		//Mitigo el error para maxLat y minLat
		if (posLat==numIntervalos)
			posLat=numIntervalos-1;

		if (posLon==numIntervalos)
			posLon=numIntervalos-1;

		//Recupero la posición del arreglo que contiene esa latitud
		ListaEnlazadaQueue porLat = sectoresOrdenados[posLat];

		//En esta cola porLat, el posLon -ésimo nodo tiene las coordenadas deseadas

		int contadorAux=0;
		Node actual = porLat.darPrimerElemento();

		while (contadorAux < posLon && actual != null)
		{
			actual = actual.darSiguiente();
			contadorAux++;
		}

		if (actual == null)
		{
			System.out.println("Micos y tigrillos");
		}

		Sector alQueAgrego = (Sector) actual.data;

		//BUSCAR EL MINIMO DE LOS VERTICES.
		int idGANADOR = encontrarMinimo(alQueAgrego, lati, longi);
		Vertice ganador = (Vertice) vertix.getSet(idGANADOR);

		return ganador;

	}

	private int encontrarMinimo (Sector match, double lati, double longi)
	{
		Node actual = match.darVerticesAsignados().darPrimerElemento();
		double max = 1000000;
		int idgana = 0;

		while (actual != null)
		{
			Vertices_Bogota_Info infoActual = (Vertices_Bogota_Info) actual.darData();

			double latAux = infoActual.darLat();
			double lonAux = infoActual.darLon();

			double costoActu = costoHaversiano.distance(latAux, lonAux, lati, longi);

			if(costoActu < max)
			{
				max = costoActu;
				idgana = infoActual.darId();
			}

			actual = actual.darSiguiente();
		}

		return idgana;

	}

	//////////////////////////////////////////////////////////
	//////////////////// REQUERIMIENTOS //////////////////////
	//////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////// Estudiante A

	//1. Obtener el camino de costo mínimo entre dos ubicaciones geográficas por distancia

	public Graph SPDosUbicaciones (double lat1, double lon1, double lat2, double lon2 )
	{
		int totalVer = 1;
		double costo = 0;
		double distancia = 0;

		/////AÑADO AL MAPA EL INICIO Y FIN.

		Vertice inicio = idMinimoAVerti(lat1, lon1);
		Vertice fin = idMinimoAVerti(lat2, lon2);

		Graph mapita = new Graph(1);
		mapita.addVertex(inicio.darId(), inicio);
		mapita.addVertex(fin.darId(), fin);

		System.out.println("------------");
		System.out.println("Vertice de inicio: " + inicio.darId());
		System.out.println("Vertice destino: " + fin.darId() + "\n---------------");

		/////BUSCO EL CAMINO MÁS CORTO.

		DijkstraSP SP = new DijkstraSP(cositaBienHecha, inicio);
		ListaEnlazadaStack ruta = SP.pathTo(fin);

		if (ruta != null)
		{			
			while(ruta.darTamaño() > 0)
			{
				///// AÑADO EL ARCO AL MAPA E IMPRIMO SU RUTA.
				Arco actual = (Arco) ruta.pop();

				Vertice inici = actual.darInicial();
				Vertice fini = actual.darFinal();

				int idInicio = (int) actual.darInicial().darId();
				int idDestino = (int) actual.darFinal().darId();
				double costoActu = actual.darCostoHaversiano();

				mapita.addVertex(idInicio, inici);
				mapita.addVertex(idDestino, fini);

				mapita.addEdge(idInicio, idDestino, costoActu);

				Vertices_Bogota_Info infoDestino = (Vertices_Bogota_Info) actual.darFinal().darInfo();
				double lon = infoDestino.darLon();
				double lat = infoDestino.darLat();

				System.out.println("Vamos en: " + idDestino + ", Long: " + lon +", Lat: " + lat);

				totalVer++;
				costo += actual.darCostoHaversiano();

			}

			System.out.println("---------\n" + " Hemos llegado al destino. Serian 50k." + "\n-------");

			System.out.println("El número de vertices es: "+ totalVer);
			System.out.println("La distancia es: "+ costo + "\n-----------");
		}
		else 
		{
			System.out.println("No existe camino entre esos dos vertices." + "\n---------");
		}

		return mapita;

	}


	//2. Camaras de video por gravedad del comparendo. 

	public Graph montarRedDeTombos(int N)
	{
		Graph drake = new Graph(1);
		ListaEnlazadaQueue<Comparendo> megan = darNComparendosGraves(N);
		System.out.println("Listo los " + N + " comparendos más graves." );

		Vertice[] josh= new Vertice [N];
		double contoTotal = 0.0;

		// Vamos a agregar todos los vertices. 
		// Asignaremos cada comparendo a su vertice más cercano. 

		Node actu = megan.darPrimerElemento();
		int pos = 0;

		while (actu != null)
		{
			Comparendo actual = (Comparendo) actu.darData();
			double lat = actual.darLatitud();
			double lon = actual.darLongitud();

			Vertice actuAñadir = idMinimoAVerti(lat, lon);
			drake.addVertex(actuAñadir.darId(), actuAñadir);
			josh[pos] = actuAñadir;

			actu = actu.darSiguiente();
			pos++;
		}

		// Vamos a armar el grafo que implican estos vertices. 
		// Haremos uso de Disj... para buscar los menores costos

		for(int i = 0; i < josh.length; i++)
		{
			DijkstraSP sp = new DijkstraSP(cositaBienHecha, josh[i]);

			for(int j = i+1; j < josh.length; j++)
			{
				//Se encuentra el menor camino en la matriz triangular y se agrega al grafo
				//Todavía no se calcula el costo porque eso se hace del MST

				ListaEnlazadaStack camino = sp.pathTo(josh[j]);

				if(camino != null)
				{
					while(camino.darTamaño() > 0)
					{
						Arco actual = (Arco) camino.pop();

						Vertice inici = actual.darInicial();
						Vertice fini = actual.darFinal();

						int idInicio = (int) actual.darInicial().darId();
						int idDestino = (int) actual.darFinal().darId();
						double costoActu = actual.darCostoHaversiano();

						//Para que no se agreguen dos veces los vértices:

						if (!drake.existeVertice(idInicio)) drake.addVertex(idInicio, inici);
						if (!drake.existeVertice(idDestino))drake.addVertex(idDestino, fini);

						if (drake.existeVertice(idInicio) && drake.existeVertice(idDestino))
						{
							drake.addEdge(idInicio, idDestino, costoActu);
						}

					}
				}
			}

		}

		System.out.println("Listo el subgrafo, vamos a hacer el MST.");


		// Ya con el subgrafo creado, el cual no tiene ni todos los vertices ni arcos.
		// Lo vamos a mandar a hacer MST y ese es el que pintaremos. 

		Graph hermanitos = vamosAverComoQuedo(drake);
		return hermanitos;
	}

	private Graph vamosAverComoQuedo (Graph Drake)
	{		
		KruskalMST vamosPibe = new KruskalMST(Drake);
		ListaEnlazadaQueue queSePuede = vamosPibe.darMST();
		Graph mapita = new Graph(1);
		double total = 0;

		if(queSePuede != null)
		{
			Node actual = queSePuede.darPrimerElemento();
			while(actual != null)
			{
				Arco arqui = (Arco) actual.darData();

				Vertice inicio = arqui.darInicial();
				Vertice fin = arqui.darFinal();

				int idInicio = (int) inicio.darId();
				int idFin = (int) fin.darId();
				double costo = arqui.darCostoHaversiano();
				total = total + costo;

				if(!mapita.existeVertice(idInicio)) mapita.addVertex(idInicio, inicio);
				if(!mapita.existeVertice(idFin))	mapita.addVertex(idFin, fin);
				mapita.addEdge(idInicio, idFin, costo);

				//Ir reportando los vertices !
				System.out.println("Inicio: " + idInicio + " --> Final: " + idFin);

				actual = actual.darSiguiente();
			}
		}
		else
		{
			System.out.println("Micos y tigrillos...");
		}


		System.out.println("Total de vertices: " + (mapita.darV()-1));
		System.out.println("Total de arcos: " + (mapita.darE()));
		System.out.println("Distancia total: " + total);
		System.out.println("Costo total ($$$): " + (total*10000));

		return mapita;
	}

	//Ordenar Comparendos

	private ListaEnlazadaQueue darNComparendosGraves(int N)
	{
		ListaEnlazadaQueue<Comparendo> resultados = new ListaEnlazadaQueue<Comparendo>();
		MaxHeapCP<Comparendo> temporal = new MaxHeapCP<Comparendo>(1);

		Node actual = booty.darPrimerElemento();

		while(actual != null)
		{
			Comparendo añadir = (Comparendo) actual.darData();
			temporal.añadir(añadir);		
			actual = actual.darSiguiente();
		}

		int conta = 0;
		while(conta < N)
		{
			Comparendo mejor = temporal.devolverMax();
			resultados.enqueue(mejor);
			conta++;
		}

		return resultados;		
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////ESTUDIANTE AMÉRICA DE CALI///////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////

	//Requerimiento 1: obtener el camino más corto por comparendos

	public Graph SPPorComparendos(double lat1, double lon1, double lat2, double lon2)
	{
		int totalVer=1;
		double costo=0;
		double distancia=0;


		//Se añaden al mapa los vértices de inicio y fin
		Vertice inicio= idMinimoAVerti(lat1, lon1);
		Vertice fin=idMinimoAVerti(lat2, lon2);

		Graph mapita = new Graph (1);
		mapita.addVertex(inicio.darId(), inicio);
		mapita.addVertex(fin.darId(), fin);

		System.out.println("----------------------");
		System.out.println("Vértice de inicio: "+inicio.darId());
		System.out.println("Vértice de destino: "+fin.darId()+"\n-------------------");


		//Buscar el camino más corto

		VirgilVanDijkstra SP= new VirgilVanDijkstra (cositaBienHecha,inicio);
		ListaEnlazadaStack ruta = SP.pathTo(fin); 


		if (ruta!=null)
		{
			while(ruta.darTamaño()>0)
			{
				//Añadir el arco e imprimir su ruta
				Arco actual=(Arco)ruta.pop();

				Vertice inici = actual.darInicial();
				Vertice fini = actual.darFinal();

				int idInicio = (int) actual.darInicial().darId();
				int idDestino = (int) actual.darFinal().darId();
				double costoActu = actual.darCantidad();

				mapita.addVertex(idInicio, inici);
				mapita.addVertex(idDestino, fini);

				mapita.addEdge(idInicio, idDestino, costoActu);
				Vertices_Bogota_Info infoDestino = (Vertices_Bogota_Info) actual.darFinal().darInfo();
				double lon = infoDestino.darLon();
				double lat = infoDestino.darLat();

				System.out.println("Vamos en: " + idDestino + ", Long: " + lon +", Lat: " + lat);

				totalVer++;
				costo += actual.darCantidad();
				distancia+=actual.darCostoHaversiano();


			}

			System.out.println("---------\n" + " Hemos llegado al destino. El estudiante B no es tan carero y cobra 30K" + "\n-------");

			System.out.println("El número de vértices fue de: "+ totalVer);
			System.out.println("El número de comparendos encontrados en la ruta es: "+ costo + "\n-----------");
			System.out.println("La distancia Haversiana recorrida total fue: "+ distancia);

		}

		else 
		{
			System.out.println("No existe camino entre esos dos vertices." + "\n---------");
		}

		return mapita;

	}

	//Requerimiento 2: Determinar la red de comunicaciones que soporte la instalación de cámaras de video en los M puntos donde se presenta el mayor número de comparendos en la ciudad

	public Graph pintarMST(Graph Zack)
	{
		KruskalMST Cody = new KruskalMST(Zack);
		ListaEnlazadaQueue moseby = Cody.darMST();
		Graph tipton = new Graph(1);
		double total=0;

		if (moseby!=null)
		{
			Node actual = moseby.darPrimerElemento();

			while (actual!=null)
			{
				Arco arqui = (Arco) actual.darData();

				Vertice inicio = arqui.darInicial();
				Vertice fin = arqui.darFinal();

				int idInicio = (int) inicio.darId();
				int idFin = (int) fin.darId();
				double costo = arqui.darCostoHaversiano();
				total = total + costo;

				if(!tipton.existeVertice(idInicio)) tipton.addVertex(idInicio, inicio);
				if(!tipton.existeVertice(idFin))    tipton.addVertex(idFin, fin);
				if(tipton.existeVertice(idInicio) && tipton.existeVertice(idFin)) tipton.addEdge(idInicio, idFin, costo);

				actual = actual.darSiguiente();
			}
		}


		System.out.println("Los Vértices implicados en el mst fueron:");
		int l=0;


		for (int i =0;i<cositaBienHecha.darV()-1;i++)
		{
			Vertice actual = (Vertice)tipton.vertis.getSet(i);

			if (actual!=null)
			{
				l ++;
				System.out.print(actual.darId()+"-");

				if(l % 20==0)
				{
					System.out.println("");
				}



			}

		}


		ListaEnlazadaQueue arcs=tipton.arcos;
		Node a=arcs.darPrimerElemento();
		System.out.println();
		System.out.println("Los arcos implicados en el proceso fueron:");
		l=0;
		while (a!=null)
		{

			Arco act=(Arco)a.data;

			System.out.print(act.darInicial().darId()+"->"+act.darFinal().darId() + "///");
			l++;

			if (l % 15 == 0)
			{
				System.out.println();
			}

			a=a.darSiguiente();
		}

		System.out.println("");
		System.out.println("Total de vertices: " + (tipton.darV()-1));
		System.out.println("Total de arcos: " + (tipton.darE()));
		System.out.println("Distancia total de la red: " + total);

		System.out.println("Costo total de la red: $"+total*10000);

		return tipton;


	}

	public Graph redPorNumComparendos(int M)
	{
		Vertice[] vertisDeMaxComparendos = organizarVertisPorComparendos(M);
		Graph mapirri=new Graph(1);
		double costoTotal=0;


		//Agrego todos los vértices al grafo
		for(Vertice v: vertisDeMaxComparendos)
		{
			mapirri.addVertex(v.darId(), v);
		}


		for (int i =0;i<vertisDeMaxComparendos.length;i++)
		{

			DijkstraSP sp=new DijkstraSP(cositaBienHecha, vertisDeMaxComparendos[i]);

			for (int j=i+1;j<vertisDeMaxComparendos.length;j++)
			{
				//Se encuentra el menor camino en la matriz triangular y se agrega al grafo
				//Todavía no se calcula el costo porque eso se hace del MST

				ListaEnlazadaStack ruta = sp.pathTo(vertisDeMaxComparendos[j]);

				if (ruta!=null)
				{
					while (ruta.darTamaño()>0)
					{
						//Añadir el arco al mapa e imprimir su ruta
						Arco actual = (Arco) ruta.pop();

						Vertice inici = actual.darInicial();
						Vertice fini = actual.darFinal();

						int idInicio = (int) actual.darInicial().darId();
						int idDestino = (int) actual.darFinal().darId();
						double costoActu = actual.darCostoHaversiano();

						//Para que no se agreguen dos veces los vértices:

						if (!mapirri.existeVertice(idInicio)) mapirri.addVertex(idInicio, inici);
						if (!mapirri.existeVertice(idDestino))mapirri.addVertex(idDestino, fini);


						mapirri.addEdge(idInicio, idDestino, costoActu);

					}
				}
			}
		}

		Graph respuesta = pintarMST(mapirri);
		return respuesta;

	}

	private Vertice[] organizarVertisPorComparendos(int M)
	{
		Vertice[] maxVertis= new Vertice [M];
		int pos;
		boolean salir=false;
		int contadorNull=M;


		//Recorrido total de los vértices para ver cuáles son los de mayor número de comparendos
		for (int i =0;i<cositaBienHecha.darV()-1;i++)
		{
			Vertice actual =(Vertice) cositaBienHecha.vertis.getSet(i);
			salir=false;

			//Se tiene que evaluar si tiene más comparendos con respecto al que guardo. 
			//El primero se agrega automáticamente para que comiencen las comparaciones

			if (i==0)
			{
				maxVertis[M-1]=actual;
				contadorNull--;
			}


			//Que no entre si es menor al último de la lista
			if (actual.darMatch()<=maxVertis[M-1].darMatch())
				salir=true;

			//Si ya está lleno y es el nuevo mayor

			if (maxVertis[0]!=null && actual.darMatch()>maxVertis[0].darMatch())
			{
				ajustarArregloDerecha(0, M, maxVertis, actual);
				salir=true;
			}

			//Se revisa en orden inverso con los que he agregado hasta ahora 

			for (int j=M-1;j>contadorNull && !salir;j--)
			{

				if (actual.darMatch()>maxVertis[j].darMatch() && actual.darMatch()<=maxVertis[j-1].darMatch()) 
				{
					//Ajustar el Arreglo
					if (contadorNull<=0)
					{
						ajustarArregloDerecha(j, M, maxVertis, actual);
					}
					else
					{
						ajustarArregloIzquierda(j,M,maxVertis,actual);
					}

					salir=true;
					contadorNull--;
				}
			}

			//Si es el nuevo mayor pero el arreglo no está lleno

			if(contadorNull>0 && maxVertis[M-1].darId() != actual.darId() && actual.darMatch()>=maxVertis[contadorNull].darMatch())
			{
				maxVertis[contadorNull-1]=actual;
				contadorNull--;
			}


		}

		return maxVertis;

	}

	private void ajustarArregloDerecha(int pos,int M, Vertice[] arreglo, Vertice porAgregar) 
	{
		Vertice anterior1=arreglo[pos],anterior2;
		arreglo[pos]=porAgregar;

		for (int i=pos;i<M-1;i++)
		{

			anterior2=arreglo[i+1];

			arreglo[i+1]=anterior1;

			anterior1=anterior2;

		}

	}

	private void ajustarArregloIzquierda(int pos, int M, Vertice[] arreglo, Vertice porAgregar)
	{
		Vertice anterior1=arreglo[pos-1], anterior2;
		arreglo[pos-1]=porAgregar;

		for (int i=pos-1;i>0;i--)
		{
			anterior2=arreglo[i-1];

			arreglo[i-1]=anterior1;

			anterior1=anterior2;
		}
	}


	/////////////////////////////////////////////////////////////////////////////////
	////////////////////////REQUERIMIENTOS AMBOS ESTUDIANTES/////////////////////////
	/////////////////////////////////////////////////////////////////////////////////


	//1. Obtener los caminos más cortos para que los policías puedan atender los M comparendos más graves

	//Asigno a las estaciones los vértices a los que son más cercanos

	public Graph atenderMComparendosGraves(int M)
	{
		asignarVertisAEstaciones();
		Comparendo[] graves = ordenarComparendosPorGravedad(M);
		int[] idEstaciones = new int[M];
		ListaEnlazadaStack[] caminos = new ListaEnlazadaStack[M];
		double[] menorCosto=new double[M];

		for (ListaEnlazadaStack l: caminos)
			l=new ListaEnlazadaStack();

		Graph yaNoMasSemestre = new Graph(1);

		//Teniendo los comparendos y las estaciones de policía tengo que conseguir el camino más corto entre todos

		for (int k =0;k<graves.length;k++)
		{
			//Encuentro el vértice más cercano
			menorCosto[k]=1000000000;
			Vertice loca = idMinimoAVerti(graves[k].darLatitud(), graves[k].darLongitud());

			DijkstraSP virgil = new DijkstraSP(cositaBienHecha, loca);

			//Encuentro el camino más cercano con cada estación

			Node est=estaciones.darPrimerElemento();

			while (est!=null)
			{

				EstPol aux = (EstPol)est.data;
				Vertice cai = (Vertice)cositaBienHecha.vertis.getSet(aux.darVertiAsociado());

				ListaEnlazadaStack ruta = virgil.pathTo(cai);

				if (ruta!=null && ruta.darTamaño()>0)
				{
					double costoActual = calcularCostoRuta(ruta);

					if (costoActual<menorCosto[k])
					{
						menorCosto[k]=costoActual;
						caminos[k]=ruta;
						idEstaciones[k]=aux.darobjetcID();
					}
				}


				est=est.darSiguiente();
			}

		}

		//Ya tengo en arreglos los comparendos, los caminos más baratos y sus costos
		//Agrego al grafo todas las rutas, y luego imprimo los costos para cada comparendo


		for (int i=0;i<M;i++)
		{

			System.out.println("Para el comparendo de ID: "+graves[i].darObjectid()+", se va a la estación con ID: "+idEstaciones[i]+" pasando por los siguientes arcos: ");


			ListaEnlazadaStack parcial=caminos[i];

			agregarRutaAGrafo(yaNoMasSemestre, parcial);

			System.out.println("Por un costo de: "+menorCosto[i]);
			System.out.println("");

		}


		return yaNoMasSemestre;


	}

	public void asignarVertisAEstaciones()
	{
		Node actual = estaciones.darPrimerElemento();

		while (actual!=null)
		{
			EstPol aux=(EstPol)actual.data;

			Vertice ganador = idMinimoAVerti(aux.darlatitud(), aux.darlongitud());

			aux.setVertice((int)ganador.darId());

			actual=actual.darSiguiente();
		}

	}

	private Comparendo[] ordenarComparendosPorGravedad(int M)
	{
		Comparendo[] graves=new Comparendo[M];
		int pos;
		boolean salir=false;
		int contadorNull=M;

		//Recorro toda la cola de comparendos
		Node aux = booty.darPrimerElemento();

		while (aux!=null)
		{
			Comparendo actual =(Comparendo) aux.data;

			salir = false;


			//Si es el primer elemento de la lista lo agrego para poder comenzar la comparación
			if (((Comparendo)aux.data).darObjectid() == ((Comparendo)booty.darPrimerElemento().data).darObjectid())
			{
				graves[M-1]=actual;
				contadorNull--;
			}

			//Que no entre si es menor al último de la lista

			if (actual.compareTo(graves[M-1])<=0)
				salir=true;

			//Si está lleno y es el nuevo mayor de todos

			if (graves[0]!=null && actual.compareTo(graves[0])>0)
			{
				ajustarArregloDerechaCompis(0,M,graves,actual);
				salir=true;
			}

			//Se revisa en orden inverso con los que llevo hasta ahora

			for (int i =M-1;i>contadorNull && !salir;i--)
			{
				if (actual.compareTo(graves[i])>0 && actual.compareTo(graves[i-1])<=0)
				{
					//Ajustar el arreglo
					if(contadorNull<=0)
					{
						ajustarArregloDerechaCompis(i,M,graves,actual);
					}
					else
					{
						ajustarArregloIzquierdaCompis(i,M,graves,actual);
					}
					salir=true;
					contadorNull--;
				}
			}

			//Si es el nuevo mayor pero el arreglo no está lleno

			if (contadorNull>0 && graves[M-1].darObjectid()!=actual.darObjectid() && actual.compareTo(graves[contadorNull])>=0)
			{
				graves[contadorNull-1]=actual;
				contadorNull--;
			}

			aux=aux.darSiguiente();

		}


		return graves;
	}

	private void ajustarArregloDerechaCompis(int pos,int M, Comparendo[] arreglo, Comparendo porAgregar)
	{
		Comparendo anterior1=arreglo[pos],anterior2;
		arreglo[pos]=porAgregar;

		for (int i=pos;i<M-1;i++)
		{

			anterior2=arreglo[i+1];

			arreglo[i+1]=anterior1;

			anterior1=anterior2;

		}
	}

	private void ajustarArregloIzquierdaCompis(int pos, int M, Comparendo[] arreglo, Comparendo porAgregar)
	{
		Comparendo anterior1=arreglo[pos-1], anterior2;
		arreglo[pos-1]=porAgregar;

		for (int i=pos-1;i>0;i--)
		{
			anterior2=arreglo[i-1];

			arreglo[i-1]=anterior1;

			anterior1=anterior2;
		}
	}

	private double calcularCostoRuta(ListaEnlazadaStack ruta)
	{
		double respuesta=0;

		ListaEnlazadaStack carnada=new ListaEnlazadaStack();

		while (ruta.darTamaño()>0)
		{
			Arco actual =(Arco)ruta.pop();
			carnada.push(actual);

			respuesta+=actual.darCostoHaversiano();
		}

		//Devuelvo la pila a como llegó

		while (carnada.darTamaño()>0)
			ruta.push(carnada.pop());


		return respuesta;
	}

	private void agregarRutaAGrafo(Graph grafo, ListaEnlazadaStack ruta)
	{
		while (ruta.darTamaño()>0)
		{
			//Añado todos los arcos y los vértices, en caso de que no los tenga

			Arco actual = (Arco) ruta.pop();

			Vertice inici=actual.darInicial();
			Vertice fini = actual.darFinal();

			int idInicio=(int)inici.darId();
			int idDestino=(int)fini.darId();

			//Para que no hayan vértices por duplicado


			if (!grafo.existeVertice(idInicio)) grafo.addVertex(idInicio, inici);
			if (!grafo.existeVertice(idDestino))grafo.addVertex(idDestino, fini);

			grafo.addEdge(idInicio, idDestino, actual.darCostoHaversiano());

			System.out.println( idInicio +"---->"+idDestino);
		}
	}


	///////////////////2. Identificar las zonas de impacto de las estaciones de policía.

		// 1. Asignar los comparendos a las estaciones. 
		// 2. Repasar que haya ruta entre las estaciones y los comparendos (SP). 
		// 3. Reasignar aquellos que no tengan ruta. 
		// 4. Parar cuando todos esten asignados con una ruta viable. 


	// Asignamos los comparendos con base a lo pedido.
	public Graph zonasDeImpacto()
	{
		asignarComparendosEstacion(booty, false, 1);
		policiasEnAccion();
		System.out.println("Vamos a proceder con pintar el grafo y el reporte final.");
		System.out.println("-------------------------------------");
		Graph SacYcody = grafoSuperPlayQueMeTieneSinCabeza();
		return SacYcody;
	}

	// Con base a la asignación vamos a ver cuantos fueron correctos y guardar aquellos que no.
	private void policiasEnAccion()
	{
		// Asigno los comparendos.
		ListaEnlazadaQueue<Comparendo> reAsignar = new ListaEnlazadaQueue<Comparendo>();

		// Recorro todas las estaciones.
		Node revision = estaciones.darPrimerElemento();

		while(revision != null)
		{
			//Consigo el vertice de la estación.
			EstPol actual = (EstPol) revision.darData();
			Vertice cai = idMinimoAVerti(actual.darlatitud(), actual.darlongitud());

			// Hago su Virgil
			DijkstraSP sp = new DijkstraSP(cositaBienHecha, cai);

			//Recorro los vertices 
			ArrayList<Comparendo> asignados = actual.darRetenidos();

			for(int i = 0; i < asignados.size(); i++)
			{
				// Accedo al comparendo.
				Comparendo compi = asignados.get(i);
				double latCompi = compi.darLatitud();
				double lonCompi = compi.darLongitud();

				// Encuentro su vertice asociado.
				Vertice idVertiCompa = idMinimoAVerti(latCompi, lonCompi);

				//Reviso si lo tengo que reasignar.
				if(!sp.hasPathTo(idVertiCompa))
				{
					// Lo añado al reasignar y lo elimino de la estación actual.
					reAsignar.enqueue(compi);
					asignados.remove(compi);
				}

			}

			//Avanzo al siguiente.
			revision = revision.darSiguiente();

		}

		System.out.println("-----------------------------");

		// Vamos a mandar a reasignar aquellos que están mal. 
		if(reAsignar.darTamanio()>0)
		{
			System.out.println("Toca reasignar: " + reAsignar.darTamanio());
			System.out.println("Re asig: " + numReAsig);

			if(numReAsig < 5)
			{
				reasignar(reAsignar);
			}
			else
			{
				System.out.println("Concideramos inconsistente encontrar una mejor solución para estos comparendos.");
				System.out.println("--------------------------------------------------------");
			}

		}
		else
		{
			System.out.println("La asignación fue un exito.");
		}

	}

	// Asignar todos los comparendos a una estación.
	private void asignarComparendosEstacion(ListaEnlazadaQueue<Comparendo> asignar, boolean reasig, int n)
	{
		Node presente = asignar.darPrimerElemento();
		EstPol masCerca = null;

		int dondeVoy = 0;

		while(presente != null)
		{
			// Accedo al comparendo.
			Comparendo actual = (Comparendo) presente.darData();
			double latCompi = actual.darLatitud();
			double lonCompi = actual.darLongitud();

			// Encuentro su vertice asociado.
			Vertice idVertiCompa = idMinimoAVerti(latCompi, lonCompi);

			// Encuentro su mejor estación --> Al ser primera asignación, busco el mejor sin ignorar ninguno.
			if(!reasig)
			{
				masCerca = MinimaEstacion(idVertiCompa, false, 1);
			}
			else
			{
				masCerca = MinimaEstacion(idVertiCompa, true, n);
			}


			// Agrego el comparendo a esa estación.
			if(actual == null || masCerca == null)
			{
				System.out.println("Micos y Bob");
				continue;
			}

			masCerca.añadirDetenido(actual);

			// Sigo al siguiente comparendo.
			presente = presente.darSiguiente();

			//Verificar donde voy.
			dondeVoy++;
			if(!reasig)
			{
				if(dondeVoy % 2500 == 0)
				{
					System.out.println("Vamos en: " + dondeVoy + " asignados.");
				}
			}
		}

//		System.out.println("-----------------------------");
//		Node verificar = estaciones.darPrimerElemento();
//
//		while(verificar != null)
//		{
//			EstPol actual = (EstPol) verificar.darData();
//			ArrayList<Comparendo> compaAsignados = actual.darRetenidos();
//
//			int compasAsig = compaAsignados.size();
//			int objId = actual.darobjetcID();
//
//			System.out.println("La estación: " + objId + " tiene " + compasAsig + " comparendos asignados.");
//
//			verificar = verificar.darSiguiente();
//		}
	}

	// Recibe un vertice y devuelve el id de la mejor estación.
	private EstPol MinimaEstacion (Vertice paraAsignar, boolean ignorarPasados, int n)
	{
		int idGanador = 0;
		double min = 10000000;
		double costo = 0.0;
		EstPol mejor = null;
		ListaEnlazadaStack<EstPol> mejores = new ListaEnlazadaStack<EstPol>();

		Node actual = estaciones.darPrimerElemento();

		while(actual != null)
		{
			// Recorro las estaciones.
			EstPol feoCai = (EstPol) actual.darData();
			Vertice ganador = idMinimoAVerti(feoCai.darlatitud(), feoCai.darlongitud());

			// Info del vertice de la policia.
			Vertices_Bogota_Info infoGanador = (Vertices_Bogota_Info) ganador.darInfo();
			double lat = infoGanador.darLat();
			double lon = infoGanador.darLon();

			// Info del vertice de la estación.
			Vertices_Bogota_Info infoCompi = (Vertices_Bogota_Info) paraAsignar.darInfo();
			double latCompi = infoCompi.darLat();
			double lonCompi = infoCompi.darLon();

			// Calculamos distancia
			costo = costoHaversiano.distance(lat, lon, latCompi, lonCompi);
			costo = Math.abs(costo);

			//Ver si es la menor distancia.
			if (costo < min)
			{
				min = costo;

				idGanador = (int) ganador.darId();
				mejores.push(feoCai);

				mejor = feoCai;
			}

			actual = actual.darSiguiente();	
		}

		if(!ignorarPasados)
		{
			return mejores.pop();
		}
		else
		{
			EstPol esteEs = null;
			int contador = 0;

			while(contador < n)
			{
				esteEs = mejores.pop();
				contador++;
			}

			if(esteEs == null) esteEs = mejor; 

			return esteEs;
		}


	}

	// Reasigna los comparendos. 
	private void reasignar(ListaEnlazadaQueue<Comparendo> reasigi)
	{
		++numReAsig;
		asignarComparendosEstacion(reasigi, true, numReAsig);
		policiasEnAccion();
	}

	// Generar reporte final solicitado, con base al grafo a pintar y persistir. 
	private Graph grafoSuperPlayQueMeTieneSinCabeza()
	{
		// 1. Vamos a añadir el vertice de policia.
		// 2. Vamos a añadir sus comparendos 
			// Armando el arco como --> Vertice Policia - Vertice Comparendo
			// Esta arquitectura servira para pintar correctamente.
		// 3. Repetiremos con base a todas las estaciones-
		// NOTA: Para facilidad, ordenaremos primero las estaciones por cantidad de comparendos asignados.
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		Graph drakeEsMejorQueJosh = new Graph(1);
		MaxHeapCP<EstPol> caisOrdenados = ordenarEstacionesPorComparendos();
		
		// Vamos a recorrer todas las estaciones.
		while(caisOrdenados.darTamaño() > 0)
		{
			EstPol actual = caisOrdenados.devolverMax();
			
			// Vamos a reportar cada estación con su id y los comparendos asignados.
			int objId = actual.darobjetcID();
			int compis = actual.darRetenidos().size();
			System.out.println("La estación: " + objId + " tiene: " + compis);
			
			// Vamos a recuperar el vertice de la policia y añadirlo (si no está ya en el grafo.)
			Vertice vertiCai = idMinimoAVerti(actual.darlatitud(), actual.darlongitud());
			if(!drakeEsMejorQueJosh.existeVertice(vertiCai)) drakeEsMejorQueJosh.addVertex(vertiCai.darId(), vertiCai);
			
			// Vamos a recorrer los comparendos y recuperar sus vertices.
			ArrayList<Comparendo> compisCai = actual.darRetenidos();
			
			for(int i = 0; i < compisCai.size(); i++)
			{
				// Recuperamos el vertice del comparendo.
				Comparendo compiActual = compisCai.get(i);
				double lat = compiActual.darLatitud();
				double lon =  compiActual.darLongitud();
				
				Vertice comparendoActual = idMinimoAVerti(lat, lon);
				
				// Vamos a añadir el vertice si es que no existe.
				if(!drakeEsMejorQueJosh.existeVertice(comparendoActual)) drakeEsMejorQueJosh.addVertex(comparendoActual.darId(), comparendoActual);
				
				// Vamos a añadir el arco, con la arquitectura correcta. 
				drakeEsMejorQueJosh.addEdge(vertiCai.darId(), comparendoActual.darId(), 1.0);
				
			}			
		}
		
		// Vamos a reportar cuantos arcos y vertices tiene el grafo a pintar.
		System.out.println("--------------------------------------------------------------------");
		System.out.println("El grafo, (sin contemplar el camino) tiene la siguiente información:");
		System.out.println("Total vertices: " + (drakeEsMejorQueJosh.darV()-1));
		System.out.println("Total arcos: " + drakeEsMejorQueJosh.darE());
		System.out.println("--------------------------------------------------------------------");
		
		return drakeEsMejorQueJosh;
	}
	
	// Ordenar estaciones por gravedad.
	private MaxHeapCP<EstPol> ordenarEstacionesPorComparendos()
	{
		MaxHeapCP<EstPol> ordenados = new MaxHeapCP<EstPol>(1);
		Node paraOrdenar = estaciones.darPrimerElemento();
		
		while(paraOrdenar != null)
		{
			EstPol actual = (EstPol) paraOrdenar.darData();
			ordenados.añadir(actual);
			
			paraOrdenar = paraOrdenar.darSiguiente();
		}
		
		caisOrdenados = ordenados;
		return ordenados;
	}
	
}

