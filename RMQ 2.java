package txk190012;

import java.util.*;

public class RMQ {
    int[] inputArray;
    int blockSize;
    int[] blockMinima;
    int[][] sparseTable;
    int partitionSize;
    int size;

    HashMap<Integer, Integer[][]> blocklevelmap;
    HashMap<String, Integer[][]> cartesianMap;
    List<Integer> blockMinimaList;


    /**
     * Constructor for RMQ
     * @param size input array's size
     */
    public RMQ(int size) {
        this.size = size;
        inputArray = new int[size];
        blockSize = (int) (Math.floor(Math.log(size)) / Math.log(2));
        blockMinima = new int[(int) (Math.ceil(size / blockSize))];
        partitionSize = (int) Math.floor(Math.log((double)blockMinima.length)/Math.log(2));

        blocklevelmap = new HashMap<Integer, Integer[][]>();
        cartesianMap = new HashMap<String, Integer[][]>();
        blockMinimaList = new ArrayList<Integer>(blockMinima.length);
        sparseTable = new int[blockMinima.length][(int) Math.ceil(Math.log(blockMinima.length) / Math.log(2)) + 1];
    }

    /**
     * Method to compute each block's minima
     */
    public void getBlockMinima() {
        for(int i=0;i<this.size;i=i+blockSize) {
            int start = i;
            int end = 0;
            if( i + blockSize < size)
                end = i+blockSize;
            else
                end = size;

            getCartesian(i/blockSize,start,end,inputArray);
            int index = i/blockSize;
            if (start+blockSize-1 < size) {
                index = blocklevelmap.get(i/blockSize)[0][blockSize-1] + index*blockSize;
                blockMinimaList.add(inputArray[index]);
            } else {
                index = blocklevelmap.get(i/blockSize)[0][size-start-1] + index*blockSize;
                blockMinimaList.add(inputArray[index]);
            }
        }
    }

    private void getCartesian(int idx, int start, int end, int[] block) {
        int k=0;
        StringBuilder str = new StringBuilder();
        Stack<Integer> stack = new Stack<Integer>();
        for (k=0;k<block.length;k++) {
            int temp = block[k];
            while(stack.size() > 0) {
                if (stack.peek() > temp) {
                    stack.pop();
                    str.append('0');
                    continue;
                }
                break;
            }
            stack.push(temp);
            str.append('1');
        }
        while(str.length()<2*blockSize) {
            str.append('0');
        }

        if (cartesianMap.containsKey(str.toString())) {
            blocklevelmap.put(idx, cartesianMap.get(str.toString()));
        } else {
            Integer[][] table = new Integer[block.length+1][block.length+1];
            for(int i=0;i<block.length;i++) {
                Integer temp = null;
                for(int j=i;j<block.length;j++) {
                    if (j==i) {
                        table[i][j] = j;
                        temp = j;
                    } else {
                        if (block[temp] > block[j]) {
                            temp = j;
                            table[i][j] = j;
                        } else {
                            table[i][j] = temp;
                        }
                    }
                }
            }
            blocklevelmap.put(idx, table);
            cartesianMap.put(str.toString(), table);
        }
    }

    /**
     * Method to construct the sparse table upon only block minima.
     *
     */
    public void constructSparseTable() {
        for (int i = 0; i < blockMinima.length; i++) {
            sparseTable[i][0] = blockMinima[i];
        }
        int k = (int) Math.ceil(Math.log(blockMinima.length));
        for (int j = 1; j <= k; j++) {
            for (int i = 0; i < blockMinima.length; i++) {
                if (i + (1 << j) <= blockMinima.length) {
                    sparseTable[i][j] = Math.min(sparseTable[i][j - 1], sparseTable[i + (1 << (j - 1))][j - 1]);
                }
            }
        }
    }

    /**
     * Method to query for minimum value within range
     * @param i
     * @param j
     * @return minimum value
     */
    public int findRMQ(int i, int j) {
        int idx1 = (int)Math.floor(i/(double)blockSize);
        int idx2 = (int)Math.floor(j/(double)blockSize);
        int temp = i%blockSize;
        int temp2 = j%blockSize;

        if (j-i < blockSize) {
            if (j < blockSize * idx1 + blockSize) {
                temp = blocklevelmap.get(idx1)[temp][j - i + temp] + blockSize * idx1;
                return inputArray[temp];
            }
        }
        int result1 = minFromST(idx1+1,idx2-1);

        temp = blocklevelmap.get(idx1)[temp][blockSize - 1] + blockSize * idx1;
        temp2 = blocklevelmap.get(idx2)[0][temp2] + blockSize * idx2;
        
        int result2 =  Math.min(inputArray[temp], inputArray[temp2]);
        return Math.min(result1, result2);
    }

    /**
     * method to find minimum from the sparse table
     * @param i
     * @param j
     * @return
     */
    public int minFromST(int i, int j) {
        int k = (int)Math.floor(Math.log(j - i + 1) / Math.log(2));
        return Math.min(sparseTable[i][k], sparseTable[(int) (j - Math.pow(2, k) + 1)][k]);
    }


    public static void main(String[] args) {
        int size = 200;
        if (args.length > 0) {
            size = Integer.parseInt(args[0]);
        }
        RMQ rmq = new RMQ(size);
        for (int i = 0; i < size; i++) {
            rmq.inputArray[i] = i;
        }

        Shuffle.shuffle(rmq.inputArray);
       // System.out.println(Arrays.toString(rmq.inputArray));

        Timer preprocess_time = new Timer();
        rmq.getBlockMinima();
        rmq.constructSparseTable();
        preprocess_time.end();

        Timer query_time = new Timer();
        int minResult = rmq.findRMQ(0,199);
        query_time.end();
        System.out.println("The minimum among the chosen range is: " + minResult + "\n");
        System.out.println("Preprocess Time: "+ preprocess_time);
        System.out.println("Query Time: "+ query_time);

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
