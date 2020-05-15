package main;
import java.io.FileNotFoundException;
import java.io.IOException;

import controller.Controller;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException 
	{
		Controller controler = new Controller();
		controler.run();
	}
}
