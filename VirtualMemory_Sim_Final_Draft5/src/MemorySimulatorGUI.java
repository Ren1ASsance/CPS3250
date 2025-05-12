import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import java.util.Collection;
import java.util.Random;
import java.util.Collection;






public class MemorySimulatorGUI extends Application {

    private static final int GRID_SIZE = 8; // The size of the grid
    private static final int MEMORY_FRAMES = 64; // Total number of frames in memory

    private ToggleGroup policyToggleGroup;

    private Label totalRequestsLabel;
    private Label totalPageFaultsLabel;

    private Stage primaryStage;

    private GridPane memoryGrid; // Class-level variable to hold the memory grid

    private StackPane tableContainer; // Declaration of tableContainer
    private TableView<PageTableEntry> loadedPageTableView;
    private TableView<PageTableEntry> unloadedPageTableView;
    private ComboBox<String> tableSelector;

    @Override
    public void start(Stage primaryStage) {

        showPolicySelectionDialog(primaryStage);
    }

    private TableView<PageTableEntry> pageTableView;

    public void showMainGUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        Button randomReplaceButton = new Button("Random Page Replace");
        randomReplaceButton.setOnAction(event -> randomPageReplace());

        // Stats panel for displaying total requests and page faults
        VBox statsPanel = createStatsPanel();

        // Header with title and policy selection
        VBox header = createHeader();

        // 8x8 grid for memory representation
        VBox memoryGrid = createMemoryGrid();

        // VBox containing the page table and the ComboBox for column selection
        VBox pageTableBox = createPageTableContainer();

        // Layout configuration for memory grid and page table
        HBox mainLayout = new HBox(10);
        mainLayout.getChildren().addAll(memoryGrid, pageTableBox);
        mainLayout.setAlignment(Pos.CENTER);

        // The control panel is now an HBox
        HBox controlPanel = createControlPanel();
        HBox destroyPanel = createDestroyProcessPanel(); // New control panel for destroying processes
        HBox pageReplacementPanel = createPageReplacementPanel(); // Control panel for replacing page

        // Create the question mark button
        Button questionButton = new Button("?");
        questionButton.setOnAction(event -> openInstructionWindow());
        // Placing the button at the bottom left
        HBox bottomBox = new HBox(questionButton);

        VBox root = new VBox(10);
        root.getChildren().addAll(header, statsPanel, mainLayout, controlPanel, pageReplacementPanel, randomReplaceButton, destroyPanel, bottomBox);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        // Scene setup
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Virtual Memory Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    private VBox createHeader() {
        Label titleLabel = new Label("Virtual Memory Simulator");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label memoryInfoLabel = new Label("64K Memory with 64 Frames");
        memoryInfoLabel.setStyle("-fx-font-size: 14px;");

        Label groupLabel = new Label("Group#666,Kaiyu Liu, Yiyang Hu, Yufan");
        groupLabel.setStyle("-fx-font-size: 10px;");
/*
//        RadioButton lruButton = new RadioButton("LRU");
//        RadioButton fifoButton = new RadioButton("FIFO");
//
//        policyToggleGroup = new ToggleGroup();
//        lruButton.setToggleGroup(policyToggleGroup);
//        fifoButton.setToggleGroup(policyToggleGroup);
//
//        // Add event listeners to disable the other option once one is selected
//        lruButton.setOnAction(event -> {
//            if (lruButton.isSelected()) {
//                fifoButton.setDisable(true);
//            }
//        });
//
//        fifoButton.setOnAction(event -> {
//            if (fifoButton.isSelected()) {
//                lruButton.setDisable(true);
//            }
//        });
//
//        HBox radioButtonsBox = new HBox(lruButton, fifoButton);
//        radioButtonsBox.setSpacing(10);
*/
        VBox header = new VBox(titleLabel, memoryInfoLabel ,groupLabel);
        header.setAlignment(Pos.CENTER);
        header.setSpacing(5);

        return header;
    }

