import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public boolean validatePassword(String password) { return this.password.equals(password); }
}

class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void setStock(int stock) { this.stock = stock; }

    public void displayProduct() {
        System.out.printf("ID: %d, Name: %s, Price: %.2f rupees, Stock: %d\n", id, name, price, stock);
    }
}

class StationeryItem extends Product {
    private String category;
    private String brand;

    public StationeryItem(int id, String name, double price, int stock, String category, String brand) {
        super(id, name, price, stock);
        this.category = category;
        this.brand = brand;
    }

    public String getCategory() { return category; }
    public String getBrand() { return brand; }

    
    public void displayProduct() {
        super.displayProduct();
        System.out.println("Category: " + category + ", Brand: " + brand);
    }
}

class Admin extends User {
    private ArrayList<Product> productInventory;
    private ArrayList<Product> cart;

    public Admin(String username, String password) {
        super(username, password);
        productInventory = new ArrayList<>();
        cart = new ArrayList<>();
    }

    public void addProduct(Product product) {
        productInventory.add(product);
    }

    public ArrayList<Product> getProductInventory() { return productInventory; }

    public ArrayList<Product> getCart() { return cart; }

    public void addToCart(Product product) {
        if (product.getStock() > 0) {
            cart.add(product);
            product.setStock(product.getStock() - 1);
            System.out.println(product.getName() + " added to cart.");
        } else {
            System.out.println("Sorry, this product is out of stock.");
        }
    }

    public void removeFromCart(Product product) {
        cart.remove(product);
        product.setStock(product.getStock() + 1);
        System.out.println(product.getName() + " removed from cart.");
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();
        for (Product product : productInventory) {
            if (product instanceof StationeryItem) {
                StationeryItem item = (StationeryItem) product;
                if (!categories.contains(item.getCategory())) {
                    categories.add(item.getCategory());
                }
            }
        }
        return categories;
    }
}

public class ShoppingECommerceApp extends Application {

    private Admin admin;

    public ShoppingECommerceApp() {
        admin = new Admin("adminUser", "securePass");
        initializeInventory();
    }

    private void initializeInventory() {
        admin.addProduct(new StationeryItem(1, "Pen", 15.0, 100, "Pen", "BIC"));
        admin.addProduct(new StationeryItem(2, "Pen", 20.0, 150, "Pen", "Parker"));
        admin.addProduct(new StationeryItem(3, "Pen", 25.0, 150, "Pen", "Pilot"));
        admin.addProduct(new StationeryItem(4, "Notebook", 250.0, 50, "Notebook", "Moleskine"));
        admin.addProduct(new StationeryItem(5, "Notebook", 300.0, 50, "Notebook", "PaperGrid"));
        admin.addProduct(new StationeryItem(6, "Eraser", 5.0, 200, "Utility", "Staedtler"));
        admin.addProduct(new StationeryItem(7, "Eraser", 7.0, 200, "Utility", "Doms"));
    }

   
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ShoppingE-Commerce");

        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(admin.getCategories());
        categoryBox.setPromptText("Select a Category");

        ListView<Product> productListView = new ListView<>();
        ListView<Product> cartListView = new ListView<>();

        Button addToCartButton = new Button("Add to Cart");
        Button removeFromCartButton = new Button("Remove from Cart");

        categoryBox.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
           
            public void handle(javafx.event.ActionEvent event) {
                String selectedCategory = categoryBox.getValue();
                displayProductsByCategory(selectedCategory, productListView);
            }
        });

        addToCartButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            
            public void handle(javafx.event.ActionEvent event) {
                Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    admin.addToCart(selectedProduct);
                    cartListView.getItems().setAll(admin.getCart());
                } else {
                    SAlert("No product selected", "Please select a product to add to the cart.");
                }
            }
        });

        removeFromCartButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            
            public void handle(javafx.event.ActionEvent event) {
                Product selectedProduct = cartListView.getSelectionModel().getSelectedItem();
                if (selectedProduct != null) {
                    admin.removeFromCart(selectedProduct);
                    cartListView.getItems().setAll(admin.getCart());
                } else {
                    SAlert("No product selected", "Please select a product to remove from the cart.");
                }
            }
        });

        HBox buttons = new HBox(10, addToCartButton, removeFromCartButton);
        root.getChildren().addAll(categoryBox, productListView, buttons, new Label("Cart:"), cartListView);

        Scene s = new Scene(root, 500, 500);
        primaryStage.setScene(s);
        primaryStage.show();
    }

    private void displayProductsByCategory(String category, ListView<Product> productListView) {
        ArrayList<Product> productsByCategory = new ArrayList<>();
        for (Product product : admin.getProductInventory()) {
            if (product instanceof StationeryItem) {
                StationeryItem item = (StationeryItem) product;
                if (item.getCategory().equalsIgnoreCase(category)) {
                    productsByCategory.add(item);
                }
            }
        }
        productListView.getItems().setAll(productsByCategory);
    }

    private void SAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}