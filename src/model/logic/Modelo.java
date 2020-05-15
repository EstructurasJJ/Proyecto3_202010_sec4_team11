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

import model.data_structures.Arco;
import model.data_structures.Graph;
import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;


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
	private ListaEnlazadaQueue estaciones;
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
		
		//ASIGNAR LOS COMPARENDOA A LOS VERTICES.
		
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
	
}

