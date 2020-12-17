package txk190012;

import java.util.Arrays;
import java.util.Random;

public class RMQ {
    int[] inputArray;
    int blockSize;
    int[] blockMinima;
    int[][] sparseTable;


    /**
     * Constructor for RMQ
     *
     * @param size input array's size
     */
    public RMQ(int size) {
        inputArray = new int[size];
        blockSize = (int) Math.floor(Math.sqrt(size));
        blockMinima = new int[(int) Math.ceil(Math.sqrt(size))];
        int blockminima_size = blockMinima.length;
        sparseTable = new int[blockminima_size][(int) Math.ceil(Math.log(blockminima_size)) + 1];
    }

    /**
     * Method to compute each block's minima
     */
    public void getBlockMinima() {
        for (int i = 0, k = 0; i < inputArray.length; i += blockSize) {
            int minima = Integer.MAX_VALUE;
            for (int j = 0; j < blockSize; j++) {
                if ((i + j) == inputArray.length) break;
                minima = Math.min(inputArray[i + j], minima);
            }
            if(k<blockMinima.length){
                blockMinima[k] = minima;
                System.out.println("    index "+k+"     minima     "+ blockMinima[k]);
                k++;
            }
        }
    }

    /**
     * Method to construct the sparse table.
     * sparseTable[i][j] stores the minimum of the range [i, i+2^j−1] of length 2^j.
     */
    public void constructSparseTable(int li, int ri) {
        /*for (int i = 0; i < inArr.length; i++) {
            sparseTable[i][0] = inArr[i];
        }
        int k = (int) Math.ceil(Math.log(inArr.length));
        for (int j = 1; j <= k; j++) {
            for (int i = 0; i < inArr.length; i++) {
                if (i + (1 << j) <= inArr.length) {
                    sparseTable[i][j] = Math.min(sparseTable[i][j - 1], sparseTable[i + (1 << (j - 1))][j - 1]);
                    System.out.println("SparseTable "+i+"  "+j+ "  value  "+ sparseTable[i][j]);
                }
            }
        }*/

        int bl_si = li/blockSize;
        int bl_ei = (ri+1 - blockSize)/blockSize;

        //build sparse table for blockminimum arr with start index bl_si and end index bl_ei
    }

    public int findRMQ(int i, int j) {
        int res = Integer.MAX_VALUE;
        int li = 0,ri=0;
        int tempj = 0;
        if (j - i <= blockSize) {
            for (int k = i; k <= j; k++) {
                res = Math.min(res, inputArray[k]);
            }
            return res;
        } else {
            int leftElements;
            int rightElements;

            int leftMinima = Integer.MAX_VALUE;
            int rightMinima = Integer.MAX_VALUE;

            // has some elements on the left (less than b elements)
            if (i % blockSize != 0) {
                leftElements = blockSize - (i % blockSize);
                tempj += i+leftElements;
                li = tempj+1;
                for (int k = i; k <=i + leftElements; k++) {
                    leftMinima = Math.min(leftMinima, inputArray[k]);
                    //System.out.println("left loop k : "+k);
                }
            }

            // has some elements on the right
            if ((j+1) % blockSize != 0) {
                rightElements = (j+1) % blockSize;
                int n = (int) Math.floor(((j+1)/blockSize));
                //System.out.println("n :"+n);
                tempj = n*blockSize-1;
                ri = tempj-1;
                for (int k = tempj; k <= tempj + rightElements; k++) {
                    rightMinima = Math.min(rightMinima, inputArray[k]);
                    //System.out.println("right loop k : "+k);
                }

            }
            System.out.println("left min: "+leftMinima);
            System.out.println("right min: "+rightMinima);
            res = Math.min(leftMinima, rightMinima);
            System.out.println("Res: "+res);

            System.out.println("LI : "+li);
            System.out.println("RI: "+ri);
            constructSparseTable(li,ri);
            // get minimum from the sparse table and return min(this, res)

        }

        return res;
    }

    public static void main(String[] args) {
        int size = 20;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        }
        RMQ rmq = new RMQ(size);
        for (int i = 0; i < size; i++) {
            rmq.inputArray[i] = i;
        }

        Shuffle.shuffle(rmq.inputArray);
        System.out.println(Arrays.toString(rmq.inputArray));
        rmq.getBlockMinima();
        rmq.constructSparseTable(rmq.blockMinima);
        Timer timer = new Timer();
        int minResult = rmq.findRMQ(3,17);
        timer.end();
        System.out.println("The minimum among the chosen range is: " + minResult + "\n" + timer);

    }
    public static class Shuffle {
        public static Random random = new Random();
        public static void shuffle(int[] arr) {
            shuffle(arr, 0, arr.length - 1);
        }

        public <T> void shuffle(T[] arr) {
            shuffle(arr, 0, arr.length - 1);
        }

        public static void shuffle(int[] arr, int from, int to) {
            int n = to - from + 1;
            for (int i = 1; i < n; i++) {
                int j = random.nextInt(i);
                swap(arr, i + from, j + from);
            }
        }

        public <T> void shuffle(T[] arr, int from, int to) {
            int n = to - from + 1;
            Random random = new Random();
            for (int i = 1; i < n; i++) {
                int j = random.nextInt(i);
                swap(arr, i + from, j + from);
            }
        }

        static void swap(int[] arr, int x, int y) {
            int tmp = arr[x];
            arr[x] = arr[y];
            arr[y] = tmp;
        }

        <T> void swap(T[] arr, int x, int y) {
            T tmp = arr[x];
            arr[x] = arr[y];
            arr[y] = tmp;
        }

        public <T> void printArray(T[] arr, String message) {
            printArray(arr, 0, arr.length - 1, message);
        }

        public <T> void printArray(T[] arr, int from, int to, String message) {
            System.out.print(message);
            for (int i = from; i <= to; i++) {
                System.out.print(" " + arr[i]);
            }
            System.out.println();
        }
    }
    public static class Timer {
        long startTime, endTime, elapsedTime, memAvailable, memUsed;
        boolean ready;

        public Timer() {
            startTime = System.currentTimeMillis();
            ready = false;
        }

        public void start() {
            startTime = System.currentTimeMillis();
            ready = false;
        }

        public Timer end() {
            endTime = System.currentTimeMillis();
            elapsedTime = endTime-startTime;
            memAvailable = Runtime.getRuntime().totalMemory();
            memUsed = memAvailable - Runtime.getRuntime().freeMemory();
            ready = true;
            return this;
        }

        public long duration() { if(!ready) { end(); }  return elapsedTime; }

        public long memory()   { if(!ready) { end(); }  return memUsed; }

        public String toString() {
            if(!ready) { end(); }
            return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed/1048576) + " MB / " + (memAvailable/1048576) + " MB.";
        }

    }

}
