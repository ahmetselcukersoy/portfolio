public class Node implements Comparable<Node> {
    private final String name;
    private final double cost;

    public Node(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.cost, other.cost);
    }
}
