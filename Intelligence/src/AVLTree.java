import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AVLTree {
    Node root;
    Map<String, Node> nodeLookup = new HashMap<>(); // use hashMap to access easily

    int height(Node node) {
        return (node != null) ? node.height : 0;
    }

    void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    Node rightRotate(Node y) {
        Node x = y.left;
        Node z = x.right;

        x.right = y;
        y.left = z;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    Node leftRotate(Node x) {
        Node y = x.right;
        Node z = y.left;

        y.left = x;
        x.right = z;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // insert functions, first one is for the main part
    void insert(double key, String name, BufferedWriter bw) throws IOException {
        root = insertNode(root, key, name, bw);
    }

    Node insertNode(Node node, double key, String name, BufferedWriter bw) throws IOException {

        // adding boss at first
        if (node == null) {
            Node newNode = new Node(key, name);
            nodeLookup.put(name, newNode);
            return newNode;
        }

        // welcoming new members
        bw.write(node.name + " welcomed " + name);
        bw.newLine();

        if (key < node.key)
            node.left = insertNode(node.left, key, name, bw);
        else if (key > node.key)
            node.right = insertNode(node.right, key, name, bw);
        else
            return node;

        // keeping the Tree balanced
        updateHeight(node);
        int balance = getBalanceFactor(node);

        if (balance > 1) {
            if (key < node.left.key) return rightRotate(node);
            if (key > node.left.key) {
                node.left = leftRotate(node.left);
                return rightRotate(node);
            }
        }
        if (balance < -1) {
            if (key > node.right.key) return leftRotate(node);
            if (key < node.right.key) {
                node.right = rightRotate(node.right);
                return leftRotate(node);
            }
        }
        return node;
    }

    // delete functions, first one is for the main part
    void delete(double key, String name, BufferedWriter bw) throws IOException {
        root = deleteNode(root, key, name, bw);
    }

    // default deleteNode function will write the leaving member
    // if tf is given as false, the writing operation won't be done
    // -> in case of one deletion makes some other members be deleted
    Node deleteNode(Node node, double key, String name, BufferedWriter bw) throws IOException {
        return deleteNode(node,  key,  name,  bw, true);
    }

    Node deleteNode(Node node, double key, String name, BufferedWriter bw, boolean tf) throws IOException {
        if (node == null) return null;

        if (key < node.key) {
            node.left = deleteNode(node.left, key, name, bw, tf);
        } else if (key > node.key) {
            node.right = deleteNode(node.right, key, name, bw, tf);
        } else {
            // Node have 1 or 0 child
            if (node.left == null || node.right == null) {
                Node temp = (node.left == null) ? node.right : node.left;
                if (temp == null) { // Node has 0 child
                    if (tf){
                        bw.write(node.name + " left the family, replaced by nobody");
                        bw.newLine();
                    }
                    node = null;
                } else { // Node has 1 child
                    if (tf){
                        bw.write(node.name + " left the family, replaced by " + temp.name);
                        bw.newLine();
                    }
                    node.key = temp.key;
                    node.name = temp.name;
                    // According to which child exists, delete temp
                    // tf = false because we do not want any other line
                    if (temp == node.right)
                        node.right = deleteNode(node.right, temp.key, temp.name, bw, false);
                    if (temp == node.left)
                        node.left = deleteNode(node.left, temp.key, temp.name, bw, false);
                }

            }
            // Node has 2 children
            else {
                Node min = minValueNode(node.right);
                if (tf) {
                    bw.write(node.name + " left the family, replaced by " + min.name);
                    bw.newLine();
                }
                node.name = min.name;
                node.key = min.key;
                // tf = false because we do not want any other line
                node.right = deleteNode(node.right, min.key, min.name, bw, false);
            }
        }

        if (node == null) return null;

        updateHeight(node);

        // keeping the Tree balanced
        int balance = getBalanceFactor(node);

        if (balance > 1) {
            if (getBalanceFactor(node.left) >= 0) return rightRotate(node);
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1) {
            if (getBalanceFactor(node.right) <= 0) return leftRotate(node);
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    // Node that have lowest GMS among the candidates that has more GMS than the leaving member
    Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    // finding the lowest common superior
    // first function is handling string -> node operation for main function
    Node findLowestCommonSuperior(String name1, String name2) {
        Node node1 = findNode(name1);
        Node node2 = findNode(name2);
        if (node1 == null || node2 == null)
            return null;
        return findLowestCommonSuperior(root, node1, node2);
    }

    Node findLowestCommonSuperior(Node node, Node node1, Node node2) {
        if (node == null) return null;

        if (node1.key < node.key && node2.key < node.key){
            Node leftSuperior = findLowestCommonSuperior(node.left, node1, node2);
            if (leftSuperior == null)
                return node;
            return leftSuperior;
        }
        else if (node1.key > node.key && node2.key > node.key)
            return findLowestCommonSuperior(node.right, node1, node2);
        else
            return node;
    }

    // finding Node from its name with using HashMap
    Node findNode(String name) {
        return nodeLookup.get(name);
    }

    int getBalanceFactor(Node node) {
        return (node != null) ? height(node.left) - height(node.right) : 0;
    }

    // finding max independent members
    // using isCached is to avoid redundant calculations
    int findMaxIndependentMembers(Node node) {
        if (node == null) return 0;

        if (node.left == null && node.right == null) {
            node.isCached = true;
            node.maxIndependentMembers = 1;
            return 1;
        }

        if (node.isCached)
            return node.maxIndependentMembers;

        int includeNodeCount = 1; // Include the current node and count its children's children
        int excludeNodeCount = 0; // Do not include the current node and count its children
        excludeNodeCount += findMaxIndependentMembers(node.left) + findMaxIndependentMembers(node.right);

        if (node.left != null)
            includeNodeCount += findMaxIndependentMembers(node.left.left) + findMaxIndependentMembers(node.left.right);

        if (node.right != null)
            includeNodeCount += findMaxIndependentMembers(node.right.left) + findMaxIndependentMembers(node.right.right);

        node.maxIndependentMembers = Math.max(includeNodeCount, excludeNodeCount);
        node.isCached = true;
        return node.maxIndependentMembers;
    }

    // resetting cache before every "INTEL_DIVIDE"
    // because some operations done after an "INTEL_DIVIDE" might cause error
    void resetCacheForAllNodes(Node node) {
        if (node != null) {
            node.maxIndependentMembers = 0;
            node.isCached = false;
            resetCacheForAllNodes(node.left);
            resetCacheForAllNodes(node.right);
        }
    }

    // updating ranks before every "INTEL_RANK"
    // because some operations done after an "INTEL_RANK" might cause error
    void updateRankValues(Node node, int currentRank) {
        if (node == null) return;
        node.rank = currentRank;
        updateRankValues(node.left, currentRank + 1);
        updateRankValues(node.right, currentRank + 1);
    }

    // findSameRank for main function
    ArrayList<Node> findSameRank(String name) {
        updateRankValues(root, 0);
        Node referenceNode = findNode(name);
        ArrayList<Node> result = new ArrayList<>();
        findNodesWithSameRank(root, referenceNode, 0, result);
        return result;
    }

    // sorting is handled by using the inOrderTraversal method
    void findNodesWithSameRank(Node node, Node referenceNode, int currentRank, ArrayList<Node> result) {
        if (node == null) return;
        findNodesWithSameRank(node.left, referenceNode, currentRank + 1, result);
        if (currentRank == referenceNode.rank)
            result.add(node);
        findNodesWithSameRank(node.right, referenceNode, currentRank + 1, result);
    }
}