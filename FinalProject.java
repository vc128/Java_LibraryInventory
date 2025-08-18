import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class FinalProject extends Application {
    private boolean showFields = false; // Flag to track field visibility
    private List<Book> books;
    private VBox layout = new VBox(10);;
    private TableView<Book> bookTableView = new TableView<>(); //set up var for book display table
    private Button saveButton;
    private Button cancelButton;
    private Button loanButton;
    // Text fields for book details (initially hidden)
    private TextField titleField = new TextField();
    private TextField authorField = new TextField();
    private TextField availabilityField = new TextField();
    private HBox saveButtonBox = new HBox(10); // 10 is spacing between buttons
    private TableRow<Book> selectedRow;


    public static void main(String[] args) {
    	launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Inventory");

        // Read book information from the file
        books = readBooksFromFile(".\\src\\books.txt");

        // Create a list view to display book titles
        ListView<String> bookListView = new ListView<>();

        String title = null;
        String author = null;
        String availability = null;
        
     // Create UI components and set actions for each button
        Button addButton = new Button("Add Book");
        Button deleteButton = new Button("Delete Book");
        Button loanButton = new Button("Loan Book");
        loanButton.setOnAction(event -> loanSelectedBook());
        Button returnButton = new Button("Return Book");
        returnButton.setOnAction(event -> returnSelectedBook());
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> exitProgram());
        saveButton = new Button("Save");
        saveButton.setVisible(false); // Initially hidden
        saveButton.setOnAction(event -> saveBookInfo());
        cancelButton = new Button("Cancel");
        cancelButton.setVisible(false); // Initially hidden
        cancelButton.setOnAction(event -> cancelBookInfo());
        deleteButton.setOnAction(event -> deleteSelectedBook());
        
     // Text fields for book details (initially hidden)
        titleField.setPromptText("Title");
        authorField.setPromptText("Author");
        availabilityField.setPromptText("Availability");
        
     // Create columns for title, author, and page count
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        //display title in table view
        titleColumn.setCellValueFactory(cellData -> {
            String titleVal = cellData.getValue().getTitle();
            return new SimpleStringProperty(titleVal);
        });


        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        //display author in table view
        authorColumn.setCellValueFactory(cellData -> {
            String authorVal = cellData.getValue().getAuthor();
            return new SimpleStringProperty(authorVal);
        });

        TableColumn<Book, String> availabilityColumn = new TableColumn<>("Availability");
        
        //display availability in table view
        availabilityColumn.setCellValueFactory(cellData -> {
            boolean isAvailable = cellData.getValue().isAvailability();
            return new SimpleStringProperty(isAvailable ? "Y" : "N");
        });

        // Add columns to the table view
        bookTableView.getColumns().addAll(titleColumn, authorColumn, availabilityColumn);

        // Add book data to the table view
        bookTableView.getItems().addAll(books);

        // Arrange buttons horizontally
        HBox buttonBox = new HBox(10); // 10 is spacing between buttons
        buttonBox.getChildren().addAll(addButton,
                deleteButton,
                loanButton,
                returnButton,
                exitButton);

        saveButtonBox.getChildren().addAll(saveButton,
                cancelButton);
        
        // Set up layout
        layout.getChildren().addAll(
                new javafx.scene.control.Label("Library Inventory"),
                bookTableView,
                buttonBox
        );
        bookTableView.setPrefHeight(700);

        // Add Book button action
        addButton.setOnAction(event -> {
        	if (!showFields) {
                showFields = true;
            }
        	toggleFieldsAndButtons(showFields);
        });
        

        // Set up scene
        Scene scene = new Scene(layout, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    //loan book functions
    public void loanSelectedBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (!selectedBook.isAvailability()) {
                // Show validation error (display a message to the user)
                System.out.println("Cannot loan book. Currently unavailable.");
            } else {
                // Update availability to "N" (false)
                selectedBook.setAvailability(false);
                // Refresh the TableView to reflect the change
                bookTableView.refresh();
                saveBooksToFile(books);
            }
        }
    }
    
    //return book functions
    public void returnSelectedBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (selectedBook.isAvailability()) {
                // Show validation error (display a message to the user)
                System.out.println("Book is already returned.");
            } else {
                // Update availability to "Y" (true)
                selectedBook.setAvailability(true);
                // Refresh the TableView to reflect the change
                bookTableView.refresh();
                saveBooksToFile(books);
            }
        }
    }
    
    //save book functions
    public void saveBooksToFile(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(".\\src\\books.txt"))) {
            for (Book book : books) {
                String line = book.getTitle() + "," + book.getAuthor() + "," + (book.isAvailability()? "Y" : "N");
                writer.write(line);
                System.out.println(line);
                writer.newLine();
            }
            System.out.println("file saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveBookInfoToArrayList(Book book) {
    	books.add(book);
    }
    
    public void saveBookInfo() {
    	Book newBook = new Book(titleField.getText(), 
    			authorField.getText(),
    			parseCustomBoolean(availabilityField.getText()));
    	saveBookInfoToArrayList(newBook);
    	saveBooksToFile(books);
    	//updates the table view with the new book info
    	bookTableView.getItems().addAll(newBook);
    	toggleFieldsAndButtons(false);
    }
    
    //cancel button functions    
    private void cancelBookInfo() {
		toggleFieldsAndButtons(false);
    }
    
    //delete button functions
    public void deleteSelectedBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            books.remove(selectedBook);
	        saveBooksToFile(books);
	        //updates the table view with the new book info
	    	bookTableView.getItems().remove(selectedBook);
        }

    }
    
    //exit book functions
    public static void exitProgram() {
        System.out.println("Exiting the program...");
        System.exit(0); // 0 indicates a normal termination
    }
    
    // Show/Hide the Book  detail fields and save, cancel buttons when book info is entered
    private void toggleFieldsAndButtons(boolean Visible) {
    	if (Visible)
    	{
    		layout.getChildren().addAll(titleField, authorField, availabilityField, saveButtonBox);
    	}
    	else
    	{
            layout.getChildren().removeAll(titleField, authorField, availabilityField, saveButtonBox);
    	}
    	saveButton.setVisible(Visible);
        cancelButton.setVisible(Visible);
    }
    
    public boolean parseCustomBoolean(String s) {
        if (s == null) {
            return false; // Default to false if input is null
        }
        return s.trim().equalsIgnoreCase("Y");
    }
    
    private List<Book> readBooksFromFile(String filePath) {
        List<Book> bookArray = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String title = parts[0];
                    String author = parts[1];
                    boolean availability = parts[2].equalsIgnoreCase("Y");
                    bookArray.add(new Book(title, author, availability));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookArray;
    }

    // Custom Book class
    private static class Book {
    	private final SimpleStringProperty title;
        private final SimpleStringProperty author;
        private final SimpleBooleanProperty availability;

        public Book(String title, String author, boolean availability) {
            this.title = new SimpleStringProperty(title);
            this.author = new SimpleStringProperty(author);
            this.availability = new SimpleBooleanProperty(availability);
        }


        // Getters
        public String getTitle() {
            return title.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public boolean isAvailability() {
            return availability.get();
        }

        // Setters (if needed)
        public void setTitle(String title) {
            this.title.set(title);
        }

        public void setAuthor(String author) {
            this.author.set(author);
        }

        public void setAvailability(boolean availability) {
            this.availability.set(availability);
        }
    }
}
