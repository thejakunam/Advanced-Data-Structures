/**
 * @Author  Shen Zhang          sxz162330
 *        Theja Shree Kunam   txk190012
 */

package txk190012;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

/**
 * Binary min-heap implementation of priority queue ADT
 * @param <T> generic type for heap elements
 */
public class BinaryHeap<T extends Comparable<? super T>> {
    Comparable[] pq;
    int size;

    /**
     * Constructor for building an empty priority queue using natural ordering of T
     * @param maxCapacity define the maximum elements the heap can hold
     */
    public BinaryHeap(int maxCapacity) {
        pq = new Comparable[maxCapacity];
        size = 0;
    }

    /** add method adds an element to the min-heap
     *
     * @param x the element that needs to be added
     * @return true if the element is added and false otherwise
     */
    public boolean add(T x) {
        if (size == pq.length) {
            this.resize();
            add(x);
        }
//        move(size, x);
        pq[size] = x;
        percolateUp(size);
        size++;
        return true;
    }

    /**
     * Offer is a helper method that further calls add method
     * This method adds an element to the min-heap
     * @param x the element that needs to be added
     * @return true if the element gets added and false otherwise
     */
    public boolean offer(T x) {
        return add(x);
    }

    /**
     * Removes and returns the minimum element in the heap
     * @return the element that is removed
     * @throws NoSuchElementException if there is no element to remove
     */
    public T remove() throws NoSuchElementException {
        T result = peek();
        if(result == null) {
            throw new NoSuchElementException("Priority queue is empty");
        } else {
//            move(0, pq[size - 1]);
            pq[0] = pq[size - 1];
//            pq[size - 1] = null;
            size--;
            percolateDown(0);
            return result;
        }
    }

    /**
     * helper method that further calls remove method
     * removes the minimum element from the heap
     * @return the element that is removed from the heap
     */
    public T poll() {
        if (this.isEmpty())
            return null;
        return remove();
    }

    /**
     * method to find the minimum element in the heap;
     * @return minimum element from the heap
     */
    public T min() {
        return peek();
    }

    /**
     * method to find the minimum element in the heap
     * @return minimum element without removing it in the heap and returns null if the heap is empty
     */
    public T peek() {
        if (this.isEmpty())
            return null;
        return (T) pq[0];
    }

    /**
     * method to find index of parent of the element at index i
     * @param i index of the element
     * @return index of parent
     */
    int parent(int i) {
        return (i-1)/2;
    }

    /**
     * method to find index of left child of elememt at index i
     * @param i index of element
     * @return index of left child of element at index i
     */
    int leftChild(int i) {
        return 2*i + 1;
    }

    /**
     * method to maintain heap order after adding any element
     * @param index index of element that may violate heap order with parent
     */
    void percolateUp(int index) {
        T x = (T) pq[index];
        while (index > 0 && pq[parent(index)].compareTo(x) > 0) {
//            swap(parent(index), index);
            move(index, pq[parent(index)]);
            index = parent(index);
        }
        move(index, x);
    }

    /**
     * method to swap elements at index1 and index2
     * @param index1 index of element 1
     * @param index2 index of element 2
     */
    private void swap(int index1, int index2) {
        Comparable temp = pq[index1];
        pq[index1] = pq[index2];
        pq[index2] = temp;
    }

    /**
     * method to maintain heap order property after removing any element
     * @param index index of element that may violate heap order with its children
     */
    void percolateDown(int index) {
        T x = (T) pq[index];
        int smallChildIndex = leftChild(index);
        while (smallChildIndex <= size - 1) {
            if (smallChildIndex < (size - 1) && pq[smallChildIndex].compareTo(pq[smallChildIndex+1]) > 0) {
                smallChildIndex += 1;
            }
            if (x.compareTo((T)pq[smallChildIndex]) <= 0) {
                break;
            }
            move(index, pq[smallChildIndex]);
            index = smallChildIndex;
            smallChildIndex = leftChild(index);
        }
        move(index, x);
//        while (leftChild(index) < size) {
//            int leftCh = leftChild(index);
//            if (leftCh + 1 < size) {
//                T smallerChild = pq[leftCh].compareTo(pq[leftCh + 1]) < 0 ? (T)pq[leftCh] : (T)pq[leftCh + 1];
//                int smallerIndex = pq[leftCh].compareTo(pq[leftCh + 1]) < 0 ? leftCh : leftCh + 1;
//                if (pq[index].compareTo(smallerChild) > 0) {
////                    swap(index, smallerIndex);
//                    move(index, smallerChild);
//                    index = smallerIndex;
//                } else {
//                    break;
//                }
//            } else { // no right child
//                if (pq[index].compareTo(pq[leftCh]) > 0) {
////                    swap(index, leftCh);
//                    move(index, leftCh);
//                    index = leftCh;
//                }
//                else break;
//            }
//        }

    }

    /**
     * use this whenever an element moved/stored in heap. Will be overridden by IndexedHeap
     * */
    void move(int dest, Comparable x) {
        pq[dest] = x;
    }

    int compare(Comparable a, Comparable b) {
        return ((T) a).compareTo((T) b);
    }

    /**
     * Create a heap.  Precondition: none.
     * */
    void buildHeap() {
        for(int i=parent(size-1); i>=0; i--) {
            percolateDown(i);
        }
    }

    /**
     * Method to check if heap is empty
     * @return true if heap is empty and false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * method to find size of heap
     * @return size of heap
     */
    public int size() {
        return size;
    }

    /**
     * Resize array to double the current size
     */
    void resize() {
        this.pq = Arrays.copyOf(pq, pq.length * 2);
    }

    public interface Index {
        public void putIndex(int index);
        public int getIndex();
    }

    public static class IndexedHeap<T extends Index & Comparable<? super T>> extends BinaryHeap<T> {
        /** Build a priority queue with a given array */
        IndexedHeap(int capacity) {
            super(capacity);
        }

        /** restore heap order property after the priority of x has decreased */
        void decreaseKey(T x) {
            // find the index of x, do percolateDown/Up(index)?
            percolateUp(x.getIndex());
//            move(x.getIndex(),x);       //not sure
        }

        @Override
        void move(int i, Comparable x) {
            super.move(i, x);
            ((T)x).putIndex(i);
        }
    }

    public static void main(String[] args) {
        Integer[] arr = {0,9,7,5,3,1,8,6,4,2};
        BinaryHeap<Integer> h = new BinaryHeap(5);

        System.out.print("BinaryHeap: Before:");
        for(Integer x: arr) {
            h.offer(x);
            System.out.print(" " + x + " ");
        }
        System.out.println();

        for(int i=0; i<arr.length; i++) {
            arr[i] = h.poll();
        }

        System.out.print("BinaryHeap: After :");
        for(Integer x: arr) {
            System.out.print(" " + x + " ");
        }
    }
}
