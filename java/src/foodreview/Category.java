package foodreview;
/**
 * Creates an enumerable datatype of food groups to be used to categorize reviews within the Food Review System application.
 *
 * <p>This enum is used by {@link Review} objects and both the {@link DatabaseHandler}/{@link ReviewSystem} to act as a
 * data filter for reviews and average calculation.</p>
 *
 * @author Brody Stewart
 * <p>Course: CEN 3024 - Software Development 1</p>
 * <p>Assignment: DMS Project</p>
 * @version 0.9
 * @since 2026-03-27
 * @see Review
 * @see ReviewSystem
 * @see DatabaseHandler
 */
public enum Category{
    /**Fresh or processed fruits.*/
    FRUIT,
    /**Fresh or processed vegetables.*/
    VEGETABLE,
    /**Milk and milk-based products.*/
    DAIRY,
    /**Grains, cereals, breads and other grain-based products.*/
    GRAIN,
    /**Poultry, beef and other land-based meats.*/
    MEAT,
    /**Fish, shellfish and other aquatic life.*/
    SEAFOOD,
    /**Drinks. Liquids.*/
    BEVERAGE,
    /**Candy, sweets and other high-sugar items.*/
    SUGARS,
    /**Any food not covered by the specific categories.*/
    OTHER
}
