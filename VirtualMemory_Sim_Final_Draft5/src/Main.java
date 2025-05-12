public class Main {
    public static void main(String[] args) {
        // Check Java version
        String javaVersion = System.getProperty("java.version");
        if (javaVersion.compareTo("11") < 0) {
            System.err.println("Error: Java 11 or higher is required.");
            System.out.println("Please download and install a compatible Java version from https://www.oracle.com/java/technologies/javase-jdk11-downloads.html or use an OpenJDK distribution.");
            return;
        } else {
            System.out.println("Java version check passed. Your version: " + javaVersion);
        }

        // Check JavaFX
        try {
            Class.forName("javafx.application.Application");
            System.out.println("JavaFX runtime is available.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: JavaFX runtime is not available.");
            System.out.println("Please ensure JavaFX is properly set up in your IDE or download it from https://openjfx.io.");
            return;
        }

        // If both checks pass, attempt to launch the JavaFX application
        try {
            MemorySimulatorGUI.launch(MemorySimulatorGUI.class, args);
        } catch (Exception e) {
            System.err.println("Error: Unable to launch the GUI. There may be an issue with your setup.");
            e.printStackTrace();
            System.out.println("Please verify your JavaFX setup or check the error stack trace for more details.");
        }
    }
}
