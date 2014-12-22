/* This class provides all the user interaction to load a graph and perform actions on it
 *
 * Author:	Michael Tangredi, Till Krischer
 * Course:	CS221
 * Assignment:	Project 4
 * Date:	12/03/2014 
 */

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Graphs {
	
	public static String askFilename(Scanner scan) {
		String filename;
		System.out.print("Enter graph file name: ");
		filename = scan.nextLine();
		return filename;
	}
	
	//print the menu and return user's choice
	public static int menu(Scanner scan) {
		int choice;
		System.out.println();
		System.out.println("1. Is Connected");
		System.out.println("2. Minimum Spanning Tree");
		System.out.println("3. Shortest Path");
		System.out.println("4. Is Metric");
		System.out.println("5. Make Metric");
		System.out.println("6. Traveling Salesman Problem");
		System.out.println("7. Approximate TSP");
		System.out.println("8. Quit");
		System.out.println();
		System.out.print("Make your choice (1 - 8): ");
		choice = scan.nextInt();
		return choice;
	}
	
	public static int askStartingNode(Scanner scan, Graph g) {
		int start;
		System.out.print("From which node would you like to find the shortest paths (0 - " + (g.size() - 1) + "): ");
		start = scan.nextInt();
		return start;
	}

	public static void main(String[] args) {
		try {
			Scanner scan = new Scanner(System.in);
			String filename = askFilename(scan);
			Graph graph = new Graph(filename);
			
			//keep asking the user for choices until a 8 is entered
			int choice;
			while((choice = menu(scan)) != 8) {
				System.out.println();
				//perform the chosen action
				switch(choice) {
				case 1:
					if(graph.isConnected())
						System.out.println("Graph is connected.");
					else
						System.out.println("Graph is not connected.");
					break;
				case 2:
					if(! graph.isConnected())
						System.out.println("Error: Graph is not connected.");
					else
						System.out.println(graph.minimumSpanningTree());
					break;
				case 3:
					int start = askStartingNode(scan, graph);
					graph.printShortestPath(start);
					break;
				case 4:
					if(! graph.isCompletelyConnected())
						System.out.println("Graph is not metric:  Graph is not completely connected.");
					else if(! graph.obeysTriangleInEq())
						System.out.println("Graph is not metric: Edges do not obey the triangle inequality.");
					else
						System.out.println("Graph is metric.");
					break;
				case 5:
					graph.makeMetric();
					System.out.println(graph);
					break;
				case 6:
					if(! graph.isConnected())
						System.out.println("Error: Graph is not connected.");
					graph.printTravelingSalesman();
					break;
				case 7:
					if(! graph.isCompletelyConnected() || ! graph.obeysTriangleInEq())
						System.out.println("Error: Graph is not metric.");
					else
						graph.printApproximateTSP();
					break;
				default:
					System.out.println("Invalid choice");
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");	
		}
	}
}
