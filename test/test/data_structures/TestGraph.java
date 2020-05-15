package test.data_structures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import model.data_structures.Graph;
import model.data_structures.Vertice;
import model.logic.Vertices_Bogota_Info;

public class TestGraph 
{
	private Graph grafo;


	public void setUp1()
	{
		grafo=new Graph<Integer, Vertices_Bogota_Info>(1);
	}

	public void setUp2()
	{
		grafo=new Graph<Integer, Vertices_Bogota_Info>(1);

		for (int i = 1; i<=100;i++)
		{
			Vertices_Bogota_Info aux = new Vertices_Bogota_Info(i,i);
			Vertice porAgregar = new Vertice(i, aux);

			grafo.addVertex(i, porAgregar);
		}

	}

	@Test
	public void addVertexTest()
	{
		setUp1();

		Vertices_Bogota_Info aux = new Vertices_Bogota_Info(1,1);
		grafo.addVertex(1, aux);
		assertEquals(1, grafo.darV()-1);

	}

	@Test
	public void addEdgeTest()
	{
		setUp2();

		grafo.addEdge(1, 2, 1);
		assertEquals(1, grafo.darE());
	}

	@Test
	public void getInfoVertexTest()
	{
		setUp2();

		Vertices_Bogota_Info buscado= (Vertices_Bogota_Info)grafo.getInfoVertex(1);

		assertEquals(1, buscado.darLat(), 0);
		assertEquals(1, buscado.darLon(), 0);
	}


	@Test
	public void setInfoVertexTest()
	{
		setUp2();
		Vertices_Bogota_Info aux = new Vertices_Bogota_Info(0,0);


		grafo.setInfoVertex(20, aux);
		assertEquals(0,((Vertices_Bogota_Info)(grafo.getInfoVertex(20))).darLat(),0);

	}


	@Test
	public void getCostYsetCostArc()
	{

		setUp2();

		grafo.addEdge(1, 2, 123456789);
		assertEquals(grafo.getCostArc(1, 2),123456789,0);

		grafo.setCostArc(1, 2, 9);
		assertEquals(grafo.getCostArc(1, 2),9,0);

	}

	@Test
	public void testDFS()
	{
		setUp2();

		for (int i=1;i<50;i++)
		{
			grafo.addEdge(i, i+1, i);
		}

		grafo.dfs(1);

		for (int i=1;i<50;i++)
		{
			Vertice aux=(Vertice)grafo.vertis.getSet(i);
			assertTrue (aux.revisarVisitado());
		}

		grafo.uncheck();

		for (int i=1;i<50;i++)
		{
			Vertice aux=(Vertice)grafo.vertis.getSet(i);
			assertFalse(aux.revisarVisitado());
		}
	}

	public void ccTest()
	{
		setUp2();

		for (int i=1;i<50;i++)
		{
			grafo.addEdge(i, i+1, i);
		}
		
		for (int i=51;i<=99;i++)
		{
			grafo.addEdge(i, i+1, i);
		}
		
		assertEquals(grafo.cc(),2);
		
	}



}
