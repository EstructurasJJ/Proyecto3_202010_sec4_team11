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
			System.out.println("4. Menor camino entre dos ubicaciones por distancia Haversiana.");
			System.out.println("5. Montar red de vigilancia para los comparendos m�s graves");
			System.out.println("6. Buscar el menor camino entre dos ubicaciones por n�mero de comparendos");
			System.out.println("7. Generar red de vigilancia para los M v�rtices con m�s comparendos");
			System.out.println("8. Obtener caminos m�s cortos para que los polic�as puedan atender los M comparendos m�s graves");
			System.out.println("9. Identificar las zonas de impacto de la polic�a");
			System.out.println("10. Exit");
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
