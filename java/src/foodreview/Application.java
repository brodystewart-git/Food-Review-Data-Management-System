package foodreview;
/**
 * The main entry point for the Food Review System application.
 *
 * <p>This class contains the {@link #main(String[])} method, serving as the startup for the software. It initializes a
 * {@link ReviewSystem} view and controller to handle the application's runtime functionality.</p>
 *
 * @author Brody Stewart
 * <p>Course: CEN 3024 - Software Development 1</p>
 * <p>Assignment: DMS Project</p>
 * @version 0.9
 * @since 2026-03-27
 * @see ReviewSystem
 *
 */
public class Application {
    /**
     * Constructs a new Application object
     */
    public Application(){
        //Explicitly defined to make satisfy Javadoc warnings.
    }

    /**
     * Simple main method to initialize the program via {@link ReviewSystem}
     * @param args currently unused by the system.
     */
    public static void main(String[] args) {
        ReviewSystem sys = new ReviewSystem();
    }
}
