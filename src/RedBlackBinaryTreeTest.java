import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.*;


/**
 * @author August Johnson Palm
 * @version JUnit 5
 */


//TODO: add more complicated scenarios of insertion and deletion

public class RedBlackBinaryTreeTest {

    private static final RedBlackBinaryTree.Color RED = RedBlackBinaryTree.Color.RED;
    private static final RedBlackBinaryTree.Color BLACK = RedBlackBinaryTree.Color.BLACK;

    private final RedBlackBinaryTree<Integer> tree = new RedBlackBinaryTree<>();


    @Test
    public void testEmptyTree() {
        tree.clear();
        assertEquals(0, tree.size());
        //assertEquals(-1, tree.depth());
        assertEquals("[]", tree.toString());
    }

    @BeforeEach
    void setUp() {
        assertTrue(tree.add(5));
        assertTrue(tree.add(4));
        assertTrue(tree.add(2));
        assertTrue(tree.add(3));
        assertTrue(tree.add(6));
        assertTrue(tree.add(1));
        verifyRedBlackRules(tree);
    }

    @AfterEach
    void verifyDefaultTree() {
        verifyRedBlackRules(tree);
    }

    void verifyRedBlackRules(RedBlackBinaryTree<Integer> tree) {
        verifyRootIsBlack(tree);
        assertDoesNotThrow(() -> verifyNodesAreEitherRedOrBlack(tree.root()));
        assertDoesNotThrow(() -> verifyRedNodesHaveOnlyBlackChildren(tree.root()));
        verifySameNumberOfBlackNodesDownToMinAndMaxNodes(tree);
        assertDoesNotThrow(() -> verifySameNumberOfBlackNodesDownToAllNilNodes(tree.root(),
                numberOfBlackNodesToMaxNodeFromAnyNode(tree.root()), 0));
    }

    void verifyRootIsBlack(RedBlackBinaryTree<Integer> tree) {
        if (tree.root() != null)
            assertEquals(BLACK, tree.root().color);
    }


    void verifyNodesAreEitherRedOrBlack(RedBlackBinaryTree.Node<Integer> root) throws IllegalStateException {
        if (root != null && root.data != null) {
            if (root.color != RedBlackBinaryTree.Color.BLACK && root.color != RedBlackBinaryTree.Color.RED)
                throw new IllegalStateException("Wrong colors in tree");
            verifyNodesAreEitherRedOrBlack(root.left);
            verifyNodesAreEitherRedOrBlack(root.right);
        }
    }

    void verifyRedNodesHaveOnlyBlackChildren(RedBlackBinaryTree.Node<Integer> root) throws IllegalStateException {
        if (root != null && root.data != null) {
            if (root.parent != null && root.color == RED && root.parent.color == RED)
                throw new IllegalStateException("Adjecent red nodes");
            if (root.color == RED && (root.left.color == RED || root.right.color == RED))
                throw new IllegalStateException("Adjecent red nodes");

            verifyRedNodesHaveOnlyBlackChildren(root.left);
            verifyRedNodesHaveOnlyBlackChildren(root.right);
        }
    }

    void verifySameNumberOfBlackNodesDownToMinAndMaxNodes(RedBlackBinaryTree<Integer> tree) {
        assertEquals(numberOfBlackNodesToMaxNodeFromAnyNode(tree.root()), numberOfBlackNodesToMinNodeFromAnyNode(tree.root()));
    }

    void verifySameNumberOfBlackNodesDownToAllNilNodes(RedBlackBinaryTree.Node<Integer> root, int expected, int count) throws IllegalStateException {
        if (root != null && root.data != null) {
            count += root.color == BLACK ? 1 : 0;
            verifySameNumberOfBlackNodesDownToAllNilNodes(root.left, expected, count);
            verifySameNumberOfBlackNodesDownToAllNilNodes(root.right, expected, count);
        } else {
            if (expected != count)
                throw new IllegalStateException("Wrong number of black nodes down to nil");
        }
    }

