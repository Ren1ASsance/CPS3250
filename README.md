# Virtual Memory explorer (GUI Included)

![GUI Screen Shot](Supporting Materials/GUI Screen Shot.png)


## Overview
This Virtual Memory Simulator is a JavaFX-based application designed to simulate and visualize the operations of a virtual memory system. It offers an interactive graphical user interface (GUI) to demonstrate how processes are managed in memory, including operations like process creation, destruction, and page replacement.

## Features
- **Memory Grid Visualization:** Displays the current state of memory in a grid format, where each cell represents a frame in memory.
- **Dynamic Page Table:** Shows details of the pages loaded in memory, including process ID, page number, segment number, and frame number.
- **Process Management:** Allows users to create, destroy, and replace processes, observing the real-time impact on memory.
- **Policy Selection:** Users can select memory management policies like FIFO and LRU for page replacement.

## Latest Updates

### Number of Pages and Requests Display
- **Feature Description**: A real-time counter has been added to the interface, displaying the total number of pages currently loaded in the memory as well as the total number of memory requests made during the simulation. This feature enhances the visibility of key metrics and aids users in monitoring the simulator's performance.

### Random Page Replacement
- **Feature Description**: The Random Page Replacement function introduces an element of stochastic behavior into our memory management simulation. By randomly selecting pages to replace, users can observe how unpredictability affects page faults and memory efficiency, providing a contrast to deterministic algorithms like FIFO and LRU.


## Getting Started with Virtual Memory Simulator

This guide will walk you through the necessary steps to get your Virtual Memory Simulator up and running, including prerequisites, installation, and setup verification.

### Prerequisites

Ensure you have the following requirements before starting:

