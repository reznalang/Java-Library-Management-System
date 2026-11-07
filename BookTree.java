// BINARY SEARCH TREE that stores the books, using the book ID as the key.
//
// Rule of a BST:  for every node,
//     everything in the LEFT  subtree has a SMALLER ID
//     everything in the RIGHT subtree has a BIGGER  ID
// Because of this rule, we can skip half of the tree at every step when searching.
public class BookTree {

    // One box in the tree: holds a book and links to its left and right child.
    // (Same idea as the Node class in the Linked List lesson, but with TWO links.)
    static class Node {
        Book book;
        Node left;
        Node right;

        Node(Book book) {
            this.book = book;
        }
    }

    Node root;      // the top of the tree
    int size = 0;   // how many books are in the tree

    // ---------- SEARCH ----------
    // Start at the root. If the ID is smaller, go left. If bigger, go right.
    Book search(int id) {
        Node current = root;
        while (current != null) {
            if (id == current.book.id) {
                return current.book;
            } else if (id < current.book.id) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;   // fell off the tree = not found
    }

    // ---------- INSERT ----------
    // Returns false if the ID already exists.
    boolean insert(Book book) {
        if (search(book.id) != null) {
            return false;
        }
        root = insertNode(root, book);
        size++;
        return true;
    }

    // Recursive helper: find the empty spot where the new book belongs.
    private Node insertNode(Node node, Book book) {
        if (node == null) {
            return new Node(book);          // empty spot found -> place it here
        }
        if (book.id < node.book.id) {
            node.left = insertNode(node.left, book);
        } else {
            node.right = insertNode(node.right, book);
        }
        return node;
    }

    // ---------- DELETE ----------
    // Call this only after you checked that the ID exists.
    void delete(int id) {
        root = deleteNode(root, id);
        size--;
    }

    private Node deleteNode(Node node, int id) {
        if (node == null) {
            return null;
        }
        if (id < node.book.id) {
            node.left = deleteNode(node.left, id);
        } else if (id > node.book.id) {
            node.right = deleteNode(node.right, id);
        } else {
            // Found the node to delete. There are 3 cases:

            // Case 1 and 2: no child, or only one child -> just replace it with that child
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            // Case 3: two children.
            // Find the SUCCESSOR = the smallest node in the right subtree.
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            // Copy the successor's book here, then delete the successor from the right side.
            node.book = successor.book;
            node.right = deleteNode(node.right, successor.book.id);
        }
        return node;
    }

    // ---------- IN-ORDER TRAVERSAL ----------
    // Visit LEFT, then the node, then RIGHT  ->  books come out sorted by ID.
    // We put them in an array so we can sort or display them.
    private Book[] list;
    private int fillIndex;

    Book[] toArray() {
        list = new Book[size];
        fillIndex = 0;
        inOrder(root);
        return list;
    }

    private void inOrder(Node node) {
        if (node == null) {
            return;
        }
        inOrder(node.left);
        list[fillIndex] = node.book;
        fillIndex++;
        inOrder(node.right);
    }
}
