package model.data_structures;

import java.util.Iterator;

public class ListaEnlazadaQueue <T extends Comparable<T>> implements Iterable, IListaEnlazadaQueue<T>
{
	
	private int tamanio;
	
	private Node<T> primerNodo;
	
	private Node<T> lastNode;
	
	public ListaEnlazadaQueue()
	{
		primerNodo=null;
		lastNode=null;
		tamanio=0;
	}
		
	public int darTamanio()
	{
		return tamanio;
	}
	
	public boolean emptyList() 
	{
		return tamanio==0;
	}

	public Node<T> darPrimerElemento() 
	{
		return primerNodo;
	}

	public Node<T> darUltimoElemento() 
	{
		return lastNode;
	}
	
	
	public void enqueue(T parte) 
	{
		Node<T> nuevo = new Node<T>(parte);
		
		if (tamanio == 0)
		{
			primerNodo=nuevo;
			lastNode=nuevo;
		}
		else
		{
			Node <T> oldLast = lastNode;
			oldLast.cambiarSiguiente(nuevo);
			lastNode=nuevo;	
		}
		tamanio++;
	}
	
	public void push (T elemento)
	{
		Node<T> nuevo = new Node<T>(elemento);
		
		if (primerNodo == null)
		{
			primerNodo = nuevo;
			lastNode=nuevo;
		}
		else
		{
			nuevo.cambiarSiguiente(primerNodo);
			primerNodo = nuevo;
		}
		
		tamanio++;
	}

	public T dequeue() 
	{
		T eliminado;
		
		if (tamanio==0)
		{
			eliminado = null;
		}
		else if(tamanio==1)
		{
			eliminado = (T) primerNodo.darData();
			primerNodo=null;
			lastNode=null;
			tamanio--;
		}
		else
		{
			eliminado=(T)primerNodo.darData();
			Node<T> anteriorPrimero=primerNodo;
			Node<T> nuevoPrimero=primerNodo.darSiguiente();
			
			primerNodo=nuevoPrimero;
			anteriorPrimero.cambiarSiguiente(null);
			tamanio--;
		}
		
		
		return eliminado;
	}
	
	/////////////////////////////////////////////////////////
	
	public Iterator<T> iterator()
	{
		return new ListIterator();
	}
	
	private class ListIterator implements Iterator<T>
	{
		private Node<T> actual = primerNodo;
		
		public boolean hasNext()
		{
			return actual!=null;
		}
		public T next()
		{
			T dat = (T) actual.darData();
			actual=actual.darSiguiente();
			return dat;
		}

	}
	
	
}
