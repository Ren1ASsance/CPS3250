//Purpose： Simulates the Memory class

public class Memory {
    private Frame[] memory;
    private int unusedFrameCount = 0;


    //Creates a memory with a specified number of unused frames.
    public Memory(int frameNum) {
        memory = new Frame[frameNum];
        for(int i = 0; i < frameNum; i++) {
            memory[i] = new Frame(i, i * OS.pageSize);
        }
        unusedFrameCount = frameNum;
    }

    /*
     * Placement policy: Preferably placed in low page frames.

     * Allocates (sets used to true) the first n unused frames and returns an array containing the frame numbers.
     * Returns null if there is not enough remaining memory.
     */
    public int[] mallocFrame(String id, int n) {
        if(unusedFrameCount < n) {
            return null;
        }

        int[] result = new int[n];
        int index = 0;
        for(int i = 0; index < n && i < memory.length; i++) {
            if(!memory[i].used) {
                result[index] = memory[i].frameNum;
                memory[i].setUsed(id);
                index++;
            }
        }
        unusedFrameCount -= n;
        System.out.println("Debug: Allocating frames for ID: " + id);
        for (int frameNum : result) {
            System.out.println("Debug: Allocated frame: " + frameNum + " to ID: " + id);
        }

        return result;
    }


    // Frees all frames with the specified frame numbers.

    public void freeFrame(int[] frames) {
        for(int i = 0; i < frames.length; i++) {
            memory[frames[i]].setUnused();
        }
        unusedFrameCount += frames.length;
    }


    //  return the number of the frame that unused
    public int unusedFrameCount() {
        return unusedFrameCount;
    }

    // Simulates reading a page from external storage.
// Reads the content of frameNum frame into memory, associated with the specified id, segmentNum, and pageNum.
    public void readPage(String id, int segmentNum, int pageNum, int frameNum) {
        System.out.println("IO: Loading process " + id + ", segment(" + segmentNum + "), page(" + pageNum + ") into frame " + frameNum);
    }

    /**
     * Simulates writing a page to external storage.
     * Writes the content of the frameNum frame to external storage, associated with the specified id, segmentNum, and pageNum.
     */
    public void writePage(String id, int segmentNum, int pageNum, int frameNum) {
        System.out.println("IO: Writing content of frame " + frameNum + " to external storage. Process " + id + ", segment(" + segmentNum + "), page(" + pageNum + ")");
    }

    //Returns the frame with the specified frame number. Returns null if the requested frame does not exist.
    public Frame getFrame(int frameNum) {
        if(frameNum >= 0 && frameNum < memory.length) {
            return memory[frameNum];
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Memory Usage:");
        for(int i = 0; i < memory.length; i++) {
            if(i % 8 == 0) {
                sb.append("\n" + i + "-" + (i + 7) + ":\t| ");
            }
            if(memory[i].used) {
                // Display the first 5 characters of the process id
                String id = memory[i].id;
                if(id.length() > 5) {
                    id = id.substring(0, 4);
                }
                sb.append(id + "\t| ");
            } else {
                sb.append("     \t| ");
            }
        }

        return sb.toString();
    }
    public Frame[] getMemory(){
        return memory;
    }
}
