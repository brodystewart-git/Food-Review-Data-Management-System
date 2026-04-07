package foodreview;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This is the main instantiable class type for the Food Review System application.
 *
 * <p>This class acts as a data object for carrying information between the {@link ReviewSystem View/Controller} and the
 * {@link DatabaseHandler Model}. It stores details including food names, ratings, categories, dates and other optional data.</p>
 *
 * @author Brody Stewart
 * <p>Course: CEN 3024 - Software Development 1</p>
 * <p>Assignment: DMS Project</p>
 * @version 0.9
 * @since 2026-03-27
 * @see Category
 * @see ReviewSystem
 * @see DatabaseHandler
 * @see LocalDate
 */
public class Review {
    /**The unique ID of the review*/
    private int reviewId;
    /**The name of the food in the review.*/
    private String foodName;
    /**The rating of the food in the review.*/
    private int foodRating;
    /**The {@link Category} of the food in the review.*/
    private Category foodCategory;
    /**The subtype of the food in the review.*/
    private String foodSubtype;
    /**The location of the review.*/
    private String reviewLocation;
    /**The date of the review.*/
    private LocalDate reviewDate;

    /**
     * Constructs a new Review object with all required and optional data.
     *
     * @param ID        The unique ID from the database.
     * @param name      The name of the food item.
     * @param rating    The {@code int} rating of the food item.
     * @param cat       The {@link Category} enum value.
     * @param subtype   The optional subtype of the food item, can be {@code null}
     * @param location  The optional location of the review, can be {@code null}
     * @param date      The {@link LocalDate} of the review.
     */
    public Review(int ID, String name, int rating, Category cat, String subtype, String location, LocalDate date) {
        reviewId = ID;
        foodName = name;
        foodRating = rating;
        foodCategory = cat;
        foodSubtype = subtype;
        reviewLocation = location;
        reviewDate = date;
    }

    /**
     * Formats the review into a single string for easy data storage and sharing.
     *
     * <p>This version is specifically designed to return a single review without labels, separated by '-'. This
     * is used mainly to allow a review to easily be split into smaller strings, in cases such as the main menu's data table
     * in {@link ReviewSystem}.</p>
     *
     * @return  A dash-separated {@link String} of the data in the object.
     */
    @Override
    public String toString() {
        String s = "";
        s = Integer.toString(reviewId) + "-" + foodName + "-" + foodRating + "-" + foodCategory + "-";
        if(foodSubtype == null) {
            s += "null"  + "-";
        }else {
            s += foodSubtype  + "-";
        }
        if(reviewLocation == null) {
            s += "null"  + "-";
        }else {
            s += reviewLocation  + "-";
        }
        s += reviewDate;
        return s;
    }

    /**
     * Formats the review data into a multi-line and user-friendly string.
     *
     * <p>This method is meant for displaying the content of a review object in the console area of the
     * {@link ReviewSystem}. It's formatted to display each data's label and ensure the date is easily readable
     * for a user to understand.</p>
     *
     * @return  A formatted {@link String} for console output.
     */
    public String consoleToString() {
        String output =
                "ID: " + reviewId
                        + "\nName: " + foodName
                        + "\nRating: " + foodRating
                        + "\nCategory: " + foodCategory.name();
        if(foodSubtype != null)
            output += "\nSubtype: " + foodSubtype;
        if(reviewLocation != null)
            output += "\nLocation: " + reviewLocation;
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        output += "\nDate: " + reviewDate.format(myFormat);
        return output;
    }
    /**
     * Gets the unique ID of the review.
     * @return the unique review ID as an {@code int}. */
    public int getID() {
        return reviewId;
    }
    /**
     * Gets the name of the food.
     * @return the food's name as a {@link String}. */
    public String getName() {
        return foodName;
    }
    /**
     * Gets the rating of the food.
     * @return the food's rating as a {code id}. */
    public int getRating() {
        return foodRating;
    }
    /**
     * Gets the category of the food.
     * @return the food's category as a {@link Category}. */
    public Category getCategory() {
        return foodCategory;
    }
    /**
     * Gets the subtype of the food.
     * @return the food's subtype as a {@link String}. */
    public String getSubtype() {
        return foodSubtype;
    }
    /**
     * Gets the location of the review.
     * @return the review's location as a {@link String}. */
    public String getLocation() {
        return reviewLocation;
    }
    /**
     * Gets the date of the review.
     * @return the review's date as a {@link LocalDate}. */
    public LocalDate getDate() {
        return reviewDate;
    }
}
