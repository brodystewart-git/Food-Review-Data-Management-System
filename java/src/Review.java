/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 8th, 2026
 * Review.java
 * This application holds the Review class for the creation of Review objects.
 * Review objects are used in the program to store information given from a user or from a file.
 * Using a review object makes it easier to load, edit and save reviews to a database or text file by having
 * all formattable functions right in the program. This can be seen with the toString() method.
 */


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Review {
    private int reviewId;
    private String foodName;
    private int foodRating;
    private Category foodCategory;
    private String foodSubtype;
    private String reviewLocation;
    private LocalDate reviewDate;

    // This is the default Review constructor, where it expects all information to exist.
    public Review(int ID, String name, int rating, Category cat, String subtype, String location, LocalDate date) {
        reviewId = ID;
        foodName = name;
        foodRating = rating;
        foodCategory = cat;
        foodSubtype = subtype;
        reviewLocation = location;
        reviewDate = date;
    }

    // This constructor exists for when the user doesn't want to enter a location.
    public Review(int ID, String name, int rating, Category cat, String subtype, LocalDate date) {
        this(ID, name, rating, cat, subtype, null, date);
    }

    // This constructor exists for when the user doesn't want to enter a subtype.
    public Review(int ID, String name, int rating, Category cat, LocalDate date, String location) {
        this(ID, name, rating, cat, null, location, date);
    }

    // This constructor exists for when the user doesn't want to enter a subtype or location.
    public Review(int ID, String name, int rating, Category cat, LocalDate date) {
        this(ID, name, rating, cat, null, null, date);
    }
    /*
     * This toString override returns a string in the format: ID-Name-Rating-Category-Subtype-Location-Year-Month-Day
     * This method makes saving and loading to the text file far easier, thanks to a set format.
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

    //Custom print method, to make displaying information easier to the usr.
    public void print() {
        System.out.println(
                "ID: " + reviewId
                        + "\nName: " + foodName
                        + "\nRating: " + foodRating
                        + "\nCategory: " + foodCategory.name()
        );
        if(foodSubtype != null)
            System.out.println("Subtype: " + foodSubtype);
        if(reviewLocation != null)
            System.out.println("Location: " + reviewLocation);
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Date: " + reviewDate.format(myFormat));
    }

    public int getID() {
        return reviewId;
    }
    public String getName() {
        return foodName;
    }
    public int getRating() {
        return foodRating;
    }
    public Category getCategory() {
        return foodCategory;
    }
    public String getSubtype() {
        return foodSubtype;
    }
    public String getLocation() {
        return reviewLocation;
    }
    public LocalDate getDate() {
        return reviewDate;
    }
}
