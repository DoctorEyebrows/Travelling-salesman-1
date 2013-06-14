package salesman1;

import java.util.Random;
import java.util.Arrays;
import java.lang.Math;
import java.io.*;

public class Tour
{
	private static Random rand = new Random();
	
	private int[][] city;
	private int[] tour;
	private int size;		//number of nodes
	private int length;
	
	public Tour(int[][] city)
	{
		this.city = city;
		size = city.length;
		tour = new int[size];
		initialize();
	}
	
	public Tour(int[][] city, int[] tour)
	{
		this.city = city;
		size = city.length;
		this.tour = Arrays.copyOf(tour,size);
	}
	
	public void initialize()
	{
		//produce a random tour:
		Arrays.fill(tour,0);
		for(int node=1; node<size; node++)
		{
			int i;
			do
			{
				i = rand.nextInt(size-1) + 1;
			}
			while(tour[i] != 0);
			tour[i] = node;
		}
	}
	
	public Tour clone()
	{
		return new Tour(Arrays.copyOf(city,size),tour);
	}
	
	public void calculateFitness()
	{
		length = 0;
		for(int i=0; i<size; i++)
		{
			length += city[tour[i]][tour[(i+1)%size]];
		}
	}
	
	public double getFitness()
	{
		return 1.0/length;
	}
	
	public double getFitness(double elitism)
	{
		return Math.pow(1.0/length,elitism);
	}
	
	public int getLength()
	{
		return length;
	}
	
	public void mutate()
	{
		//swap the positions of two nodes in the tour:
		int i = rand.nextInt(size-1) + 1;
		int j = rand.nextInt(size-1) + 1;
		int swap = tour[j];
		tour[j] = tour[i];
		tour[i] = swap;
	}
	
	public int[] getTour()
	{
		return tour;
	}
	
	private boolean tourContains(int[] tour, int key, int upTo)
	{
		for(int i=0; i<upTo; i++)
		{
			if(tour[i] == key)
				return true;
		}
		return false;
	}
	
	private int getSuccessor(int[] tour, int node)
	{
		//returns which node the given node points to in the given tour
		int i=0;
		for(; tour[i] != node; i++){}
		return tour[(i+1)%size];
	}
	
	public Tour crossover(Tour other)
	{
		//try to incorporate edges from both tours into a new tour
		
		int[] otherTour = other.getTour();
		int[] child = new int[size];
		int[] nextTour = otherTour;
		int nextNode;
		
		child[0] = tour[0];
		for(int i=0; i<size-1; i++)
		{
			//find the current node in the nextTour, see where it points:
			nextNode = getSuccessor(nextTour,child[i]);
			
			//now check that it doesn't point to a node that's already in the child tour:
			if(!tourContains(child,nextNode,i+1))
			{
				child[i+1] = nextNode;
			}
			else
			{
				/* couldn't use the edge from nextTour, so try the other
				 * tour. failing that, find any free node.
				 */
				nextTour = nextTour==tour ? otherTour : tour;
				nextNode = getSuccessor(nextTour,child[i]);
				if(!tourContains(child,nextNode,i+1))
				{
					child[i+1] = nextNode;
				}
				else
				{
					for(int k=0; k<size; k++)
					{
						if(!tourContains(child,k,i+1))
						{
							child[i+1] = k;
							break;
						}
					}
				}
			}
			nextTour = nextTour==tour ? otherTour : tour;
		}
		Tour output = new Tour(city,child);
		/*
		System.out.println(toString());
		System.out.println(other.toString());
		System.out.println(output.toString());
		System.out.println();*/
		return output;
	}
	
	public String toString()
	{
		String output = String.format("%d",tour[0]+1);
		for(int i=1; i<size; i++)
		{
			output += String.format(",%d",tour[i]+1);
		}
		return output;
	}
}
	