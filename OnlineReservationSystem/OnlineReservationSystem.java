import java.sql.*;
import java.util.Scanner;

public class OnlineReservationSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reservation_system";
    private static final String DB_USER = "gowtham";
    private static final String DB_PASSWORD = "1212";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database.");

            Scanner scanner = new Scanner(System.in);

            // Login
            System.out.println("=== Login ===");
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            if (isValidUser(connection, username, password)) {
                System.out.println("Login successful!");

                // Reservation System
                System.out.println("=== Reservation System ===");
                makeReservation(connection);

                // Cancellation Form
                System.out.println("=== Cancellation Form ===");
                cancelReservation(connection);
            } else {
                System.out.println("Invalid credentials. Exiting...");
            }

            connection.close();
            System.out.println("Disconnected from the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidUser(Connection connection, String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    private static void makeReservation(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        // User input for reservation
        System.out.print("Enter your basic details: ");
        String basicDetails = scanner.nextLine();
        System.out.print("Enter train number: ");
        int trainNumber = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        System.out.print("Enter class type: ");
        String classType = scanner.nextLine();
        System.out.print("Enter date of journey: ");
        String journeyDate = scanner.nextLine();
        System.out.print("Enter departure place: ");
        String departurePlace = scanner.nextLine();
        System.out.print("Enter destination: ");
        String destination = scanner.nextLine();

        // Implement reservation logic
        try {
            String insertQuery = "INSERT INTO reservations (user_id, train_number, class_type, journey_date, departure_place, destination) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                // Assuming user_id is obtained from the logged-in user's information
                preparedStatement.setInt(1, getUserId(connection, "logged_in_username"));
                preparedStatement.setInt(2, trainNumber);
                preparedStatement.setString(3, classType);
                preparedStatement.setString(4, journeyDate);
                preparedStatement.setString(5, departurePlace);
                preparedStatement.setString(6, destination);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cancelReservation(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        // User input for cancellation
        System.out.print("Enter your PNR number: ");
        int pnrNumber = scanner.nextInt();

        // Implement cancellation logic
        try {
            String selectQuery = "SELECT * FROM reservations WHERE pnr_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setInt(1, pnrNumber);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Display information related to the PNR number
                    System.out.println("PNR Number: " + resultSet.getInt("pnr_number"));
                    System.out.println("Train Number: " + resultSet.getInt("train_number"));
                    System.out.println("Class Type: " + resultSet.getString("class_type"));
                    System.out.println("Journey Date: " + resultSet.getString("journey_date"));
                    System.out.println("Departure Place: " + resultSet.getString("departure_place"));
                    System.out.println("Destination: " + resultSet.getString("destination"));

                    // Ask for confirmation
                    System.out.print("Do you want to confirm cancellation? (Press OK to confirm): ");
                    String confirmation = scanner.next();

                    if (confirmation.equalsIgnoreCase("OK")) {
                        // Implement cancellation logic
                        String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ?";
                        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                            deleteStatement.setInt(1, pnrNumber);
                            int rowsAffected = deleteStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Cancellation confirmed. Ticket canceled successfully.");
                            } else {
                                System.out.println("Cancellation failed. Please try again.");
                            }
                        }
                    } else {
                        System.out.println("Cancellation aborted.");
                    }
                } else {
                    System.out.println("No reservation found for the given PNR number.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getUserId(Connection connection, String username) throws SQLException {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            } else {
                throw new SQLException("User not found with username: " + username);
            }
        }
    }
}
