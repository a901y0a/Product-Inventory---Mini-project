import java.sql.*;
import java.util.Scanner;

public class Customer {

    public void eventdetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/product";
            String user = "root";
            String password = "agalya@2004";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Welcome to PRODUCT INVENTORY MANAGEMENT SYSTEM(Customer)");
                System.out.println("==============================================");

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Stocks");
                    System.out.println("2. Book Stock");
                    System.out.println("3. Exit");
                    System.out.println("Enter your choice");
                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            listStocks(connection);
                            break;
                        case "2":
                            AddStock(connection, scanner);
                            break;
                        case "3":
                            System.out.println("Exiting the customer.");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listStocks(Connection connection) throws SQLException {
        String query = "SELECT * FROM products";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("Products List:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String productname = resultSet.getString("productname");
                

                System.out.println(id + " - " + productname);
            }
        }
    }

    private static void AddStock(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the product you want to Add: ");
        int productId = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the Product Name: ");
        String productName=scanner.nextLine();
        System.out.println("Enter the Product Quantity: ");
        String productQuantity=scanner.nextLine();
        System.out.println("Enter the  Date: ");
        String Date=scanner.nextLine();
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
                System.out.println("Enter Date: " + Date);

                System.out.println("Confirm Update? (Y/N): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("y")) {
                    String insertOrderQuery = "INSERT INTO orders (product_id, product_name, date, product_quantity) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderQuery)) {
                        insertOrderStmt.setInt(1, productId);
                        insertOrderStmt.setString(2, productName);
                        insertOrderStmt.setString(3, Date);
                        insertOrderStmt.setString(4, productQuantity);

                        int rowsAffected = insertOrderStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Your adding request of the product '" + productName + "' has been sent to the admin.");
                        } else {
                            System.out.println("Failed to add the product. Please try again.");
                        }
                    }
                } else {
                    System.out.println("updation canceled.");
                }
            } else {
                System.out.println("Product with ID " + productId + " not found.");
            }
        }
    }
}