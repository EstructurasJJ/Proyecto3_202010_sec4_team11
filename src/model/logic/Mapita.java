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
import model.data_structures.Node;
import model.data_structures.TablaHashSondeoLineal;
import model.data_structures.Vertice;

public class Mapita<K extends Comparable<K>,V extends Comparable<V>> extends MapView {

	// Objeto Google Maps
	private Map map;
	private Modelo modelo;

	private LatLng[] locations = {}; 
	private boolean centrar = false;
	private LatLng centro = new LatLng(4.609537, -74.078715);

	public Mapita(Graph grafo)
	{	
		//Recupero los vertices
		TablaHashSondeoLineal vertices = grafo.vertis;
		//Recupero los arcos
		ListaEnlazadaQueue arcos = grafo.arcos;

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

					// Recorrer los vertices
					Iterator vertex = vertices.keys();

					while(vertex.hasNext())
					{
						//Recupero el vertice y su información
						K vertiID = (K) vertex.next();
						Vertice vertiActual = (Vertice) vertices.getSet(vertiID);
						Vertices_Bogota_Info infoVertiActu = (Vertices_Bogota_Info) vertiActual.darInfo();

						double lon = infoVertiActu.darLon();
						double lat = infoVertiActu.darLat();

						//Añado el vertice
						LatLng coor = new LatLng(lat, lon);
						
						// Vertices
						CircleOptions middleLocOpt= new CircleOptions(); 
						middleLocOpt.setFillOpacity(0.5);
						middleLocOpt.setStrokeWeight(1.0);
						middleLocOpt.setFillColor("#a6ff4c");
							
						Circle middleLoc1 = new Circle(map);
						middleLoc1.setOptions(middleLocOpt);
						middleLoc1.setCenter(coor); 
						middleLoc1.setRadius(15);
						
					}        	 


					// Recorrer los vertices
					Node<Arco> actual = arcos.darPrimerElemento();
					
					while(actual != null)
					{
						
						Arco hola = actual.data;
						K vertiID = (K) hola.darInicial().darId();
						
						Vertices_Bogota_Info infoInicio = (Vertices_Bogota_Info) hola.darInicial().darInfo();
						double latInicio = infoInicio.darLat();
						double lonInicio = infoInicio.darLon();
						
						if(!centrar)
						{
							centro = new LatLng(latInicio, lonInicio);
							centrar = true;
						}
						
						Vertices_Bogota_Info infoFinal = (Vertices_Bogota_Info) hola.darFinal().darInfo();
						double latFinal = infoFinal.darLat();
						double lonFinal = infoFinal.darLon();
						
						LatLng ver1 = new LatLng(latInicio, lonInicio);
						LatLng ver2 = new LatLng(latFinal, lonFinal);
						
						LatLng[] camino = new LatLng[2];
						camino[0] = ver1;
						camino[1] = ver2;
						
						PolygonOptions po = new PolygonOptions();
				
						po.setStrokeColor("#0f0f0f");						
						po.setStrokeWeight(4);
							
						Polygon linea  = new Polygon(map);
						linea.setOptions(po);
						
						linea.setPath(camino);
						linea.setVisible(true);
						
						actual = actual.darSiguiente();
					}


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
	map.setZoom(18);
	

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
}