    int numberOfBlackNodesToMinNodeFromAnyNode(RedBlackBinaryTree.Node<Integer> root) {
        int count = 0;
        while (root != null && root.data != null) {
            if (root.color == BLACK) {
                count++;
            }
            root = root.left;
        }
        return count;

    }

    int numberOfBlackNodesToMaxNodeFromAnyNode(RedBlackBinaryTree.Node<Integer> root) {
        int count = 0;
        while (root != null && root.data != null) {
            if (root.color == BLACK) {
                count++;
            }
            root = root.right;
        }
        return count;

    }

    @Test
    void testAddUnique() {
        for (int n = 1; n <= 6; n++) {
            assertTrue(tree.contains(n));
        }
    }

    @Test
    void testSize() {
        assertEquals(6, tree.size());
    }


    @Test
    void testToString() {
        assertEquals("[(1: RED, Parent: 2, Left: nil, Right: nil), " +
                "(2: BLACK, Parent: 4, Left: 1, Right: 3), " +
                "(3: RED, Parent: 2, Left: nil, Right: nil), " +
                "(4: BLACK, Parent: null, Left: 2, Right: 5), " +
                "(5: BLACK, Parent: 4, Left: nil, Right: 6), " +
                "(6: RED, Parent: 5, Left: nil, Right: nil)]", tree.toString());
    }


    @Test
    void testAddDuplicates() {
        for (int n = 1; n <= 6; n += 2)
            assertFalse(tree.add(n));
    }

    @Test
    void testRemoveExistingLeaf() {
        assertTrue(tree.remove(1));
        assertEquals(5, tree.size());
        assertEquals("[(2: BLACK, Parent: 4, Left: nil, Right: 3), " +
                "(3: RED, Parent: 2, Left: nil, Right: nil)" +
                ", (4: BLACK, Parent: null, Left: 2, Right: 5), " +
                "(5: BLACK, Parent: 4, Left: nil, Right: 6), " +
                "(6: RED, Parent: 5, Left: nil, Right: nil)]", tree.toString());
    }

    @Test
        //testar att ta bort roten
    void testRemoveRoot() {
        assertTrue(tree.remove(4));
        assertEquals(5, tree.size());
        assertEquals(3, tree.root().data);
        assertEquals("[(1: RED, Parent: 2, Left: nil, Right: nil), " +
                "(2: BLACK, Parent: 3, Left: 1, Right: nil), " +
                "(3: BLACK, Parent: null, Left: 2, Right: 5), " +
                "(5: BLACK, Parent: 3, Left: nil, Right: 6), " +
                "(6: RED, Parent: 5, Left: nil, Right: nil)]", tree.toString());
    }

    @Test
        //testar att ta bort nod med två barn
    void testRemoveExistingMiddleItemWithTwoChildren() {
        assertTrue(tree.add(7));
        assertTrue(tree.remove(6));
        assertEquals(6, tree.size());
        assertEquals("[(1: RED, Parent: 2, Left: nil, Right: nil), " +
                "(2: BLACK, Parent: 4, Left: 1, Right: 3), " +
                "(3: RED, Parent: 2, Left: nil, Right: nil), " +
                "(4: BLACK, Parent: null, Left: 2, Right: 5), " +
                "(5: BLACK, Parent: 4, Left: nil, Right: 7), " +
                "(7: RED, Parent: 5, Left: nil, Right: nil)]", tree.toString());
    }

    @Test
        //testar att ta bort ExistingMiddleItemWithEmptyLeftChild
    void testRemoveExistingMiddleItemWithEmptyLeftChild() {
        assertTrue(tree.remove(5));
        assertEquals(5, tree.size());
        assertEquals("[(1: RED, Parent: 2, Left: nil, Right: nil), " +
                "(2: BLACK, Parent: 4, Left: 1, Right: 3), " +
                "(3: RED, Parent: 2, Left: nil, Right: nil), " +
                "(4: BLACK, Parent: null, Left: 2, Right: 6), " +
                "(6: BLACK, Parent: 4, Left: nil, Right: nil)]", tree.toString());
    }

