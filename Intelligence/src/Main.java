import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        AVLTree tree = new AVLTree();
        String fileName = args[0];
        String outputFile = args[1];
        BufferedReader reader = new BufferedReader(new FileReader(fileName)); // creating reader & writer only once
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile, true)); // creating reader & writer only once
        String line = reader.readLine();
        String[] tokens = line.split(" ");
        String name = tokens[0];
        double gms = Double.parseDouble(tokens[1]);
        tree.root = new Node(gms, name); // adding boss
        tree.nodeLookup.put(name, tree.root);
        bw.flush();

        while ((line = reader.readLine()) != null) {
            tokens = line.split(" ");
            switch (tokens[0]) {
                case "MEMBER_IN" -> { // a member joins
                    name = tokens[1];
                    gms = Double.parseDouble(tokens[2]);
                    tree.insert(gms, name, bw);
                }
                case "MEMBER_OUT" -> { // a member leaves
                    name = tokens[1];
                    gms = Double.parseDouble(tokens[2]);
                    tree.delete(gms, name, bw);
                }
                case "INTEL_TARGET" -> { // targeting the family
                    String name1 = tokens[1];
                    String name2 = tokens[3];
                    Node lowestCommonSuperior = tree.findLowestCommonSuperior(name1, name2);
                    if (lowestCommonSuperior != null)
                        bw.write("Target Analysis Result: " + lowestCommonSuperior.name + " " + String.format("%.3f", lowestCommonSuperior.key));
                    else
                        bw.write("Target Analysis Result: No common superior found");
                    bw.newLine();
                }
                case "INTEL_DIVIDE" -> { // dividing the family
                    tree.resetCacheForAllNodes(tree.root); // resetting the cache first
                    int maxIndependentMembers = tree.findMaxIndependentMembers(tree.root);
                    bw.write("Division Analysis Result: " + maxIndependentMembers);
                    bw.newLine();
                }
                case "INTEL_RANK" -> { // monitoring ranks in the family
                    name = tokens[1];
                    ArrayList<Node> result = tree.findSameRank(name);
                    bw.write("Rank Analysis Result:");
                    for (Node node : result)
                        bw.write(" " + node.name + " " + String.format("%.3f", node.key));
                    bw.newLine();
                }
            }
            bw.flush();
        }
    }
}
