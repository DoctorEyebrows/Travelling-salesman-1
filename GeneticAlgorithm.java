package salesman1;

import java.util.ArrayList;
import java.io.*;

public class GeneticAlgorithm
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		System.out.println(args.length);
		int graphsize = 48;
		if(args.length == 1)
			graphsize = Integer.parseInt(args[0]);
		Breeder breeder = new Breeder(graphsize);
		breeder.breed();
	}
}