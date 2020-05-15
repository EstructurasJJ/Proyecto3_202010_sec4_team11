package model.data_structures;


//TODO Ambos

public class Node <T extends Comparable> implements Comparable<Node>
{
	public T data;
	Node<T> siguiente;
	
	public Node (T elemento)
	{
		data = elemento;
		siguiente = null;
	}
	
	public T darData()
	{
		return data;
	}
	
	public Node<T> darSiguiente()
	{
		return siguiente;
	}
	public void cambiarSiguiente(Node<T> primerElemento)
	{
		siguiente = primerElemento;
	}

	
	/////////////////////////////////////////////////NO LO USAMOS
	@Override
	public int compareTo(Node o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