    @Test
        //testar att ta bort ExistingMiddleItemWithEmptyLeftChild
    void testRemoveExistingMiddleItemWithEmptyRightChild() {
        assertTrue(tree.add(8));
        assertTrue(tree.add(7));
        assertTrue(tree.remove(8));
        assertEquals(7, tree.size());
        assertEquals("[(1: RED, Parent: 2, Left: nil, Right: nil), " +
                "(2: BLACK, Parent: 4, Left: 1, Right: 3), " +
                "(3: RED, Parent: 2, Left: nil, Right: nil), " +
                "(4: BLACK, Parent: null, Left: 2, Right: 6), " +
                "(5: BLACK, Parent: 6, Left: nil, Right: nil), " +
                "(6: RED, Parent: 4, Left: 5, Right: 7), " +
                "(7: BLACK, Parent: 6, Left: nil, Right: nil)]", tree.toString());
    }

    //funkar som binärt träd än så länge men balancering för delete måste fixas
    @Test
    void testRandomAddAndRemove() {
        Random rnd = new Random();

        SortedSet<Integer> oracle = new TreeSet<Integer>();
        for (int n = 1; n <= 6; n++)
            oracle.add(n);

        for (int n = 0; n < 1000; n++) {
            int toAdd = rnd.nextInt(10);
            assertEquals(oracle.add(toAdd), tree.add(toAdd));
            int toRemove = rnd.nextInt(10);
            assertEquals(oracle.remove(toRemove), tree.remove(toRemove));
            int checkExists = rnd.nextInt(10);
            assertEquals(oracle.contains(checkExists), tree.contains(checkExists));
            assertEquals(oracle.size(), tree.size());
            assertEquals(oracle.first(), tree.first());
            assertEquals(oracle.last(), tree.last());
            // assertEquals(oracle.toString(), tree.toString());
        }
    }

    @Test
    void testFirst() {
        assertEquals(1, tree.first());
        assertTrue(tree.remove(1));
        assertEquals(2, tree.first());
        assertTrue(tree.add(1));
        assertEquals(1, tree.first());


    }

    @Test
    void testLast() {
        assertEquals(6, tree.last());
        assertTrue(tree.remove(6));
        assertEquals(5, tree.last());
        assertTrue(tree.add(6));
        assertEquals(6, tree.last());
    }


    @Test
    void testOtherType() {
        RedBlackBinaryTree<String> stringTree = new RedBlackBinaryTree<>();
        stringTree.add("D");
        stringTree.add("A");
        stringTree.add("C");
        stringTree.add("A");
        stringTree.add("B");
        assertEquals(4, stringTree.size());
        assertTrue(stringTree.contains("C"));
        assertEquals("C", stringTree.root().data);
        stringTree.remove("C");
        assertFalse(stringTree.contains("C"));
        assertEquals("B", stringTree.root().data);
    }

    @Test
    public void testBasicIteration() {
        Iterator<Integer> i = tree.iterator();
        assertTrue(i.hasNext());
        assertEquals(1, i.next());
        assertTrue(i.hasNext());
        assertEquals(2, i.next());
        assertTrue(i.hasNext());
        assertEquals(3, i.next());
        assertTrue(i.hasNext());
        assertEquals(4, i.next());
        assertTrue(i.hasNext());
        assertEquals(5, i.next());
        assertTrue(i.hasNext());
        assertEquals(6, i.next());
        assertFalse(i.hasNext());
    }

    @Test
    public void testBasicDescendingIteration() {
        Iterator<Integer> i = tree.descendingIterator();
        assertTrue(i.hasNext());
        assertEquals(6, i.next());
        assertTrue(i.hasNext());
        assertEquals(5, i.next());
        assertTrue(i.hasNext());
        assertEquals(4, i.next());
        assertTrue(i.hasNext());
        assertEquals(3, i.next());
        assertTrue(i.hasNext());
        assertEquals(2, i.next());
        assertTrue(i.hasNext());
        assertEquals(1, i.next());
        assertFalse(i.hasNext());
    }

