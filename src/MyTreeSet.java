import java.util.*;


/**
 * Implementation of TreeSet
 *
 * @author August Johnson Palm
 */

//TODO: javadocs, implement relevant methods, clean up

public class MyTreeSet<T extends Comparable<? super T>> implements NavigableSet<T> {

    private final RedBlackBinaryTree<T> tree = new RedBlackBinaryTree<>();;



    @Override
    public T lower(T t) {
        return null;
    }

    @Override
    public T floor(T t) {
        return null;
    }

    @Override
    public T ceiling(T t) {
        return null;
    }

    @Override
    public T higher(T t) {
        return null;
    }

    @Override
    public T pollFirst() {
        T result = tree.first();
        tree.remove(result);
        return result;
    }

    @Override
    public T pollLast() {
        T result = tree.last();
        tree.remove(result);
        return result;
    }

    @Override
    public int size() {
        return tree.size();
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return tree.contains((T)o);
    }

    @Override
    public Iterator<T> iterator() {
        return tree.iterator();
    }

    @Override
    public Object[] toArray() {
        Iterator<T> i = tree.iterator();
        Object[] arr = new Object[tree.size()];
        int j = 0;
        while (i.hasNext()) {
            arr[j] = i;
            j++;
        }
        return arr;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean add(T t) {
        return tree.add(t);
    }



    @Override
    public boolean remove(Object o) {
        return tree.remove((T)o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        tree.clear();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return null;
    }

    @Override
    public Iterator<T> descendingIterator() {
        return tree.descendingIterator();
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        return null;
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        return null;
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        return null;
    }

    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return null;
    }

    @Override
    public T first() {
        return tree.first();
    }

    @Override
    public T last() {
        return tree.last();
    }
}
