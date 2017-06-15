import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Kyle on 6/14/2017.
 */

public class MazeSolver {
    static boolean debug = false;

    public static void main(String[] args) {
        if (!(args.length > 0)) {
            System.out.println("File name not specified. Exiting program.");
            System.exit(1);
        }
        int startNode = 1;
        int endNode = 8;

        if (args.length > 1 && args[1].equals("-d")) debug = true;

        String fileName = args[0];
        int[][] maze = readMazeFromFile(fileName);
        if (debug) printMaze(maze);

        HashMap<Integer, ArrayList<Integer>> mazeHash = mazeMatrixToMazeHash(maze);
        System.out.println(findBestPathInMazeHash(mazeHash, startNode, endNode));
    }

    private static String findBestPathInMazeHash(HashMap<Integer, ArrayList<Integer>> mazeHash, int startNode, int endNode) {
        String returnStr;

        boolean[] visitedNodes = new boolean[mazeHash.size()+1];
        int[] nodePaths = new int[mazeHash.size()+1];
        for (int i = 0; i < mazeHash.size()+1; i++) {
            visitedNodes[i] = false;
            nodePaths[i] = Integer.MAX_VALUE;
        }

        nodePaths[startNode] = 0;
        for (Integer node : mazeHash.get(startNode)) {
            nodePaths[node] = 1;
        }
        visitedNodes[0] = true;
        visitedNodes[startNode] = true;

        while (!allNodesVisited(visitedNodes)) {
            int minPathValue = Integer.MAX_VALUE;
            int minPathValuePos = 0;

            for (int i = 1; i < nodePaths.length; i++) {
                if (nodePaths[i] < minPathValue && !visitedNodes[i]) {
                    minPathValue = nodePaths[i];
                    minPathValuePos = i;
                }
            }

            for (Integer node : mazeHash.get(minPathValuePos)) {
                if (nodePaths[minPathValuePos]+1 < nodePaths[node]) nodePaths[node] = nodePaths[minPathValuePos]+1;
            }
            visitedNodes[minPathValuePos] = true;
        }
        System.out.println("\n" + Arrays.toString(nodePaths));

        returnStr = String.valueOf(endNode);

        while (endNode != startNode) {
            int shortestPath = Integer.MAX_VALUE;
            int nextValue = -1;

            for (Integer node : mazeHash.get(endNode)) {
                if (nodePaths[node] < shortestPath) {
                    shortestPath = nodePaths[node];
                    nextValue = node;
                }
            }
            if (nextValue == -1) System.out.println("No successful path in maze.");

            endNode = nextValue;
            returnStr = String.valueOf(endNode) + "," + returnStr;
        }

        return returnStr;
    }

    private static boolean allNodesVisited(boolean[] visitedNodes) {
        for (boolean nodeVisited : visitedNodes) if (!nodeVisited) return false;
        return true;
    }

    private static HashMap<Integer, ArrayList<Integer>> mazeMatrixToMazeHash(int[][] maze) {
        int nodeCount = 0;
        for (int i = 0; i < maze.length ;i++) {
            for (int j = 0; j < maze.length; j++) {
                if (maze[i][j] == 1) maze[i][j] = 0;
                else if (maze[i][j] == 0) maze[i][j] = ++nodeCount;
                else if (maze[i][j] == 9) maze[i][j] = ++nodeCount;
            }
        }
        if (debug) printMaze(maze);

        HashMap<Integer, ArrayList<Integer>> adjHash= new HashMap<>();
        for (int i = 1; i <= nodeCount; i++) adjHash.put(i, new ArrayList<>());

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                if (maze[i][j] != 0) {
                    if (i < maze.length-1 && maze[i+1][j] != 0) adjHash.get(maze[i][j]).add(maze[i+1][j]);
                    if (i > 0 && maze[i-1][j] != 0) adjHash.get(maze[i][j]).add(maze[i-1][j]);
                    if (j > 0 && maze[i][j-1] != 0) adjHash.get(maze[i][j]).add(maze[i][j-1]);
                    if (j < maze.length-1 && maze[i][j+1] != 0) adjHash.get(maze[i][j]).add(maze[i][j+1]);

                    /* Comment in these for diagonal traversal
                    if (i > 0 && j > 0 && maze[i-1][j-1] != 0) adjHash.get(maze[i][j]).add(maze[i-1][j-1]);
                    if (i > 0 && j < maze.length-1 && maze[i-1][j+1] != 0) adjHash.get(maze[i][j]).add(maze[i-1][j+1]);
                    if (i < maze.length-1 && j > 0 && maze[i+1][j-1] != 0) adjHash.get(maze[i][j]).add(maze[i+1][j-1]);
                    if (i < maze.length-1 && j < maze.length-1 && maze[i+1][j+1] != 0) adjHash.get(maze[i][j]).add(maze[i+1][j+1]);
                    */
                }
            }
        }
        for (int i = 1; i <= nodeCount; i++) Collections.sort(adjHash.get(i));
        if (debug) for (int i = 1; i <= nodeCount; i++) System.out.println(i + " -> " + adjHash.get(i));

        return adjHash;
    }

    private static int[][] readMazeFromFile(String fileName) {
        int lineNum = 0;
        int[][] maze = null;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String currentLine;
            String[] currentLineArray = null;


            lineNum++;
            if ((currentLine = br.readLine()) != null) {
                currentLineArray = currentLine.split(",");
                maze = new int[currentLineArray.length][currentLineArray.length];
            } else {
                System.out.println("File is empty. Exiting program.");
                System.exit(1);
            }

            for (int i = 0; i < maze.length; i++) {
                if (!(currentLineArray[i].equals("0") || currentLineArray[i].equals("1") || currentLineArray[i].equals("9"))) {
                    System.out.println("Maze is incorrectly formatted at line " + lineNum + ". Exiting program.");
                    System.exit(1);
                }
                maze[0][i] = Integer.parseInt(currentLineArray[i]);
            }

            lineNum++;
            while ((currentLine = br.readLine()) != null) {
                try {
                    currentLineArray = currentLine.split(",");

                    for (int i = 0; i < maze.length; i++) {
                        if (!(currentLineArray[i].equals("0") || currentLineArray[i].equals("1") || currentLineArray[i].equals("9"))) {
                            System.out.println("Maze is incorrectly formatted at line " + lineNum + ". Exiting program.");
                            System.exit(1);
                        }
                        maze[lineNum-1][i] = Integer.parseInt(currentLineArray[i]);
                    }

                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Maze is incorrectly formatted at line " + lineNum + ". Exiting program.");
                    System.exit(1);
                }
                lineNum++;
            }

        } catch (FileNotFoundException f) {
            System.out.println("File not does not exist. Exiting program.");
            System.exit(1);
        } catch (Exception e) {
            if (lineNum == 0) System.out.println("Error opening file. Exiting program.");
            else System.out.println("Error parsing file at line " + lineNum + ". Exiting program.");
            System.exit(1);
        }
        return maze;
    }

    private static void printMaze(int[][] maze) {
        for (int[] row : maze) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

}