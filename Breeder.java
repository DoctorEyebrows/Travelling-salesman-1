package salesman1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;
import java.util.Scanner;
import java.io.*;

public class Breeder
{
	private int popSize;
	private int maxGenerations;
	private double elitism;
	private double mutationRate;
	private double crossoverRate;
	
	private String name;
	private int size;
	private int[][] city;
	
	private ArrayList<Tour> population;
	private Random rand = new Random();
	private Tour bestEver;
	
	public Breeder(int size) throws FileNotFoundException, IOException
	{
		popSize = 100;
		maxGenerations = 80000;
		elitism = 2;
		mutationRate = 0.2;
		crossoverRate = 1.0;
		this.size = size;
		
		
		
		load();					//initializes name and city from the file that corresponds to size
		System.out.println("Loaded city");
		population = new ArrayList<Tour>();
		for(int i=0; i<popSize; i++)
		{
			population.add(new Tour(city));
		}
		
	}
	
	public void breed() throws java.io.IOException
	{
		int n = population.size();		//for convenience
		
		//initialize population:
		System.out.println("Initializing...");
		for(Tour competitor : population)
			competitor.initialize();
		/*int[] goodTour = {0,13,36,12,16,24,17,51,31,40,52,1,27,42,6,11,20,7,26,18,37,50,3,41,14,54,25,57,30,46,15,47,23,29,34,38,21,32,53,56,10,43,9,22,48,8,35,55,28,19,44,4,33,39,2,49,5,45,0};
		population.set(0,new Tour(city,goodTour));*/
		System.out.println("Done...");
		System.out.println("Breeding...");
		
		//now breed them:
		int[] chosen = new int[n];
		double totalFitness = 0;
		double maxFitness = 0;
		int totalLength = 0;
		int bestIndex;
		ArrayList<Tour> crossoverBuffer = new ArrayList<Tour>();
		bestEver = population.get(0);
		Tour best = null;
		for(int generation=0; generation<maxGenerations; generation++)
		{
			//System.out.printf("\nGeneration %d\n",generation);
			
			//calculate fitness:
			//System.out.println("Calculating...");
			totalFitness = 0;
			maxFitness = 0;
			totalLength = 0;
			bestIndex = 0;
			int index=0;
			for(Tour competitor : population)
			{
				competitor.calculateFitness();
				totalFitness += competitor.getFitness(2);
				totalLength += competitor.getLength();
				if(competitor.getFitness() > maxFitness)
				{
					maxFitness = competitor.getFitness();
					best = competitor;
					bestIndex = index;
					if(competitor.getFitness() > bestEver.getFitness())
					{
						bestEver = competitor.clone();
						bestEver.calculateFitness();		//because a fresh clone won't have counted its length yet
					}
				}
				index++;
			}
			
			//System.out.println("Selecting");
			//roulette wheel selection:
			double ball, acc;
			Arrays.fill(chosen,0);
			chosen[bestIndex] = 3;	//ensure the best gets picked at least thrice
			for(int i=0; i<n-3; i++)
			{
				ball = rand.nextDouble()*totalFitness;		//where the ball lands
				acc = 0;
				for(int j=0; j<n; j++)
				{
					acc += population.get(j).getFitness(2);
					if(acc>ball)
					{
						chosen[j]++;
						break;
					}
				}
			}
			
			//System.out.println();
			if(maxGenerations < 50)
			{
				for(int i=0; i<n; i++)System.out.printf("%d ",chosen[i]);
			}
			//System.out.println();
			
			//copy
			//System.out.println("Copying / mutating...");
			int lucky = 0, unlucky = 0;
			crossoverBuffer.clear();
			Tour a, b, child;
			while(true)
			{
				while(lucky < n && chosen[lucky] < 2)
					lucky++;
				if(lucky==n)break;
				// now lucky is the index of a competitor chosen several times
				crossoverBuffer.add(population.get(lucky));
				chosen[lucky]--;
			}
			
			while(true)
			{
				while(unlucky < n && chosen[unlucky] > 0)unlucky++;
				if(unlucky==n)break;
				//unlucky is the index of one chosen 0 times
				a = crossoverBuffer.get(0);
				crossoverBuffer.remove(0);
				population.set(unlucky,a.clone());
				if(rand.nextDouble() < mutationRate)
					population.get(unlucky).mutate();
				unlucky++;		//only gets to die once, he's not that unlucky
				
				while(unlucky < n && chosen[unlucky] > 0)unlucky++;
				if(unlucky==n)break;
				//grabbed another unlucky one, if there's one left
				b = crossoverBuffer.get(0);
				crossoverBuffer.remove(0);
				child = a.crossover(b);
				population.set(unlucky,child);
				if(rand.nextDouble() < mutationRate)
					population.get(unlucky).mutate();
				unlucky++;
			}
			
			
			//System.out.printf("%d deaths\n",deaths);
			if(maxGenerations - generation < 1000)
			{
				System.out.printf("Shortest length: %d\n",best.getLength());
				System.out.printf("Average length: %f\n",(double)totalLength/popSize);
			}
		}
		
		System.out.printf("Shortest length after %d generations was %d\n",maxGenerations,bestEver.getLength());
		System.out.println();
		saveTour();
	}
	
	private void load() throws FileNotFoundException, IOException
	{
		File fin = new File(String.format("SAfile%03d.txt",size));
		Scanner scanner = new Scanner(fin);
		
		int pos = 0;
		int node_a = 0;
		int node_b = 1;
		int node = 0;		//how many distances have already been read in
		String [] expected = {"NAME", "=", "", "SIZE", "=", ""};
		scanner.useDelimiter(" |,\r\n");
		while(scanner.hasNext())
		{
			if(pos == 2)
			{
				name = scanner.next();
			}
			else if(pos == 5)
			{
				size = scanner.nextInt();
				city = new int[size][size];
				city[0][0] = 10000;
				scanner.useDelimiter(",\r\n|,");
			}
			else if(pos < 6)
			{
				if(!scanner.next().equals(expected[pos]))
				{
					System.out.println("Bad input file. Exiting...");
					throw new IOException("Expected " + expected[pos]);
				}
			}
			else
			{
				if(node_b == size)
				{
					node_a++;
					node_b = node_a + 1;
				}
				else
				{
					city[node_a][node_b] = scanner.nextInt();
					city[node_b][node_a] = city[node_a][node_b];
					node_b++;
					node++;
				}
			}
			pos++;
		}
		
	}
	
	private void saveTour() throws java.io.IOException
	{
		FileWriter fstream = new FileWriter(String.format("tourSAfile%03d.txt",size));
		BufferedWriter out = new BufferedWriter(fstream);
		
		int length = bestEver.getLength();
		int[] tour = bestEver.getTour();
		String output = String.format("%d",tour[0]+1);
		for(int i=1; i<size; i++)
		{
			output += String.format(",%d",tour[i]+1);
		}
		
		out.write(String.format("NAME = %s,\r\nTOURSIZE = %d,\r\nLENGTH = %d,\r\n",name,size,length));
		out.write(output);
		out.close();
		
		System.out.printf("Saved tour of size %d, length %d",size,length);
	}
}