/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 18th, 2026
 * ReviewSystem.java
 * This application is the main system (or the View & Controller) of the program.
 * The program can be called to load a file (loadFile()) based on a file path.
 * Once loaded, can be used to display an interactable menu (initGUI()).
 * Users can then interact with the program by adding reviews, removing reviews, finding reviews, updating reviews,
 * getting the average review score of a category, display all reviews and exit the program.
 * The program will always close after being run, so the application class is required to make it run repeatedly.
 */

import java.util.ArrayList;
import java.util.Locale;
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
        Review r = newReviewDialogue();
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
            if (temp.equalsIgnoreCase("Y")) {
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
            }else if (temp.equalsIgnoreCase("N")){
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
        if (reviews == null){
            System.out.println("Nothing in the database. Please add reviews first.");
            return;
        }
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
        Review newR = updateReviewDialogue(r);
        int result = dbHandler.updateReview(ID, newR);
        if (result == 1) {
            System.out.println("Successfully updated review with ID : " + ID);
        }else {
            System.out.println("Failed to update database, returning to main menu.");
        }
    }

    /*
     * updateReviewDialogue is a temporary method for Phase 1.
     * This method was created to supplement the fact that there are no text-boxes in the GUI-less phase 1.
     * It will not exist in other phases.
     * For now, it takes an integer ID and finds a review to be updated. It then displays this data.
     * The User can then choose which information about the review they'd like to change.
     * It then returns the review.
     */
    private Review updateReviewDialogue(Review r){
        boolean running = true;
        Review updated = r;
        int ID = r.getID();
        String name = r.getName();
        int rating = r.getRating();
        Category cat = r.getCategory();
        String subtype = r.getSubtype();
        String location = r.getLocation();
        LocalDate date = r.getDate();
        while (running){
            updated.print();
            System.out.println("\n\t   Menu ");
            System.out.println(
                    "Name\t\tRating\n" +
                    "Category\tSubtype\n" +
                    "Location\tDate\n" +
                    "Please type one you'd like to change or type 'exit' to save changes: "
            );
            String temp = scanner.nextLine();
            temp = temp.toUpperCase();
            boolean validCheck = true;
            switch (temp) {
                case "NAME":
                    name = nameValidation(name);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "RATING":
                    rating = ratingValidation(rating);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "CATEGORY":
                    cat = categoryValidation(cat);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "SUBTYPE":
                    subtype = subtypeValidation(subtype);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "LOCATION":
                    location = locationValidation(location);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "DATE":
                    date = dateValidation(date);
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
                case "EXIT":
                    running = false;
                    updated = createReview(ID, name, rating, cat, subtype, location, date);
                    break;
            }
        }
        return updated;
    }

    /*
     * newReviewDialogue is a temporary method for Phase 1.
     * This method was created to supplement the fact that there are no text-boxes in the GUI-less phase 1.
     * It will not exist in other phases.
     * For now, it takes an integer ID and prompts the user for information through the validation methods.
     * It generates a new ID to be used for that review.
     * It then returns the review.
     */
    private Review newReviewDialogue() {
        String name = null;
        int rating = -1;
        Category category = null;
        String subtype = null;
        String location = null;
        LocalDate date = null;
        Review rev = null;

        name = nameValidation(null);
        rating = ratingValidation(-1);
        category = categoryValidation(null);
        subtype = subtypeValidation(null);
        location = locationValidation(null);
        date = dateValidation(null);
        int ID  = dbHandler.idGenerator();
        rev = createReview(ID, name, rating, category, subtype, location, date);
        return rev;
    }
    /* createReview is for creating a review object.
     * It's made to reduce redundancy in having to check which constructor to use, by having a method that does it.
     * It returns the review object that is created.
     */
    private Review createReview(int ID, String name, int rating, Category cat, String subtype, String location, LocalDate date){
        Review rev;
        if (location != null && subtype != null) {
            rev = new Review(ID, name, rating, cat, subtype, location, date);
        }else if (location != null) {
            rev = new Review(ID, name, rating, cat, date, location);
        }else if (subtype != null) {
            rev = new Review(ID, name, rating, cat, subtype, date);
        }else {
            rev = new Review(ID, name, rating, cat, date);
        }
        return rev;
    }

    /* nameValidation is a simple function which takes input in regard to the Name field of the review object.
     * It checks if the text is valid: 2-100 characters and contains only letters.
     * Returns the validated string.
     */
    private String nameValidation(String input){
        String name = input;
        boolean validCheck = true;
        while(validCheck) {
            System.out.print("Enter Food Name: ");
            name = scanner.nextLine();
            int l = name.length();
            if (l < 2 || l > 100 || name.isBlank() || name.matches(".*[^a-zA-Z ].*")) {
                System.out.println("Invalid input. Make sure it's between 2 and 100 characters and only contains letters.");
                name = input;
            }else {
                validCheck = false;
            }
        }
        return name;
    }

    /* ratingValidation is a simple function which takes input in regard to the rating field of the review object.
     * It checks if the input is valid: an integer from 1-10.
     * Returns the validated integer.
     */
    private int ratingValidation(int input){
        int rating = input;
        boolean validCheck = true;
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
                rating = input;
                System.out.println("Invalid input. Must be a number between 1 and 10 (inclusive).");
            }
        }
        return rating;
    }

    /* categoryValidation is a simple function which takes input in regard to the category field of the review object.
     * It checks if the input is valid: matches a category.
     * Returns the validated category.
     */
    private Category categoryValidation(Category input){
        Category category = input;
        boolean validCheck = true;
        while(validCheck) {
            validCheck = false;
            System.out.println("\tCategories");
            for (Category cat : Category.values()) {
                System.out.println(cat);
            }
            System.out.print("Enter Category: ");
            String temp = scanner.nextLine();
            temp = temp.toUpperCase();
            try {
                category = Category.valueOf(temp);
            }catch (IllegalArgumentException e) {
                validCheck = true;
                category = input;
                System.out.println("Invalid input. Must be one of the given categories and written exactly as displayed.");
            }
        }
        return category;
    }

    /* subtypeValidation is a simple function which takes input in regard to the subtype field of the review object.
     * It checks if the input is valid: 2-50 characters, contains only letters.
     * Returns the validated subtype or null if they don't want one.
     */
    private String subtypeValidation(String input){
        String subtype = input;
        boolean  validCheck = true;
        boolean wantSubtype = false;
        while(validCheck) {
            System.out.print("Would you like to add a subtype? (Y/N): ");
            String temp = scanner.nextLine();
            if (temp.equalsIgnoreCase("Y")) {
                System.out.print("Enter Subtype: ");
                subtype = scanner.nextLine();
                int l = subtype.length();
                if (l < 2 || l > 50 || subtype.isBlank() || subtype.matches(".*[^a-zA-Z ].*")) {
                    System.out.println("Invalid input. Make sure it's between 2 and 50 characters and only contains letters.");
                    subtype = input;
                }else {
                    wantSubtype = true;
                    validCheck = false;
                }
            }else if (temp.equalsIgnoreCase("N")){
                validCheck = false;
                break;
            }else {
                System.out.println("Invalid input. Type 'Y' or 'N'.");
                continue;
            }
        }
        return subtype;
    }

    /* locationValidation is a simple function which takes input in regard to the location field of the review object.
     * It checks if the input is valid: 2-100 characters and isn't blank.
     * Returns the validated location or null if they don't want one.
     */
    private String locationValidation(String input){
        String location = input;
        boolean wantLocation = false;
        boolean validCheck = true;
        while(validCheck) {
            System.out.print("Would you like to add a location? (Y/N): ");
            String temp = scanner.nextLine();
            if (temp.equalsIgnoreCase("Y")) {
                System.out.print("Enter Location: ");
                location = scanner.nextLine();
                int l = location.length();
                if (l < 2 || l > 100 || location.isBlank()) {
                    System.out.println("Invalid input. Make sure it's between 2 and 100 characters and only contains letters.");
                    location = input;
                }else {
                    wantLocation = true;
                    validCheck = false;
                }
            }else if (temp.equalsIgnoreCase("N")){
                validCheck = false;
                break;
            }else {
                System.out.println("Invalid input. Type 'Y' or 'N'.");
                continue;
            }
        }
        return location;
    }

    /* dateValidation is a simple function which takes input in regard to the date field of the review object.
     * It checks if the input is valid: Meets the format of LocalDate (YYYY-MM-DD), ensures it's a valid date.
     * Returns the validated date.
     */
    private LocalDate dateValidation(LocalDate input){
        LocalDate date = input;
        boolean validCheck = true;
        while(validCheck) {
            validCheck = false;
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String temp = scanner.nextLine();
            try {
                date = LocalDate.parse(temp);
                LocalDate today = LocalDate.now();
                if (date.isAfter(today)){
                    throw new java.time.format.DateTimeParseException("Invalid date value", temp, 0);
                }
            }catch (DateTimeParseException e) {
                validCheck = true;
                date = input;
                System.out.println("Invalid input. Make sure it's YYYY-MM-DD, such as 2020-05-03.");
            }
        }
        return date;
    }
}
