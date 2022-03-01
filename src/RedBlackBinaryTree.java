import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Implementation of a Red-Black Binary Tree
 *
 * @author August Johnson Palm
 */


public class RedBlackBinaryTree<T extends Comparable<? super T>> implements Iterable<T> {


    /**
     * Inner Enum representing the red and black color of the nodes
     */

    protected enum Color {
        RED, BLACK
    }

    /**
     * Inner class representing the nodes in the tree
     */

    protected static class Node<T> {

        T data;
        Color color;

        Node<T> parent; //parent node
        Node<T> left;  //left child node
        Node<T> right; //right child node

        Node<T> nextSmallest;
        Node<T> nextLargest;


        //Constructors

        Node(T data, Node<T> left, Node<T> right) {
            this.data = data;
            this.left = left;
            this.right = right;
            color = Color.RED;
        }


        Node() {
            left = right = parent =
                    nextLargest = nextSmallest = this;
            color = Color.BLACK;
        }


        // Shifts the color of to node from red to black or vice versa
        void recolor() {
            color = (color == Color.RED) ? Color.BLACK : Color.RED;
        }

        void copy(Node<T> other) {
            data = other.data;
            nextSmallest = other.nextSmallest;
            nextLargest = other.nextLargest;
        }

        boolean isLeftChild() {
            return parent != null && this == parent.left;
        }

        boolean isRightChild() {
            return parent != null && !isLeftChild();
        }

        boolean isRoot() {
            return parent == null;
        }

        boolean isBlack() {
            return color == Color.BLACK;
        }

        boolean isRed() {
            return color == Color.RED;
        }

        Node<T> grandparent() {
            return parent != null ? parent.parent : null;
        }

        Node<T> uncle() {
            Node<T> grandparent = grandparent();
            if (grandparent == null)
                return null;
            return parent == grandparent.left ? grandparent.right : grandparent.left;
        }

        Node<T> sibling() {
            if (parent == null)
                return null;
            return this == parent.left ? parent.right : parent.left;
        }

        public String toString() {
            return "(" +
                    data + ": " + color + ", " +
                    "Parent: " + (parent == null ? "null" : parent.data) + ", " +
                    "Left: " + (left.data == null ? "nil" : left.data) + ", " +
                    "Right: " + (right.data == null ? "nil" : right.data) + ")";

        }
    }


    private final Node<T> nil = new Node<>(); // deafault black node that every leaf has a pointer to
    private Node<T> root;

    private int size = 0;
    private int modCount = 0;


    public boolean add(T data) {
        int originalSize = size();
        if (insert(data)) {
            size++;
            modCount++;
        }
        return size() > originalSize;
    }

    public boolean remove(T data) {
        int originalSize = size();
        if (delete(data)) {
            size--;
            modCount++;
        }
        return size() < originalSize;
    }


    public boolean isEmpty() {
        return size == 0 && root == null;
    }

    public int size() {
        return size;
    }

    public T first() {
        return findMin(root);
    }

    public T last() {
        return findMax(root);
    }

    public void clear() {
        root = null;
        size = 0;
        modCount++;
    }

