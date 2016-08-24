//Kyle Hagood

import java.util.Scanner;
public class cmsc401 {
	public static void main(String[] args){
		String input = "";
		Scanner in = new Scanner(System.in);
		Scanner readLine;
		int numNodes = 0;
		int numEdges = 0;
		int i = 0;
		int j = 0;
		int k = 0;
		int minI;
		int minValue;
		
		//System.out.print("Enter the number of nodes: ");
		if (in.hasNextInt()) numNodes = in.nextInt();
		//System.out.print("\nEnter the number of edges: ");
		if (in.hasNextInt()) numEdges = in.nextInt();
		
		int[] costToStay = new int[numNodes+1];
		int[] valChecked = new int[numNodes+1];
		int[][] adjMatrix = new int[numNodes+1][numNodes+1];
		
		for (int a = 0; a < costToStay.length; a++){
			costToStay[a] = 0;
			valChecked[a] = 0;
		}
		for (int a = 0; a < adjMatrix.length; a++){
			for (int b = 0; b < adjMatrix.length; b++){
				adjMatrix[a][b] = 0;
			}
		}
		in.nextLine();
		
		//System.out.println("\nEnter the motel prices at each city:");
		numNodes -= 2;
		while(numNodes > 0){
			if (in.hasNextLine()) input = in.nextLine();
			readLine = new Scanner(input);
			if (readLine.hasNextInt()) i = readLine.nextInt();
			if (readLine.hasNextInt()) j = readLine.nextInt();
			costToStay[i] = j;
			numNodes--;
		}
		
		//System.out.println("Enter the gas prices for traveling between two cities:");
		while (in.hasNextLine()){
			if (in.hasNextLine()) input = in.nextLine();
			readLine = new Scanner(input);
			if (readLine.hasNextInt()) i = readLine.nextInt();
			if (readLine.hasNextInt()) j = readLine.nextInt();
			if (readLine.hasNextInt()) k = readLine.nextInt();
			adjMatrix[i][j] = k + costToStay[j];
			adjMatrix[j][i] = k + costToStay[i];
			numEdges--;
		}
		
		//FOR DEBUGGING PURPOSES ONLY
		//System.out.println("Cost To Stay");
		//for (int a = 1; a < costToStay.length; a++){
		//	System.out.print(costToStay[a] + " ");
		//}
		//System.out.println();
		///////////////////
		
		//System.out.println("Value Checked");
		//FOR DEBUGGING PURPOSES ONLY
		//for (int a = 1; a < valChecked.length; a++){
		//	System.out.print(valChecked[a] + " ");
		//}
		//System.out.println();
		///////////////////
		
		
		//FOR DEBUGGING PURPOSES ONLY
		//System.out.println("Adjacency Matrix");
		//for (int a = 1; a < adjMatrix.length; a++){
		//	for (int b = 1; b < adjMatrix.length; b++){
		//		System.out.print(adjMatrix[a][b] + " ");
		//	}
		//	System.out.println();
		//}
		//System.out.println();
		////////////////////////
		
		int[] travelQueue = new int[costToStay.length];
		
		travelQueue[1] = 0;
		for (int z = 2; z < travelQueue.length; z++){
			travelQueue[z] = Integer.MAX_VALUE; //Use -1 to denote infinity
		}
		
		
		//MORE DEBUGGING
		//System.out.println("Travel Queue");
		//for (int a = 1; a < travelQueue.length; a++){
		//	System.out.print(travelQueue[a] + " ");
		//}
		//System.out.println();
		/////////////////////////
		boolean keepGoing = true;
		
		while(notDone(valChecked)){
			minI = -1;
			keepGoing = true;
			for (int z = 1; z < valChecked.length && keepGoing; z++){
				if (valChecked[z] == 0 &&
						travelQueue[z] < Integer.MAX_VALUE){
					minI = z;
					keepGoing = false;
				}
			}
			for (int z = 1; z < travelQueue.length; z++){
				if (travelQueue[z] != Integer.MAX_VALUE &&
						valChecked[z] == 0 &&
						travelQueue[z] < travelQueue[minI]){
					minI = z;
				}
			}

			valChecked[minI] = 1;
			
			
			for (int z = 1; z < adjMatrix.length; z++){ //Infinitely looping here
				if (adjMatrix[minI][z] != 0){
					if (travelQueue[z] == Integer.MAX_VALUE){
						travelQueue[z] = travelQueue[minI] + adjMatrix[minI][z];
					}

					else{
						if (adjMatrix[minI][z] + travelQueue[minI] < travelQueue[z]){
							travelQueue[z] = travelQueue[minI] + adjMatrix[minI][z];
						}
					}
				}
			}
			//System.out.println();
			//for (int z = 1; z < travelQueue.length; z++){
			//	System.out.print(travelQueue[z] + " ");
			//}
		}
		System.out.println(travelQueue[2]);

		//FOR DEBUGGING PURPOSES ONLY
		//for (int z = 1; z < valChecked.length; z++){
		//	System.out.print(valChecked[z] + " ");
		//}
		//System.out.println();
		//for (int z = 1; z < travelQueue.length; z++){
		//	System.out.print(travelQueue[z] + " ");
		//}
		//////////////
	}
	static boolean notDone(int[] valChecked){
		for (int i = 1; i < valChecked.length; i++){
			if (valChecked[i] == 0) return true;
		}
		return false;
	}
}
