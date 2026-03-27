/*
 * Brody Stewart
 * CEN 3024 - Software Development 1
 * March 26th, 2026
 * ReviewSystem.java
 * This application is the main system (or the View & Controller) of the program.
 * It handles all UI and user interaction, essentially the controller of the program.
 * It prompts the user to input a file on the first menu upon startup. It then loads this file.
 * Once loaded, the main menu is displayed with multiple options.
 * Users can then interact with the program by adding reviews, removing reviews, finding reviews, updating reviews,
 * getting the average review score of a category, and displaying all reviews.
 */

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
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
    CardLayout cardLayout;
    JPanel deck;
    JButton[] buttons;
    JTextArea console;
    Font buttonFont = new Font("Serif", Font.BOLD, 18);

    //Simple constructor that initiates scanner.
    public ReviewSystem() {
        scanner = new Scanner(System.in);
    }

    // UI Methods Section
    /* The initGui method initializes the GUI system for the user to be displayed content.
     * It uses a Deck system, where pages are stored similarly to a deck of cards and chosen based on when it's needed.
     * This method is where all pages (except for the update subpage) are initialized and added to the deck.
     * It's set up for the first page to ask for a path to a text file.
     * Contains a persistent console.
     */
    public void initGui() {
        Color lightBlue = new Color(145, 175, 199);
        Color darkerBlue = new Color(65,89,110);
        cardLayout = new CardLayout();
        deck = new JPanel(cardLayout);
        deck.setBackground(lightBlue);
        console = new JTextArea(20,30);
        console.setEditable(false);
        console.setBackground(darkerBlue);
        console.setForeground(Color.WHITE);
        console.setFont(new Font("Monospaced", Font.PLAIN, 18));
        console.setLineWrap(true);
        console.setWrapStyleWord(true);

        // Add extra elements and create add them to the page.
        JFrame frame = new JFrame("Food Review System");
        frame.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(console);
        scrollPane.setPreferredSize(new Dimension(400, 700));
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setPreferredSize((new Dimension(0,0)));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, deck, scrollPane);
        splitPane.setResizeWeight(1.0);
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(700);
        frame.add(splitPane, BorderLayout.CENTER);

        // Initialize and add pages
        JPanel inputPage = createInputPage();
        deck.add(inputPage, "INPUT");
        JPanel mainMenuPanel = mainMenuPage();
        deck.add(mainMenuPanel, "MAIN");
        JPanel addPage = addReviewPage();
        deck.add(addPage, "ADD");
        JPanel remPage = removeReviewPage();
        deck.add(remPage, "REM");
        JPanel findPage = findReviewPage();
        deck.add(findPage, "FIND");
        JPanel updPage = updateReviewPageOne();
        deck.add(updPage, "UPD");
        JPanel avgPage = avgReviewPage();
        deck.add(avgPage, "AVG");

        // Display First Page
        cardLayout.show(deck, "INPUT");
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /* The mainMenuPage method creates a JPanel with all elements needed in the Main Menu.
     * This is where the main ways to interact with the program is stored through buttons.
     * It uses a GridBagLayout to display these buttons, along with a title.
     * Botton interactions are all sent to handler methods, usually titled [task]ReviewHandler.
     * * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel mainMenuPage(){
        JPanel panel = new JPanel (new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Food Review System");
        title.setFont(new Font("Serif", Font.BOLD, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(0, 0, 50, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gbc);

        JButton addBtn = new JButton("Add Review");
        JButton remBtn = new JButton("Remove Review");
        JButton findBtn = new JButton("Find Review");
        JButton updBtn = new JButton("Update Review");
        JButton avgBtn = new JButton("Average Review by Category");
        JButton dispBtn = new JButton("Display Reviews");
        buttons = new JButton[]{addBtn, remBtn, findBtn, updBtn, avgBtn, dispBtn};
        Dimension btnSize = new Dimension(300,60);
        gbc.gridwidth = 1;
        gbc.insets = new java.awt.Insets(15,15,15,15);

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setFont(buttonFont);
            buttons[i].setPreferredSize(btnSize);
            gbc.gridx = i % 2;
            gbc.gridy = (i / 2) + 1;
            panel.add(buttons[i], gbc);
        }

        addBtn.addActionListener(e -> {
            cardLayout.show(deck, "ADD");
        });

        remBtn.addActionListener(e -> {
            cardLayout.show(deck, "REM");
        });

        findBtn.addActionListener(e -> {
            cardLayout.show(deck, "FIND");
        });

        updBtn.addActionListener(e -> {
            cardLayout.show(deck, "UPD");
        });

        avgBtn.addActionListener(e -> {
            cardLayout.show(deck, "AVG");
        });

        dispBtn.addActionListener(e -> {
            //Disable input
            for (JButton btn : buttons) {
                btn.setEnabled(false);
            } // Remember to re-enable buttons
            dispAllHandler();
        });
        return panel;
    };

    /* The createInputPage method creates a JPanel with all elements needed in the first file path input page.
     * This is where the user is prompted to input a path to a text file.
     * It features a simple text field for the user to input the path and a button to submit it.
     * The content of the text field is sent to the loadFile method to ensure it's a real path.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel createInputPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Food Review System", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 40, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        JLabel label = new JLabel("Enter File Path:");
        label.setFont(new Font("Serif", Font.BOLD, 18));
        gbc.gridy = 1;
        panel.add(label,gbc);

        JTextField inputField = new JTextField(20);
        gbc.gridy = 2;
        panel.add(inputField, gbc);

        JButton okBtn = new JButton("OK");
        okBtn.setFont(buttonFont);
        gbc.gridy = 3;
        panel.add(okBtn,gbc);
        okBtn.addActionListener(e ->{
            boolean gotFile = loadFile(inputField.getText());
            if(gotFile){
                printToConsole("Path Found, data added to system.");
                inputField.setText("");
                cardLayout.show(deck, "MAIN");
            }else{
                printToConsole("Error: Invalid path, please try again.");
            }
        });
        return panel;
    }

    /* The addReviewPage method creates a JPanel with all elements needed in the add review page.
     * This is where the user is prompted to input various information for adding a review.
     * This includes four text fields for name, subtype, location and date.
     * As well as two dropdown menus for category and rating.
     * It takes the information and sends it to newReviewHandler, creating a new review.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel addReviewPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JTextField nameField, subtypeField, locationField, dateField;
        JComboBox<Category> categoryDropDown;
        JComboBox<Integer> ratingDropDrown;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;



        JLabel title = new JLabel("Add A Review");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(15);
        gbc.gridy = 1;
        panel.add(createLabeledField("Food Name:", nameField), gbc);
        subtypeField = new JTextField(15);
        gbc.gridy = 2;
        panel.add(createLabeledField("Subtype (Optional, ex: honeycrisp):", subtypeField), gbc);
        locationField = new JTextField(15);
        gbc.gridy = 3;
        panel.add(createLabeledField("Location (Optional):", locationField), gbc);
        dateField = new JTextField(15);
        gbc.gridy = 4;
        panel.add(createLabeledField("Date (YYYY-MM-DD):", dateField), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        categoryDropDown = new JComboBox<>(Category.values());
        panel.add(createLabeledField("Category:", categoryDropDown), gbc);
        ratingDropDrown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        gbc.gridy = 2;
        panel.add(createLabeledField("Rating: ", ratingDropDrown), gbc);
        JComponent fields[] = new JComponent[]{nameField, subtypeField, locationField, dateField, categoryDropDown, ratingDropDrown};

        JButton backBtn = new JButton("Back");
        backBtn.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        okBtn.setFont(buttonFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            clearFields(fields);
            cardLayout.show(deck, "MAIN");
        });

        okBtn.addActionListener(e ->{
            String name = nameField.getText().trim();
            int rating = (int) ratingDropDrown.getSelectedItem();
            Category cat = (Category) categoryDropDown.getSelectedItem();
            String subtype = subtypeField.getText().trim();
            if(subtype.isEmpty())
                subtype = null;
            String location = locationField.getText().trim();
            if(location.isEmpty())
                location = null;
            String date = dateField.getText().trim();
            boolean added = newReviewHandler(name, rating, cat, subtype, location, date);
            if(added){
                clearFields(fields);
            }
        });

        return panel;
    }

    /* The removeReviewPage method creates a JPanel with all elements needed in the remove review page.
     * This is where the user is prompted to input the ID of a review to be removed.
     * It takes the information and sends it to remReviewHandler, removing it from the system.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel removeReviewPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(15,15,15,15);
        JTextField idField;

        JLabel title = new JLabel("Remove A Review");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.weighty = 0;
        idField = new JTextField(15);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabeledField("Enter Review ID (If unknown, use Find Review):", idField), gbc);

        gbc.gridwidth = 1;
        JButton backBtn = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            idField.setText("");
            cardLayout.show(deck, "MAIN");
        });

        okBtn.addActionListener(e ->{
          boolean removed = remReviewHandler(idField.getText().trim());
            if(removed){
                idField.setText("");
            }
        });
        return panel;
    }

    /* The findReviewPage method creates a JPanel with all elements needed in the find review page.
     * This is where the user is prompted to input the name and optional subtype of the review.
     * It takes the information and sends it to findReviewHandler, where it will find reviews matching the data.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel findReviewPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(15,15,15,15);
        JTextField nameField, subtypeField;

        JLabel title = new JLabel("Find A Review");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.weighty = 0;
        nameField = new JTextField(15);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabeledField("Enter Review Name:", nameField), gbc);

        subtypeField = new JTextField(15);
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabeledField("Enter Subtype Name (Optional):", subtypeField), gbc);

        gbc.gridwidth = 1;
        JButton backBtn = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            nameField.setText("");
            subtypeField.setText("");
            cardLayout.show(deck, "MAIN");
        });

        okBtn.addActionListener(e ->{
            String nameInput = nameField.getText().trim();
            String subInput = subtypeField.getText().trim();
            boolean found = findReviewHandler(nameInput, subInput);
            if(found){
                nameField.setText("");
                subtypeField.setText("");
            }
        });
        return panel;
    }

    /* The updateReviewPageOne method creates a JPanel with all elements needed in the first page of updating a review.
     * This is where the user is prompted to input the ID of a review.
     * It takes the information and sends it to updReviewLocationHandler, receiving a review object.
     * When received, it sets up a temporary subpage and displays it, titled updateReviewPageTwo.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel updateReviewPageOne(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(15,15,15,15);
        JTextField idField;

        JLabel title = new JLabel("Update A Review");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.weighty = 0;
        idField = new JTextField(15);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createLabeledField("Enter Review ID (If unknown, use Find Review):", idField), gbc);

        gbc.gridwidth = 1;
        JButton backBtn = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            idField.setText("");
            cardLayout.show(deck, "MAIN");
        });

        okBtn.addActionListener(e ->{
            Review r = updReviewLocationHandler(idField.getText().trim());
            idField.setText("");
            if(r != null) {
                JPanel editPage = updateReviewPageTwo(r);
                deck.add(editPage, "EDIT");
                cardLayout.show(deck, "EDIT");
            }
        });
        return panel;
    }

    /* The updateReviewPageTwo method creates a JPanel with all elements needed in the second  page of updating a review.
     * This is where the user is prompted to change the review's information to be updated.
     * It takes the review information from the last page and fills all the fields with its data.
     * These fields are four text fields for food name, subtype, location and date.
     * As well as two dropdowns for category and rating.
     * When the user presses okay, it sends the new information to updateReviewHandler.
     * It then deletes itself, returning to the first update page.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel updateReviewPageTwo(Review rev){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JTextField nameField, subtypeField, locationField, dateField;
        JComboBox<Category> categoryDropDown;
        JComboBox<Integer> ratingDropDrown;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("Update A Review");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(rev.getName(),15);
        gbc.gridy = 1;
        panel.add(createLabeledField("Food Name:", nameField), gbc);
        if(rev.getSubtype() == null){
            subtypeField = new JTextField(15);
        }else subtypeField = new JTextField(rev.getSubtype(),15);
        gbc.gridy = 2;
        panel.add(createLabeledField("Subtype (Optional, ex: honeycrisp):", subtypeField), gbc);
        if(rev.getLocation() == null){
            locationField = new JTextField(15);
        }else locationField = new JTextField(rev.getLocation(),15);
        gbc.gridy = 3;
        panel.add(createLabeledField("Location (Optional):", locationField), gbc);
        dateField = new JTextField(rev.getDate().toString(),15);
        gbc.gridy = 4;
        panel.add(createLabeledField("Date (YYYY-MM-DD):", dateField), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        categoryDropDown = new JComboBox<>(Category.values());
        categoryDropDown.setSelectedItem(rev.getCategory());
        panel.add(createLabeledField("Category:", categoryDropDown), gbc);
        ratingDropDrown = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        ratingDropDrown.setSelectedItem(rev.getRating());
        gbc.gridy = 2;
        panel.add(createLabeledField("Rating: ", ratingDropDrown), gbc);
        JComponent fields[] = new JComponent[]{nameField, subtypeField, locationField, dateField, categoryDropDown, ratingDropDrown};

        JButton backBtn = new JButton("Back");
        backBtn.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        okBtn.setFont(buttonFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            clearFields(fields);
            cardLayout.show(deck, "UPD");
            deck.remove(panel);
            deck.revalidate();
            deck.repaint();
        });

        okBtn.addActionListener(e ->{
            String name = nameField.getText().trim();
            int rating = (int) ratingDropDrown.getSelectedItem();
            Category cat = (Category) categoryDropDown.getSelectedItem();
            String subtype = subtypeField.getText().trim();
            if(subtype.isEmpty())
                subtype = null;
            String location = locationField.getText().trim();
            if(location.isEmpty())
                location = null;
            String date = dateField.getText().trim();
            boolean added = updReviewHandler(rev.getID(), name, rating, cat, subtype, location, date);
            if(added){
                clearFields(fields);
                cardLayout.show(deck, "UPD");
                deck.remove(panel);
                deck.revalidate();
                deck.repaint();
            }
        });

        return panel;
    }

    /* The avgReviewPage method creates a JPanel with all elements needed in the average review by category page.
     * The user is shown a simple dropdown that contains all possible food review categories.
     * When the user preses okay, it sends the category information to the avgReviewHandler method.
     * Returns a functioning JPanel to be stored in the deck.
     */
    private JPanel avgReviewPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(15,15,15,15);
        JComboBox<Category> categoryDropDown;

        JLabel title = new JLabel("Average Review By Category");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 30, 0);
        panel.add(title, gbc);

        gbc.insets = new java.awt.Insets(10,10,10,10);
        gbc.weighty = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        categoryDropDown = new JComboBox<>(Category.values());
        panel.add(createLabeledField("Category:", categoryDropDown), gbc);


        gbc.gridwidth = 1;
        JButton backBtn = new JButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(backBtn, gbc);

        JButton okBtn = new JButton("OK");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        panel.add(okBtn, gbc);

        backBtn.addActionListener(e ->{
            categoryDropDown.setSelectedIndex(0);
            cardLayout.show(deck, "MAIN");
        });

        okBtn.addActionListener(e ->{
            boolean found = avgReviewHandler((Category) categoryDropDown.getSelectedItem());
            if(found){
                categoryDropDown.setSelectedIndex(0);
            }
        });
        return panel;
    }

    // The clearFields method simply clears and resets any JTextFields or JDropDowns in the fields array.
    private void clearFields(JComponent[] fields){
        for(JComponent c: fields){
            if(c.getClass() == JTextField.class){
                ((JTextField) c).setText("");
            }else if (c.getClass() == JComboBox.class){
                ((JComboBox) c).setSelectedIndex(0);
            }
        }
    };

    /* The createLabeledField method is for creating a labeled JComponent.
     * It takes a string and a JComponent. It then creates a JLabel and puts it above that component.
     * It puts these two elements together in a JPanel and returns that.
    */
    private JPanel createLabeledField(String text, JComponent field){
        JPanel panel = new JPanel(new BorderLayout(0,5));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // Logic/ Button Handling Section

    // printToConsole is a simple method that "prints" a string to the console UI element.
    private void printToConsole(String content){
        console.append(content + "\n");

        // Automatically move the scrollbar
        console.setCaretPosition(console.getDocument().getLength());
    }

    /*
     * Temporary method for text file loading.
     * It takes a String path when called.
     * If correct, it will pass the file to the Database Handler object and return true if successfully loaded.
     * Otherwise, it will return false.
     */
    public boolean loadFile(String path) {
        path = path.replace("\"", "");
        File checkFile = new File(path);
        if (checkFile.exists() && checkFile.isFile() && path.toLowerCase().endsWith(".txt")) {
            dbHandler = new DatabaseHandler(path);
            if (!dbHandler.loadDatabase())
                return false;
            return true;
        }else{
            return false;
        }
    }

    /*
     * The newReviewHandler communicates between the View and the Model that a new review needs to be saved.
     * It calls for the program to create a review with the given parameters.
     * It checks these parameters for validity, alerting the user if something is wrong and returning false.
     * If it's successfully created, it sends the review to the Database Handler object to be saved and returns true.
     * * If the database fails to do its job, it will return false.
     */
    private boolean newReviewHandler(String name, int rating, Category cat, String subtype, String location, String date) {
       int id = dbHandler.idGenerator();
       boolean usingSubtype = false;
       boolean usingLocation = false;
       if (subtype != null) {
           usingSubtype = true;
           subtype = stringFormatter(subtype);
       }
       if (location != null) {
           usingLocation = true;
           location = stringFormatter(location);
       }
       if(!nameValidation(name)){
           printToConsole("Error: Invalid food name. It should be 2-100 characters and only contain letters.");
           return false;
       }
       if(usingSubtype && !subtypeValidation(subtype)){
           printToConsole("Error: Invalid subtype. It should be 2-50 characters and only contain letters.");
           return false;
       }
       if(usingLocation && !locationValidation(location)){
           printToConsole("Error: Invalid location. It should be 2-100 characters.");
           return false;
       }
       LocalDate trueDate  = dateValidation(date);
       if(trueDate == null) {
           printToConsole("Error: Invalid date. Should be a valid date, formatted as YYYY-MM-DD");
           return false;
       }
       name = stringFormatter(name);
       Review r = createReview(id, name, rating, cat, subtype, location, trueDate);
       int added = dbHandler.addReview(r, true);
       if(added == 0){
           printToConsole("Error in creating review. Retry.");
           return false;
       }
       printToConsole("\n\nCreated new review\n-----------\n" + r.consoleToString());
       return true;
    }

    /*
     * The remReviewHandler communicates between the View and the Model that a review needs to be removed.
     * It calls for the program to remove a review with the given ID in string form.
     * It then checks for validity and converts it to an integer.
     * It gets the ID of the review, then sends that information to the Database Handler to do its job.
     * If it removes the review, it alerts the user and returns true. Otherwise, alerts and returns false.
     */
    private boolean remReviewHandler(String input) {
        Review r = null;
        int ID = -1;
        try{
            ID = Integer.parseInt(input);
            if (ID < 1)
                throw new NumberFormatException("Out of Range");
        }catch (NumberFormatException e) {
            printToConsole("Error: Invalid input. Please enter a positive integer.");
            return false;
        }
        r = dbHandler.findReview(ID);
        if (r == null) {
            printToConsole("Error: Object with that ID doesn't exist in the system. Please search for the review in Find Review.");
            return false;
        }
        printToConsole("\n\nRemoving Review\n-----------------\n" + r.consoleToString());
        int result = dbHandler.remReview(ID);
        if (result == 1) {
            printToConsole("\nSuccessfully removed Review at ID: " + ID);
            return true;
        }else {
            printToConsole("Failed to remove from database, returning to main menu.");
            return false;
        }
    }


    /*
     * The findReviewHandler communicates between the View and the Model that a review needs to be found.
     * It takes a string name and subtype as parameters.
     * It checks if they decided to add a subtype or left it blank, changing which DatabaseHandler findReview method it'll use.
     * It validates input, alerting the user and returning false if something is incorrect.
     * In the instance it finds reviews, the Database Handler will
     * return a list of reviews matching the search criteria, and it will be displayed to the user.
     * If the database fails to do its job, it will let the user know and return false.
     */
    private boolean findReviewHandler(String name, String subtype) {
        ArrayList<Review> result = null;
        boolean getSubtype = !subtype.isEmpty();

        int l = name.length();
        if (l < 2 || l > 100 || name.isBlank() || name.matches(".*[^a-zA-Z ].*")) {
            printToConsole("Error: Invalid food name. It should be 2-100 characters and only contain letters.");
            return false;
        }
        name = stringFormatter(name);
        if(getSubtype){
            l = subtype.length();
            if (l < 2 || l > 50 || subtype.isBlank() || subtype.matches(".*[^a-zA-Z ].*")) {
                printToConsole("Error: Invalid subtype. It should be 2-100 characters and only contain letters.");
                return false;
            }
            subtype = stringFormatter(subtype);
            result = dbHandler.findReview(name, subtype);
        }else
            result = dbHandler.findReview(name);

        if (result == null) {
            printToConsole("Failed to find any Reviews matching that information.");
            return false;
        } else {
            printToConsole("\n\nReviews matching that information:");
            for (Review r : result) {
                printToConsole("----------------");
                printToConsole(r.consoleToString());
            }
        }
        return true;
    }

    /*
     * The updReviewLocationHandler communicates between the View and the Model that a review needs to be found for updating.
     * It takes a string ID as a parameter.
     * It validates input and converts it to an integer, alerting the user and returning false if something is incorrect.
     * In the instance the review is found, it returns the review. Otherwise, returns nul and alerts the user.
     */
    private Review updReviewLocationHandler(String input) {
        Review r = null;
        int ID = -1;
        try{
            ID = Integer.parseInt(input);
            if (ID < 1)
                throw new NumberFormatException("Out of Range");
        }catch (NumberFormatException e) {
            printToConsole("Error: Invalid input. Please enter a positive integer.");
            return null;
        }
        r = dbHandler.findReview(ID);
        if (r == null) {
            printToConsole("Error: Object with that ID doesn't exist in the system. Please search for the review in Find Review.");
            return null;
        }
        printToConsole("\n\nFound Review\n-----------------\n" + r.consoleToString());
        printToConsole("Successfully removed Review at ID: " + ID);
        return r;
    }

    /*
     * The updReviewHandler communicates between the View and the Model that a review needs to be updated.
     * It takes review information as a parameter to be added to a Review object.
     * It validates input and converts into a review.
     * If something is wrong with input, it alerts the user and returns false.
     * It then attempts to delete the old review and replace it with the new one.
     * If successful, returns true. If not, returns false and alerts the user.
     */
    private boolean updReviewHandler(int id, String name, int rating, Category cat, String subtype, String location, String date) {
        boolean usingSubtype = false;
        boolean usingLocation = false;
        if (subtype != null) {
            usingSubtype = true;
            subtype = stringFormatter(subtype);
        }
        if (location != null) {
            usingLocation = true;
            location = stringFormatter(location);
        }
        if(!nameValidation(name)){
            printToConsole("Error: Invalid food name. It should be 2-100 characters and only contain letters.");
            return false;
        }
        if(usingSubtype && !subtypeValidation(subtype)){
            printToConsole("Error: Invalid subtype. It should be 2-50 characters and only contain letters.");
            return false;
        }
        if(usingLocation && !locationValidation(location)){
            printToConsole("Error: Invalid location. It should be 2-100 characters.");
            return false;
        }
        LocalDate trueDate  = dateValidation(date);
        if(trueDate == null) {
            printToConsole("Error: Invalid date. Should be a valid date, formatted as YYYY-MM-DD");
            return false;
        }
        name = stringFormatter(name);
        Review r = createReview(id, name, rating, cat, subtype, location, trueDate);
        int removed = dbHandler.remReview(id);
        if (removed == 0){
            printToConsole("Error in updating review. Retry.");
            return false;
        }
        int added = dbHandler.addReview(r, true);
        if(added == 0){
            printToConsole("Error in updating review. Retry.");
            return false;
        }
        printToConsole("\n\nUpdated review at " + id + "\n-------------\n" + r.consoleToString());
        return true;
    }

    /*
     * The avgReviewHandler communicates between the View and the Model that an average needs to be found.
     * It calls for the program to get the average review of a specified category.
     * It gets the category from the parameter, then sends that information to the Database Handler to do its job.
     * If it's successful, returns true and prints out the average value.
     * If not, it alerts the user and returns false.
     */
    private boolean avgReviewHandler(Category category) {
        double result = dbHandler.getAverage(category);
        if (result < 0) {
            printToConsole("Error: Failed to get average, might be no reviews in category.");
            return false;
        }
        printToConsole(String.format("Average Review Score in %s is %.2f", category, result));
        return true;
    }

    /*
     * The dispAllHandler communicates between the View and the Model that the reviews need to be displayed.
     * It simply calls for the Database Handler to give it all of its reviews in its array.
     * Then, it loops through them and prints them out to the user.
     */
    private void dispAllHandler() {
        ArrayList<Review> reviews;
        reviews = dbHandler.getAll();
        if (reviews == null){
            printToConsole("Error: Nothing in the database. Please add reviews first.");
            return;
        }
        printToConsole("\tDisplaying All Reviews: ");
        for(Review r: reviews) {
            printToConsole("-------------------");
            printToConsole(r.consoleToString());
        }
        printToConsole("\n");
        for (JButton btn : buttons) {
            btn.setEnabled(true);
        }
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

    /* nameValidation is a simple method which takes input in regard to the Name field of the review object.
     * It checks if the text is valid: 2-100 characters and contains only letters.
     * Returns true if valid, false if not.
     */
    private boolean nameValidation(String input){
        int l = input.length();
        if (l < 2 || l > 100 || input.isBlank() || input.matches(".*[^a-zA-Z ].*"))
            return false;
        return true;
    }

    /* subtypeValidation is a simple method which takes input in regard to the subtype field of the review object.
     * It checks if the input is valid: 2-50 characters, contains only letters.
     * Returns true if valid, false if not.
     */
    private boolean subtypeValidation(String input){
        int l = input.length();
        if (l < 2 || l > 50 || input.isBlank() || input.matches(".*[^a-zA-Z ].*"))
            return false;
        return true;
    }

    /* locationValidation is a simple method which takes input in regard to the location field of the review object.
     * It checks if the input is valid: 2-100 characters and isn't blank.
     * Returns true if valid, false if not.
     */
    private boolean locationValidation(String input){
        int l = input.length();
        if (l < 2 || l > 100 || input.isBlank())
            return false;
        return true;
    }

    /* dateValidation is a simple method which takes input in regard to the date field of the review object.
     * It checks if the input is valid: Meets the format of LocalDate (YYYY-MM-DD), ensures it's a valid date.
     * Returns the validated date.
     */
    private LocalDate dateValidation(String input){
        LocalDate date = null;
        try {
            date = LocalDate.parse(input);
            LocalDate today = LocalDate.now();
            if (date.isAfter(today)){
                throw new java.time.format.DateTimeParseException("Invalid date value", input, 0);
            }
        }catch (DateTimeParseException e) {
            return null;
        }
        return date;
    }

    // StringFormatter simply ensures that an inputted string and makes the first letters of every word upper case.
    private String stringFormatter(String input){
        StringBuilder result = new StringBuilder();
        if(input == null || input.isEmpty())
            return input;
        for (String word : input.split(" ")){
            if(!word.isEmpty()){
                result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return result.toString().trim();
    }

}
