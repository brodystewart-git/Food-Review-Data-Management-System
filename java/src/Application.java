/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 26th, 2026
 * Application.java
 * This application launches the DMS Review System program, currently done this way for Phase 1 of the project.
 * It does this by calling the actual program's system class, the Review System.
 * Usage of the program will be explained in the comments of that file.
 */
public class Application {

    /*
    * This is the main and only function of this file.
    * It sets up the program by getting a file to save a read from.
    * Then, it runs the program until the user exits in some way.
     */
    public static void main(String[] args) {
        ReviewSystem sys = new ReviewSystem();
        sys.initGui();
    }
}
