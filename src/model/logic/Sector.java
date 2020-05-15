package model.logic;

import model.data_structures.ListaEnlazadaQueue;
import model.data_structures.Vertice;

public class Sector <T extends Comparable <T>> implements Comparable<Sector>
{

	private double minLat, minLon, maxLat, maxLon;
	private ListaEnlazadaQueue<Vertices_Bogota_Info> vertices;
	
	
	public Sector (double minLong, double maxLong, double minLati, double maxLati)
	{
		minLon=minLong;
		maxLon=maxLong;
		minLat=minLati;
		maxLat=maxLati;
		vertices= new ListaEnlazadaQueue();
	}
	
	
	///////////////////////////Métodos Consultores
	
	public double darMinLat()
	{
		return minLat;
	}
	
	public double darMaxLat()
	{
		return maxLat;
	}
	
	public double darMinLon()
	{
		return minLon;
	}
	
	public double darMaxLon()
	{
		return maxLon;
	}
	
	public ListaEnlazadaQueue darVerticesAsignados()
	{
		return vertices;
	}
	
	////////////////////////////////Métodos Set
	
	
	public void asignarMinLat(double m)
	{
		minLat = m;
	}
	
	public void darMaxLat(double m)
	{
		maxLat=m;
	}
	
	public void asignarMinLon(double m)
	{
		minLon=m;
	}
	
	public void asignarMaxLon(double m)
	{
		maxLon=m;
	}
	
	
	/////////////////////Agregar a la lista
	
	public void agregarVertice(Vertices_Bogota_Info par)
	{
		vertices.enqueue(par);
	}


	@Override
	public int compareTo(Sector arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
