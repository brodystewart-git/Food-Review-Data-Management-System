package foodreview;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * This class consists exclusively of instance methods that are designed to display information and handle user interaction.
 *
 * <p>This is the main 'VIEW' and 'CONTROLLER' of the Model-View-Controller architectural pattern. It is meant to handle
 * all UI and handle all user interaction with the UI. When a user interacts with the program, it directly calls the
 * Model ({@link DatabaseHandler} to do its job in relation to the chosen task. It also passes all necessary information along,
 * sometimes in the form of a {@link Review} object. The UI is designed to have a console on the right-hand side,
 * displaying any output for the user to read. The rest of the UI is designed with a {@link CardLayout} in mind,
 * treating each page like a card in a deck that can be switched to when told so.</p>
 *
 * <p>The user is asked to give basic database information, which is then used to display review information as well as
 * add, remove, find and update review records in the database. This means that this class relies heavily on the
 * {@link DatabaseHandler} to do most of the actual functionality in regard to JDBC interactions, {@link Review} for
 * object storage and passing, and a multitude of {@link JComponent} objects.</p>
 *
 * @author Brody Stewart
 * <p>Course: CEN 3024 - Software Development 1</p>
 * <p>Assignment: DMS Project</p>
 * @version 0.9
 * @since 2026-03-27
 * @see Review
 * @see Category
 * @see DatabaseHandler
 * @see javax.swing.JComponent
 * @see java.awt.CardLayout
 */

public class ReviewSystem {
    /**The model that handles all JDBC interactions with the MySQL database.*/
    DatabaseHandler dbHandler;
    /**The layout managed for switching between different UI pages.*/
    CardLayout cardLayout;
    /**The main container panel that holds the various pages of the program.*/
    JPanel deck;
    /**Array containing all main menu buttons to be easily disabled and enabled.*/
    JButton[] buttons;
    /**The output area on the right-hand side of the screen, used for displaying information to the user.*/
    JTextArea console;
    /**Standardized font for all primary application buttons.*/
    Font buttonFont = new Font("Serif", Font.BOLD, 18);
    /**The visual table used to display database records in {@link #mainMenuPage()}.*/
    private JTable reviewTable;
    /**The table model for managing the rows and columns of the {@link #reviewTable}.*/
    private DefaultTableModel tableModel;

    /**
     * Constructs the ReviewSystem and initializes the graphical user interface.
     *
     * <p>This constructor calls {@link #initGui()} to set up all {@link JComponent} objects, layouts,
     * and event listeners.</p>
     */
    public ReviewSystem() {
        initGui();
    }

    // UI Methods Section
    /* The initGui method initializes the GUI system for the user to be displayed content.
     * It uses a Deck system, where pages are stored similarly to a deck of cards and chosen based on when it's needed.
     * This method is where all pages (except for the update page) are initialized and added to the deck.
     * It's set up for the first page to ask for a path to a text file.
     * Contains a persistent console.
     */

    /**
     * Initializes the core UI components and displays the first page {@link #createLoginPage()}.
     *
     * <p>This method sets up the main {@link JFrame}, {@link CardLayout} and console ({@link JTextArea}. It initializes
     * all pages ({@link #createLoginPage() Login Page},{@link #mainMenuPage() Main Menu Page},
     * {@link #addReviewPage() Add Review Page}, {@link #findReviewPage() Find Review Page}
     * {@link #avgReviewPage() Average Review Page}). These pages are then added to the cardlayout and the Login Page
     * is selected to be displayed to the user.</p>
     */
    private void initGui() {
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

        // Add  elements and add them to the page.
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
        JPanel inputPage = createLoginPage();
        deck.add(inputPage, "INPUT");
        JPanel mainMenuPanel = mainMenuPage();
        deck.add(mainMenuPanel, "MAIN");
        JPanel addPage = addReviewPage();
        deck.add(addPage, "ADD");
        JPanel findPage = findReviewPage();
        deck.add(findPage, "FIND");
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
     * It uses a BorderLayout to display these buttons, along with a title and a JTable of all reviews.
     * Button interactions are all sent to handler methods, usually titled [task]ReviewHandler.
     * * Returns a functioning JPanel to be stored in the deck.
     */

    /**
     * Creates the main menu panel featuring a data table and navigational buttons.
     *
     * <p>This method creates a {@link JTable} with a {@link DefaultTableModel} to display review information.
     * To allow users to interact with the data, it constructs a grid of buttons that, when pressed, do a specific function.
     * Some will switch the page using the {@link CardLayout navigation system}. This can be seen with the buttons:
     * Add Review, Find Review, Update Review and Average Review. For Update review, it even takes the data of the selected
     * review from the data table to be used in the next page. Others will simply send the selected review's data and
     * chosen function to the Model to be completed and returned. </p>
     *
     * @return {@link JPanel} of the created main menu page, complete with all listeners and JComponents.
     */
    private JPanel mainMenuPage(){
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        String[] columns = {"ID", "Food Name", "Rating", "Category", "Subtype", "Location", "Date"};

        JLabel title = new JLabel("Food Review System", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 40));
        panel.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        reviewTable = new JTable(tableModel);
        reviewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);    // might need to change this
        reviewTable.getTableHeader().setReorderingAllowed(false);
        reviewTable.setFont(new Font("Monospaced", Font.PLAIN, 18));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < reviewTable.getColumnCount(); i++) {
            reviewTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(reviewTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2,3,10,10));
        buttonPanel.setPreferredSize(new Dimension(0, 150));
        buttonPanel.setOpaque(false);
        JButton addBtn = new JButton("Add Review");
        JButton remBtn = new JButton("Remove Review");
        JButton findBtn = new JButton("Find Review");
        JButton updBtn = new JButton("Update Review");
        JButton avgBtn = new JButton("Average Review by Category");
        JButton dispBtn = new JButton("Display Reviews");
        buttons = new JButton[]{addBtn, remBtn, findBtn, updBtn, avgBtn, dispBtn};
        for(JButton b: buttons){
            b.setFont(new Font ("Serif", Font.BOLD, 20));
            buttonPanel.add(b);
        }
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            cardLayout.show(deck, "ADD");
        });

        remBtn.addActionListener(e -> {
            int selectedRow = reviewTable.getSelectedRow();
            if (selectedRow != -1) {
                String id = reviewTable.getValueAt(selectedRow, 0).toString();
                boolean removed = remReviewHandler(id);
                if(removed) refreshListHandler();
            }
        });

        findBtn.addActionListener(e -> {
            cardLayout.show(deck, "FIND");
        });

        updBtn.addActionListener(e -> {
            int selectedRow = reviewTable.getSelectedRow();
            if (selectedRow != -1) {
                String stringId = reviewTable.getValueAt(selectedRow, 0).toString();
                Review r = updReviewLocationHandler(stringId);
                if(r != null) {
                    JPanel editPage = updateReviewPage(r);
                    deck.add(editPage, "EDIT");
                    cardLayout.show(deck, "EDIT");
                }
            }
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

    /**
     * Create the initial page for database authentication.
     *
     * <p>This method creates a panel which features {@link JTextField input fields} to take the server address,
     * username and password of a MySQL server from the user. It features a single 'OK' button for the user
     * to tell the program they are ready to send their input information.</p>
     *
     * <p>When the user presses the 'OK' button, an action listener instantiates the
     * {@link DatabaseHandler dbHandler} using the information from the text field,
     * where it attempts to connect to the database. If it's able to connect, it switches the page to the main menu page
     * using the {@link CardLayout navigation system}. If it fails, it alerts the user that something went wrong
     * and to try again.</p>
     *
     * @return {@link JPanel} of the created login page, complete with all listeners and JComponents.
     */
    private JPanel createLoginPage(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Food Review System Login", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(20, 0, 40, 0);
        panel.add(title, gbc);

        gbc.gridy = 1;
        JTextField addField = new JTextField(20);
        panel.add(createLabeledField("Server Address:", addField), gbc);

        gbc.gridy = 2;
        JTextField userField = new JTextField(20);
        panel.add(createLabeledField("Username:", userField), gbc);

        gbc.gridy = 3;
        JPasswordField passField = new JPasswordField(20);
        panel.add(createLabeledField("Password:", passField), gbc);

        JButton okBtn = new JButton("OK");
        okBtn.setFont(buttonFont);
        gbc.gridy = 4;
        panel.add(okBtn,gbc);

        okBtn.addActionListener(e ->{
           String address = addField.getText();
           String user = userField.getText();
           String pass = new String(passField.getPassword());
           dbHandler = new DatabaseHandler(address, user, pass);
           if(dbHandler.connect()){
               refreshListHandler();
               cardLayout.show(deck, "MAIN");
           }else{
               printToConsole("Login Failed. Please check your credentials.");
               dbHandler = null;
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

    /**
     * Creates the input page for adding a new review to the system.
     *
     * <p>This method features multiple {@link JTextField text fields} and {@link JComboBox dropdowns} for the user to
     * input and select data for a new Food Review. It features a single 'OK' button for the user
     * to tell the program they are ready to send their input information.</p>
     * The featured fields are:
     * <ul>
     *     <li>A text field for the food's name.</li>
     *     <li>An optional text field for the food's subtype.</li>
     *     <li>An optional text field for the review location.</li>
     *     <li>A text field for the date that it was eaten.</li>
     *     <li>A dropdown for the {@link Category}.</li>
     *     <li>A dropdown for the rating (0-10).</li>
     * </ul>
     *
     * <p>It handles optional data from the subtype and location fields by setting them to {@code null} if they are empty
     * strings before calling the {@link #newReviewHandler(String, int, Category, String, String, String)} method to
     * create the review object and add it to the database.</p>
     *
     * <p>The 'OK' button takes the input data and sends it to
     * {@link #newReviewHandler(String, int, Category, String, String, String)}, where the data is validated, and
     * it returns to let the action listener know if it successfully added the review to the database.
     * If it passes, it {@link #clearFields(JComponent[]) clears the fields}.
     * It also features a back button, which clears the fields and returns the page to the main menu.
     *
     * @return {@link JPanel} of the created add review page, complete with all listeners and JComponents.
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
            refreshListHandler();
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

    /**
     * Creates the find review page for locating specific reviews within the database.
     *
     * <p>This method creates a panel that features {@link JTextField input fields} for a review's name and optional
     * subtype along with an 'OK' button. The 'OK' button triggers an action listener, where if the subtype is empty, sets
     * them to {@code null}. It then calls {@link #findReviewHandler(String, String)} with the name and subtype, receiving
     * a boolean that represents if the data was found our not. If it was found, it
     * {@link #clearFields(JComponent[]) clears the text fields.}</p>
     *
     *
     * @return {@link JPanel} of the created find review page, complete with all listeners and JComponents.
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
            refreshListHandler();
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

    /**
     * Creates the update page for updating a pre-existing review, selected from the main menu.
     *
     * <p>This method features multiple {@link JTextField text fields} and {@link JComboBox dropdowns} for the user to
     * input and select data to update the review. It features a single 'OK' button for the user
     * to tell the program they are ready to send their input information.</p>
     * The featured fields are:
     * <ul>
     *     <li>A text field for the food's name.</li>
     *     <li>An optional text field for the food's subtype.</li>
     *     <li>An optional text field for the review location.</li>
     *     <li>A text field for the date that it was eaten.</li>
     *     <li>A dropdown for the {@link Category}.</li>
     *     <li>A dropdown for the rating (0-10).</li>
     * </ul>
     *
     * <p>These fields are all pre-filled with the information of the given {@link Review} object that was passed
     * to it. This allows the user to easily see what the old review was and what they want to edit.</p>
     *
     * <p>The 'OK' button takes the input data and sends it to
     * {@link #updReviewHandler(int, String, int, Category, String, String, String)}, where the data is validated, and
     * it returns to let the action listener know if it successfully updated the review. If it passes,
     * it {@link #clearFields(JComponent[]) clears the fields}. It also features a back button, which clears the fields
     * and returns the page to the main menu. This page is deleted upon completion of the task.</p>
     *
     * @param rev   The {@link Review} object that was selected by the user on the Main Menu to be updated.
     * @return {@link JPanel} of the created update review page, complete with all listeners and JComponents.
     */
    private JPanel updateReviewPage(Review rev){
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
            cardLayout.show(deck, "MAIN");
            refreshListHandler();
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
                refreshListHandler();
                cardLayout.show(deck, "MAIN");
                deck.remove(panel);
                deck.revalidate();
                deck.repaint();
            }
        });

        return panel;
    }

    /**
     * Creates a page for the user to select a category of food reviews to be calculated into an average review, filtered
     * by category.
     *
     * <p>This method creates a panel which features a single {@link JComboBox dropdown} of {@link Category categories},
     *  and an 'OK' button for the user to alert the system they are ready for the average of the selected category. It
     *  also features a back button, which resets the dropdown and returns to the main menu.</p>
     *
     *  <p>When the 'OK' button is pressed, {@link #avgReviewHandler(Category)} is called and returns a boolean. If it
     *  passes, it resets the dropdown.</p>
     *
     * @return {@link JPanel} of the created average review page, complete with all listeners and JComponents.
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
            refreshListHandler();
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

    /**
     * Synchronizes the UI table in {@link #mainMenuPage()} with the current state of the database.
     *
     * <p>This method clears all existing rows in the {@link #tableModel} and fetches a refreshed list of {@link Review}
     * objects from the {@link DatabaseHandler#getAll()} method. It iterates through the returned list, converting
     * each object into row format for the table to display. For visual clarity, subtype and location are converted to
     * {@code 'N/A'} if they are {@code null}.</p>
     */
    private void refreshListHandler(){
        tableModel.setRowCount(0);
        ArrayList<Review> allReviews = dbHandler.getAll();
        if(allReviews == null){
            return;
        }
        for (Review r: allReviews){
            Object[] data = new Object[7];
            data[0] = r.getID();
            data[1] =r.getName();
            data[2] = r.getRating();
            data[3] = r.getCategory();
            if(r.getSubtype() == null) {
                data[4] = "N/A";
            }else
                data[4] = r.getSubtype();
            if(r.getLocation() == null) {
                data[5] = "N/A";
            }else
                data[5] = r.getLocation();
            data[6] =  r.getDate();
            tableModel.addRow(data);
        }
    }

    /**
     * Clears all text fields and resets all dropdowns given in the array.
     * @param fields    An array of {@link JComponent} containing {@link JTextField JTextFields} and {@link JComboBox JComboBoxes}.
     */
    private void clearFields(JComponent[] fields){
        for(JComponent c: fields){
            if(c.getClass() == JTextField.class){
                ((JTextField) c).setText("");
            }else if (c.getClass() == JComboBox.class){
                ((JComboBox) c).setSelectedIndex(0);
            }
        }
    };

    /**
     * Creates a labeled JComponent
     *
     * <p>Takes a {@link JComponent} and creates a {@link JLabel} using the given text, combining them into a {@link JPanel}.
     * This text is set to the top of the component.</p>
     *
     * @param text  A {@link String} of the text that will be used to label the component.
     * @param field The {@link JComponent} to be labeled.
     * @return  {@link JPanel} containing the labeled component.
     */
    private JPanel createLabeledField(String text, JComponent field){
        JPanel panel = new JPanel(new BorderLayout(0,5));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // Logic/Button Handling Section

    /**
     * Appends a message to the on-screen console and automatically scrolls to the bottom.
     *
     * <p>This method adds the provided text content to the {@link javax.swing.JTextArea console} on the right-hand side
     * of the screen. It then updates the position to the end of the JComponent to ensure the most recent output is always
     * the first visible text to the user.</p>
     *
     * @param content   The {@link String} message to be displayed to the console.
     */
    private void printToConsole(String content){
        console.append(content + "\n");
        console.setCaretPosition(console.getDocument().getLength());
    }

    /**
     * Processes and validates new review data before attempting to create the object and send to the database to be saved.
     *
     * This method does the following:
     * <ol>
     *     <li>Generates a unique ID using {@link DatabaseHandler#idGenerator()}.</li>
     *     <li>Validates the format and length of {@code name, subtype, location, and date}</li>
     *     <li>Standardizes string formatting using {@link #stringFormatter(String)}.</li>
     *     <li>Constructs a {@link Review} object and passes it to {@link DatabaseHandler#addReview(Review)} to be
     *     saved to the database.</li>
     * </ol>
     *
     *
     *<p>If any data validation fails or the database fails to save, it displays an error message to the user and the process
     * is aborted.</p>
     *
     * @param name      The name of the food.
     * @param rating    The int rating of the review.
     * @param cat       The {@link Category} enum value.
     * @param subtype   The optional subtype, can be {@code null}.
     * @param location  The optional location, can be {@code null}.
     * @param date      The string representing the date (YYY-MM-DD).
     * @return  {@code true} if the review was successfully validated and added; {@code false} if any validation failed
     * or database insertion failed.
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
       Review r = new Review(id, name, rating, cat, subtype, location, trueDate);
       int added = dbHandler.addReview(r);
       if(added == 0){
           printToConsole("Error in creating review. Retry.");
           return false;
       }
       printToConsole("\n\nCreated new review\n-----------\n" + r.consoleToString());
       return true;
    }

    /**
     * Removes a given review from the system based on the provided ID string.
     *
     * <p>This method validates that the id input is a valid, positive integer. It then validates that it actually exists
     * in the database using the {@link DatabaseHandler#findReview(int)} method. It logs the details of the review to the
     * user and removes it from the database with {@link DatabaseHandler#remReview(int)}</p>
     *
     * <p>If it fails at any point in validating the id or interacting with the database, it returns false and alerts
     * the user. Otherwise, it alerts the user it was removed successfully and returns true.</p>
     *
     * @param input The ID string retrieved from the UI table.
     * @return  {@code true} if the review was successfully found and deleted from the database; {@code false} if the ID
     * was invalid, not found or a database error occurred.
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

    /**
     * Validates search criteria and queries the database for reviews matching the information.
     *
     * <p>This method validates that the {@code name} and optional {@code subtype} meet length and character requirements.
     * If the subtype is empty, it calls the {@link DatabaseHandler#findReview(String)} with only the name parameter.
     * Otherwise, it calls {@link DatabaseHandler#findReview(String, String)} with both to retrieve results. Matching
     * reviews are then displayed to the user. If no matches are found, a failure message is instead displayed.</p>
     *
     * @param name      The name of the food being searched.
     * @param subtype   The optional subtype to refine the search.
     * @return  {@code true} if at least one matching review was found; {@code false} if no reviews were found or
     * a database error occurred.
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

    /**
     * locates and retrieves a review with the matching ID input for the update page.
     *
     * <p>This method parses the ID from the provided string and verifies its existence in the database. If found,
     * the {@link Review} object is returned. If the ID is invalid or the record doesn't exist in the database,
     * the user is alerted and it returns null.</p>
     *
     * @param input The ID string retrieved from the UI table.
     * @return  {@code Review} object if successfully found; {@code null} if the ID is invalid, the review doesn't exist
     * or a database error occurs.
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

    /**
     * Takes review data then validates it, creating a review from the information and passing it to update an existing
     * record in the database.
     *
     * This method does the following:
     * <ol>
     *     <li>Validates the format and length of {@code name, subtype, location, and date}</li>
     *     <li>Standardizes string formatting using {@link #stringFormatter(String)}.</li>
     *     <li>Gathers the old ID and constructs a {@link Review} object, passing it to
     *     {@link DatabaseHandler#updateReview(int, Review)} to be saved to the database.</li>
     * </ol>
     *
     *
     *<p>If any data validation fails or the database fails to update, it displays an error message to the user and the
     * process.</p>
     *
     * @param id        The unique ID of the review being updated.
     * @param name      The updated name of the food item.
     * @param rating    The updated rating of the review.
     * @param cat       The updated category of the review.
     * @param subtype   The updated subtype of the review.
     * @param location  The updated location of the review.
     * @param date      The updated date of the review.
     * @return  {@code true} if validation passed and the database was updated; {@code false} if validation failed or
     * an error occurred.
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
        Review r = new Review(id, name, rating, cat, subtype, location, trueDate);
        int removed = dbHandler.remReview(id);
        if (removed == 0){
            printToConsole("Error in updating review. Retry.");
            return false;
        }
        int added = dbHandler.addReview(r);
        if(added == 0){
            printToConsole("Error in updating review. Retry.");
            return false;
        }
        printToConsole("\n\nUpdated review at " + id + "\n-------------\n" + r.consoleToString());
        return true;
    }

    /**
     * Retrieves and displays the average rating for a given category.
     *
     * <p>This method calls {@link DatabaseHandler#getAverage(Category)} to perform calculations the ratings of all records
     * matching the given {@link Category}. If an average that's 0 or more is returned, the result is formatted to two
     * decimal places and displayed to the user. If the database returns a negative value, a failure message is displayed
     * to the user.</p>
     *
     * @param category  The {@link Category} to be averaged.
     * @return  {@code true} if the average was successfully retried and displayed; {@code false} if the calculation failed
     * or no data was found.
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

    /**
     * Retrives all reviews from the database and prints them to the console.
     *
     * <p>This method retries the complete list of {@link Review} objects using the {@link DatabaseHandler#getAll()}
     * method. It iterates through the returned list, printing each review's details using the
     * {@link Review#consoleToString()} method for visual clarity. After the process is complete, it re-enables all buttons
     * for user interaction. If the database is empty, alerts the user.</p>
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

    /**
     * Validates a given 'name' string, in relation to name text fields.
     *
     * <p>Validates that the given input is between 2 and 100 characters and contains only letters.</p>
     *
     * @param input The string that represents the food name.
     * @return  {@code true} if the input was valid; {@code false} if the input was invalid.
     */
    private boolean nameValidation(String input){
        int l = input.length();
        if (l < 2 || l > 100 || input.isBlank() || input.matches(".*[^a-zA-Z ].*"))
            return false;
        return true;
    }

    /**
     * Validates a given 'subtype' string, in relation to subtype text fields.
     *
     * <p>Validates that the given input is between 2 and 50 characters and contains only letters.</p>
     *
     * @param input The string that represents the food subtype.
     * @return  {@code true} if the input was valid; {@code false} if the input was invalid.
     */
    private boolean subtypeValidation(String input){
        int l = input.length();
        if (l < 2 || l > 50 || input.isBlank() || input.matches(".*[^a-zA-Z ].*"))
            return false;
        return true;
    }

    /**
     * Validates a given 'name' string, in relation to location text fields.
     *
     * <p>Validates that the given input is between 2 and 100 characters.</p>
     *
     * @param input The string that represents the location.
     * @return  {@code true} if the input was valid; {@code false} if the input was invalid.
     */
    private boolean locationValidation(String input){
        int l = input.length();
        if (l < 2 || l > 100 || input.isBlank())
            return false;
        return true;
    }

    /**
     * Validates a given 'date' string, in relation to date text fields.
     *
     * <p>Validates that the given input is able to be converted into a {@link LocalDate}. Also validates if the date
     * is real and not set in the future.</p>
     *
     * @param input The string that represents the date (YYYY-MM-DD).
     * @return  {@link LocalDate Converted Date} if the input was valid; {@code null} if the input was invalid.
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

    /**
     * Formats a string to title case, ensuring consistent data entry.
     *
     * <p>This method splits the given input into words and capitalizes the first letter of each word utilizing a for each
     * loop. Every other letter is made to be lowercase. Use this to standardize text input before storage in the database.</p>
     *
     * @param input The text to be formatted.
     * @return  {@link String}, formatted to title-case;
     */
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
