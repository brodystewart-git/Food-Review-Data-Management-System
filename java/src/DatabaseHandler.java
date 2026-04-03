/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * April 3rd, 2026
 * Application.java
 * This application handles all database interactions. This means that it will connect to a MySQL database
 * with the given information from the ReviewSystem.
 * All methods in the program are for directly acting on data based on what the controller tells it to do.
 * Every time something is changed with the database array, it is saved to a text file.
 * The load, save and id generator methods are temporary, for the sake of accessing the text file.
 * It adds, removes, finds and updates reviews in the database.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.sql.*;

public class DatabaseHandler {
    private Connection connection;
    private String serverAddress;
    private String username;
    private String password;

    // Simple constructor that gets the MySQL database information.
    public DatabaseHandler(String address, String user, String pass){
        this.serverAddress = address;
        this.username = user;
        this.password = pass;
    }

    /* This is the connect method. Its task is to connect a MySQL database using the information provided by the user.
     * It concatenates a string of server address to a real mysql address
     * and attempts to connect using the username and password.
     * If successful, it creates a food review database if there isn't one in the schema.
     * Similarly, it creates a review table with all necessary values if there isn't one in the database.
     * If it fails to connect or do any of these functions, it returns false. Otherwise, returns true.
     */
    public boolean connect(){
        try{
            String url = "jdbc:mysql://" + serverAddress;
            Connection con1 = DriverManager.getConnection(url, username, password);
            Statement s = con1.createStatement();
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS food_review_project");
            s.executeUpdate("USE food_review_project");

            String createTableSQL =
                    "CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INT PRIMARY KEY," +
                    "name VARCHAR(255)," +
                    "rating INT," +
                    "category VARCHAR(50)," +
                    "subtype VARCHAR(100)," +
                    "location VARCHAR(255)," +
                    "date DATE)";
            s.executeUpdate(createTableSQL);
            this.connection = con1;
            return true;
        }catch(SQLException e){
            System.err.println("Error connecting to database: " + e.getMessage());
            return false;
        }
    }

    /* This is the idGenerator method. It returns the next free int ID.
     * It asks the database for the highest id value and adds one, returning it.
     * If this fails, it returns -1.
     */
    public int idGenerator() {
        String query = "SELECT MAX(id) FROM reviews";
        try (Statement s = connection.createStatement()){
            ResultSet rs = s.executeQuery(query);
            if(rs.next())
                return rs.getInt(1) + 1;
        }catch (SQLException e){
            System.err.println("Error generating id: " + e.getMessage());
            return -1;
        }
        return -1;
    }

