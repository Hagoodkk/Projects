import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.*;
import org.jgrapht.graph.SimpleGraph;


public class SNAPProj {
	public static void main(String[] args){
		int adjMatrix[][] = readCsv(args[0]);
		UndirectedGraph<String, DefaultEdge> g = buildGraph(adjMatrix);
		double[] btwnCArray = calculateBetweenessArray(adjMatrix, g);
		double[] degreeCArray = calculateDegreeArray(adjMatrix);
		int[] capitalNodes;

		if (adjMatrix.length <= 1000) capitalNodes = new int[1];
		else if (adjMatrix.length >= 1001 && adjMatrix.length <= 2000) capitalNodes = new int[2];
		else if (adjMatrix.length >= 2001 && adjMatrix.length <= 3000) capitalNodes = new int[3];
		else if (adjMatrix.length >= 3001 && adjMatrix.length <= 4000) capitalNodes = new int[4];
		else capitalNodes = new int[5];
		
		for (int i = 0; i < capitalNodes.length; i ++){
			capitalNodes[i] = (int)(Math.random() * btwnCArray.length-1 + 0);
		}
		
		
		System.out.println("Capital Nodes:");
		System.out.println(printArray(capitalNodes));
		System.out.println("\nBetweeness Array:");
		System.out.println(printDoubleArray(btwnCArray));
		System.out.println("\nDegree Array:");
		System.out.println(printDoubleArray(degreeCArray));
		System.out.println("\nIterations:");
		
		
		int count = 0;
		double largestVal;
		while(true){
			count++;
			
			String rootInfo = "Capital Nodes:\r\n"
					+ printArray(capitalNodes) + "\r\n\r\n"
					+ "Betweeness Array:\r\n"
					+ printDoubleArray(btwnCArray) + "\r\n\r\n"
					+ "Degree Array:\r\n"
					+ printDoubleArray(degreeCArray) + "\r\n\r\n"
					+ "Iterations:\r\n"
					+ count;
			
			int nodeNum = 0;
			largestVal = 0;
			for (int i = 0; i < btwnCArray.length; i++){
				if (largestVal < btwnCArray[i]){
					largestVal = btwnCArray[i];
					nodeNum = i;
				}
			}
			if (largestVal == 0){
				for (int i = 0; i < degreeCArray.length; i++){
					if (largestVal < degreeCArray[i]){
						largestVal = degreeCArray[i];
						nodeNum = i;
					}
				}
			}
			for (int i = 0; i < adjMatrix.length; i++){
				adjMatrix[nodeNum][i] = 0;
				adjMatrix[i][nodeNum] = 0;
			}
			int currentNode = 0;
			boolean done = true;
			
			for (int i = 0; i < capitalNodes.length; i++){
				currentNode = capitalNodes[i];
				done = true;
				for (int j = 0; j < adjMatrix.length; j++){
					if (adjMatrix[currentNode][j] == 1){
						done = false;
					}
				}
				if (done){
					System.out.println(count);
					outputIterationInformation(adjMatrix, args[0], count, rootInfo, args[1]);
					System.exit(0);
				}
			}
			
			g = buildGraph(adjMatrix);
			btwnCArray = calculateBetweenessArray(adjMatrix, g);
			degreeCArray = calculateDegreeArray(adjMatrix);
			outputIterationInformation(adjMatrix, args[0], count, rootInfo, args[1]);
		}
	}
	public static UndirectedGraph buildGraph(int[][] adjMatrix){
		UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
		if (adjMatrix == null){
			System.out.println("Null matrix");
			System.exit(0);
		}
		int count = 0;
		for (int n = 0; n < adjMatrix.length; n++){
			g.addVertex(String.valueOf(n));
			count++;
		}
		
		for (int i = 0; i < adjMatrix.length; i++){
			for (int j = 0; j < adjMatrix.length; j++){
				if (adjMatrix[i][j] == 1 && !g.containsEdge(String.valueOf(i), String.valueOf(j)))
					g.addEdge(String.valueOf(i), String.valueOf(j));
			}
		}
		return g;
	}
	public static double[] calculateBetweenessArray(int[][] adjMatrix, UndirectedGraph g){
		CentralityComputer cc = new CentralityComputer(g);
		int x = 0;
		int count = g.vertexSet().size();
		double btwnCArray[] = new double[count];
		while (g.containsVertex(String.valueOf(x))){
			btwnCArray[x] = cc.findBetweennessOf(String.valueOf(x));
			x++;
		}
		return btwnCArray;
	}
	public static String printDoubleArray(double[] dArray){
		String result = "";
		for (int i = 0; i < dArray.length; i++){
			result+= dArray[i] + " ";
		}
		return result;
	}
	public static String printArray(int[] array){
		String result = "";
		for (int i = 0; i < array.length; i++){
			result+= array[i] + " ";
		}
		return result;
	}
	public static int[][] readCsv(String fileName){
		int[][] adjMatrix = null;
		String line = null;
		Scanner sc;
		int count = 0;
		int row = 0;
		int col = 0;
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
			if ((line = br.readLine()) != null){
				sc = new Scanner(line);
				sc.useDelimiter(";");
				while (sc.hasNext()){
					sc.next();
					count++;
				}
				adjMatrix = new int[count][count];
			}
			while ((line = br.readLine()) != null){
				sc = new Scanner(line);
				sc.useDelimiter(";");
				if (sc.hasNext()) sc.next();
				while (sc.hasNext()){
					adjMatrix[row][col] = (int) sc.nextDouble();
					col++;
				}
				row++;
				col = 0;
			}
		}catch(FileNotFoundException f){
			System.out.println("Input graph not found.");
		}
		catch(Exception e){
			System.out.println("Input graph not formatted incorrectly.");
		}
		
