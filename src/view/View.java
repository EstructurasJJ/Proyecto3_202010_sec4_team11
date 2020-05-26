package view;

import model.logic.Modelo;

public class View 
{
	    /**
	     * Metodo constructor
	     */
	    public View()
	    {
	    	
	    }
	    
		public void printMenu()
		{
			System.out.println("1. Crear y cargar todo.");
			System.out.println("2. Encontrar el vertice m�s cercano dada una ubicaci�n geografica.");
			System.out.println("3. Desplegar grafo limitado en mapa.");
			System.out.println("4. Menor camino entre dos ubicaciones.");
			System.out.println("5. Intentar Kruskal..");
			System.out.println("6. Exit");
			System.out.println("Dar el numero de opcion a resolver, luego oprimir tecla Return: (e.g., 1):");
		}

		public void printMessage(String mensaje) {

			System.out.println(mensaje);
		}		
		
		public void printModelo(Modelo modelo)
		{
			// TODO implementar
		}
}
