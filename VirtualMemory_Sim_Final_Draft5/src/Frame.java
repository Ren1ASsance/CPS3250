/*Purpose:This class represents a frame in a memory management system.
  A frame is a fixed-size block of memory used to store data.
 */
public class Frame {

    public int frameNum;

    // A flag indicating whether the frame is currently in use.
    public boolean used;

    // The starting address of the frame in memory.
    public int beginAddress;

    // An identifier associated with the frame, typically representing the data it holds.
    public String id;

    //Constructs a new Frame object with the specified frame number and starting address.
    // The frame is initially set to unused and has no associated identifier
    public Frame(int frameNum, int beginAddress) {
        // super();
        this.frameNum = frameNum;
        this.beginAddress = beginAddress;
        setUnused();
    }

    public void setUsed(String id) {
        this.used = true;
        this.id = id;
    }

    public void setUnused() {
        this.used = false;
        this.id = null;
    }

    public int getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(int frameNum) {
        this.frameNum = frameNum;
    }

    public boolean isUsed() {
        return used;
    }

    public int getBeginAddress() {
        return beginAddress;
    }

    public void setBeginAddress(int beginAddress) {
        this.beginAddress = beginAddress;
    }

    public String getId() {
        return id;
    }

    // Improved setId method with format validation
    public void setId(String id) {
        if (validateIdFormat(id)) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Invalid ID format. Expected format: processId_segmentNumber_pageNumber");
        }
    }

    // Helper method to validate the ID format
    private boolean validateIdFormat(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        String[] parts = id.split("_");
        if (parts.length != 3) {
            return false;
        }
        try {
            // Validate that segment and page numbers are integers
            Integer.parseInt(parts[1]);
            Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Improved parseIdComponent method
    private String parseIdComponent(int index) {
        if (id != null && !id.isEmpty()) {
            String[] parts = id.split("_");
            if (parts.length > index) {
                return parts[index];
            } else {
                return "Component missing"; // Indicate missing component
            }
        }
        return "ID not set"; // Indicate ID is not set
    }

    // Method to extract the process ID from the frame's ID
    public String getProcessId() {
        return parseIdComponent(0);
    }

    // Method to extract the segment number from the frame's ID
    public int getSegmentNumber() {
        String segmentNumberStr = parseIdComponent(1);
        try {
            return Integer.parseInt(segmentNumberStr);
        } catch (NumberFormatException e) {
            return -1; // Return an invalid number if parsing fails
        }
    }

    // Method to extract the page number from the frame's ID
    public int getPageNumber() {
        String pageNumberStr = parseIdComponent(2);
        try {
            return Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            return -1; // Return an invalid number if parsing fails
        }
    }

}