   @Test
    public void testToLongIteration() {
        Iterator<Integer> i = tree.iterator();
        for (int n = 0; n < tree.size(); n++) {
            i.next();
        }
        assertThrows(NoSuchElementException.class, () -> {
            i.next();
        });
    }

    @Test
    public void testIterationOnEmptyList() {
        tree.clear();
        Iterator<Integer> i = tree.iterator();
        assertFalse(i.hasNext());
        assertThrows(NoSuchElementException.class, () -> {
            i.next();
        });
    }

    @Test
    public void testMultipleConcurrentIterators() {
        Iterator<Integer> i1 = tree.iterator();
        assertTrue(i1.hasNext());
        assertEquals(1, i1.next());
        assertEquals(2, i1.next());
        Iterator<Integer> i2 = tree.iterator();
        assertTrue(i2.hasNext());
        assertEquals(1, i2.next());
        assertEquals(3, i1.next());
        assertEquals(2, i2.next());
        assertEquals(4, i1.next());
        assertEquals(3, i2.next());
        assertEquals(4, i2.next());
        assertEquals(5, i2.next());
        assertEquals(5, i1.next());
        assertEquals(6, i2.next());
        assertEquals(6, i1.next());
        assertFalse(i1.hasNext());
        assertFalse(i2.hasNext());
    }

    @Test
    public void testRemoveOnIterator() {
        Iterator<Integer> i = tree.iterator();
        assertEquals(1, i.next());
        i.remove();
        assertEquals(5, tree.size());
        assertEquals(2, tree.first());
        assertEquals(2, i.next());
        assertEquals(3, i.next());
        i.remove();
        assertEquals(4, tree.size());
        assertEquals(2, tree.first());
        //assertEquals(4, list.get(1));
        assertEquals(4, i.next());
        assertEquals(5, i.next());
        i.remove();
        System.out.println(tree);
        //System.out.println(list.toString());
        assertEquals(3, tree.size());
        assertEquals(2, tree.first());
        assertEquals(6, i.next());
        i.remove();
        assertEquals(2, tree.size());
        assertEquals(4, tree.last());
        //assertEquals("Fourth", list.get(1));
    }

    @Test
    public void testRemoveOnDescendingIterator() {
        Iterator<Integer> i = tree.descendingIterator();
        assertEquals(6, i.next());
        i.remove();
        assertEquals(5, tree.size());
        assertEquals(5, tree.last());
        assertEquals(5, i.next());
        assertEquals(4, i.next());
        i.remove();
        assertEquals(4, tree.size());
        assertEquals(5, tree.last());
        //assertEquals(4, list.get(1));
        assertEquals(3, i.next());
        assertEquals(2, i.next());
        i.remove();
        //System.out.println(tree.toString());
        assertEquals(3, tree.size());
        assertEquals(5, tree.last());
        assertEquals(1, i.next());
        i.remove();

        assertEquals(2, tree.size());
        assertEquals(3, tree.first());
        //assertEquals("Fourth", list.get(1));
    }

    @Test
    public void testRemoveOnIteratorWithoutNext() {
        Iterator<Integer> i = tree.iterator();
        assertThrows(IllegalStateException.class, () -> {
            i.remove();
        });
    }

    @Test
    public void testRemoveOnIteratorTwice() {
        Iterator<Integer> i = tree.iterator();
        i.next();
        i.remove();
        assertThrows(IllegalStateException.class, () -> {
            i.remove();
        });
    }

    @Test
    public void testRemoveAllElementsOnIterator() {
        Iterator<Integer> i = tree.iterator();
        while (i.hasNext()){
            i.next();
            i.remove();
        }
        assertThrows(IllegalStateException.class, () -> {
            i.remove();
        });
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());

    }

    @Test
    public void testRemoveAllElementsOnDescendingIterator() {
        Iterator<Integer> i = tree.descendingIterator();
        while (i.hasNext()){
            i.next();
            i.remove();
        }
        assertThrows(IllegalStateException.class, () -> {
            i.remove();
        });
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());

    }


}