package foodreview;
import java.util.ArrayList;
import java.sql.*;

/**
 * This class consists exclusively of instance methods that are designed to directly interact with a MySQL database.
 *
 * <p>This is the main 'MODEL' of the Model-View-Controller architectural pattern. It is meant to handle all JDBC
 * functionality within the Food Review System application. This means it's related to {@link Review} for objects and data,
 * {@link Category} for Category enum variables and {@link ReviewSystem} for the user interaction and object instantiation.</p>
 *
 * <p>The methods in this class utilize information from {@link Review} objects, taking the data from the objects and
 * adding it to the database. It also returns, removes and updates Review data from the database. The class expects a
 * valid server address, username and password when constructing an object. If a valid one isn't given, then it fails.</p>
 *
 * @author Brody Stewart
 * <p>Course: CEN 3024 - Software Development 1</p>
 * <p>Assignment: DMS Project</p>
 * @version 0.9
 * @since 2026-03-27
 * @see Review
 * @see Category
 * @see ReviewSystem
 * @see java.sql.Connection
 * @see java.sql.DriverManager
 * @see java.sql.ResultSet
 */

public class DatabaseHandler {
    /**The connection to the database.*/
    private Connection connection;
    /**The server address of the database server.*/
    private String serverAddress;
    /**The username used for database authentication.*/
    private String username;
    /**The password used for database authentication.*/
    private String password;

    /**
     * Constructs a new DatabaseHandler and initializes the credentials.
     *
     * @param address   The server address for the MySQL database.
     * @param user      The username for database authentication.
     * @param pass      The password for database authentication.
     */
    public DatabaseHandler(String address, String user, String pass){
        this.serverAddress = address;
        this.username = user;
        this.password = pass;
    }

    /**
     * Establishes a connection with a MySQL server, ensuring the required database and table exist.
     *
     * <p>This method attempts to create a {@code food_review_project} database and a {@code reviews} table
     * if they aren't already present in the server.</p>
     *
     * @return {@code true} if the connection and/or setup were successful; {@code false} if any task failed and
     * a {@link SQLException} occured.
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

    /**
     * Generates the next free and unique ID from the database for a review.
     *
     * <p>This method queries the {@code reviews} table for the highest existing ID and increments it by one
     * to ensure the next review has a unique identifier.</p>
     *
     * @return The next available {@code int} ID; {@code -1} if the ID can't be generated or a
     * {@code SQLException} occurs.
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

    /**
     * Inserts a new record into the database using review data.
     *
     * <p>This method takes data from the provided {@link Review} object and runs an {@code INSERT} statement on the
     * table. It handles optional fields, checking for null values before insertion.</p>
     *
     * @param r The {@link Review} object with the data to be stored.
     * @return {@code 1} if the review was successfully added; {code 0} if it failed due to a {code SQLException}.
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

    /**
     * Deletes a record from the database at the given id.
     *
     * <p>This method takes an int {@code id} and uses it to run {@code DELETE} query on the reviews table. This will
     * remove the review from the database entirely.</p>
     *
     * @param id    The ID of the review to be deleted.
     * @return  {@code 1} if the review was successfully removed; {code 0} if it failed due to a {code SQLException}.
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

    /**
     * Updates a record with at the given id with the given review data.
     *
     * <p>This method takes an int {@code id} and a {@link Review} object. An {@code UPDATE} query is then run on the
     * record at the given id, replacing its information with the data in the Review object. This updates the record
     * to reflect the new given information.</p>
     *
     * @param id    The id of the review to be updated.
     * @param r     The {@link Review} object with the data to be stored.
     * @return    {@code 1} if the review was successfully updated; {code 0} if it failed due to a {code SQLException}
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

    /**
     * Searches the database for reviews that match the given name.
     *
     * <p>This method performs a query on the reviews table, {@code SELECT}. It then constructs objects from the results,
     * mapping the database types back to Java types, such as String and {@link Category}.</p>
     *
     * @param name  The name of the review(s) to search for.
     * @return  {@link ArrayList} of matching {@link Review} objects; {@code null} if no matches are found or a
     * {@code SQLException} occurs.
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

    /**
     * Searches the database for reviews that match the given name and subtype.
     *
     * <p>This method performs a query on the reviews table, {@code SELECT}. It then constructs objects from the results,
     * mapping the database types back to Java types, such as String and {@link Category}.</p>
     *
     * @param name  The name of the review(s) to search for.
     * @param subtype The subtype of the review(s) to search for.
     * @return  {@link ArrayList} of matching {@link Review} objects; {@code null} if no matches are found or a
     * {@code SQLException} occurs.
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

    /**
     * Searches the database for the review with the given id.
     *
     * <p>This method performs a query on the reviews table, {@code SELECT}. It then constructs an object from the result,
     * mapping the database types back to Java types, such as String and {@link Category}.</p>
     *
     * @param id  The id of the review to search for.
     * @return  {@link Review} object matching the id; {@code null} if no match is found or a {@code SQLException} occurs.
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

    /**
     * Calculates the average rating for all reviews from a given category.
     *
     * <p>This method performs a query {@code SELECT ROUND(AVG)} on the reviews in the database that match the given
     * {@link Category}. The SQL Query gathers this information, averages it and reounds it to two decimal places. The
     * method then takes this information and converts it into a decimal to be returned.</p>
     *
     * @param cat The {@link Category} to filter the reviews by.
     * @return  The average rating as a {@code double}; {@code -1.0} if there are no reviews in that category or a
     * {@code SQLException} occurs.
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

    /**
     *  Gathers all reviews in the database, converted to Java datatypes.
     *
     *  <p>This method runs a {@code SELECT} query that returns all records in the database. Each record is then
     *  converted into its Java equivalent, including {@link Category}. They are all then put into an
     *  ArrayList to be returned.</p>
     *
     * @return {@link ArrayList} of {@link Review} objects; {@code null} if there were no reviews in the database or
     * an {@code SQLException} occurred.
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