		for (int i = 0; i < adjMatrix.length; i++){
			adjMatrix[i][i] = 0;
		}
		return adjMatrix;
	}
	public static void outputIterationInformation(int[][] adjMatrix, String fileName, int iterationNum, String rootInfo, String testNum){
		if (adjMatrix == null || fileName == null) System.out.println("Matrix of file name was null.");
		else{
			String directoryPath = fileName + "-test" + testNum;
			File directory = new File(directoryPath);
			if (!directory.exists()) directory.mkdir();
			
			String filePath = directoryPath + "/Iter" + iterationNum + ".csv";
			try(PrintWriter pw = new PrintWriter(new FileWriter(filePath))){
				File outputFile = new File(filePath);
				if (!outputFile.exists()) outputFile.createNewFile();
				for (int i = 0; i < adjMatrix.length; i++){
					pw.write(";" + i);
				}
				
				for (int i = 0; i < adjMatrix.length; i++){
					pw.write("\r\n" + i + ";");
					for (int j = 0; j < adjMatrix.length; j++){
						pw.write(adjMatrix[i][j] + ";");
					}
				}
				
			}catch(Exception e){ e.printStackTrace(); }
		
			String filePath2 = directoryPath + "/Iter" + iterationNum + "Info.txt";
			try(PrintWriter pw = new PrintWriter(new FileWriter(filePath2))){
				File outputFile = new File(filePath2);
				if (!outputFile.exists()) outputFile.createNewFile();
				pw.write(rootInfo);
			}
			catch(Exception e){
				System.out.println("Error printing RootInfo.txt");
			}
		}
	}
	public static void printAdjMatrix(int[][] adjMatrix){
		for (int i = 0; i < adjMatrix.length; i++){
			for (int j = 0; j < adjMatrix.length; j++){
				System.out.print(adjMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	public static double[] calculateDegreeArray(int[][] adjMatrix){
		double degreeCArray[] = new double[adjMatrix.length];
		for (int i = 0; i < adjMatrix.length ;i++){
			for (int j = 0; j < adjMatrix.length; j++){
				degreeCArray[i]+= adjMatrix[i][j];
			}
		}
		return degreeCArray;
	}
}