- **Java Development Kit (JDK):** Requires Java 11 or higher. Download from [Oracle's Java SE Downloads](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or an OpenJDK distribution.
- **JavaFX SDK:** Must be properly set up in your IDE. Obtain it from [OpenJFX](https://openjfx.io/).

### Installation and Setup Verification

1. **Clone the Repository:** Get the project repository from the source control page to your local machine.
2. **Open the Project:** Launch your IDE and open the cloned project.
3. **Configure JavaFX:** Ensure JavaFX libraries are in your project's build path. Refer to OpenJFX site for IDE-specific setup instructions.

#### IntelliJ IDEA Setup

1. **Project Initialization:** In IntelliJ IDEA, create a new project or open an existing one.
2. **JavaFX Library Integration:**
   - Navigate to `File` > `Project Structure` > `Libraries`.
   - Click `+`, choose `Java`, and locate your JavaFX `lib` directory (e.g., `path/to/javafx-sdk-11/lib`).
   - Confirm the libraries in the project structure.
3. **VM Options Configuration:**
   - Go to `Run` > `Edit Configurations`.
   - Add VM options: `--module-path path/to/javafx-sdk-11/lib --add-modules javafx.controls,javafx.fxml`.
   - Adjust the path to your JavaFX SDK `lib` directory.
4. **Run Your Project:** Ensure JavaFX is correctly set up by running the project.

#### Eclipse Setup

1. **Project Initialization:** In Eclipse, create a new Java project or open an existing one.
2. **JavaFX Library Integration:**
   - Right-click your project in `Package Explorer`, select `Build Path` > `Configure Build Path`.
   - Add `User Library` and create a new one (e.g., `JavaFX11`).
   - Add all JAR files from the JavaFX SDK `lib` directory.
3. **Modulepath Adjustment:**
   - Move the JavaFX JARs to the `Modulepath` in the `Java Build Path` window.
4. **VM Arguments Configuration:**
   - Under `Run` > `Run Configurations`, add VM arguments: `--module-path path/to/javafx-sdk-11/lib --add-modules javafx.controls,javafx.fxml`.
   - Update the path to your JavaFX SDK `lib` directory.
5. **Run Your Project:** Verify JavaFX integration by running the application.

### Final Steps for Both IDEs

- Ensure accurate paths to the JavaFX SDK.
- Replace placeholders with your system-specific paths.

### Verification Process

1. **Run `Main.java`:** Execute `Main.java` in your IDE to test your setup.
   - The script confirms Java version and JavaFX availability.
2. **Monitor Output:** Check the console for errors. Successful setup launches the Virtual Memory Simulator GUI.
3. **Troubleshooting:** Follow console guidance for any issues related to JavaFX or Java version compatibility.

### First Launch
After successfully verifying your setup with `Main.java`, you can start interacting with the simulator through its GUI. Follow the Usage section in this README for detailed instructions on how to use the simulator's features.

By following these steps, you can ensure that your development environment is correctly configured to run and develop the Virtual Memory Simulator.

## Usage
- **Create Process:** Input the process ID and segment details, then click the 'Create Process' button to allocate memory.
- **Destroy Process:** Enter the process ID to be destroyed and click the 'Destroy Process' button.
- **Replace Page:** Use this feature to simulate page replacement in memory.
- **View Memory Grid:** Observe the changes in the memory grid as processes are created and destroyed.
- **View Page Table:** Check the detailed view of pages loaded in memory.

## Usage Guide

### Memory Grid
The memory grid on the left represents the physical frames of memory. Each square in the grid corresponds to one memory frame, and the label "Free" indicates that the frame is currently not allocated to any process.

### Page Table
The page table on the right side provides detailed information about the pages loaded in memory. It displays the `Process ID`, `Page Number`, `Segment Number`, and `Frame Number` for each page that is currently loaded.

### Process Creation
To create a process:
1. **Process Name:**
   Enter the name of the process in the `Process ID` field located at the bottom of the GUI.
   
2. **Segment and Page Details:**
   Specify the number of segments and pages that the process will use. Fill in the `Segment Number` and `Page Number` fields accordingly.

3. **Create Process:**
   Once all details are entered, click the `Create Process` button to allocate memory for the new process. Upon successful creation, you will see the memory grid update with the new process's frames marked with its ID instead of "Free".

### Process Destruction
To destroy a process:
1. **Process Name:**
   In the `Process to Destroy` field, enter the ID of the process you wish to terminate.
   
2. **Destroy Process:**
   Click the `Destroy Process` button. The memory grid will update to reflect the deallocation of frames, marking them as "Free" once again.

### Page Replacement
To perform a page replacement:
1. **Process ID:**
   Enter the ID of the process for which you want to replace a page in the `Process ID` field under the `Replace Page` section.
   
2. **Segment and Page:**
   Specify the segment and page number that you want to load into memory by using the `Segment Number` and `Page Number` fields.

3. **Replace Page:**
   Click the `Replace Page` button. The memory grid and page table will update to show the new page loaded into the frame previously occupied by the page being replaced.

### Help and Instructions
For additional help or instructions on how to use the simulator:
- Click the `?` button at the bottom left corner of the GUI. A window should appear providing further information about virtual memory concepts and detailed instructions for using the simulator.

By following these steps, users can interact with the Virtual Memory Simulator to understand how processes are allocated to memory frames, how virtual memory management works, and how different page replacement policies can affect the state of memory.

## GUI Components
- **Memory Grid:** Represents each frame in memory. Used frames are marked with the process ID.
- **Page Table:** A table view showing the mapping of process pages in memory frames.
- **Control Panel:** Includes inputs and buttons for managing processes and memory operations.

### Project Directory Structure
- **`Main.java`:** The entry point of the application. This class contains the `main` method which initiates the JavaFX application by calling `launch` on an instance of `MemorySimulatorGUI.java` and also acts as a setup verification tool.

- **`MemorySimulatorGUI.java`:** The entry point of the application. Contains the code for the graphical user interface of the simulator. It extends `Application` from JavaFX and creates the GUI components that users interact with, such as the memory grid, page table, and control panels for simulating process and memory management operations.

- **`Frame.java`:** This file defines the `Frame` class, which represents a single frame in the memory. A frame is a fixed-size block of memory and is a fundamental unit in the memory management system of an operating system. The class includes attributes to determine whether a frame is in use, the process occupying it, and methods to set and retrieve this information.

- **`Memory.java`:** This file encapsulates the logic for the memory model used by the simulator. It maintains an array of `Frame` objects and provides methods for allocating and freeing frames (`mallocFrame` and `freeFrame`), simulating the reading and writing of pages to and from disk, and reporting memory usage.

- **`OS.java`:** Represents the operating system's functionality in managing virtual memory. This class includes methods for process creation, destruction, and page replacement policy implementation (e.g., FIFO, LRU).

- **`PCB.java`:** Stands for Process Control Block. This file defines the `PCB` class which keeps track of the information about each process, including its ID, segment and page information, and the state of the process. It also contain methods to handle process state changes and memory allocation for the process.

- **`Shell.java`:** This class provides a command-line interface for the simulator, allowing for text-based interaction. It parse user commands and invoke corresponding operations in the virtual memory system.

### Understanding the Relationships

- The `OS` class manages the processes and their interaction with memory, which is represented by the `Memory` class.
- The `PCB` class holds process-specific information that the `OS` class uses to manage processes.
- `Frame` objects are managed by the `Memory` class and represent the actual storage units within the simulated memory.
- `MemorySimulatorGUI` creates the visual representation and allows users to interact with the system, visualizing the operations performed by the `OS` class.
- `Main` boots up the application, and `Shell` offer a different mode of interaction, not necessarily tied to the GUI.

This structure follows a modular design, separating the concerns of memory management, process management, user interface, and potentially command-line interaction, making the system easier to maintain and extend.

## Contributions
Contributions to this project are welcome.

## License
This project is licensed under the [MIT License](https://rem.mit-license.org).

## Contact
For any queries, please contact our team.

## Acknowledgments
- Thanks to Dr. Rashid for instructions and to everyone who contributed to the development and testing of this application.

## Team
- Liu Kaiyu
- Yu Fan
- Hu Yiyang
- Xia Lei

---# CPS3250
