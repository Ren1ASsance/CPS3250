import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class OS {
    public static final int memorySize = 64 * 1024;		//Memory size
    public static final int pageSize = 1 * 1024;		//Page size, a power of 2 for simplified address translation
    public static final int maxSegmentNum = 4;			//Maximum number of segments in a program
    public static final int maxSegmentSize = 16 * 1024;	//Maximum size of a segment
    public static final int maxResidentSetNum = 8;		//Maximum number of pages in a process's resident set

    private static final AtomicInteger TotalRequest = new AtomicInteger(0); //number keep track of the number of request made
    private static final AtomicInteger PageFault = new AtomicInteger(0); //number keep track of the number of request made
    public static enum REPLACE_POLICY {FIFO, LRU};
    public static REPLACE_POLICY ReplacePolicy = REPLACE_POLICY.LRU;	// Default replacement policy is LRU

    private Map<String, PCB> processes = new HashMap<>();
    Memory memory = new Memory(memorySize / pageSize);

    //Constructor for the OS class
    public OS() {
        PCB.setMemory(memory);
    }

    //Judge the legality of the created process; return an error message if not legal, otherwise return null
    private String validate(String id, int[] segments) {
        if(processes.containsKey(id)) {
            return "Duplicate process name";
        }
        if(segments.length == 0 || segments.length > 4) {
            return "A process can only have 1 to " + OS.maxSegmentNum + "segments";
        }
        for(int i = 0; i < segments.length; i++) {
            if(segments[i] <= 0 || segments[i] > OS.maxSegmentSize) {
                return "A segment must be less than " + OS.maxSegmentSize + "KB";
            }
        }

        return null;
    }

    //Create a process and return whether the creation was successful
    public boolean createProcess(String id, int[] segments) {
        String mess = validate(id, segments);
        if(mess != null) {
            System.out.println("Failed to create process (" + mess + ")");
            return false;
        }

        //Ensure if there is enough memory
        PCB process = new PCB(id, segments, ReplacePolicy);
        if(process.residentSetCount > memory.unusedFrameCount()) {
            System.out.println("Failed to create process (insufficient memory)");
            return false;
        }
        //Allocate memory and set the resident set
        processes.put(id, process);
        int[] frame = memory.mallocFrame(id, process.residentSetCount);
        process.residentSet = frame;

        //Load some pages randomly
        process.initLoad();

        System.out.println("Process creation successful: " + id);
        return true;
    }


    //Destroy a process
    public void destroyProcess(String id) {
        PCB process = processes.get(id);
        if(process == null) {
            System.out.println("Operation failed, process " + id + " does not exist");
            return ;
        }

        int[] frames = process.residentSet;
        memory.freeFrame(frames);
        processes.remove(id);
        System.out.println("Process destruction successful: " + id);
    }


    //Convert logical address (segment number + segment offset) to physical address. Return -1 if an error occurs.
    //If a page fault occurs, choose a page to replace based on the replacement policy and load the requested page into memory.

    public int toPhysicalAddress(String id, int segmentNum, int segmentOffset) {
        PCB process = processes.get(id);
        if(process == null) {
            System.out.println("Operation failed, process " + id + " does not exist");
            return -1;
        }
        //Check if the requested segment exists
        if (segmentNum < 0 || segmentNum >= process.STable.length) {
            System.out.println("Operation failed, segment " + id + " (" + segmentNum + ") does not exist");
            return -1;
        }

        SegmentEntry segment = process.STable[segmentNum];
        //If the segment offset is greater than the segment size, the request fails
        if(segmentOffset > segment.segmentSize) {
            System.out.println("Operation failed, process " + id + " segment offset (" + segmentOffset +") out of bounds");
            return -1;
        }
        //Calculate page number and page offset based on segment offset
        int pageNum = segmentOffset / OS.pageSize;
        int pageOffset = segmentOffset % OS.pageSize;

        PageEntry page = segment.PTable[pageNum];
        if(page.load == false) {
            //If the frame is not in memory, perform a page fault, replace a page based on the replacement policy, and load the page
            System.out.println("Requested frame is not in memory, page fault occurred");
            process.replacePage(segmentNum, pageNum);
        }

        // Calculate the physical address
        page.usedTime = System.currentTimeMillis();
        int frameNum = page.frameNum;
        int beginAddress = memory.getFrame(frameNum).beginAddress;
        System.out.println("process" + id + "segment(" + segmentNum +") segment offset(" + segmentOffset + ") physical address:" + (beginAddress + pageOffset));
        return beginAddress + pageOffset;
    }

    //Set the page mechanism we use
    public static void setReplacePolicy(OS.REPLACE_POLICY policy) {
        OS.ReplacePolicy = policy;
    }

    //Show the memory
    public void showMemory() {
        System.out.println(memory.toString());
        System.out.println();
    }

    //Show the process
    public void showProcess(String id) {
        PCB process = processes.get(id);
        if(process == null) {
            System.out.println("This process is not exist");
            return ;
        }

        StringBuilder sb = new StringBuilder();

        int[] frames = process.residentSet;
        sb.append("Resident set: [ ");
        for(int elem : frames) {
            sb.append(elem + " ");
        }
        sb.append("]\n");
        sb.append("Replacement policy: ");
        if(process.policy == REPLACE_POLICY.FIFO) {
            sb.append("FIFO ");
            sb.append("[ ");
            for(Integer[] something : process.loadQueue) {
                sb.append("(" + something[0] + ", " + something[1] + ") ");
            }
            sb.append("]\n\n");
        } else {
            sb.append("LRU\n\n");
        }

        for(SegmentEntry segment : process.STable) {
            sb.append("Process " + id + " Segment number: " + segment.segmentNum + " Segment size: " + segment.segmentSize + "\n");
            sb.append("-----------------------------------------------------------------\n");
            sb.append("| Page Number\t| Load Status\t| Frame Number\t| Frame Start Address\t| Last Access Time\t|\n");
            sb.append("-----------------------------------------------------------------\n");
            for(PageEntry page : segment.PTable) {
                sb.append("| " + page.pageNum + "\t\t\t\t");
                if(page.load) {
                    sb.append("| load\t\t\t| " + page.frameNum + "\t\t\t\t| " + memory.getFrame(page.frameNum).beginAddress + "\t\t| " + page.usedTime + " |\n");
                } else {
                    sb.append("| unload\t| \t| \t\t| \t\t|\n");
                }
            }
            sb.append("-----------------------------------------------------------------\n\n");
        }

        System.out.print(sb.toString());
    }

    public void pageReplace(String id, int segmentNum, int pageNum) {
        TotalRequest.incrementAndGet(); // Increment on every request

        try {
            PCB process = processes.get(id);

            if (segmentNum >= process.STable.length) {
                System.out.println("Segment " + segmentNum + " is not in the process");
                return;
            }

            if (pageNum >= process.STable[segmentNum].PTable.length) {
                System.out.println("Page " + pageNum + " is not in segment " + segmentNum);
                return;
            }

            if (!process.STable[segmentNum].PTable[pageNum].load) {
                process.replacePage(segmentNum, pageNum);
                PageFault.incrementAndGet();
            } else {
                process.STable[segmentNum].PTable[pageNum].setUsedTime(); // Update used time
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace(); // For debugging
        }
        System.out.println("Total Requests: " + TotalRequest.get()); // Print Total Requests
        System.out.println("Page Faults: " + PageFault.get()); // Print Page Faults
    }

    public int getTotalRequest() {
        return TotalRequest.get();
    }

    public int getPageFault() {
        return PageFault.get();
    }
    public Map<String, PCB> getProcesses(){
        return processes;
    }

    public Collection<PCB> getAllActivePCBs() {
        return processes.values();
    }
}
