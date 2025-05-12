import java.util.Queue;
import java.util.LinkedList;

/**
 * Process Control Block (PCB) representing a process in the operating system.
 */
public class PCB {

    private static Memory memory = null; // Static reference to the Memory object

    public String id; // Process ID
        public SegmentEntry[] STable; // Segment Table
    public int residentSetCount; // Number of pages in the resident set
    public int[] residentSet; // Frame numbers of the pages in the resident set
    public OS.REPLACE_POLICY policy; // Page replacement policy for this process

    // Queue to track the order of page loads into memory, used for FIFO replacement policy.
    // The Integer array elements represent segment number and page number respectively.
    public Queue<Integer[]> loadQueue = new LinkedList<>();

    /**
     * Constructor for PCB.
     *
     * @param id The process ID.
     * @param segments An array representing the size of each segment.
     * @param policy The page replacement policy.
     */
    public PCB(String id, int[] segments, OS.REPLACE_POLICY policy) {
        this.id = id;
        this.policy = policy;
        STable = new SegmentEntry[segments.length];
        for (int i = 0; i < STable.length; i++) {
            STable[i] = new SegmentEntry(i, segments[i]);
        }

        // Calculate the size of the resident set
        residentSetCount = 0;
        for (SegmentEntry segment : STable) {
            residentSetCount += segment.PTable.length;
        }
        if (residentSetCount > OS.maxResidentSetNum) {
            residentSetCount = OS.maxResidentSetNum;
        }
    }

    /**
     * Sets the Memory object for the PCB class. This needs to be called only once before using PCB class methods.
     *
     * @param m The Memory object to set.
     */
    public static void setMemory(Memory m) {
        memory = m;
    }

    /**
     * After creating the process, loads some pages. If the entire program can fit into the resident set, then load all of it.
     * Initial loading strategy: Load pages from the 0th, 1st, etc., segments until the resident set is fully loaded.
     */
    public void initLoad() {
        int index = 0;
        for (SegmentEntry segment : STable) {
            for (PageEntry page : segment.PTable) {
                if (index >= residentSetCount) {
                    break;
                }
                page.setLoad(residentSet[index]);
                loadQueue.add(new Integer[]{segment.segmentNum, page.pageNum});
                memory.readPage(id, segment.segmentNum, page.pageNum, residentSet[index]);
                index++;
            }
        }
    }

    /**
     * Selects a page to replace based on the FIFO policy, returns the segment and page numbers.
     *
     * @return An array containing the segment and page numbers of the selected page.
     */
    private Integer[] selectReplacePage_FIFO() {
        return loadQueue.poll();
    }

    /**
     * Selects a page to replace based on the LRU policy, returns the segment and page numbers.
     *
     * @return An array containing the segment and page numbers of the selected page.
     */
    private Integer[] selectReplacePage_LRU() {
        long leastTime = System.currentTimeMillis() + 1000000; // Set to a future time
        int segmentNum = -1;
        int pageNum = -1;

        // Iterate through all pages to find the one with the smallest usedTime
        for (SegmentEntry segment : STable) {
            for (PageEntry page : segment.PTable) {
                if (page.load && page.usedTime < leastTime) {
                    leastTime = page.usedTime;
                    segmentNum = segment.segmentNum;
                    pageNum = page.pageNum;
                }
            }
        }

        return new Integer[]{segmentNum, pageNum};
    }

    /**
     * Replaces a page in the resident set based on the policy and loads the page from segmentNum segment and pageNum page.
     *
     * @param inSN Segment number of the page to load.
     * @param inPN Page number of the page to load.
     */
    public void replacePage(int inSN, int inPN) {
        Integer[] something;
        if (policy == OS.REPLACE_POLICY.FIFO) {
            something = selectReplacePage_FIFO();
        } else {
            something = selectReplacePage_LRU();
        }

        int outSN = something[0];
        int outPN = something[1];

        PageEntry inPage = STable[inSN].PTable[inPN];
        PageEntry outPage = STable[outSN].PTable[outPN];
        int frameNum = outPage.frameNum;
        memory.writePage(id, outSN, outPN, frameNum);
        outPage.setUnload();
        memory.readPage(id, inSN, inPN, frameNum);
        inPage.setLoad(frameNum);
        loadQueue.add(new Integer[]{inSN, inPN});
    }
}

/**
 * Segment Table Entry representing a segment in a process.
 */
class SegmentEntry {
    public int segmentNum; // Segment number
    public int segmentSize; // Segment size
    public PageEntry[] PTable; // Corresponding page table

    /**
     * Constructor for SegmentEntry.
     *
     * @param segmentNum The segment number.
     * @param segmentSize The size of the segment.
     */
    public SegmentEntry(int segmentNum, int segmentSize) {
        this.segmentNum = segmentNum;
        this.segmentSize = segmentSize;

        // Calculate the size of the page table
        int count = segmentSize / OS.pageSize;
        if (segmentSize % OS.pageSize != 0) {
            count++;
        }
        PTable = new PageEntry[count];
        for (int i = 0; i < count; i++) {
            PTable[i] = new PageEntry(i);
        }
    }
}

/**
 * Page Table Entry representing a page in a segment.
 */
class PageEntry {
    public int pageNum; // Page number
    public boolean load; // Whether this page is loaded into memory
    public int frameNum; // Frame number where the page is loaded. Irrelevant if load is false.
    // The last time this page was accessed. Used for LRU page replacement strategy. Irrelevant if load is false.
    public long usedTime;
    public String info; // Additional information, such as protection, sharing settings, etc.

    /**
     * Constructor for PageEntry. Creates an unloaded page with a specified page number.
     *
     * @param pageNum The page number.
     */
    public PageEntry(int pageNum) {
        this.pageNum = pageNum;
        setUnload();
    }

    /**
     * Sets the page as loaded into the frame with number frameNum.
     *
     * @param frameNum The frame number where the page is loaded.
     */
    public void setLoad(int frameNum) {
        this.load = true;
        this.frameNum = frameNum;
        usedTime = System.currentTimeMillis(); // Update the last used time
    }

    /**
     * Unloads the page from memory.
     */
    public void setUnload() {
        this.load = false;
        this.frameNum = -1;
        usedTime = -1; // Reset the last used time
    }

    public void setUsedTime(){
        usedTime = System.currentTimeMillis(); // Update the last used time
    }
}
