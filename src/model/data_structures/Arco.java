package model.data_structures;

public class Arco implements Comparable<Arco> {
	
	private transient Vertice inicial; 				//Donde empieza
	private transient Vertice ifinal;				//Donde termina
	private double costoHaversiano;					//Peso
	
	public Arco()
	{
		inicial = null;
		ifinal=null;
		costoHaversiano=0;
	}
	
	
	public Arco(Vertice pInicial, Vertice pFinal, double pCostoHaversiano)
	{
		costoHaversiano=pCostoHaversiano;
		inicial=pInicial;
		ifinal=pFinal;
	}
	
	public Vertice darInicial()
	{
		return inicial;
	}
	
	public Vertice darFinal()
	{
		return ifinal;
	}
	
	public double darCostoHaversiano()
	{
		return costoHaversiano;
	}
	
	public void cambiarCostoHaversiano(double pCosto)
	{
		costoHaversiano = pCosto;
	}
	
	@Override
	public int compareTo(Arco that) 
	{
		if (this.darCostoHaversiano() < that.darCostoHaversiano()) return -1;
		else if (this.darCostoHaversiano() > that.darCostoHaversiano()) return 1;
		else return 0;
		
	}
	
	
	//Modificadores de Atributos
	

	
}