    private OS os = new OS(); // Declare an instance variable for the OS class
    private Memory memory = os.memory;

    private void showPolicySelectionDialog(Stage primaryStage) {

        // Create a dialog for policy selection
        Dialog<OS.REPLACE_POLICY> dialog = new Dialog<>();
        dialog.setTitle("Select Replacement Policy");
        dialog.setHeaderText("Please select the replacement policy:");

        // Add buttons
        ButtonType fifoButtonType = new ButtonType("FIFO", ButtonBar.ButtonData.OK_DONE);
        ButtonType lruButtonType = new ButtonType("LRU", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(fifoButtonType, lruButtonType);

        // Set result converter for button types
        dialog.setResultConverter(buttonType -> {
            if (buttonType == fifoButtonType) {
                return OS.REPLACE_POLICY.FIFO;
            } else if (buttonType == lruButtonType) {
                return OS.REPLACE_POLICY.LRU;
            }
            return null;
        });

        // Show dialog and wait for response
        Optional<OS.REPLACE_POLICY> result = dialog.showAndWait();
        result.ifPresent(policy -> {
            os.setReplacePolicy(policy); // Set the replacement policy
            showMainGUI(primaryStage);   // Show the main GUI window
        });
    }

    private VBox createMemoryGrid() {
        memoryGrid = new GridPane();
        memoryGrid.setPadding(new Insets(10));
        memoryGrid.setHgap(10); // Horizontal gap
        memoryGrid.setVgap(10); // Vertical gap

        // Initialize all cells as "Free"
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                StackPane frameVisual = createFrameVisual("Free");
                memoryGrid.add(frameVisual, j, i);
            }
        }

