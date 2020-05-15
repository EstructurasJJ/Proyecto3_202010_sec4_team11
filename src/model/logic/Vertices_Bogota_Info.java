package model.logic;

public class Vertices_Bogota_Info implements Comparable<Vertices_Bogota_Info>
{

	private int id;
	private double longitud;
	private double latitud;
	
	public Vertices_Bogota_Info(double iLongitud, double iLatitud)
	{
		longitud = iLongitud;
		latitud = iLatitud;
	}
	
	
	public double darLon()
	{
		return longitud;
	}

	public double darLat()
	{
		return latitud;
	}
	
	public void asignarLon(double longi)
	{
		longitud = longi;
	}
	
	public void asignarLat(double lati)
	{
		latitud = lati;
	}
	
	
	public int darId()
	{
		return id;
	}
	
	public void asignarId(int nuevo)
	{
		id = nuevo;
	}
	

	///////////////////////////////////////////////////////////////NO LOS NECESITAMOS.
	@Override
	public int compareTo(Vertices_Bogota_Info o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}