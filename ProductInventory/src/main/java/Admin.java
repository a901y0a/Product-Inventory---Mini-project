import java.sql.*;
import java.util.Scanner;

// Abstract class to represent a Manager
abstract class Manager {
    // Abstract method for manager details
    public abstract void managerDetails();

    // Method to view product requests (common implementation)
    protected void viewProductRequests(Connection connection) throws SQLException {
        String query = "SELECT * FROM orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No Product requests found.");
                return;
            }

            System.out.println("Product Requests:");
            while (resultSet.next()) {
                int ProductId = resultSet.getInt("product_id");
                String ProductName = resultSet.getString("product_name");
                String Date = resultSet.getString("date");
                String ProductQuantity = resultSet.getString("product_quantity");

                System.out.println(ProductId + " - " + ProductName + " - " + Date + " - " + ProductQuantity);
            }
        }
    }
}

// Interface to represent a ProductRequestHook
interface ProductRequestHook {
    void onAccept(int requestId);
    void onReject(int requestId);
}

// Class representing an Administrator (inherits from Manager)
class Administrator extends Manager {

    private ProductRequestHook hook; // Reference to the hook

    // Setter method to set the hook
    public void setProductRequestHook(ProductRequestHook hook) {
        this.hook = hook;
    }

    // Implementing the managerDetails method from the Manager abstract class
    @Override
    public void managerDetails() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/product";
            String user = "root";
            String password = "agalya@2004";

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                System.out.println("Welcome to Inventory Management System (Manager)");
                System.out.println("==============================================");

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Product Requests");
                    System.out.println("2. Accept Product Request");
                    System.out.println("3. Reject Product Request");
                    System.out.println("4. Exit");

                    Scanner scanner = new Scanner(System.in);
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            viewProductRequests(connection); // Call the common implementation
                            break;
                        case "2":
                            acceptProductRequest(connection, scanner);
                            break;
                        case "3":
                            rejectProductRequest(connection, scanner);
                            break;
                        case "4":
                            System.out.println("Exiting the manager application.");
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

    // Method to accept a product request
    private void acceptProductRequest(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the Product request you want to accept: ");
        int requestId;
        try {
            requestId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer ID.");
            return; // Exit the method to avoid further processing with invalid input
        }

        String updateQuery = "UPDATE orders SET status = 'Accepted' WHERE product_id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, requestId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product request ID " + requestId + " has been accepted.");
                // If the hook is set, call the hook's onAccept method
                if (hook != null) {
                    hook.onAccept(requestId);
                }
            } else {
                System.out.println("Product request ID " + requestId + " not found, or it is already accepted.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while updating the request. Please try again.");
            e.printStackTrace();
        }
    }

    // Method to reject a product request
    private void rejectProductRequest(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter the ID of the Product request you want to reject: ");
        int requestId;
        try {
            requestId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer ID.");
            return; // Exit the method to avoid further processing with invalid input
        }

        String updateQuery = "UPDATE orders SET status = 'Rejected' WHERE product_id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, requestId);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product request ID " + requestId + " has been rejected.");
                // If the hook is set, call the hook's onReject method
                if (hook != null) {
                    hook.onReject(requestId);
                }
            } else {
                System.out.println("Product request ID " + requestId + " not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while updating the request. Please try again.");
            e.printStackTrace();
        }
    }
}

// Implementation of the ProductRequestHook
class MyProductRequestHook implements ProductRequestHook {
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

    public static void main(String[] args) {
        Administrator administrator = new Administrator();
        administrator.setProductRequestHook(new MyProductRequestHook()); // Set the hook with custom behavior
        administrator.managerDetails();
    }
}