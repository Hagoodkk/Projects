//Kyle Hagood

import java.io.FileReader;
import java.util.Scanner;
import java.io.BufferedReader;



public class cmsc401 {
	public static void main(String[] args){
		LinkedList hashTable = readFile(args);
		runCommandInterface(hashTable);
	}
	static LinkedList readFile(String[] args){
		LinkedList list = new LinkedList();
		
		try(BufferedReader br = new BufferedReader(new FileReader(args[0]))){
			String fileLine = "";
			Scanner lineScan = new Scanner(fileLine);
			String cityName = null;
			double latitude = 0;
			double longitude = 0;
			boolean validLine;
			
			while ((fileLine = br.readLine()) != null){
				validLine = true;
				lineScan = new Scanner(fileLine);
				lineScan.useDelimiter(",");

				if (lineScan.hasNext())
					cityName = lineScan.next();
				else
					validLine = false;
				
				if (lineScan.hasNextDouble()) 
					latitude = lineScan.nextDouble();
				else 
					validLine = false;
				
				if (lineScan.hasNextDouble()) 
					longitude = lineScan.nextDouble();
				else
					validLine = false;

				if (validLine){
					list.insert(cityName, latitude, longitude);
				}

			}
			lineScan.close();
		}
		catch(Exception e){
			System.out.println("Invalid file entry. System closing.");
			System.exit(0);
		}
		return list;
	}
	static void runCommandInterface(LinkedList list){
		Scanner in = new Scanner(System.in);
		Scanner lineScan;
		String input,command,arg1, arg2;
		boolean keepGoing = true;
		
		while(in.hasNextLine()){
			input = "";
			command = "";
			arg1 = null;
			arg2 = null;
			
			//System.out.println("\nCommands:\n1. retrieve city\n2. distance city, city\n3. stop");
			input = in.nextLine();
			lineScan = new Scanner(input);
			
			if (lineScan.hasNext()) command = lineScan.next().trim();
			lineScan.useDelimiter(",");
			if (lineScan.hasNext()) arg1 = lineScan.next().trim();
			lineScan.useDelimiter(" ");
			if (lineScan.hasNext()){
				arg2 = lineScan.nextLine().trim();
				arg2 = arg2.substring(1,arg2.length()).trim();
			}
			
			
			if (command.equals("retrieve") && arg1 != null){
				System.out.println(list.retrieve(arg1));
			}
			else if (command.equals("distance") && arg1 != null && arg2 != null){
				if (list.retrieve(arg1).equals("Entry does not exist.") || list.retrieve(arg2).equals("Entry does not exist.")){
					System.out.println("Invalid entry. Please try again.");
				}
				else{
					System.out.println(list.distance(arg1,arg2));
				}	
			}
			else if (command.equals("stop")){
				System.out.println(list.getAvgBucketLength());
				System.exit(0);
			}
			else{
				System.out.println("\nInvalid input. Please try again.\n");
			}
		}
		in.close();
	}
}
















