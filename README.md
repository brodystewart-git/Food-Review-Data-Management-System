# Food-Review-Data-Management-System
Created by Brody Stewart. This is a Java application meant for locally storing food reviews. All data is stored on a MySQL server. This is for 3024C Software Development I at Valencia Community College.
Made during the 2026 Spring Semester.

The FRDMS (Food Review Data Management System) is designed to store the user's food review information. It stores the food's name, category, location, date, rating and more! 
The food reviews will be stored to a MySQL database in a future phase of development For now, it's a simple text file storage system. 
The user interacts with the program via the terminal. They must enter the path of a text file for the system to load and save from.
The user can add a review with all of its information.
They can remove a review by its ID. They can search for a review's specific information by name and/or subtype.
Subtype is a subtype of the specific food. For instance, if the food is an apple its subtype would be Honeycrisp. A pizza's subtype would be pepperoni. 
They're also able update a review from a given review's ID and get the average score of every review within a category.
Finally, the user can display all review information in the system.

Below is the text file and other formatting rules:
 * This text file can hold multiple reviews.
 * It must be formatted as "ID-Name-Rating-Category-Subtype-Location-Year-Month-Day", separated by line for each review.
 * ID field should be unique to the system. It will check if this is true.
 * Name field should be between 1 and 100 characters (inclusive)
 * Rating should be 1-10 (inclusive).
 * Category should match EXACTLY what is shown in the output.
 * Subtype field should be between 1 and 100 characters and is optional (inclusive).
 * Location field should be between 1 and 100 characters and is optional (inclusive).
 * Date fields should be exactly as shown.

This repository includes the source code and a text file to test it with.
To use this application, please download all java/src files and make sure to run through the Application.java program in IntelliJ.
