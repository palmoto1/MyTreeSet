import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * My implementation of a Red-Black Binary Tree
 *
 * @author August Johnson Palm
 */


public class RedBlackBinaryTree<T extends Comparable<? super T>> implements Iterable<T> {


    /**
     * Inner Enum representing the red and black color of the nodes
     */

    enum Color {
        RED, BLACK
    }

    /**
     * Inner class representing the nodes in the tree
     */
    static class Node<T> {

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
            this.color = Color.RED;
        }


        Node() {
            left = right = parent =
                    nextLargest = nextSmallest = this;
            color = Color.BLACK;
        }


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

    /**
     * Inserts an element into the tree
     *
     * @param data the data to be inserted
     * @return false if the size was not changed and true if the size incremented proposing that the insertion was successful or not
     */

    public boolean add(T data) {
        int originalSize = size();

        if (insert(data)) {
            size++;
            modCount++;
        }
        return size() > originalSize;
    }

    /**
     * Deletes an element from the tree
     *
     * @param data the data to be removed
     * @return false if the size was not changed and true if the size decremented proposing that the removal was successful or not
     */

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


    private static final boolean LOWER = false;
    private static final boolean HIGHER = true;


    public T lower(T data) {
        Node<T> param = new Node<T>(data, nil, nil);
        Node<T> end = findMinNode(root);

        if (data.compareTo(end.data) <= 0)
            return null;

        return getLowerOrHigher(param, end, LOWER);
    }

    public T higher(T data) {
        Node<T> param = new Node<T>(data, nil, nil);
        Node<T> end = findMaxNode(root);

        if (data.compareTo(end.data) >= 0)
            return null;

        return getLowerOrHigher(param, end, HIGHER);
    }

