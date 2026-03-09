/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 8th, 2026
 * Application.java
 * This application handles all "database" interactions. For now, this means it handles all interactions
 * with the text file and all data handling. This means it's the main program for the Model part of the MVC.
 * All methods in the program are for directly acting on data based on what the controller tells it to do.
 * Every time something is changed with the database array, it is saved to a text file.
 * The load, save and id generator methods are temporary, for the sake of accessing the text file.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class DatabaseHandler {
    private ArrayList<Review> database;
    private String path;

    // Simple constructor that gets the file path and loads the data from the file.
    public DatabaseHandler(String p){
        path = p;
        loadDatabase();
    }

    /*
     * The loadDatabase method loads data from the file indicated with the path variable.
     * It uses scanner to take a new line, break it into pieces and turn it into a review object.
     * These objects are stored in the database ArrayList for program use.
     */
    private void loadDatabase() {
        this.database = new ArrayList<>();
        File file = new File(path);

        try (Scanner scanner = new Scanner(file)){
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                try {
                    if (line.trim().isEmpty()) continue;
                    // Should be split into ID,NAME,RATING,CATEGORY,SUBTYPE,LOCATION,DATE
                    String[] parts = line.split("-", 7);
                    if (parts.length < 7)
                        throw new Exception ("Missing data fields.");
                    if (parts.length == 7) {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        int rating = Integer.parseInt(parts[2]);
                        Category category = Category.valueOf(parts[3]);
                        String subtype = parts[4];
                        if(subtype.equals("null"))
                            subtype = null;
                        String location = parts[5];
                        if(location.equals("null"))
                            location = null;
                        LocalDate date = LocalDate.parse(parts[6]);
                        Review r = new Review(id, name, rating, category, subtype, location, date);
                        int added = this.addReview(r, false);
                        if (added == 0)
                            System.out.println("Error, ID on line " + i +" is incorrect. Skipping line.");
                    }
                }catch(Exception e) {
                    System.out.println("Error parsing line: " + i + ". Error: " + e.getMessage() + ".\n Skipping....");
                }
            }
        }catch(FileNotFoundException e) {
            System.out.println("Error. Could not access the file at: " + path);
        }
    }

    // The saveDatabase method saves the current database ArrayList to the path text file, using the toString method.
    private void saveDatabase() {
        try(PrintWriter writer = new PrintWriter(new FileWriter(path))){
            for(Review r: database) {
                if (r!= null) {
                    writer.println(r.toString());
                }
            }
            System.out.println("Successfully updated file.");
        }catch (IOException e) {
            System.out.println("Error writing to file " + e.getMessage());
        }
    }
    // The idGenerator method generated the next free ID to be used in the database ArrayList.
    public int idGenerator() {
        int id = 1;
        boolean idFound = true;
        while(idFound) {
            idFound = false;
            for (Review r: database) {
                if(r != null && r.getID() == id) {
                    idFound = true;
                    id++;
                    break;
                }
            }
        }
        return id;
    }

    /*
     * The addReview method simply adds the review to the database, then calls the database to be saved.
     * It returns 0 if it finds a matching ID in the system, meaning that something went wrong with the ID field.
     * It returns 1 if it was successfully added.
    */
    public int addReview(Review r, boolean save) {
        for(Review d: database) {
            if(r.getID() == d.getID()) {
                return 0;  // Matching ID already in system
            }
        }
        database.add(r);
        if (save)
            saveDatabase();
        return 1;
    }

    /*
     * The remReview method takes an ID and tries to find it in the system.
     * If found, it removes it from the database ArrayList then saves it to file.
     * It returns 0 if it removes nothing.
     * It returns 1 if it was successfully removed.
     */
    public int remReview(int id) {
        for(int i = 0; i < database.size(); i++) {
            Review r = database.get(i);
            if(r.getID() == id) {
                database.remove(i);
                saveDatabase();
                return 1;
            }
        }
        return 0;
    }

    /*
     * The updateReview method sets the review in the database ArrayList at the given ID to the given review.
     * When replaced, it updates the text file.
     * It returns 0 if nothing is done.
     * It returns 1 if it was successfully updated.
     */
    public int updateReview(int id, Review r) {
        for(int i = 0; i < database.size(); i++) {
            Review d = database.get(i);
            if(d.getID() == id) {
                database.set(i, r);
                saveDatabase();
                return 1;
            }
        }
        return 0;
    }

    /*
     * This findReview method is for finding a review only by name.
     * It takes a String name and searches the database for any matching reviews with that name.
     * It adds all matching reviews to an arraylist and returns it.
     * If nothing is found, it returns null.
     */
    public ArrayList<Review> findReview(String name) {
        if(database.isEmpty()) {
            return null;
        }
        boolean found = false;
        ArrayList<Review> searchList = new ArrayList<>();
        for(int i = 0; i < database.size(); i++) {
            Review r = database.get(i);
            if(r.getName().equals(name)) {
                found = true;
                searchList.add(r);
            }
        }
        if(found)
            return searchList;
        return null;
    }

    /*
     * This findReview method is for finding a review by name and subtype.
     * It takes a String name and String subtype, searching the database for any matching reviews with that data.
     * It adds all matching reviews to an arraylist and returns it.
     * If nothing is found, it returns null.
     */
    public ArrayList<Review> findReview(String name, String subtype) {
        if(database.isEmpty()) {
            return null;
        }
        boolean found = false;
        ArrayList<Review> searchList = new ArrayList<>();
        for(int i = 0; i < database.size(); i++) {
            Review r = database.get(i);
            if(r.getName().equals(name) && r.getSubtype().equals(subtype)) {
                found = true;
                searchList.add(r);
            }
        }
        if(found)
            return searchList;
        return null;
    }

    /*
     * This findReview method is for finding a review by ID.
     * It takes an integer ID and searches the database for any matching reviews with that data.
     * It returns the matching Review.
     * If nothing is found, it returns null.
     */
    public Review findReview(int id) {
        if(database.isEmpty()) {
            return null;
        }
        for(int i = 0; i < database.size(); i++) {
            Review r = database.get(i);
            if(r.getID() == id) {
                return r;
            }
        }
        return null;
    }

    /*
     * The getAverage method gets the average reviews from a given Category variable.
     * It loops through the database ArrayList,
     * counting how many in the category exist and adds their score to the total.
     * If nothing is found, returns -1.
     * If found, it returns the average (total/count).
     */
    public double getAverage(Category cat) {
        double total = 0;
        double count = 0;
        boolean found = false;
        for(int i = 0; i < database.size(); i++) {
            Review r = database.get(i);
            if(r.getCategory() == cat) {
                found = true;
                total += (double) r.getRating();
                count++;
            }
        }
        if (found) {
            return (total/count);
        }
        return -1.0;
    }

    public ArrayList<Review> getAll() {
        return database;
    }
}
