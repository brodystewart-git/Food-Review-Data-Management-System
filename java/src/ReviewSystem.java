/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 8th, 2026
 * ReviewSystem.java
 * This application is the main system (or the View & Controller) of the program.
 * The program can be called to load a file (loadFile()) based on a file path.
 * Once loaded, can be used to display an interactable menu (initGUI()).
 * Users can then interact with the program by adding reviews, removing reviews, finding reviews, updating reviews,
 * getting the average review score of a category, display all reviews and exit the program.
 * The program will always close after being run, so the application class is required to make it run repeatedly.
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class ReviewSystem {
    DatabaseHandler dbHandler;
    Scanner scanner;
    String path;

    //Simple constructor that initiates scanner.
    public ReviewSystem() {
        scanner = new Scanner(System.in);
    }

    /*
     * Temporary function for text file loading.
     * The user is prompted to give a path with quotation marks around it of a text file.
     * If correct, it will pass the file to the Database Handler object.
     * Otherwise, it will tell the user to try again
     * and call loadFile() recursively until it's correct or 'exit' is typed.
     */
    public int loadFile() {
        int v = -1;
        System.out.print("Please type the path of your current text file (between two quotation marks): "		);
        path = scanner.nextLine();
        path = path.replace("\"", "");
        if (path.equals("exit")) {
            scanner.close();
            return v;
        }
        File checkFile = new File(path);
        if (checkFile.exists() && checkFile.isFile() && path.toLowerCase().endsWith(".txt")) {
            System.out.println("Text file found. Sending to Database Handler.");
            dbHandler = new DatabaseHandler(path);
            v = 0;
        }else {
            System.out.println("File does not exist or is not a text file. Try again or type 'exit' to quit.");
            v = loadFile();
        }
        return v;
    }

    /*
     * initGUI() is the main menu of the program.
     * Right now, that means it's a text menu. Later on, this will actually initialize the real GUI.
     * In the case a program calls this before they get a correct path, this method will immediately return false.
     * This method displays a menu then asks for the user's input.
     * If they type 1-6, it calls the necessary method related to their choice.
     * If they type 7, it returns false to say the user wants to exit.
     */
    public boolean initGui() {
        boolean w = true;
        if (path == null){
            return false;
        }
        System.out.print(
                "\n\n\tFood Review System\n"
                        + "---------------------------\n"
                        + "1) Add Review\n"
                        + "2) Remove Review\n"
                        + "3) Find Review\n"
                        + "4) Update Review\n"
                        + "5) Average Reviews of Category\n"
                        + "6) Display All Reviews\n"
                        + "7) Exit program\n"
                        + "Please type the number of the menu item: "
        );
        String input = scanner.nextLine();
        switch (input) {
            case "1":
                newReviewHandler();
                break;
            case "2":
                remReviewHandler();
                break;
            case "3":
                findReviewHandler();
                break;
            case "4":
                updReviewHandler();
                break;
            case "5":
                avgReviewHandler();
                break;
            case "6":
                dispAllHandler();
                break;
            case "7":
                scanner.close();
                w = false;
        }
        return w;
    }

    /*
     * The newReviewHandler is for adding a review.
     * It calls for the program to create a review.
     * If it's successfully created, it sends the review to the Database Handler object to be saved to the file.
     * In later development phases, this will be a button event handler instead.
     * * If the database fails to do its job, it will let the user know.
     */
    private void newReviewHandler() {
        System.out.println("\tAdding Review");
        Review r = createReview(-1);
        int result = dbHandler.addReview(r, true);
        if (result == 1) {
            System.out.println("Successfully added Review:" );
            r.print();
        }else {
            System.out.println("Failed to add to database, returning to main menu.");
        }
    }

    /*
     * The remReviewHandler is for removing a review.
     * It calls for the program to remove a review.
     * It gets the ID of the review, then sends that information to the Database Handler to do its job.
     * In later development phases, this will be a button event handler instead.
     * If the database fails to do its job, it will let the user know.
     */
    private void remReviewHandler() {
        System.out.println("\tRemoving Review");
        boolean validCheck = true;
        int ID = -1;
        Review r = null;
        while(validCheck) {
            validCheck = false;
            System.out.println("Please enter an ID or type 'exit': ");
            String temp = scanner.nextLine();
            if (temp.equals("exit")) {
                return;
            }
            try{
                ID = Integer.parseInt(temp);
                if (ID < 1)
                    throw new NumberFormatException("Out of Range");
            }catch (NumberFormatException e) {
                validCheck = true;
                ID = -1;
                System.out.println("Invalid input. Must be a number above 0.");
            }
            r = dbHandler.findReview(ID);
            if (r == null) {
                System.out.println("Error. Object with that ID doesn't exist in the system.");
                validCheck = true;
            }
        }
        System.out.println("Removing Review: ");
        r.print();
        int result = dbHandler.remReview(ID);
        if (result == 1) {
            System.out.println("Successfully removed Review at ID: " + ID);
        }else {
            System.out.println("Failed to remove from database, returning to main menu.");
        }
    }

    /*
     * The avgReviewHandler is for getting the average review score of a category.
     * It calls for the program to get the average review.
     * It gets the category, then sends that information to the Database Handler to do its job.
     * In later development phases, this will be a button event handler instead.
     * If the database fails to do its job, it will let the user know.
     */
    private void avgReviewHandler() {
        boolean validCheck = true;
        Category category = null;
        System.out.println("\tAverage Review by Category");
        while(validCheck) {
            validCheck = false;
            System.out.println("\tCategories");
            for (Category cat : Category.values()) {
                System.out.println(cat);
            }
            System.out.print("Enter Category (exactly as shown): ");
            String temp = scanner.nextLine();
            try {
                category = Category.valueOf(temp);
            }catch (IllegalArgumentException e) {
                validCheck = true;
                category = null;
                System.out.println("Invalid input. Must be one of the given categories and written exactly as displayed.");
            }
        }
        double result = dbHandler.getAverage(category);
        if (result < 0) {
            System.out.println("Failed to get average, might be no reviews in category. Returning to main menu.");
        }else {
            System.out.println("Average Review Score in " + category + " is " + result);
        }

    }

    /*
     * The findReviewHandler is for finding a review by name and/or subtype.
     * It gets the food's name then asks if the user would like to add a food subtype.
     * If they do, it calls the Database Handler's findReview function that takes two strings for both.
     * If they don't, it calls the Database Handler's findReview function that takes one string for name only.
     * In the instance it finds reviews, the Database Handler will
     * return a list of reviews matching the search criteria, and it will be displayed to the user.
     * In later development phases, this will be a button event handler instead.
     * If the database fails to do its job, it will let the user know.
     */
    private void findReviewHandler() {
        boolean validCheck = true;
        boolean wantSubtype = false;
        String name = null;
        String subtype = null;

        System.out.println("\tFind Review");
        while(validCheck) {
            System.out.print("Enter Food Name: ");
            name = scanner.nextLine();
            int l = name.length();
            if (l < 2 || l > 100 || name.isBlank() || name.matches(".*[^a-zA-Z ].*")) {
                System.out.println("Invalid input. Make sure it's between 2 and 100 characters and only contains letters.");
                name = null;
            }else {
                validCheck = false;
            }
        }
        validCheck = true;
        while(validCheck) {
            System.out.print("Would you like to add a subtype? (Y/N): ");
            String temp = scanner.nextLine();
            if (temp.equals("Y")) {
                System.out.print("Enter Subtype: ");
                subtype = scanner.nextLine();
                int l = subtype.length();
                if (l < 2 || l > 50 || subtype.isBlank() || subtype.matches(".*[^a-zA-Z ].*")) {
                    System.out.println("Invalid input. Make sure it's between 2 and 50 characters and only contains letters.");
                    subtype = null;
                }else {
                    wantSubtype = true;
                    validCheck = false;
                }
            }else if (temp.equals("N")){
                validCheck = false;
                break;
            }else {
                System.out.println("Invalid input. Type 'Y' or 'N'.");
                continue;
            }
        }
        ArrayList<Review> result = null;
        if(wantSubtype) {
            result = dbHandler.findReview(name, subtype);
        }else {
            result = dbHandler.findReview(name);
        }
        if (result == null) {
            System.out.println("Failed to find any Reviews matching that information. Returning to main menu.");
        }else {
            System.out.println("\tReviews matching that information: ");
            for(Review r: result) {
                r.print();
                System.out.println("----------------");
            }
        }
    }

    /*
     * The dispAllHandler is for displaying all reviews in the system.
     * It simply calls for the Database Handler to give it all of its reviews in its array.
     * Then, it loops through them and prints them out to the user.
     */
    private void dispAllHandler() {
        ArrayList<Review> reviews;
        reviews = dbHandler.getAll();
        System.out.println("\tDisplaying All Reviews: ");
        for(Review r: reviews) {
            r.print();
            System.out.println("-------------------");
        }
    }

    /*
     * The updReviewHandler is for updating a review by its ID.
     * It gets the ID from the user then has the Database Handler search for the ID in the program.
     * If it's found, it will show the review's information to the user.
     * If not, it will alert the user.
     * When found, the user will be prompted to input data as if it were a new review.
     * They are told to simply repeat the information they would like to be the same.
     * This is because later phases will simply have text boxes, so there's no need to make a complex update system.
     * If all is input correctly, the Database Handler will be prompted to update the database.
     */
    private void updReviewHandler() {
        System.out.println("\tUpdating Review");
        boolean validCheck = true;
        int ID = -1;
        Review r = null;
        while(validCheck) {
            validCheck = false;
            System.out.println("Please enter an ID or type 'exit': ");
            String temp = scanner.nextLine();
            if (temp.equals("exit")) {
                return;
            }
            try{
                ID = Integer.parseInt(temp);
                if (ID < 1)
                    throw new NumberFormatException("Out of Range");
            }catch (NumberFormatException e) {
                validCheck = true;
                ID = -1;
                System.out.println("Invalid input. Must be a number above 0.");
            }
            r = dbHandler.findReview(ID);
            if (r == null) {
                System.out.println("Error. Object with that ID doesn't exist in the system.");
                validCheck = true;
            }
        }
        System.out.println("Review to be updated: ");
        r.print();
        System.out.println("\n-----------------------------\n");
        //Updating portion.
        System.out.println("\tUpdating Review");
        System.out.println("Please input information as if you were making a new review. Anything you want unchanged, retype as it was before.");
        Review newR = createReview(ID);
        int result = dbHandler.updateReview(ID, newR);
        if (result == 1) {
            System.out.println("Successfully updated review with ID : " + ID);
        }else {
            System.out.println("Failed to update database, returning to main menu.");
        }
    }

    /*
     * createReview is a temporary method for Phase 1.
     * This method was created to supplement the fact that there are no text-boxes in the GUI-less phase 1.
     * It will not exist in other phases.
     * For now, it takes an integer ID and prompts the user for information about their Review.
     * It ensures input is correct and then creates a new review.
     * If the ID was -1, it generates a new ID to be used for that review.
     * Otherwise, it keeps the same ID as that means it is actually going to be updating an old review.
     * It then returns the review.
     */
    private Review createReview(int ID) {
        String name = null;
        int rating = -1;
        Category category = null;
        String subtype = null;
        String location = null;
        LocalDate date = null;
        Review rev = null;
        boolean wantLocation = false;
        boolean wantSubtype = false;
        boolean validCheck = true;
        //Get name
        while(validCheck) {
            System.out.print("Enter Food Name: ");
            name = scanner.nextLine();
            int l = name.length();
            if (l < 2 || l > 100 || name.isBlank() || name.matches(".*[^a-zA-Z ].*")) {
                System.out.println("Invalid input. Make sure it's between 2 and 100 characters and only contains letters.");
                name = null;
            }else {
                validCheck = false;
            }
        }

        //Get Rating
        validCheck = true;
        while(validCheck) {
            validCheck = false;
            System.out.print("Enter Food Rating (1-10): ");
            String temp = scanner.nextLine();
            try{
                rating = Integer.parseInt(temp);
                if (rating < 1 || rating > 10)
                    throw new NumberFormatException("Out of Range");
            }catch (NumberFormatException e) {
                validCheck = true;
                rating = -1;
                System.out.println("Invalid input. Must be a number between 1 and 10 (inclusive).");
            }
        }
        //Get Category
        validCheck = true;
        while(validCheck) {
            validCheck = false;
            System.out.println("\tCategories");
            for (Category cat : Category.values()) {
                System.out.println(cat);
            }
            System.out.print("Enter Category (exactly as shown): ");
            String temp = scanner.nextLine();
            try {
                category = Category.valueOf(temp);
            }catch (IllegalArgumentException e) {
                validCheck = true;
                category = null;
                System.out.println("Invalid input. Must be one of the given categories and written exactly as displayed.");
            }
        }
        //Get Subtype
        validCheck = true;
        while(validCheck) {
            System.out.print("Would you like to add a subtype? (Y/N): ");
            String temp = scanner.nextLine();
            if (temp.equals("Y")) {
                System.out.print("Enter Subtype: ");
                subtype = scanner.nextLine();
                int l = subtype.length();
                if (l < 2 || l > 50 || subtype.isBlank() || subtype.matches(".*[^a-zA-Z ].*")) {
                    System.out.println("Invalid input. Make sure it's between 2 and 50 characters and only contains letters.");
                    subtype = null;
                }else {
                    wantSubtype = true;
                    validCheck = false;
                }
            }else if (temp.equals("N")){
                validCheck = false;
                break;
            }else {
                System.out.println("Invalid input. Type 'Y' or 'N'.");
                continue;
            }
        }
        //Get Location
        validCheck = true;
        while(validCheck) {
            System.out.print("Would you like to add a location? (Y/N): ");
            String temp = scanner.nextLine();
            if (temp.equals("Y")) {
                System.out.print("Enter Location: ");
                location = scanner.nextLine();
                int l = location.length();
                if (l < 2 || l > 100 || location.isBlank()) {
                    System.out.println("Invalid input. Make sure it's between 2 and 100 characters and only contains letters.");
                    location = null;
                }else {
                    wantLocation = true;
                    validCheck = false;
                }
            }else if (temp.equals("N")){
                validCheck = false;
                break;
            }else {
                System.out.println("Invalid input. Type 'Y' or 'N'.");
                continue;
            }
        }
        //Get Date
        validCheck = true;
        while(validCheck) {
            validCheck = false;
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String temp = scanner.nextLine();
            try {
                date = LocalDate.parse(temp);
            }catch (DateTimeParseException e) {
                validCheck = true;
                System.out.println("Invalid input. Make sure it's YYYY-MM-DD, such as 2020-05-03.");
            }
        }
        //Completed input gathering, making review and passing to database.
        if (ID == -1)
            ID  = dbHandler.idGenerator();
        if (wantLocation && wantSubtype) {
            rev = new Review(ID, name, rating, category, subtype, location, date);
        }else if (wantLocation) {
            rev = new Review(ID, name, rating, category, date, location);
        }else if (wantSubtype) {
            rev = new Review(ID, name, rating, category, subtype, date);
        }else {
            rev = new Review(ID, name, rating, category, date);
        }
        return rev;
    }
}
