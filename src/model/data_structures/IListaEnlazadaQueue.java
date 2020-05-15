package model.data_structures;

public interface IListaEnlazadaQueue <T extends Comparable<T>>
{
	public void enqueue(T parte);
	
	public void push(T parte);
	
	public T dequeue ();
	
	public int darTamanio();
	
	public boolean emptyList();
	
	public Node<T> darPrimerElemento();
	
	public Node <T> darUltimoElemento();

}
