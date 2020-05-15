package model.logic;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

//TODO Ambos

public class Comparendo implements Comparable<Comparendo>
{	
	private int OBJECTID;
	private String MEDIO_DETE;
	private String CLASE_VEHI;
	private String TIPO_SERVI;
	private String INFRACCION;
	private String DES_INFRAC;
	private String LOCALIDAD;

	private double latitud;
	private double longitud; 

	//
	private Date FECHA_HORA;
	private String MUNICIPIO;
	//

	public Comparendo ()
	{
		OBJECTID = 0;
		MEDIO_DETE = "";
		CLASE_VEHI = "";
		TIPO_SERVI = "";
		INFRACCION = "";
		DES_INFRAC = "";
		LOCALIDAD = "";

		//
		FECHA_HORA = new Date();
		MUNICIPIO = "";
		//
	}

	public int darObjectid()
	{
		return OBJECTID;
	}

	//
	public Date darFecha_Hora()
	{
		return FECHA_HORA;
	}
	public String darMunicipio()
	{
		return MUNICIPIO;
	}
	//

	public String darMedio_Dete()
	{
		return MEDIO_DETE;
	}
	public String darClase_Vehi()
	{
		return CLASE_VEHI;
	}
	public String darTipo_Servicio()
	{
		return TIPO_SERVI;
	}
	public String darInfraccion()
	{
		return INFRACCION;
	}
	public String darDes_Infrac()
	{
		return DES_INFRAC;
	}
	public String darLocalidad()
	{
		return LOCALIDAD;
	}

	public void asignarObjectid(int i)
	{
		OBJECTID = i;
	}
	//
	public void asignarFecha_Hora(String fechita)
	{
		try 
		{
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			FECHA_HORA = formato.parse(fechita);
			//System.out.println(FECHA_HORA.toString());
		}
		catch(Exception e)
		{
			FECHA_HORA = null;
		}
	}
	public void asignarMunicipio (String i)
	{
		MUNICIPIO = i;
	}
	//
	public void asignarMedio_Dete(String i)
	{
		MEDIO_DETE = i;
	}
	public void asignarClase_Vehi(String i)
	{
		CLASE_VEHI = i;
	}
	public void asignarTipo_Servicio(String i)
	{
		TIPO_SERVI = i;
	}
	public void asignarInfraccion(String i)
	{
		INFRACCION = i;
	}
	public void asignarDes_Infrac(String i)
	{
		DES_INFRAC = i;
	}
	public void asignarLocalidad(String i)
	{
		LOCALIDAD = i;
	}

	public double darLatitud()
	{
		return latitud;
	}
	public double darLongitud()
	{
		return longitud;
	}
	public void asignarLatitud(double i)
	{
		latitud = i;
	}
	public void asignarLongitud(double i)
	{
		longitud = i; 
	}

	public int compareTo(Comparendo compi) 
	{
		double id1 = OBJECTID;
		double id2 = compi.darObjectid();

		if(id1 > id2)
		{
			return 1;
		}
		else if(id1 < id2)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}


//	//HASH CODE
//
//	public int hashCode()
//	{
//		int hash = 17;
//
//		hash = 31*hash + ((Integer) OBJECTID).hashCode();
//
//		hash = 31*hash + MEDIO_DETE.hashCode();
//		hash = 31*hash + CLASE_VEHI.hashCode();
//		hash = 31*hash + TIPO_SERVI.hashCode();
//		hash = 31*hash + INFRACCION.hashCode();
//		hash = 31*hash + DES_INFRAC.hashCode();
//		hash = 31*hash + LOCALIDAD.hashCode();
//		hash = 31*hash + MUNICIPIO.hashCode();
//
//		hash = 31*hash + ((Double) latitud).hashCode();
//		hash = 31*hash + ((Double) longitud).hashCode();
//
//		hash = 31*hash + FECHA_HORA.hashCode();
//
//		return hash;
//	}

}