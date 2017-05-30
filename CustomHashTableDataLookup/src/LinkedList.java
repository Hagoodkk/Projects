import java.util.Scanner;

public class LinkedList {
	private Node rootNode;
	private int numNodes;
	private int numBuckets;
	
	private class Node{
		private String cityName;
		private double latitude;
		private double longitude;
		private Node underNode;
		private Node rightNode;
		 
		private Node(){
			this.cityName = null;
			this.latitude = -1;
			this.longitude = -1;
			this.underNode = null;
			this.rightNode = null;
		}
		
		private Node(String cityName,double latitude,double longitude){
			this.cityName = cityName;
			this.latitude = latitude;
			this.longitude = longitude;
			this.underNode = null;
			this.rightNode = null;
		}
	}
	public LinkedList(){
		this.rootNode = new Node();
		Node currentNode = rootNode;
		this.numBuckets = 10000;
		
		int i = 0;
		while(i < 10000){
			Node newNode = new Node();
			currentNode.underNode = newNode;
			currentNode = newNode;
			i++;
		}
	}
	public void insert(String cityName, double latitude, double longitude){
		int num = getHash(cityName);
		
		int i = 0;
		Node currentNode = rootNode;
		while (i < num){
			currentNode = currentNode.underNode;
			i++;
		}
		if (currentNode.rightNode != null) currentNode = currentNode.rightNode;
		while (currentNode.cityName != null && currentNode.rightNode != null){
			currentNode = currentNode.rightNode;
		}
		Node newNode = new Node(cityName,latitude,longitude);
		currentNode.rightNode = newNode;
		numNodes++;
	}
	public String retrieve(String cityName){
		//Do hash function, will return a number
		String errorReturn;
		errorReturn = "Entry does not exist.";
		
		int num = getHash(cityName);
		
		int i = 0;
		Node currentNode = rootNode;
		while (i < num){
			currentNode = currentNode.underNode;
			i++;
		}
		if (currentNode.rightNode != null) currentNode = currentNode.rightNode;
		while (currentNode.cityName != null && !currentNode.cityName.equals(cityName) && currentNode.rightNode != null){
			currentNode = currentNode.rightNode;
		}
		if (currentNode.cityName != null && currentNode.cityName.equals(cityName)){
			return currentNode.latitude + " " + currentNode.longitude;
		}
		else{
			return errorReturn;
		}
		
		
	}
	public int distance(String city1,String city2){
		String coord1 = retrieve(city1);
		String coord2 = retrieve(city2);
		Scanner s1 = new Scanner(coord1);
		Scanner s2 = new Scanner(coord2);
		
		double lat1 = s1.nextDouble();
		double lat2 = s2.nextDouble();
		double lon1 = s1.nextDouble();
		double lon2 = s2.nextDouble();
		
		s1.close();
		s2.close();
		
		double p1 = Math.pow(Math.sin((Math.toRadians(lat2)-Math.toRadians(lat1))/2),2);
		double p2 = Math.pow(Math.sin((Math.toRadians(lon2)-Math.toRadians(lon1))/2),2);
		double p3 = p1 + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*p2;
		double p4 = Math.sqrt(p3);
		double p5 = 6373*2*Math.asin(p4);
		p5 = Math.round(p5);
		return (int) p5;
	}
	public double getAvgBucketLength(){
		return (double) numNodes/numBuckets;
	}
	public int getHash(String cityName){
		double preHash = cityName.hashCode();
		double hashCode = (preHash*Math.PI)%10000;
		return (int) hashCode;
	}
}





















