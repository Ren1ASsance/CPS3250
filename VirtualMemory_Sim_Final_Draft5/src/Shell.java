// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.


import java.util.Scanner;

public class Shell {
    public static final String helpMess =
            "create process processId sizes of segments\t--> Create a process\n" +
                    "destroy process processId\t--> Destroy a process\n" +
                    "show memory\t--> Display memory usage\n" +
                    "show process processId\t--> Display resident set, replacement policy, segment table, page table of the process\n" +
                    "address processName segmentNumber segmentOffset\t\t--> Map logical address to physical address\n" +
                    "page replace processID segmentNum pageNum\t\t-->Do page replacement"+
                    "help or h\t-> Get help\n" +
                    "quit or q\t--> Exit\n";

    public static void main(String[] args) {
        printMessage();
        setReplacePolicy();
        System.out.println("Enter 'help' for more information");
        shell();
        Input.close();
    }

    /**
     * 1. create process pname segments
     * 2. destroy process pname
     * 3. show memory
     * 4. show process pname
     * 5. help or h
     * 6. quit or q
     * 8. address pname sgementNum segmentOffset
     */
    public static void shell() {
        OS os = new OS();
        System.out.print(">>> ");
        while(true) {
            String command = Input.nextLine();
            if(command == null || command.trim().equals("")) {
                System.out.print(">>> ");
                continue;
            }

            String[] words = command.split(" ");
            if(words.length >= 4 && "create".equals(words[0].trim()) && "process".equals(words[1].trim())) {
                String processId = words[2].trim();
                int[] segments = new int[words.length - 3];
                try{
                    for(int i = 3, index = 0; i < words.length; i++, index++) {
                        segments[index] = Integer.parseInt(words[i]);
                        if(segments[index] <= 0) {
                            throw new Exception();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("Invalid command. Segment size must be a positive integer (use 'help' for assistance)");
                    System.out.print(">>> ");
                    continue;
                }
                os.createProcess(processId, segments);

            } else if(words.length == 3 && "destroy".equals(words[0].trim()) && "process".equals(words[1].trim()) ) {
                String processId = words[2].trim();
                os.destroyProcess(processId);


            } else if(words.length == 2 && "show".equals(words[0].trim()) && "memory".equals(words[1].trim()) ) {
                os.showMemory();
            }
            else if (words.length == 5 && "page".equals(words[0].trim()) && "replace".equals(words[1].trim())) {
                   String porcessId = words[2].trim();
                    int segmentNum = Integer.parseInt(words[4].trim());
                    int pageNum = Integer.parseInt(words[3].trim());
                    os.pageReplace(porcessId, pageNum, segmentNum);
}
            else if(words.length == 3 && "show".equals(words[0].trim()) && "process".equals(words[1].trim()) ) {
                String processId = words[2].trim();
                os.showProcess(processId);

            } else if(words.length == 1 && "help".equals(words[0].trim()) || "h".equals(words[0].trim()) ) {
                System.out.println(helpMess);

            } else if(words.length == 1 && "quit".equals(words[0].trim()) || "q".equals(words[0].trim()) ) {
                System.out.println("quit");
                break;
            } else if(words.length == 4 && "address".equals(words[0].trim())) {
                String porcessId = words[1].trim();
                int segmentNum, segmentOffset;
                try{
                    segmentNum = Integer.parseInt(words[2].trim());
                    segmentOffset = Integer.parseInt(words[3].trim());
                    if(segmentNum < 0 || segmentOffset < 0) {
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    System.out.println("Invalid command. Segment number and offset must be positive integers (use 'help' for assistance)");
                    System.out.print(">>> ");
                    continue;
                }
                os.toPhysicalAddress(porcessId, segmentNum, segmentOffset);

            } else {
                System.out.println("Invalid command (use 'help' for assistance)");
            }

            System.out.print(">>> ");
        }
    }

    /**
     * 璁剧疆榛樿缃崲绛栫暐
     */
    public static void setReplacePolicy() {
        System.out.print(">>> Please set the replacement policy (0 for FIFO, 1 for LRU): ");
        while(true) {
            String mess = Input.nextLine().trim();
            if("0".equals(mess)) {
                OS.setReplacePolicy(OS.REPLACE_POLICY.FIFO);
                System.out.println("Set replacement policy to FIFO");
                break;
            } else if("1".equals(mess)) {
                OS.setReplacePolicy(OS.REPLACE_POLICY.LRU);
                System.out.println("Set replacement policy to LRU");
                break;
            } else {
                System.out.print(">>> Invalid input. Please set the replacement policy (0 for FIFO, 1 for LRU): ");
            }
        }
    }

    /**
     * Print the basic and necessary information
     */
    public static void printMessage() {
        String version = "1.0";

        System.out.println("Memory Management [version " + version + "]");
        System.out.println("Author: group666");
        System.out.println();
        System.out.println("Memory size is 64K, page frame size is 1K, a process can have a maximum of 4 segments, and each segment is at most 16K. A process resident set has a maximum of 8 pages.");
        System.out.println("Resident set replacement policy: Local policy (select one page only from the process's resident set)");
        System.out.println("Page eviction policy: FIFO, LRU");
        System.out.println("Process initial loading policy: Load pages sequentially from segment 0, segment 1, ... until the resident set is fully loaded");
        System.out.println("Placement policy: Determine where a process's resident set is placed in memory. Preferably placed in low page frames");
        System.out.println();
    }
}

/*
 * When there are multiple Scanner objects, calling the close() method will also close the System.in object.
 * If other Scanner object methods are called, a java.util.NoSuchElementException exception may occur.
 *
 * This class is a simple wrapper for Scanner, used to unify input retrieval and close Scanner objects.
 */
class Input {
    private static Scanner input = new Scanner(System.in);

    public static void close() {
        input.close();
    }

    public static String nextLine() {
        return input.nextLine();
    }
}
