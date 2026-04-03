# Food-Review-Data-Management-System
Created by Brody Stewart. This is a Java application meant for locally storing food reviews. All data is stored on a MySQL server. This is for 3024C Software Development I at Valencia Community College.
Made during the 2026 Spring Semester.

The FRDMS (Food Review Data Management System) is designed to store the user's food review information. It stores the food's name, category, location, date, rating and more! 
The food reviews are stored in a MySQL database. A same database can be created using the script supplied in the zip folder when you download the program in \downloads.

The user interacts with the program via GUi. The user is prompted for their MySQL server address, username and password to use the application.
The user can add a review with all of its information.
They can remove a review by its ID. They can search for a review's specific information by name and/or subtype.
Subtype is a subtype of the specific food. For instance, if the food is an apple its subtype would be Honeycrisp. A pizza's subtype would be pepperoni. 
They're also able update a review from a given review's ID and get the average score of every review within a category.
Finally, the user can display all review information in the system.

This repository includes the source code and a downloads folder which contains different zipped versions of the program.
Each version comes with a runnable jar file and a sample database MySQL script, with 20 pre-made reviews. If you want an empty schema, create a MySQL server without the script for the program to use.
