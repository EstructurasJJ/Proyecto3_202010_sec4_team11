package model.data_structures;

import java.util.Iterator;
import java.util.Stack;

public class DijkstraSP 
{
	private double[] distTo;          
	private Arco[] edgeTo;    
	private IndexMinPQ<Double> pq;  


	public DijkstraSP(Graph grafito, Vertice s) 
	{
		distTo = new double[grafito.darV()];
		edgeTo = new Arco[grafito.darV()];
		TablaHashSondeoLineal vertix = grafito.vertis;

		validateVertex(s);

		for (int v = 0; v < grafito.darV(); v++)
		{
			distTo[v] = Double.POSITIVE_INFINITY;
		}

		int idVerti = (int) s.darId();
		distTo[idVerti] = 0.0;

		pq = new IndexMinPQ<Double>(grafito.darV());
		pq.insert(idVerti, distTo[idVerti]);

		while (!pq.isEmpty()) 
		{
			int v = pq.delMin();
			Vertice actual = (Vertice) vertix.getSet(v);

			Iterator arqui = actual.iterator();

			while(arqui.hasNext())
			{
				Arco este = (Arco) arqui.next();
				relax(este);
			}

		}

	}

	private void relax(Arco e) 
	{
		int v = (int) e.darInicial().darId(), w = (int) e.darFinal().darId();

		if (distTo[w] > distTo[v] + e.darCostoHaversiano()) 
		{
			distTo[w] = distTo[v] + e.darCostoHaversiano();
			edgeTo[w] = e;

			if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
			else                pq.insert(w, distTo[w]);
		}
	}

	private void validateVertex(Vertice vw) 
	{
		int V = distTo.length;
		int v=(int) vw.darId();

		if (v < 0 || v >= V)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
	}


	public double distTo(Vertice vw) 
	{
		int v = (int) vw.darId();
		validateVertex(vw);
		return distTo[v];
	}

	public boolean hasPathTo(Vertice vw) 
	{
		validateVertex(vw);
		int v = (int) vw.darId();
		return distTo[v] < Double.POSITIVE_INFINITY;
	}

	public ListaEnlazadaStack<Arco> pathTo(Vertice vw) 
	{
		validateVertex(vw);
		
		int v = (int) vw.darId();
		if (!hasPathTo(vw)) return null;
		
		ListaEnlazadaStack<Arco> path = new ListaEnlazadaStack<Arco>();
		for (Arco e = edgeTo[v]; e != null; e = edgeTo[(int) e.darInicial().darId()]) 
		{
			path.push(e);
		}
		
		return path;
	}
}
