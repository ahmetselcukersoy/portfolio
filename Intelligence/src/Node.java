class Node {
    double key;
    int height;
    int maxIndependentMembers;
    int rank;
    Node left, right;
    String name;
    boolean isCached;

    Node(double key, String name) {
        this.key = key;
        this.height = 1;
        this.maxIndependentMembers = 0;
        this.name = name;
        this.rank = 0;
    }
}
