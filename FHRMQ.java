/**
 * Theja Shree Kunam   txk190012
 */

package txk190012;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class FHRMQ {
    static int[] inputArray;
    static int size;
    static int blockSize;
    static int partitionSize;
    static int blockMinima;
    static List<Integer> partitionResult;
    static int[] result;
    static int[][] sparseTable;
    static HashMap<Integer, int[][]> blockResult;
    static HashMap<String, int[][]> cartesian;


    FHRMQ(int size) {
        inputArray = new int[size];

        blockSize = (int) Math.ceil(Math.log((double)size)/(2*Math.log(4)));
        blockMinima = (int) Math.ceil(size/(double)blockSize);
        partitionSize = (int) Math.floor(Math.log((double)blockMinima)/Math.log(2));

        blockResult = new HashMap<Integer, int[][]>();
        cartesian = new HashMap<String, int[][]>();

        result = new int[size];
        partitionResult = new ArrayList<>(blockMinima);
        sparseTable = new int[partitionResult.size()][partitionSize+1];

    }

    private static void preprocess() {
        for(int i=0;i<size;i=i+blockSize) {
            int start = i;
            int end = (i+blockSize)<size?(i+blockSize):size;
            buildCartesian(i/blockSize,result, start, end);
            int temp1 = i/blockSize;
            if (start+blockSize-1 <= size) {
                partitionResult.add(result[(blockResult.get(i/blockSize)[0][blockSize-1] + temp1*blockSize)]);
            } else {
                partitionResult.add(result[(blockResult.get(i/blockSize)[0][size-start-1] + temp1*blockSize)]);
            }
        }

        for (int i=0;i<partitionResult.size();i++) {
            sparseTable[i][0] = partitionResult.get(i);
        }
        for (int j = 1; j <= partitionSize; j++) {
            for (int i = 0; i + (1 << j) <= partitionResult.size(); i++) {
                sparseTable[i][j] = Math.min(sparseTable[i][j-1], sparseTable[i + (1 << (j - 1))][j - 1]);
            }
        }
    }

    public static void buildCartesian(int bIndex, int[] block, int start, int end) {
        int i=0;
        StringBuilder sb = new StringBuilder();
        Stack<Integer> s = new Stack<Integer>();
        for (i=0;i<block.length;i++) {
            Integer newnode = block[i];
            while(s.size() > 0) {
                if (s.peek() > newnode) {
                    s.pop();
                    sb.append('0');
                    continue;
                }
                break;
            }
            s.push(newnode);
            sb.append('1');
        }
        while(sb.length()<2*blockSize) {
            sb.append('0');
        }

        if (cartesian.containsKey(sb.toString())) {
            blockResult.put(bIndex, cartesian.get(sb.toString()));
        } else {
            int[][] table = buildblockResulttable(block);
            blockResult.put(bIndex, table);
            cartesian.put(sb.toString(), table);
        }
    }

    public static int[][] buildblockResulttable(int[] block) {
        int[][] table = new int[blockSize][blockSize];
        for(int i=0;i<block.length;i++) {
            Integer minIndex = null;
            for(int j=i;j<block.length;j++) {
                if (j==i) {
                    table[i][j] = j;
                    minIndex = j;
                } else {
                    if (block[minIndex] > block[j]) {
                        minIndex = j;
                        table[i][j] = j;
                    } else {
                        table[i][j] = minIndex;
                    }
                }
            }
        }
        return table;
    }

    public static int query(Integer sI, Integer eI) {
        int i1 = (int)Math.floor(sI/(double)blockSize);
        int i2 = (int)Math.floor(eI/(double)blockSize);
        int ind1 = sI%blockSize;
        int ind2 = eI%blockSize;
        if (eI-sI < blockSize) {
            if (eI < blockSize*i1 + blockSize) {
                return result[blockResult.get(i1)[ind1][eI-sI+ind1]+blockSize*i1];
            } else {
                return Math.min(result[blockResult.get(i1)[ind1][blockSize-1]+blockSize*i1], result[blockResult.get(i2)[0][ind2]+blockSize*i2]);
            }
        }
        if (eI-sI < 2*blockSize) {
            return Math.min(result[blockResult.get(i1)[ind1][blockSize-1]+blockSize*i1], result[blockResult.get(i2)[0][ind2]+blockSize*i2]);
        }
        int i3 = min(i1+1,i2-1);
        int tmp =  Math.min(result[blockResult.get(i1)[ind1][blockSize-1]+blockSize*i1], result[blockResult.get(i2)[0][ind2]+blockSize*i2]);
        return Math.min(tmp, i3);
    }

    public static Integer min(Integer sI, Integer eI) {
        int j = (int) Math.floor(Math.log(eI - sI + 1)/Math.log(2));
        return Math.min(sparseTable[sI][j], sparseTable[eI - (1 << j) + 1][j]);
    }

    public static void main(String[] args) {
        int size = 100;

        FHRMQ fhrmq = new FHRMQ(size);
        for (int i = 0; i < size; i++) {
            inputArray[i] = i;
        }

        FHRMQ.Shuffle.shuffle(inputArray);

        Timer time = new Timer();
        preprocess();
        time.end();
        System.out.println("Preprocessing time: "+time);

        Timer querytime = new Timer();
        query( 20,50);
        querytime.end();
        System.out.println("Query time: " + querytime);
    }

    public static class Shuffle {
        public static Random random = new Random();
        public static void shuffle(int[] arr) {
            shuffle(arr, 0, arr.length - 1);
        }

        public static <T> void shuffle(T[] arr) {
            shuffle(arr, 0, arr.length - 1);
        }

        public static void shuffle(int[] arr, int from, int to) {
            int n = to - from + 1;
            for (int i = 1; i < n; i++) {
                int j = random.nextInt(i);
                swap(arr, i + from, j + from);
            }
        }

        public static <T> void shuffle(T[] arr, int from, int to) {
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

        static <T> void swap(T[] arr, int x, int y) {
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
            startTime = System.nanoTime();
            ready = false;
        }

        public void start() {
            startTime = System.nanoTime();
            ready = false;
        }

        public Timer end() {
            endTime = System.nanoTime();
            elapsedTime = endTime-startTime;
            memAvailable = Runtime.getRuntime().totalMemory();
            memUsed = memAvailable - Runtime.getRuntime().freeMemory();
            ready = true;
            return this;
        }

        public long duration() { if(!ready) { end(); }  return elapsedTime;}
        public long memory()   { if(!ready) { end(); }  return memUsed; }

        public String toString() {
            if(!ready) { end(); }
            return "Time: " + elapsedTime + " msec.\n" + "Memory: " + (memUsed/1048576) + " MB / " + (memAvailable/1048576) + " MB.";
        }

    }
}
