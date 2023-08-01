

import java.sql.*;
import java.util.Scanner;

public class Customer {

    // Class representing a product with encapsulation
    private static class Product {
        private int id;
        private String productName;
        private String productQuantity;
        private String date;

        public Product(int id, String productName, String productQuantity, String date) {
            this.id = id;
            this.productName = productName;
            this.productQuantity = productQuantity;
            this.date = date;
        }

        // Getters for the private fields
        public int getId() {
            return id;
        }

        public String getProductName() {
            return productName;
        }

        public String getProductQuantity() {
            return productQuantity;
        }

        public String getDate() {
            return date;
        }

        // toString method for printing product details
        @Override
        public String toString() {
            return id + " - " + productName + " - " + productQuantity + " - " + date;
        }
    }

    // Interface representing a product request hook for polymorphism
    private interface ProductRequestHook {
        void onAccept(int requestId);
        void onReject(int requestId);
    }

    // Class representing a product request hook with custom behavior for onAccept and onReject
    private static class MyProductRequestHook implements ProductRequestHook {
        @Override
        public void onAccept(int requestId) {
            System.out.println("Product request ID " + requestId + " has been accepted. Custom hook behavior.");
            // Add your custom logic here for the onAccept hook (e.g., notify someone, update inventory, etc.)
        }

        @Override
        public void onReject(int requestId) {
            System.out.println("Product request ID " + requestId + " has been rejected. Custom hook behavior.");
            // Add your custom logic here for the onReject hook (e.g., notify someone, update inventory, etc.)
        }
    }

    public void eventdetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/product";
            String user = "root";
            String password = "agalya@2004";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Welcome to Inventory Management(Customer)");
                System.out.println("==============================================");

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Product");
                    System.out.println("2. Book Product");
                    System.out.println("3. Exit");
                    System.out.println("Enter your choice");
                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            listProduct(connection);
                            break;
                        case "2":
                            AddProduct(connection, scanner, new MyProductRequestHook()); // Use MyProductRequestHook here
                            break;
                        case "3":
                            System.out.println("Exiting the customer.");
                            
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listProduct(Connection connection) throws SQLException {
        String query = "SELECT * FROM products";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Products List:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productName = resultSet.getString("productname");
        
                System.out.println(id + " - " + productName);
            }
        }
    }

    private static void AddProduct(Connection connection, Scanner scanner, ProductRequestHook hook) throws SQLException {
        System.out.println("Enter the ID of the product you want to Add: ");
        int productId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the Product Name: ");
        String productName = scanner.nextLine();
        System.out.println("Enter the Product Quantity: ");
        String productQuantity = scanner.nextLine();
        System.out.println("Enter the Date: ");
        String date = scanner.nextLine();

        String checkProductQuery = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement checkProductStmt = connection.prepareStatement(checkProductQuery)) {
            checkProductStmt.setInt(1, productId);
            ResultSet productResult = checkProductStmt.executeQuery();

            if (productResult.next()) {
                String productname = productResult.getString("productname");
               
                System.out.println("Product Details:");
                System.out.println("Product ID: " + productId);
                System.out.println("Product Name: " + productName);
                System.out.println("Product Quantity: " + productQuantity);
                System.out.println("Date: " + date);

                System.out.println("Confirm Update? (Y/N): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("y")) {
                    String insertOrderQuery = "INSERT INTO orders (product_id, product_name, date, product_quantity) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderQuery)) {
                        insertOrderStmt.setInt(1, productId);
                        insertOrderStmt.setString(2, productName);
                        insertOrderStmt.setString(3, date);
                        insertOrderStmt.setString(4, productQuantity);

                        int rowsAffected = insertOrderStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Your adding request of the product '" + productName + "' has been sent to the admin.");

                            // Custom behavior using the ProductRequestHook
                            hook.onAccept(productId);
                        } else {
                            System.out.println("Failed to add the product. Please try again.");
                            hook.onReject(productId);
                        }
                    }
                } else {
                    System.out.println("Updation canceled.");
                }
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        }
    }

    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.eventdetails();
    }
}