    public boolean contains(T data) {
        return findNode(data) != nil;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isEmpty())
            return "[]";
        builder.append("[");
        buildString(builder, root);
        builder.append("]");
        return builder.toString();
    }

    public Iterator<T> iterator() {
        return new RedBlackTreeIterator();
    }

    public Iterator<T> descendingIterator() {
        return new RedBlackTreeIterator(true);
    }


    private boolean insert(T data) {
        if (data == null)
            return false;

        Node<T> node = new Node<>(data, nil, nil);

        // if the tree is empty we make the new node the root
        if (!isEmpty()) {
            Node<T> current = root;
            Node<T> parent = nil;
            //traverse the tree down to the last node or return false if we found node with same value
            while (current != nil) {
                parent = current;
                if (node.data.compareTo(current.data) == 0)
                    return false;
                current = (node.data.compareTo(current.data) < 0) ? current.left : current.right;
            }
            if (node.data.compareTo(parent.data) < 0)
                parent.left = node;
            else
                parent.right = node;
            node.parent = parent;
        } else
            root = node;
        insertUpdateNextNodes(node);
        balanceInsertion(node);
        return true;
    }


    private void insertUpdateNextNodes(Node<T> node) {
        if (node.isRoot()) {
            node.nextLargest = node.nextSmallest = nil;

        } else if (node.isLeftChild()) {
            node.nextLargest = node.parent;
            node.nextSmallest = node.parent.nextSmallest;
            node.parent.nextSmallest.nextLargest = node;
            node.parent.nextSmallest = node;

        } else {
            node.nextSmallest = node.parent;
            node.nextLargest = node.parent.nextLargest;
            node.parent.nextLargest.nextSmallest = node;
            node.parent.nextLargest = node;
        }
    }



    private void balanceInsertion(Node<T> node) {

        while (!node.isRoot() && node.isRed() && node.parent.isRed()) {
            Node<T> grandParent = node.grandparent();
            Node<T> uncle = node.uncle();

            // if the both the parent and uncle is red we balance the tree by recoloring
            if (uncle.isRed()) {
                insertCase1(node);
                //check if we need further recoloring by checking balanceInsertion at the grandfather which is now red
                if (!grandParent.isRoot())
                    node = grandParent;
            } else // the uncle is black and rotations and recoloring is needed
                insertCase2(node);
        }
        // making sure that the root stays black
        root.color = Color.BLACK;
    }

    private void insertCase1(Node<T> node) {

        Node<T> grandParent = node.grandparent();
        Node<T> uncle = node.uncle();

        node.parent.recolor();
        uncle.recolor();
        grandParent.recolor();
    }

    private void insertCase2(Node<T> node) {
        Node<T> grandParent = node.grandparent();

        if (node.parent.isLeftChild()) {
            // if the node is a right child when parent is a left child we have to double rotate (LR)
            // otherwise we just single rotate right (RR)
            if (node.isRightChild()) {
                leftRotate(node.parent); // left rotation with parent
                node.recolor();
            } else
                node.parent.recolor();

            // right rotation and recoloring with grandfather
            rightRotate(grandParent);
            grandParent.recolor();

        } else if (node.parent.isRightChild()) {
            // if the node is a left child when parent is a right child we have to double rotate (RL)
            // otherwise we just single rotate (LL)
            if (node.isLeftChild()) {
                rightRotate(node.parent); // right rotation with parent
                node.recolor();
            } else
                node.parent.recolor();

            // left rotation and recoloring with grandfather
            leftRotate(grandParent);
            grandParent.recolor();
        }

    }


    //TODO: write better how it works
    private boolean delete(T data) {
        if (data == null || isEmpty())
            return false;

        // find node to be deleted
        Node<T> node = findNode(data);

        if (node != nil) {
            deleteUpdateNextNodes(node); // update nextSmallest and nextLargest link
            Node<T> toDelete = node;
            if (node.left != nil && node.right != nil) { // node to be removed has two children

                // traverse the to find the node to be cut of the tree aswell as copy data upwards
                while (node.left != nil) {
                    toDelete = findMaxNode(node.left);
                    node.copy(toDelete);
                    node = toDelete;
                }

            } else { // node has at least one child
                if (node.left != nil)
                    toDelete = node.left;
                else if (node.right != nil)
                    toDelete = node.right;

                node.data = toDelete.data;
            }
            deleteCase1(toDelete); // balance the tree if needed

            //finally delete the node
            if (toDelete.isRoot())
                root = null;
            else {  //node is guaranteed to be a leaf at this point)
                cutOf(toDelete);
                //make sure the root stays black
                root.color = Color.BLACK;
            }
        }
        return node != nil;


    }

    private void deleteUpdateNextNodes(Node<T> node) {
        node.nextSmallest.nextLargest = node.nextLargest;
        node.nextLargest.nextSmallest = node.nextSmallest;
    }

    private void deleteCase1(Node<T> node) {
        if (node.isBlack())
            deleteCase2(node);
    }

    private void deleteCase2(Node<T> node) {
        if (!node.isRoot())
            deleteCase3(node);

    }

    private void deleteCase3(Node<T> node) {
        Node<T> sibling = node.sibling();
        if (sibling != null) {
            if (sibling.isBlack() && sibling.left.isBlack()
                    && sibling.right.isBlack()) {
                sibling.recolor(); //make sibling red
                if (node.parent.isRed())
                    node.parent.recolor(); // make parent black
                else
                    deleteCase2(node.parent); // parent was already black and is now "double black"
            } else
                deleteCase4(node);
        }

    }


    private void deleteCase4(Node<T> node) {
        Node<T> sibling = node.sibling();
        if (sibling != null) {
            if (sibling.isRed()) {

                swapColors(node.parent, sibling); // sibling becomes black and parent becomes red

                if (node.isLeftChild())
                    leftRotate(node.parent);
                else if (node.isRightChild())
                    rightRotate(node.parent);
                deleteCase1(node); //check if further balancing needs to be done
            } else
                deleteCase5(node);

        }
    }

    private void deleteCase5(Node<T> node) {
        Node<T> sibling = node.sibling();
        if (sibling != null) {
            // sibling is guaranteed to be black
            if (sibling.isLeftChild() && sibling.left.isBlack() && sibling.right.isRed()) {

                sibling.right.recolor();
                sibling.recolor();
                leftRotate(sibling);

            } else if (sibling.isRightChild() && sibling.right.isBlack() && sibling.left.isRed()) {

                sibling.left.recolor();
                sibling.recolor();
                rightRotate(sibling);
            }
            deleteCase6(node);
        }

    }

    private void deleteCase6(Node<T> node) {
        Node<T> sibling = node.sibling();
        // sibling is guaranteed to be black and the far child is red
        if (sibling != null) {
            //swap colors of sibling and parent
            swapColors(node.parent, sibling);

            if (node.isLeftChild()) {
                leftRotate(node.parent);
                sibling.right.recolor(); // make far child black
            } else if (node.isRightChild()) {
                rightRotate(node.parent);
                sibling.left.recolor(); // make far child black
            }
        }
    }

    private void cutOf(Node<T> node) {
        if (node.isLeftChild())
            node.parent.left = nil;
        else
            node.parent.right = nil;
    }

    private Node<T> findNode(T data) {
        Node<T> node = root;
        while (node != nil && data.compareTo(node.data) != 0)
            node = (data.compareTo(node.data) < 0) ? node.left : node.right;

        return node;
    }

    private T findMin(Node<T> node) {
        return findMinNode(node).data;
    }

    private Node<T> findMinNode(Node<T> node) {
        Node<T> current = node;

        //traverse left tree until there is no left node
        while (current.left != nil)
            current = current.left;

        return current;
    }

    private T findMax(Node<T> node) {
        return findMaxNode(node).data;
    }

    private Node<T> findMaxNode(Node<T> node) {
        Node<T> current = node;

        //traverse left tree until there is no left node
        while (current.right != nil)
            current = current.right;

        return current;
    }

    private void swapColors(Node<T> x, Node<T> y) {
        Color temp = x.color;
        x.color = y.color;
        y.color = temp;
    }

    /**
     * Creates string of nodes by inorder traversal
     */
    private void buildString(StringBuilder builder, Node<T> root) {

        if (root.left != nil) {
            buildString(builder, root.left);
            builder.append(", ");
        }
        builder.append(root.toString());

        if (root.right != nil) {
            builder.append(", ");
            buildString(builder, root.right);
        }
    }

    private void rightRotate(Node<T> oldRoot) {
        Node<T> newRoot = oldRoot.left;

        oldRoot.left = newRoot.right;

        // make the right child of the old root a left child of the new root
        if (oldRoot.left != nil)
            oldRoot.left.parent = oldRoot;

        // make the left child (newRoot) the new root in the subtree by making the old root a child of the new root
        // and setting its parent to the old roots parent
        newRoot.right = oldRoot;
        newRoot.parent = oldRoot.parent;

        // update so that old roots parent points to the new root
        updateOldRootParentChildNodes(oldRoot, newRoot);

        // finally set the parent of the old root to the new root
        oldRoot.parent = newRoot;

    }

    private void leftRotate(Node<T> oldRoot) {
        Node<T> newRoot = oldRoot.right;

        // make the right child of the old root a left child of the new root
        oldRoot.right = newRoot.left;
        if (oldRoot.right != nil)
            oldRoot.right.parent = oldRoot;

        // make the right child (newRoot) the new root in the subtree by making the old root a child of the new root
        // and setting its parent to the old roots parent
        newRoot.left = oldRoot;
        newRoot.parent = oldRoot.parent;

        // update so that the old roots parent points to the new root
        updateOldRootParentChildNodes(oldRoot, newRoot);

        // finally set the parent of the old root to the new root
        oldRoot.parent = newRoot;
    }


    private void updateOldRootParentChildNodes(Node<T> oldRoot, Node<T> newRoot) {

        // if the root parent is null we make the newRoot the root of the whole tree
        if (oldRoot.isRoot())
            root = newRoot;

            //if the root is a left child the new root will be a left child
        else if (oldRoot.isLeftChild()) {
            oldRoot.parent.left = newRoot;
        } else //the root is a right child and the new root will be a right child
            oldRoot.parent.right = newRoot;
    }


    private class RedBlackTreeIterator implements Iterator<T> {

        private final boolean descending;

        private Node<T> current;

        private int expectedModCount = modCount;
        private boolean okToRemove;

        RedBlackTreeIterator() {
            this(false);
        }

        RedBlackTreeIterator(boolean descending) {
            this.descending = descending;
            if (isEmpty())
                current = nil;
            else
                current = descending ? findMaxNode(root) : findMinNode(root);

        }

        @Override
        public boolean hasNext() {
            return current != nil;
        }

        @Override
        public T next() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (!hasNext())
                throw new NoSuchElementException();
            T data = current.data;
            current = descending ? current.nextSmallest : current.nextLargest;
            okToRemove = true;

            return data;
        }

        @Override
        public void remove() {
            if (expectedModCount != modCount)
                throw new ConcurrentModificationException();
            if (!okToRemove)
                throw new IllegalStateException();

            RedBlackBinaryTree.this.remove(descending ? current.nextLargest.data : current.nextSmallest.data);

            expectedModCount++;
            okToRemove = false;
        }
    }

    //only for JUNIT-tests
    protected Node<T> root() {
        return root;
    }
}
