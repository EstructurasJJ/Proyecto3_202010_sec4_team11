package model.logic;

import model.data_structures.Vertice;

public class EstPol implements Comparable<EstPol>
{
	
	private int objetcID;	
	private String descrip;
	private String dirSitio;
	private double latitud;
	private double longitud;
	private String tel;
	private String mail;
	private String nombre;
	private String identificador;
	private int ganador;
	
	
	public EstPol()
	{
		objetcID=0;
		descrip="";
		dirSitio="";
		latitud=0;
		longitud=0;
		tel="";
		mail="";
		nombre="";
		identificador="";
		ganador =-1;
	}

	
	//MÉTODOS CONSULTORES
	
	public int darobjetcID(){return objetcID;}
	public String dardescrip(){return descrip;}
	public String dardirSitio(){return dirSitio;}
	public double darlatitud(){return latitud;}
	public double darlongitud(){return longitud;}
	public String dartel(){return tel;}
	public String darmail(){return mail;}
	public String darnombre(){return nombre;}
	public String darindentificador(){return identificador;}
	public int darVertiAsociado() {return ganador;}

	//MÉTODOS SET
	
	public void setobjetcID(int p){objetcID=p;}
	public void setdescrip(String p){descrip=p;}
	public void setdirSitio(String p){dirSitio=p;}
	public void setlatitud(double p){latitud=p;}
	public void setlongitud(double p){longitud=p;}
	public void settel(String p){tel=p;}
	public void setmail(String p){mail=p;}
	public void setnombre(String p){nombre=p;}
	public void setidentificador(String p){identificador=p;}
	public void setVertice(int p){ganador=p;}
	
	
	@Override
	public int compareTo(EstPol arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
