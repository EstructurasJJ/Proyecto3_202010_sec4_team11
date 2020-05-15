package model.data_structures;

import java.util.ArrayList;
import java.util.Iterator;

public class Vertice <K extends Comparable<K>, V extends Comparable<V>> implements Comparable
{
	//Declaración
	
	private K id;
	private V informacion; 
	
	private ArrayList<Arco> vecinos;
	
	private int visitado;

	//Constructor
	
	public Vertice(K ID, V pInfo)
	{
		vecinos = new ArrayList<Arco>();
		id = ID;
		informacion = pInfo;
		
		visitado=0;
	}
	
	//Dar atributos
	
	public K darId()
	{
		return id;
	}
	
	public V darInfo()
	{
		return informacion;
	}
	
	public int darCC()
	{
		return visitado;
	}
	
	
	/////////////////////////////////////////////////////////////////////Metodos
	
	// Cambiar la info de un vertice,.
	
	public void cambiarInfo(V nuevaInfo)
	{
		informacion = nuevaInfo;
	}
	
	
	// Agregar, verificar, contar y eliminar los vecinos de un vertice.
	
	public void agregarArco(Arco v)
	{
		vecinos.add(v);
	}
	
	public boolean esVecino(Arco v)
	{
		return vecinos.contains(v);
	}
	
	public int numVecinos()
	{
		return vecinos.size();
	}
	
	public void eliminarVecino(Arco v)
	{
		vecinos.remove(v);
	}
	
	public Iterator<Arco> iterator() 
	{
		return vecinos.iterator();
	}
	
	// Verificar, desmarcar y marcar como visitido.
	
		// 0 es False
		// 1 es True
	
	public boolean revisarVisitado()
	{
		if(visitado==0)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public void marcarVisitado(int marca)
	{
		visitado = marca;
	}
	public void quitarVisitado()
	{
		visitado = 0;
	}

	public void asignarId(K ID)
	{
		id = ID;
	}
	
	//////////////////////////////////////////////NO LO USAMOS
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}