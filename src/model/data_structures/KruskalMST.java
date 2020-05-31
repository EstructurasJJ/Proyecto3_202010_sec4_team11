package model.data_structures;

public class KruskalMST 
{
	private static final double FLOATING_POINT_EPSILON = 1E-12;
	private double pesoBobi;
	private long pesoJuanjo;
	private ListaEnlazadaQueue<Arco> mst = new ListaEnlazadaQueue<Arco>();
	
	public KruskalMST (Graph grafito)
	{
		MaxHeapCP<Arco> pq = new MaxHeapCP<Arco>(grafito.darE());
		
		ListaEnlazadaQueue<Arco> todArcos = grafito.arcos;
		Node actual = todArcos.darPrimerElemento();
		
		while(actual != null)
		{
			Arco arqui = (Arco) actual.darData();
			pq.añadir(arqui);							//TODO JUANJO
			actual = actual.darSiguiente();
		}
		
		Uf uf = new Uf(228046);
		
        while (!pq.isEmpty() && mst.darTamanio() < grafito.darV() - 1) 
        {
            Arco e = pq.devolverMax();
            Vertice ini = e.darInicial();
            Vertice fin = e.darFinal();
            
            int v = (int) ini.darId();
            int w = (int) fin.darId();
            
            if (uf.find(v) != uf.find(w)) 
            { 
                uf.union(v, w);
                mst.enqueue(e);
                
                pesoBobi += e.darCostoHaversiano();
                pesoJuanjo += e.darCantidad();
            }
        }
	}
	
	public ListaEnlazadaQueue<Arco> darMST()
	{
		return mst;
	}
	
	public double darPesoBobi()
	{
		return pesoBobi;
	}
	
	public long darPesoJuanjo()
	{
		return pesoJuanjo;
	}
	
}
