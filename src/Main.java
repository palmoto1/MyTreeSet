import java.util.Iterator;
import java.util.TreeSet;

public class Main {


    public static void main(String[] args) {

        RedBlackBinaryTree<Integer> tree = new RedBlackBinaryTree<>();
        RedBlackBinaryTree<Integer> n = new RedBlackBinaryTree<>();
        tree.add(50);

        tree.add(40);

        tree.add(20);

        tree.add(30);

        tree.add(60);

        tree.add(10);

        System.out.println(tree.higher(20));

        TreeSet<Integer> oracle = new TreeSet<>();

        oracle.add(50);

        oracle.add(40);

        oracle.add(20);

        oracle.add(30);

        oracle.add(60);

        oracle.add(10);

        System.out.println(oracle.lower(20));

        int a = 8;
        int b = 5;
        int c = 10;

        int median;

        if (a < b) {
            if (b > c) median = c;


            //tree.remove(5);

            //tree.remove(7);

            //tree.remove(2);

            Iterator<Integer> it = tree.iterator();


            while (it.hasNext()) {
                System.out.print(it.next());
                it.remove();
            }

            System.out.println(tree.isEmpty());

            tree.add(6);

            tree.add(0);

            tree.add(4);

            tree.add(7);

            tree.add(2);

            tree.add(9);

            System.out.println();

            it = tree.iterator();

            while (it.hasNext()) {
                System.out.print(it.next());
                //it.remove();
            }


/*

        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());
        it.remove();
        System.out.println(it.next());
        it.remove();
*/

            //tree.remove(8);

        /*tree.add(50);

        tree.add(40);

        tree.add(20);

        tree.add(30);

        tree.add(60);

        tree.add(10);

        //tree.remove(5);

        tree.add(70);

        tree.add(90);

        //tree.remove(5);

        tree.add(80);

        tree.add(0);

        tree.add(100);

        tree.remove(0);

        tree.remove(50);



       /* tree.add(13);

        tree.add(11);

        tree.add(12);

       /* tree.add(3);
        tree.remove(7);
        tree.add(2);
        tree.remove(6);
        tree.add(2);
        tree.remove(4);
        tree.add(8);
        tree.remove(5);
        tree.add(6);
        tree.remove(2);
        tree.add(2);
        tree.remove(3);
        tree.add(11);
        tree.add(9);
        tree.remove(11);
        tree.remove(6);
        tree.remove(9);
        tree.remove(8);*/

        /*Random rnd = new Random();
        for (int n = 0; n < 600; n++) {
            int toAdd = rnd.nextInt(20);
            int toRemove = rnd.nextInt(20);
            tree.add(toAdd);
            tree.remove(toRemove);
        }*/


            //System.out.println(tree.toString());


        }
    }
}