        VBox vbox = new VBox(10); // Vertical spacing
        vbox.getChildren().addAll(new Label("Memory"), memoryGrid);
        return vbox;
    }

    private StackPane createFrameVisual(String label) {
        StackPane frameVisual = new StackPane();
        frameVisual.setStyle("-fx-border-color: black; -fx-background-color: grey;");
        frameVisual.setPrefSize(50, 50);

        Label frameLabel = new Label(label);
        frameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        StackPane.setAlignment(frameLabel, Pos.BOTTOM_CENTER);

        frameVisual.getChildren().add(frameLabel);
        return frameVisual;
    }

    private Frame[][] swap(Frame[] frames){
        Frame[][] res = new Frame[8][8];
        int indexOfFrames = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(frames[indexOfFrames].used){
                    res[i][j] = frames[indexOfFrames];
                }
                else{
                    res[i][j] = null;
                }

                indexOfFrames++;
            }
        }
        return res;
    }



    private VBox createFrameLabels() {
        VBox labelsBox = new VBox();
        labelsBox.setSpacing(5);
        for (int i = 0; i < MEMORY_FRAMES; i++) {
            Label label = new Label("Frame " + i + ": Free");
            label.setPadding(new Insets(2));
            label.setStyle("-fx-font-size: 10px;");
            labelsBox.getChildren().add(label);
        }
        return labelsBox;
    }

    public class TableRowData {
        private final SimpleStringProperty pageTable;
        private final SimpleStringProperty pcb;

        public TableRowData(String pageTable, String pcb) {
            this.pageTable = new SimpleStringProperty(pageTable);
            this.pcb = new SimpleStringProperty(pcb);
        }

        public String getPageTable() {
            return pageTable.get();
        }

        public SimpleStringProperty pageTableProperty() {
            return pageTable;
        }

        public String getPcb() {
            return pcb.get();
        }

        public SimpleStringProperty pcbProperty() {
            return pcb;
        }
    }

    private VBox createPageTableContainer() {
        // Initialize both tables
        loadedPageTableView = createPageTable();
        unloadedPageTableView = createPageTable();

        // Initialize selector
        tableSelector = new ComboBox<>();
        tableSelector.getItems().addAll("Loaded Pages", "Unloaded Pages");
        tableSelector.setValue("Loaded Pages"); // Default selection

        // Event handler for selector
        tableSelector.setOnAction(event -> {
            boolean showLoadedPages = tableSelector.getValue().equals("Loaded Pages");
            loadedPageTableView.setVisible(showLoadedPages);
            unloadedPageTableView.setVisible(!showLoadedPages);
        });

        // Configure StackPane to overlap tables
        tableContainer = new StackPane();
        tableContainer.getChildren().addAll(loadedPageTableView, unloadedPageTableView);
        unloadedPageTableView.setVisible(false); // Initially, only show loaded pages table

        // Configure layout with selector and table container
        VBox container = new VBox(10, tableSelector, tableContainer);

        return container;
    }

    // Method to create and update the page table view
    private TableView<PageTableEntry> createPageTable() {
        TableView<PageTableEntry> pageTableView = new TableView<>();

        // Define columns
        TableColumn<PageTableEntry, String> processIdColumn = new TableColumn<>("Process ID");
        processIdColumn.setCellValueFactory(cellData -> cellData.getValue().processId);

        TableColumn<PageTableEntry, Number> pageNumberColumn = new TableColumn<>("Page Number");
        pageNumberColumn.setCellValueFactory(cellData -> cellData.getValue().pageNumber);

        TableColumn<PageTableEntry, Number> segmentNumberColumn = new TableColumn<>("Segment Number");
        segmentNumberColumn.setCellValueFactory(cellData -> cellData.getValue().segmentNumber);

        TableColumn<PageTableEntry, Number> frameNumberColumn = new TableColumn<>("Frame Number");
        frameNumberColumn.setCellValueFactory(cellData -> cellData.getValue().frameNumber);

        // Add columns to the table
        pageTableView.getColumns().addAll(processIdColumn, pageNumberColumn, segmentNumberColumn, frameNumberColumn);

        // Initialize with empty data
        pageTableView.setItems(FXCollections.observableArrayList());

        VBox tableBox = new VBox(10, pageTableView);
        tableBox.setAlignment(Pos.CENTER);

        pageTableView.setItems(FXCollections.observableArrayList());
        return pageTableView;
    }

    // Method to create the control panel
    private HBox createControlPanel() {
        Label processNameLabel = new Label("Process Name:");
        TextField processNameField = new TextField();
        processNameField.setPrefWidth(100); // Set preferred width for better layout

        Label[] segmentLabels = new Label[4];
        TextField[] segmentFields = new TextField[4]; // Array to store segment TextFields
        HBox nameBox = new HBox(5, processNameLabel, processNameField);

        HBox segmentBoxes = new HBox(5);
        for (int i = 0; i < 4; i++) {
            Label segmentLabel = new Label("Segment " + (i + 1) + ":");
            TextField segmentField = new TextField();
            segmentField.setPrefWidth(50); // Set preferred width for segment fields
            segmentFields[i] = segmentField; // Store the TextField in the array
            segmentBoxes.getChildren().add(new HBox(5, segmentLabel, segmentField));
        }

        Button createProcessButton = new Button("Create Process");
        createProcessButton.setOnAction(event -> handleCreateProcess(processNameField.getText(), segmentFields));

        HBox controlPanel = new HBox(10, nameBox, segmentBoxes, createProcessButton);
        controlPanel.setAlignment(Pos.CENTER);

        return controlPanel;
    }



    // Handler method for the create process button
    private void handleCreateProcess(String processId, TextField[] segmentFields) {
        List<Integer> segmentList = new ArrayList<>();

        // Debugging: Print the state of each element in segmentFields
        for (int i = 0; i < segmentFields.length; i++) {
            System.out.println(processId + " " + "Segment " + (i + 1) + ": " + (segmentFields[i] != null ? "Initialized" : "Null"));
        }

        try {
            for (int i = 0; i < segmentFields.length; i++) {
                if (segmentFields[i] == null) {
                    throw new IllegalArgumentException("Segment field " + (i + 1) + " is not initialized.");
                }
                int segmentSize = Integer.parseInt(segmentFields[i].getText());
                if (segmentSize < 0 || segmentSize > 16384) {
                    throw new IllegalArgumentException("Segment size must be a non-negative integer and less than or equal to 16384KB.");
                }
                if (segmentSize > 0) {
                    segmentList.add(segmentSize);
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert("Invalid input", e.getMessage());
            return;
        }

        int[] segments = segmentList.stream().mapToInt(i -> i).toArray();

        boolean success = os.createProcess(processId, segments);
        /**
         * if (success) {
            showAlert("Success", "Process creation successful: " + processId);

            // Debugging: Check if memory frames are updated
            System.out.println("Debug: Memory after process creation:");
            for (Frame frame : memory.getMemory()) {
                System.out.println("Frame " + frame.getFrameNum() + ": " + frame.getId());
            }
            // Update and refresh the page table
            Platform.runLater(() -> {
                updatePageTable();
                pageTableView.refresh(); // Refresh the TableView
                showMainGUI(primaryStage);
            });
        } else {
            showAlert("Failure", "Failed to create process.");
        }
         **/

        if (success) {
            showAlert("Success", "Process creation successful: " + processId);
            Platform.runLater(() -> {
                updatePageTable(); // Assuming this is the correct method to update the loaded pages table
                loadedPageTableView.refresh(); // Refresh the correct TableView
                updateUnloadedPageTable();
                updateStats();
                unloadedPageTableView.refresh(); // Refresh the unloaded page table
                updateMemoryGrid();
            });
        } else {
            showAlert("Failure", "Failed to create process.");
        }

    }




    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private HBox createDestroyProcessPanel() {
        Label processNameLabel = new Label("Process to Destroy:");
        TextField processNameField = new TextField();
        processNameField.setPrefWidth(100); // Adjust width as needed

        Button destroyProcessButton = new Button("Destroy Process");
        destroyProcessButton.setOnAction(event -> handleDestroyProcess(processNameField.getText()));

        HBox destroyPanel = new HBox(10, processNameLabel, processNameField, destroyProcessButton);
        destroyPanel.setAlignment(Pos.CENTER);

        return destroyPanel;
    }

    private void openInstructionWindow() {
        Stage instructionStage = new Stage();
        instructionStage.setTitle("Simulator Instructions");

        // Step 3: Create the instruction window content
        TextArea instructionText = new TextArea("When creating a process, all feilds should be filled, if you only want one segment, \n the other three should be set to 0.\n For addtional imformation, please refer to liukai@kean.edu");
        instructionText.setEditable(false); // Make the TextArea read-only

        Scene instructionScene = new Scene(instructionText, 400, 300);
        instructionStage.setScene(instructionScene);
        instructionStage.show();
    }

    private void handleDestroyProcess(String processName) {
        // Logic for destroying the process
        // You need to implement the logic based on how your application handles process destruction

        try{
            PCB process = os.getProcesses().get(processName);
            if(process == null){
                throw new IllegalArgumentException("Operation failed, process " + processName + " does not exist");
            }
        }catch (IllegalArgumentException e){
            showAlert("Invalid input", e.getMessage());
            return;
        }
        os.destroyProcess(processName);
        showAlert("Success", "Process destruction successful: " + processName);
        updatePageTable(); // Assuming this is the correct method to update the loaded pages table
        loadedPageTableView.refresh(); // Refresh the correct TableView
        updateUnloadedPageTable();
        updateStats();
        unloadedPageTableView.refresh(); // Refresh the unloaded page table
        updateMemoryGrid();
    }

    private HBox createPageReplacementPanel(){
        Label processIDLabel = new Label("Process ID:");
        TextField processIDField = new TextField();
        processIDField.setPrefWidth(100);

        Label segmentNumLabel = new Label("Segment Number " + ":");
        TextField segmentNumField = new TextField();
        segmentNumField.setPrefWidth(50);

        Label pageNumLabel = new Label("Page Number " + ":");
        TextField pageNumField = new TextField();
        pageNumField.setPrefWidth(50);

        Button pageReplaceButton = new Button("Replace Page");
        pageReplaceButton.setOnAction(event -> handlePageReplacement(processIDField.getText(), segmentNumField.getText(),
                pageNumField.getText()));

        HBox PageReplacementPanel = new HBox(10, processIDLabel, processIDField, segmentNumLabel, segmentNumField
                , pageNumLabel, pageNumField, pageReplaceButton);
        PageReplacementPanel.setAlignment(Pos.CENTER);

        return PageReplacementPanel;
    }

    private void handlePageReplacement(String processId, String segmentNum, String pageNum){
        //Does not exist the Process
        PCB process = os.getProcesses().get(processId);

        //Process existed
        try {
            if(process == null) {
                throw new IllegalArgumentException("Operation failed, process " + processId + " does not exist");
            }
            if(Integer.parseInt(segmentNum) < 0 || Integer.parseInt(segmentNum) > 3){
                throw new IllegalArgumentException("Invalid segment number");
            }
            if(Integer.parseInt(pageNum) < 0 || Integer.parseInt(pageNum) > 15){
                throw new IllegalArgumentException("Invalid page number");
            }
        } catch (IllegalArgumentException e) {
            showAlert("Invalid input", e.getMessage());
            return;
        }

        //Valid Input, execute replacePage()
        os.pageReplace(processId, Integer.parseInt(segmentNum), Integer.parseInt(pageNum));
        showAlert("Success", "page replacement successful: " + "page(" + pageNum + ")" + " in " +
                "segment(" + segmentNum + ")" +" is loaded.");
        updatePageTable(); // method to update the loaded pages table
        loadedPageTableView.refresh(); // Refresh the correct TableView
        updateUnloadedPageTable();
        updateStats();
        unloadedPageTableView.refresh(); // Refresh the unloaded page table
        updateMemoryGrid();

    }

    /*
    // Method to update the page table data
    private void updatePageTable() {
        ObservableList<PageTableEntry> data = FXCollections.observableArrayList();
        // Iterate over memory frames and update the list
        for (Frame frame : memory.getMemory()) {
            if (frame.isUsed()) {
                // Extract process ID, page number, segment number, and frame number
                String processId = frame.getProcessId();
                int pageNumber = frame.getPageNumber();
                int segmentNumber = frame.getSegmentNumber();
                int frameNumber = frame.getFrameNum();
                data.add(new PageTableEntry(processId, pageNumber, segmentNumber, frameNumber));
            }
        }
        pageTableView.setItems(data); // Update the TableView's items
    }
     */

    private void updatePageTable() {
        ObservableList<PageTableEntry> data = FXCollections.observableArrayList();

        // A way to get all active PCB instances, e.g., a list or map in OS class
        Collection<PCB> allPcbs = os.getAllActivePCBs(); // This method needs to be implemented in OS class

        for (PCB pcb : allPcbs) {
            for (int frameNum : pcb.residentSet) {
                Frame frame = memory.getFrame(frameNum);
                if (frame != null && frame.isUsed()) {
                    for (SegmentEntry segment : pcb.STable) {
                        for (PageEntry page : segment.PTable) {
                            if (page.frameNum == frameNum && page.load) {
                                data.add(new PageTableEntry(pcb.id, page.pageNum, segment.segmentNum, frameNum));
                            }
                        }
                    }
                }
            }
        }

        loadedPageTableView.setItems(data); // Update the TableView's items
    }


    private void updateUnloadedPageTable() {
        ObservableList<PageTableEntry> data = FXCollections.observableArrayList();

        // Assuming you have a method to get all active PCBs
        Collection<PCB> allPcbs = os.getAllActivePCBs();

        for (PCB pcb : allPcbs) {
            for (SegmentEntry segment : pcb.STable) {
                for (PageEntry page : segment.PTable) {
                    if (!page.load) { // Check if the page is not loaded
                        int frameNumber = -1; // Frame number is not applicable for unloaded pages
                        data.add(new PageTableEntry(pcb.id, page.pageNum, segment.segmentNum, frameNumber));
                    }
                }
            }
        }

        unloadedPageTableView.setItems(data); // Update the TableView's items
    }


    private void updateMemoryGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int frameIndex = i * GRID_SIZE + j;
                Frame frame = memory.getFrame(frameIndex);
                StackPane frameVisual = (StackPane) getNodeFromGridPane(memoryGrid, j, i);

                String label = frame != null && frame.isUsed() ? frame.getId() : "Free";
                Label frameLabel = (Label) frameVisual.getChildren().get(0);
                frameLabel.setText(label);
            }
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    // Method to request a random page replacement
        private void randomPageReplace() {
            Random random = new Random();
            try {
                Collection<PCB> allPcbs = os.getAllActivePCBs();
                PCB[] pcbArray = allPcbs.toArray(new PCB[0]);

                if (pcbArray.length == 0) {
                    showAlert("No active processes", "There are no active processes to replace pages.");
                    return;
                }
                PCB selectedPCB = pcbArray[random.nextInt(pcbArray.length)];

                if (selectedPCB.STable.length == 0) {
                    showAlert("No segments", "The selected process has no segments.");
                    return;
                }
                SegmentEntry selectedSegment = selectedPCB.STable[random.nextInt(selectedPCB.STable.length)];

                if (selectedSegment.PTable.length == 0) {
                    showAlert("No pages", "The selected segment has no pages.");
                    return;
                }
                int pageNum = random.nextInt(selectedSegment.PTable.length);

                os.pageReplace(selectedPCB.id, selectedSegment.segmentNum, pageNum);
                updatePageTable(); // A method to update the loaded pages table
                loadedPageTableView.refresh(); // Refresh the correct TableView
                updateUnloadedPageTable();
                updateStats();
                unloadedPageTableView.refresh(); // Refresh the unloaded page table
                updateMemoryGrid();

            } catch (Exception e) {
                showAlert("Error", "An error occurred during random page replacement: " + e.getMessage());
                e.printStackTrace();
            }
        }

    private VBox createStatsPanel() {
        // Initialize labels with default text
        totalRequestsLabel = new Label("Total Number of Requests: 0");
        totalPageFaultsLabel = new Label("Total Number of Page Faults: 0");

        // Update labels with current data
        updateStats();

        // Create a VBox to hold the stats
        VBox statsPanel = new VBox(10, totalRequestsLabel, totalPageFaultsLabel);
        statsPanel.setAlignment(Pos.CENTER_LEFT);
        statsPanel.setPadding(new Insets(10));

        return statsPanel;
    }

    private void updateStats() {
        totalRequestsLabel.setText("Total Number of Requests: " + os.getTotalRequest());
        totalPageFaultsLabel.setText("Total Number of Page Faults: " + os.getPageFault());
    }



    //    create a class for page table entries
// Page Table Entry class
class PageTableEntry {
    private SimpleStringProperty processId;
    private SimpleIntegerProperty pageNumber;
    private SimpleIntegerProperty segmentNumber;
    private SimpleIntegerProperty frameNumber;

    public PageTableEntry(String processId, int pageNumber, int segmentNumber, int frameNumber) {
        this.processId = new SimpleStringProperty(processId);
        this.pageNumber = new SimpleIntegerProperty(pageNumber);
        this.segmentNumber = new SimpleIntegerProperty(segmentNumber);
        this.frameNumber = new SimpleIntegerProperty(frameNumber);
    }

    // Getters and Setters...
}


    public static void main(String[] args) {
        launch(args);
    }
}
