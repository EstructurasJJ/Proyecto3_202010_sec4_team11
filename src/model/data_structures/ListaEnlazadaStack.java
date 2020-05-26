package model.data_structures;

import java.util.Iterator;


//TODO Bobby

public class ListaEnlazadaStack <T extends Comparable> implements IListaEnlazadaStack <T> 
{

	//Atributos
	
	private Node<T> topePila;
	private int numeroElementos;
	
	//Contructor
	
	public ListaEnlazadaStack()
	{
		topePila = null;
		numeroElementos = 0;
	}
	
	//Tamaño y primer nodo.
	
	public int darTamaño() 
	{
		return numeroElementos;
	}
	
	public Node<T> devolverCabezaPila()
	{
		return topePila;
	}
	
	//Agregar
	
	public void push (T elemento)
	{
		Node<T> nuevo = new Node<T>(elemento);
		
		if (topePila == null)
		{
			topePila = nuevo;
		}
		else
		{
			nuevo.cambiarSiguiente(topePila);
			topePila = nuevo;
		}
		
		numeroElementos++;
	}
	
	//Eliminar
	
	public T pop ()
	{		
		if (topePila != null)
		{
			T elemento = (T) topePila.darData();
			
			Node<T> nuevoTope = topePila.darSiguiente();
			topePila.cambiarSiguiente(null);
			topePila = nuevoTope;
			numeroElementos--;
			
			return elemento;
		}
		else
		{
			return null;
		}
	}
	
	//Esta vacia
	public boolean isEmpty()
	{
		boolean vacia = true;
		
		if (topePila != null)
		{
			vacia = false;
		}
		
		return vacia; 
	}
	
	//No lo uso
	
	public Iterator<T> iterator() {
		return null;
	}

}
