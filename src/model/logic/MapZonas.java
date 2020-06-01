package model.logic;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import com.teamdev.jxmaps.Circle;
import com.teamdev.jxmaps.CircleOptions;
import com.teamdev.jxmaps.ControlPosition;
import com.teamdev.jxmaps.InfoWindow;
import com.teamdev.jxmaps.InfoWindowOptions;
import com.teamdev.jxmaps.LatLng;
import com.teamdev.jxmaps.Map;
import com.teamdev.jxmaps.MapOptions;
import com.teamdev.jxmaps.MapReadyHandler;
import com.teamdev.jxmaps.MapStatus;
import com.teamdev.jxmaps.MapTypeControlOptions;
import com.teamdev.jxmaps.Marker;
import com.teamdev.jxmaps.Polygon;
import com.teamdev.jxmaps.PolygonOptions;
import com.teamdev.jxmaps.Polyline;
import com.teamdev.jxmaps.PolylineOptions;
import com.teamdev.jxmaps.swing.MapView;

import model.data_structures.Arco;
import model.data_structures.Graph;
import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.ListaEnlazadaStack;
import model.data_structures.MaxHeapCP;
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;

public class MapZonas<K extends Comparable<K>,V extends Comparable<V>> extends MapView {

	// Objeto Google Maps
	private Map map;
	private Modelo modelo;

	private LatLng[] locations = {}; 
	private LatLng centro = new LatLng(4.609537, -74.078715);

	public MapZonas(Graph grafo, MaxHeapCP<EstPol> estacionesCais, int numCai)
	{	
		//Recupero los arcos
		ListaEnlazadaQueue arcos = grafo.arcos;
		
		//Recupero las estaciones ordenadas
		System.out.println(estacionesCais.darTamaño());
		
		//Empezamos
		setOnMapReadyHandler( new MapReadyHandler() 
		{
			@Override
			public void onMapReady(MapStatus status)
			{
				if ( status == MapStatus.MAP_STATUS_OK )
				{
					map = getMap();

					//CREAR EL MAPA
					initMap( map );

					///////////////////////////////////////////////// PINTO LAS ESTACIONES CON SU ANCHO Y COLOR

					// Tamaño y color
					int tamaño = 800;
					ListaEnlazadaStack<String> colores = colores();

					while(estacionesCais.darTamaño() > 0)
					{
						EstPol elQueNoEstudiaEsPoliciaNacional = estacionesCais.devolverMax();

						double lon = elQueNoEstudiaEsPoliciaNacional.darlongitud();
						double lat = elQueNoEstudiaEsPoliciaNacional.darlatitud();

						LatLng policia = new LatLng(lat, lon);

						Circle poli = new Circle(map);
						poli.setCenter(policia);
						poli.setRadius(tamaño);
						
						String colorcito = colores.pop();
						
						System.out.println("------------------------");
						System.out.println("Policia id: " + elQueNoEstudiaEsPoliciaNacional.darobjetcID());
						System.out.println("Color: " + colorcito);
						System.out.println("Radio: " + tamaño);
						
						CircleOptions co = new CircleOptions();
						co.setStrokeColor(colorcito);
						co.setFillColor(colorcito);
						co.setFillOpacity(0.5);
						co.setStrokeWeight(1.0);

						poli.setOptions(co);
						poli.setVisible(true);

						tamaño -= 25;
					}

					System.out.println("------------------------");
					System.out.println("El mapa fue un exito.");

					///////////////////////////////////////////////// Recorrer los arcos
					
					Node<Arco> actual = arcos.darPrimerElemento();
					int conti = 0;
					
					ListaEnlazadaStack<String> mismosColores = colores();
					String colorcito = mismosColores.pop();
					boolean col = true;
					
					while(actual != null)
					{
						// Recupero el arco inicial-final (OJO: Policia-Comparendo). 
						
						Arco hola = actual.data;

						Vertices_Bogota_Info infoInicio = (Vertices_Bogota_Info) hola.darInicial().darInfo();
						double latInicio = infoInicio.darLat();
						double lonInicio = infoInicio.darLon();

						Vertices_Bogota_Info infoFinal = (Vertices_Bogota_Info) hola.darFinal().darInfo();
						double latFinal = infoFinal.darLat();
						double lonFinal = infoFinal.darLon();
						
						//Voy a tener el id donde estoy y el del siguiente. (vertice de la policia)
						int idActual = infoInicio.darId();
						int idNext = (int) actual.darSiguiente().data.darInicial().darId();
						
						// Cambiar y reiniciar.
						if(!col)
						{
							conti = 0;
							colorcito = mismosColores.pop();
							System.out.println("Color nuevo: " + colorcito);
							col = true;
						}
						
						// Voy a pintar tantos como el usuario diga por estación.
						if(idActual != idNext)
						{
							System.out.println("---------------------------");
							System.out.println(idActual + " --> " + idNext);
							col = false;
						}

						if(conti < numCai)
						{
							LatLng ver1 = new LatLng(latInicio, lonInicio);
							LatLng ver2 = new LatLng(latFinal, lonFinal);

							LatLng[] camino = new LatLng[2];
							camino[0] = ver1;
							camino[1] = ver2;

							PolygonOptions po = new PolygonOptions();

							po.setStrokeColor(colorcito);						
							po.setStrokeWeight(1);

							Polygon linea  = new Polygon(map);
							linea.setOptions(po);

							linea.setPath(camino);
							linea.setVisible(true);
							
							conti++;
						}

						actual = actual.darSiguiente();
						
					}
					
					
					System.out.println("-----------------------------------------");
					System.out.println("ACAMOS ESTRUCTURAS HP JAJAJAJA POR FIN !!");
					System.out.println("-----------------------------------------");
				}
			}

		} );


	}

	public void initMap(Map map)
	{
		MapOptions mapOptions = new MapOptions();
		MapTypeControlOptions controlOptions = new MapTypeControlOptions();
		controlOptions.setPosition(ControlPosition.BOTTOM_LEFT);
		mapOptions.setMapTypeControlOptions(controlOptions);

		map.setOptions(mapOptions);
		map.setCenter(centro);
		map.setZoom(15);


	}

	public void initFrame(String titulo)
	{
		JFrame frame = new JFrame(titulo);
		frame.setSize(900, 800);
		frame.add(this, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}


	private ListaEnlazadaStack<String> colores()
	{
		ListaEnlazadaStack<String> color = new ListaEnlazadaStack<String>();

		color.push("#F7DC6F"); // 1
		color.push("#DC7633"); // 2
		color.push("#AF7AC5"); // 3
		color.push("#CB4335"); // 4
		color.push("#797D7F"); // 5
		color.push("#273746"); // 6
		color.push("#2874A6"); // 7
		color.push("#5B2C6F"); // 8
		color.push("#C39BD3"); // 9
		color.push("#145A32"); // 10
		color.push("#FF0000"); // 11
		color.push("#00FF00"); // 12
		color.push("#00FFFF"); // 13
		color.push("#008080"); // 14
		color.push("#000080"); // 15
		color.push("#C0C0C0"); // 16
		color.push("#FA8072"); // 17
		color.push("#F8C471"); // 18
		color.push("#C982EE"); // 19
		color.push("#82EECC"); // 20
		color.push("#F0BC6C"); // 21


		return color;
	}
}
