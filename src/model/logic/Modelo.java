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
	
	public final static double MIN_LAT=3.819966340000008;
	public final static double MAX_LAT=4.836643219999985;
	public final static double MIN_LON=-74.39470032000003;
	public final static double MAX_LON=-73.99132694999997;
	
	
	//Atributos necesarios para la lectura desde txt
	private Vertice vertiAgregar;
	private Vertices_Bogota_Info infoVertice;
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
	

	//Atributos necesarios para cargar los sectores
	
	
	private ListaEnlazadaQueue[] sectoresOrdenados;
	private int numIntervalos;
	
	
	///////////////////////////////////////////////////////Constructor

	public Modelo()
	{
		parteDelaEstacion = "";
		parteDelVerti="";
		
		
		numIntervalos=10;
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

	/////////////////////////////////////////////////////////////////////////////////LECTURA de TXT 

 	public void leerTxtVertix(String archivo) throws FileNotFoundException, IOException 
	{
		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		String cadena = b.readLine();

		generarSectores(numIntervalos);
		
		while( cadena != null) 
		{	    	  
			infoVertice = new Vertices_Bogota_Info(0, 0);
			vertiAgregar = new Vertice(0, infoVertice);

			String[] partes = cadena.split(",");

			int ID = Integer.parseInt(partes[0]);
			double Longitud = Double.parseDouble(partes[1]);
			double Latitud = Double.parseDouble(partes[2]);
			
			
			infoVertice.asignarLon(Longitud);
			infoVertice.asignarLat(Latitud);
			infoVertice.asignarId(ID);
			
			vertiAgregar.asignarId(ID);
			vertiAgregar.cambiarInfo(infoVertice);

			agregarVerticeACola(infoVertice);
			cositaBienHecha.addVertex(ID, vertiAgregar);
			cadena = b.readLine();

		}

		int totalVertices = cositaBienHecha.darV()-1;
		System.out.println("Total de Vertices: " + totalVertices + "\n--------------");
		b.close();
		revisarAgregados();
		
		
	}

	public void leerTxtArc(String archivo) throws FileNotFoundException, IOException 
	{
		costoHaversiano = new Haversine();
		double costo = 0.0;

		FileReader f = new FileReader(archivo);
		BufferedReader b = new BufferedReader(f);
		String cadena = b.readLine();

		while( cadena != null) 
		{
			if(!cadena.contains("#"))
			{
				String[] partes = cadena.split(" ");
				int origen = Integer.parseInt(partes[0]);

				for (int i = 1; i< partes.length; i++)
				{
					int vecino = Integer.parseInt(partes[i]);

					if(cositaBienHecha.existeVertice(origen) && cositaBienHecha.existeVertice(vecino))
					{
						Vertices_Bogota_Info infoOrigen = (Vertices_Bogota_Info) cositaBienHecha.getInfoVertex(origen);
						Vertices_Bogota_Info infoVecino = (Vertices_Bogota_Info) cositaBienHecha.getInfoVertex(vecino);

						costo = costoHaversiano.distance(infoOrigen.darLat(), infoOrigen.darLon(), infoVecino.darLat(), infoVecino.darLon());

						cositaBienHecha.addEdge(origen, vecino, costo);
					}
				}
			}
			cadena = b.readLine();
		}

		System.out.println("Numero de arcos: " + cositaBienHecha.darE() + "\n---------------");
		b.close(); 
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

	public void componentesDelComparendo(String palabra)
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

	public void agregarCoordenada(double pCor)
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
				infoPorAgregar = new Vertices_Bogota_Info (0,0);
				
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

	public void componentesDelGrafo(String palabra)
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

	public void agregarCoordenadaGrafo(double pCor)
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


	///////////////////////////////////////////////////////////////////Imprimir el grafo

	public void imprimirLaCosaBienHecha()
	{

		Graph base = cositaBienHecha;

		TablaHashSondeoLineal vertex = base.vertis;
		ListaEnlazadaQueue arcos = base.arcos;
		char comillas = '"';


		try 
		{
			FileWriter file = new FileWriter("./data/grafoCreado.json");


			//Apertura del Archivo

			file.write("{");
			file.write(comillas+"type"+comillas+":"+comillas+"FeatureCollection"+comillas+",");
			file.write(comillas+"crs"+comillas+":");
			file.write("{");
			file.write(comillas+"type"+comillas+":"+comillas+"name"+comillas +",");
			file.write(comillas +"properties"+comillas+":");
			file.write("{");
			file.write(comillas +"name"+comillas+":"+comillas+"EPSG:4686"+comillas);
			file.write("}");
			file.write("},");
			file.write(comillas+"features"+comillas+":[");


			//Lo que se debe imprimir para cada vértice
			//vertex.darDatos()

			for (int i=0;i<cositaBienHecha.darV()-1;i++)
			{
				Vertice aux = (Vertice) vertex.getSet(i);
				Integer id = (Integer) aux.darId();
				Vertices_Bogota_Info info = (Vertices_Bogota_Info) aux.darInfo();
				Double lat = info.darLat(), lon=info.darLon();

				file.write("{");	
				file.write(comillas+"type"+comillas+":"+comillas+"Feature"+comillas+",");
				file.write(comillas+"id"+comillas+":"+id+",");
				file.write(comillas+"geometry"+comillas+":{");
				file.write(comillas+"type"+comillas+":"+comillas+"Point"+comillas+",");
				file.write(comillas+"coordinates"+comillas+":[");
				file.write(lon+",");
				file.write(lat+"");
				file.write("]");
				file.write("},");
				file.write(comillas+"properties"+comillas+":");
				file.write("{");
				file.write(comillas+"OBJECTID"+comillas+":"+id+",");
				file.write(comillas+"LATITUD"+comillas+":"+lat+",");
				file.write(comillas+"LONGITUD"+comillas+":"+lon+"");
				file.write("}");
				file.write("},");
			}


			//Lo que se debe imprimir para cada arco:

			Node actual = arcos.darPrimerElemento();
			
			while (actual!=null)
			{
				Arco aux= (Arco) actual.data;

				file.write("{");	
				file.write(comillas+"type"+comillas+":"+comillas+"Feature"+comillas+",");
				file.write(comillas+"id"+comillas+":0,");

				file.write(comillas+"properties"+comillas+":");
				file.write("{");

				file.write(comillas+"ORIGEN"+comillas+":"+aux.darInicial().darId()+",");
				file.write(comillas+"DESTINO"+comillas+":"+aux.darFinal().darId()+",");
				file.write(comillas+"COSTO"+comillas+":"+aux.darCostoHaversiano());
				file.write("}");


				//Este depende de si es el último o de si quedan más con la coma

				if (actual.darSiguiente()==null)
				{
				file.write("}");
				}
				else
				{
				file.write("},");
				}

				actual= actual.darSiguiente();
			}


			//Cerrar el JSON
			file.write("]");
			file.write("}");


			file.close();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public void generarSectores(int numIntervalos)
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
	
	public void agregarVerticeACola(Vertices_Bogota_Info porAgregar)
	{
		double tamIntervaloLat=(MAX_LAT-MIN_LAT)/numIntervalos;
		double tamIntervaloLon=(MAX_LON-MIN_LON)/numIntervalos;
		
		
		double cocienteLat = (porAgregar.darLat()-MIN_LAT)/tamIntervaloLat;
		int posLat = (int)Math.floor(cocienteLat);
		
		double cocienteLon = (porAgregar.darLon()-MIN_LON)/tamIntervaloLon;
		int posLon=(int)Math.floor(cocienteLon);
		
		//Mitigo el error para maxLat y minLat
		if (posLat==10)
				posLat=9;
		
		if (posLon==10)
				posLon=9;
			
		//Recupero la posición del arreglo que contiene esa latitud
		ListaEnlazadaQueue porLat = sectoresOrdenados[posLat];
		
		//En esta cola porLat, el posLon -ésimo nodo tiene las coordenadas deseadas
		
		int contadorAux=0;
		Node actual = porLat.darPrimerElemento();
		
		while (contadorAux <posLon)
		{
			actual = actual.darSiguiente();
			contadorAux++;
		}
		
		Sector alQueAgrego = (Sector)actual.data;
		
		alQueAgrego.agregarVertice(porAgregar);
	
		
	}
	
	
	public void revisarAgregados()
	{
		for (ListaEnlazadaQueue a:sectoresOrdenados)
		{
			Node actual = a.darPrimerElemento();
			
			while (actual !=null)
			{
				
				Sector pres = (Sector) actual.data;
				double minlat=pres.darMinLat(), maxlat=pres.darMaxLat(),minlon=pres.darMinLon(), maxlon=pres.darMaxLon();
				ListaEnlazadaQueue vertices = pres.darVerticesAsignados();
				
				Node actualVertex=vertices.darPrimerElemento();
				while (actualVertex!=null)
				{
					Vertices_Bogota_Info actVer=(Vertices_Bogota_Info)actualVertex.data;
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
	
	
	
	
}

