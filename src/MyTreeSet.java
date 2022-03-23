import java.util.*;


/**
 * Implementation of TreeSet
 *
 * @author August Johnson Palm
 */


public class MyTreeSet<T extends Comparable<? super T>>{

    private final RedBlackBinaryTree<T> tree = new RedBlackBinaryTree<>();;




    public T lower(T t) {
        return tree.lower(t);
    }


    public T floor(T t) {
        return null;
    }


    public T ceiling(T t) {
        return null;
    }


    public T higher(T t) {
        return tree.higher(t);
    }


    public T pollFirst() {
        T result = tree.first();
        tree.remove(result);
        return result;
    }

    public T pollLast() {
        T result = tree.last();
        tree.remove(result);
        return result;
    }


    public int size() {
        return tree.size();
    }


    public boolean isEmpty() {
        return tree.isEmpty();
    }


    public boolean contains(Object o) {
        return tree.contains((T)o);
    }


    public Iterator<T> iterator() {
        return tree.iterator();
    }


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



    public boolean add(T t) {
        return tree.add(t);
    }




    public boolean remove(Object o) {
        return tree.remove((T)o);
    }



    public void clear() {
        tree.clear();
    }



    public Iterator<T> descendingIterator() {
        return tree.descendingIterator();
    }


    public T first() {
        return tree.first();
    }


    public T last() {
        return tree.last();
    }
}