    private T getLowerOrHigher(Node<T> param, Node<T> result, boolean higher) {
        Node<T> node = root;
        while (node != nil) {

            if (higher && greaterThan(node, param) && lessThan(node, result))
                result = node;
            else if (lessThan(node, param) && greaterThan(node, result))
                result = node;

            if (equals(param, node))
                node = !higher ? node.left : node.right;
            else
                node = lessThan(param, node) ? node.left : node.right;
        }
        return result.data;
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


    /**
     * Inserts a node into the tree by traversing to its potential parent node
     * Calls method for ensuring balance of the tree as well as updating links to reference nodes used by the iterator
     *
     * @param data the data to be inserted
     * @return false if the data is null or if it is already in the tree and true if insertion was successful
     */

    private boolean insert(T data) {
        if (data == null)
            return false;

        Node<T> node = new Node<>(data, nil, nil);
        if (isEmpty())
            root = node; // if the tree is empty we make the inserted node the root

        else {
            Node<T> current = root;
            Node<T> parent = nil;
            //traverse the tree down to the last node or return false if we found node with same value
            while (current != nil) {
                parent = current;

                if (equals(node, current))
                    return false;

                current = lessThan(node, current) ? current.left : current.right;
            }
            if (lessThan(node, parent))
                parent.left = node;
            else
                parent.right = node;

            node.parent = parent;
        }
        insertUpdateNextNodes(node);
        balanceInsertion1(node);

        return true;
    }

    /**
     * Updates references to the next smallest and next largest node after an insertion.
     *
     * @param node the newly inserted node
     */

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


    /**
     * Handling the situation after an insertion when a node is red as well as its parent.
     * If the nodes uncle also is red then it will recolor the nodes parent, uncle and grandparent and checking for
     * further imbalance by checking the grandparent.
     * If the uncle is black instead it will call to another method handling that case
     *
     * @param node the newly inserted node
     */

    private void balanceInsertion1(Node<T> node) {
        while (!node.isRoot() && node.isRed() && node.parent.isRed()) {
            Node<T> grandParent = node.grandparent();
            Node<T> uncle = node.uncle();

            // if the both the parent and uncle is red we balance the tree by recoloring
            if (uncle.isRed()) {
                balanceInsertion2(node);

                //check if we need further recoloring by checking balanceInsertion at the grandfather which is now red
                if (!grandParent.isRoot())
                    node = grandParent;
            }
            else // the uncle is black and rotations and recoloring is needed
                balanceInsertion3(node);
        }
        // making sure that the root stays black
        root.color = Color.BLACK;
    }

    /**
     * Recolors the nodes parent, uncle and grandparent and checking for
     * further imbalance by checking the grandparent.
     * The parent and uncle becomes black, the grandparent becomes red
     *
     * @param node the newly inserted node
     */

    private void balanceInsertion2(Node<T> node) {
        Node<T> grandParent = node.grandparent();
        Node<T> uncle = node.uncle();

        node.parent.recolor();
        uncle.recolor();
        grandParent.recolor();
    }

    /**
     * Handling the situation after an insertion when a node is red as well as its parent but the uncle is black.
     * Checks if the parent is a left or right child and as well as the newly inserted node.
     * Does single or double rotations depending on the relations described above. Double rotations
     * will be done by first rotate the parent node away from its child. Then the grandparent will rotate
     * away from the parent.
     * Recolors the inserted node if a double rotation was made, otherwise the parent is recolored instead,
     * making one of them black.
     * Grandparent is recolored in both situations, making it red.
     *
     * @param node the newly inserted node
     */

    private void balanceInsertion3(Node<T> node) {
        Node<T> grandParent = node.grandparent();

        if (node.parent.isLeftChild()) {
            // if the node is a right child when parent is a left child we have to double rotate (LR)
            // otherwise we just single rotate right (RR)
            if (node.isRightChild()) {
                leftRotate(node.parent); // left rotation with parent
                node.recolor();
            }
            else
                node.parent.recolor();

            // right rotation and recoloring with grandfather
            rightRotate(grandParent);

        } else {
            // if the node is a left child when parent is a right child we have to double rotate (RL)
            // otherwise we just single rotate (LL)
            if (node.isLeftChild()) {
                rightRotate(node.parent); // right rotation with parent
                node.recolor();
            }
            else
                node.parent.recolor();
            // left rotation and recoloring with grandfather
            leftRotate(grandParent);
        }
        grandParent.recolor();

    }


    /**
     * Deletes a node from the tree. Depending on the number of children the node handles the deletion differently.
     * If the node has two, non-nil children we copy the data from its maximum predecessor and then making
     * the same operation on the predecessor until the node to be deleted is a leaf node.
     * If the node has one child we simply copy the data from its only child due to the fact that the balance
     * of the tree ensures that the only child will be a leaf node
     * If the node has no child we can simply cut it of.
     * <p>
     * Calls on methods rebalancing the tree if the node to be deleted is black. It can be seen as carrying
     * an extra black color making it "double black". We need to get rid of this to ensure balance in the tree.
     *
     * @param data the data to be deleted
     * @return false if the data is null, if the tree was empty or if the data was not in the tree. Otherwise
     * true if the removal was successful.
     */

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

            balanceDeletion1(toDelete); // balance the tree if needed

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


    /**
     * Updates references to the next smallest and next largest node after an deletion.
     *
     * @param node the newly deleted node
     */
    private void deleteUpdateNextNodes(Node<T> node) {
        node.nextSmallest.nextLargest = node.nextLargest;
        node.nextLargest.nextSmallest = node.nextSmallest;
    }


    /**
     * Aborts if the deleted node is red or is the root since no balancing is needed other when the
     * deleted node is black.
     * Otherwise it handles the case when the nodes sibling is black as well as the siblings children
     * (including nil-nodes). Makes the sibling red and the parent black if it is red making the tree balanced.
     * If the parent was black the method is called recursively on the parent since now the parent is "double black"
     * and further rebalancing is needed.
     * If any of the sibling and its children were red another balancing method is called instead.
     *
     * @param node the "double black" node needing balance
     */

    private void balanceDeletion1(Node<T> node) {
        if (node.isRed() || node.isRoot())
            return;

        Node<T> sibling = node.sibling();
        if (sibling != null) {

            if (sibling.isBlack() && sibling.left.isBlack()
                    && sibling.right.isBlack()) {
                sibling.recolor(); //make sibling red

                if (node.parent.isRed())
                    node.parent.recolor(); // make parent black

                else
                    balanceDeletion1(node.parent); // parent was already black and is now "double black"
            }
            else
                balanceDeletion2(node);
        }

    }

    /**
     * Handles the case when the sibling is red. If it is red then the parent and sibling swap colors
     * making the sibling black and parent red. Then it rotates the nodes parent towards the "double black"
     * node.
     * Calls to the first case to check for further balancing.
     * If the sibling was not red another method is called.
     *
     * @param node the "double black" node needing balance
     */

    private void balanceDeletion2(Node<T> node) {
        Node<T> sibling = node.sibling();
        if (sibling != null) {

            if (sibling.isRed()) {

                swapColors(node.parent, sibling); // sibling becomes black and parent becomes red

                if (node.isLeftChild())
                    leftRotate(node.parent);

                else if (node.isRightChild())
                    rightRotate(node.parent);

                balanceDeletion1(node); //check if further balancing needs to be done
            }
            else
                balanceDeletion3(node);

        }
    }

    /**
     * Handles the case when the sibling is black, its child nearest the "double black" node is red and the
     * farthest child is black. Makes the near child of the sibling black and the sibling red.
     * Rotates the sibling away from the "double black" node and calls another method handling another case
     *
     * @param node the "double black" node needing balance
     */

    private void balanceDeletion3(Node<T> node) {
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
            balanceDeletion4(node);
        }

    }

    /**
     * Handles the case when the sibling is black, its child furthest the "double black" node is red and the
     * nearest child is black. Swaps the colors of the sibling and the parent and rotates the parent in the
     * "double black" nodes direction. The siblings red child becomes black. The tree is now rebalanced.
     *
     * @param node the "double black" node needing balance
     */

    private void balanceDeletion4(Node<T> node) {
        Node<T> sibling = node.sibling();
        // sibling is guaranteed to be black and the far child is red
        if (sibling != null) {
            //swap colors of sibling and parent
            swapColors(node.parent, sibling);

            if (node.isLeftChild()) {
                leftRotate(node.parent);
                sibling.right.recolor(); // make far child black
            }
            else if (node.isRightChild()) {
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

    private void swapColors(Node<T> nodeA, Node<T> nodeB) {
        Color temp = nodeA.color;
        nodeA.color = nodeB.color;
        nodeB.color = temp;
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

    /**
     * Handles rotation of a node by
     */

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
        }
        else //the root is a right child and the new root will be a right child
            oldRoot.parent.right = newRoot;
    }

    private boolean lessThan(Node<T> node, Node<T> other) {
        return node.data.compareTo(other.data) < 0;
    }

    private boolean greaterThan(Node<T> node, Node<T> other) {
        return node.data.compareTo(other.data) > 0;
    }

    private boolean equals(Node<T> node, Node<T> other) {
        return node.data.compareTo(other.data) == 0;
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
    Node<T> root() {
        return root;
    }
}