    /*
     * The addReview method simply adds a given review object to the database.
     * It runs a query to insert the review into the database using the review's variables.
     * It returns 0 if it fails to be added.
     * It returns 1 if it was successfully added.
    */
    public int addReview(Review r) {
        String query = "INSERT INTO reviews (id, name, rating, category, subtype, location, date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement s = connection.prepareStatement(query)){
            s.setInt(1, r.getID());
            s.setString(2, r.getName());
            s.setInt(3, r.getRating());
            s.setString(4, r.getCategory().name());
            if(r.getSubtype() != null){
                s.setString(5,r.getSubtype());
            }else{
                s.setNull(5, Types.VARCHAR);
            }
            if(r.getLocation() != null){
                s.setString(6,r.getLocation());
            }else{
                s.setNull(6, Types.VARCHAR);
            }
            s.setDate(7, java.sql.Date.valueOf(r.getDate()));
            s.executeUpdate();
            return 1;
        }catch (SQLException e){
            System.err.println("Error adding review: " + e.getMessage());
            return 0;
        }
    }

    /*
     * The remReview method takes an ID of a review and tried to remove that review from the database.
     * It runs a query to delete the review at the given id on the database.
     * It returns 0 if it fails.
     * It returns 1 if it was successfully removed.
     */
    public int remReview(int id) {
      String query = "DELETE FROM reviews WHERE id = ?";

      try (PreparedStatement s = connection.prepareStatement(query)){
          s.setInt(1,id);
          int rows = s.executeUpdate();
          if(rows > 0){
              return 1;
          }
          return 0;
      }catch (SQLException e){
          System.err.println("Error removing review: " + e.getMessage());
          return 0;
      }
    }


    /*
     * The updateReview method updates a review at a given ID with the given Review object.
     * It runs a query on the database to update the review with the Review r's information at the given ID.
     * It returns 0 if it fails.
     * It returns 1 if it was successfully updated.
     */
    public int updateReview(int id, Review r) {
        String query = "UPDATE reviews SET name = ?, rating = ?, category = ?, subtype = ?, location = ?, date = ? WHERE id = ?";

        try(PreparedStatement s = connection.prepareStatement(query)){
            s.setString(1, r.getName());
            s.setInt(2, r.getRating());
            s.setString(3, r.getCategory().name());
            if(r.getSubtype() != null){
                s.setString(4,r.getSubtype());
            }else{
                s.setNull(4, Types.VARCHAR);
            }
            if(r.getLocation() != null){
                s.setString(5,r.getLocation());
            }else{
                s.setNull(5, Types.VARCHAR);
            }
            s.setDate(6, java.sql.Date.valueOf(r.getDate()));
            s.setInt(7, id);

            int rows = s.executeUpdate();
            if (rows > 0){
                return 1;
            }
            return 0;
        }catch (SQLException e){
            System.err.println("Error updating review: " + e.getMessage());
            return 0;
        }
    }

    /*
     * This findReview method is for finding a review only by name.
     * It takes a String name and searches the database for any matching reviews with that name.
     * It adds all matching reviews to an arraylist and returns it.
     * If nothing is found, it returns null.
     */
    public ArrayList<Review> findReview(String name) {
        ArrayList<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE name = ?";

        try(PreparedStatement s = connection.prepareStatement(query)){
            s.setString(1, name);

            try(ResultSet rs = s.executeQuery()){
                while(rs.next()){
                    Review r = new Review(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("rating"),
                            Category.valueOf(rs.getString("category")),
                            rs.getString("subtype"),
                            rs.getString("location"),
                            rs.getDate("date").toLocalDate()
                    );
                    reviews.add(r);
                }
            }
            if(reviews.isEmpty()){
                return null;
            }
            return reviews;
        }catch (SQLException e){
            System.err.println("Error finding reviews: " + e.getMessage());
            return null;
        }
    }

    /*
     * This findReview method is for finding a review by name and subtype.
     * It takes a String name and String subtype, searching the database for any matching reviews with that data.
     * It adds all matching reviews to an arraylist and returns it.
     * If nothing is found, it returns null.
     */
    public ArrayList<Review> findReview(String name, String subtype) {
        ArrayList<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews WHERE name = ? AND subtype = ?";

        try(PreparedStatement s = connection.prepareStatement(query)){
            s.setString(1, name);
            s.setString(2, subtype);

            try(ResultSet rs = s.executeQuery()){
                while(rs.next()){
                    Review r = new Review(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("rating"),
                            Category.valueOf(rs.getString("category")),
                            rs.getString("subtype"),
                            rs.getString("location"),
                            rs.getDate("date").toLocalDate()
                    );
                    reviews.add(r);
                }
            }
            if(reviews.isEmpty()){
                return null;
            }
            return reviews;
        }catch (SQLException e){
            System.err.println("Error finding reviews: " + e.getMessage());
            return null;
        }
    }

    /*
     * This findReview method is for finding a review by ID.
     * It takes an integer ID and searches the database for any matching reviews with that data.
     * It returns the matching Review.
     * If nothing is found, it returns null.
     */
    public Review findReview(int id) {
        String query = "SELECT * FROM reviews WHERE id = ?";

        try(PreparedStatement s = connection.prepareStatement(query)){
            s.setInt(1, id);

            try(ResultSet rs = s.executeQuery()){
                if(rs.next()){
                    Review r = new Review(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("rating"),
                            Category.valueOf(rs.getString("category")),
                            rs.getString("subtype"),
                            rs.getString("location"),
                            rs.getDate("date").toLocalDate()
                    );
                    return r;
                }
            }
            return null;
        }catch (SQLException e){
            System.err.println("Error finding review: " + e.getMessage());
            return null;
        }
    }

    /*
     * The getAverage method gets the average reviews from a given Category variable.
     * It sets up a query for the database that gets the rounded average review of every object with the matching category.
     * That number is then returned.
     * If it fails, it returns -1.
     * If found, it returns the average (total/count).
     */
    public double getAverage(Category cat) {
       String query = "SELECT ROUND(AVG(rating), 2) FROM reviews WHERE category = ?";

       try(PreparedStatement s = connection.prepareStatement(query)){
            s.setString(1, cat.name());

           try(ResultSet rs = s.executeQuery()){
               if (rs.next()){
                   double avg = rs.getDouble(1);

                   if(rs.wasNull()){
                       return -1.0;
                   }
                   return avg;
               }
               return -1.0;
           }
       }catch (SQLException e){
            System.err.println("Error averaging reviews: " + e.getMessage());
            return -1.0;
        }
    }

    /* The getAll method returns an ArrayList of every review in the database.
     * This method calls a query for the database to return all reviews in the system.
     * The method then loops through the resultset, taking the data of each review and turning it into a review object.
     * These objects are then added to the reviews ArrayList, which is returned.
     * If there are no reviews in the database or it fails in some way, it returns null.
     */
    public ArrayList<Review> getAll() {
        ArrayList<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM reviews";
        try (Statement s = connection.createStatement()){
            ResultSet rs = s.executeQuery(query);
            while(rs.next()){
                Review r = new Review(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("rating"),
                        Category.valueOf(rs.getString("category")),
                        rs.getString("subtype"),
                        rs.getString("location"),
                        rs.getDate("date").toLocalDate()
                );
                reviews.add(r);
            }
            if (reviews.isEmpty())
                return null;
            return reviews;
        }catch (SQLException e){
            System.err.println("Error gathering reviews: " + e.getMessage());
            return null;
        }
    }
}
