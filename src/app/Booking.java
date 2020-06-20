package app;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

/**
 *
 * 
 * @author eluthro
 *
 */

public class Booking {
	
	public static void selectOptions(User c) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println();
		System.out.println("Please enter the following information:");
		
		System.out.print("First Name: ");
		c.firstname = sc.nextLine(); 
		System.out.print("Last Name: ");
		c.lastname = sc.nextLine(); 
		
		System.out.print("Destination: ");
		c.destination = sc.nextLine(); 
		
		SqliteDB db = new SqliteDB();
		db.newUser(c.username);
		db.enterInfo(c, "Users", "firstname", c.firstname);
		db.enterInfo(c, "Users", "lastname", c.lastname);
		db.enterInfo(c, "Users", "destination", c.destination);
		db.enterBooked(c, true);
		
		db.closeConnection();
		
		viewFlights(c, "airline");

	}
	
	public static void viewFlights(User c, String sort) {
		
		// TODO sorting, reviews to each flight
		
		System.out.println();
		System.out.println("Viewing flights to " + c.destination + ".");
		System.out.println();
		
		SqliteDB db = new SqliteDB();
		// db.listFlights("price");
		Connection conn = db.c;
		DBTablePrinter.printTable(conn, "Flights", sort, c.destination, c);
		
		System.out.println();
		System.out.println("1. Sort Flights");
		System.out.println("2. Book Ticket");
		System.out.println();
		System.out.print("To select an option, enter number: ");
		
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		System.out.println();
		if (input.equals("1")) {
			System.out.println("Sort Flights: ");
			System.out.println("1. Date");
			System.out.println("2. Airline");
			System.out.println("3. Departure");
			System.out.println("4. # of Empty Seats");
			System.out.println();
			System.out.print("To select an option, enter number: ");
			String newInput = sc.nextLine();
			if (newInput.equals("1")) viewFlights(c, "date");
			if (newInput.equals("2")) viewFlights(c, "airline");
			if (newInput.equals("3")) viewFlights(c, "departure");
			if (newInput.equals("4")) viewFlights(c, "emptySeats");
		}  if (input.equals("2")) {
			System.out.print("Enter Preferred Flight ID: ");
			c.bookedFlightID = sc.nextLine(); 
			db.enterInfo(c, "Users", "flightID", c.bookedFlightID);
			// db.closeConnection();
			flightInfo(c);
		}
	
		System.exit(0);
	}
	
	public static void flightInfo(User c) {
		
		SqliteDB db = new SqliteDB();
		
		db.listReviews(c.bookedFlightID);
		
		
		System.out.println();
		System.out.println("Enter following information: ");
		Scanner sc = new Scanner(System.in);
		System.out.print("# of Adults: ");
		String numAdults = sc.nextLine();
		db.enterInfo(c, "Users", "adults", numAdults);
		System.out.print("# of Children: ");
		String numChildren = sc.nextLine();
		db.enterInfo(c, "Users", "children", numChildren);
		System.out.print("Enter Class Number [(1) First, (2) Business, (3) Economy]: ");
		db.enterInfo(c, "Users", "class", sc.nextLine());
		int a = Integer.parseInt(numAdults); c.adults = a;
		int ch = Integer.parseInt(numChildren);  c.children = ch;
		System.out.print("Your booking comes with a maximum of " + (a+ch)*2 + " bag(s).");
		System.out.println();
		
		ResultSet rs = db.executeQuery("SELECT * FROM Flights WHERE id='" + c.bookedFlightID + "'");
		int price = 0;
		try {
			while (rs.next()) { 
				price = rs.getInt("price");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.print("Total cost: $" + (price*(a) + price*0.90*ch));
		System.out.println();
		
		db.executeUpdate("UPDATE Flights SET emptySeats = emptySeats - " + (a+ch) + " WHERE id='" + c.bookedFlightID + "' and emptySeats > 0");
		db.closeConnection(); 
		paymentSystem(c);
	}
	
	public static void paymentSystem(User c) {
		
		System.out.println();
		System.out.println("Payment System");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Card Holder Name: ");
		sc.nextLine();
		System.out.print("Credit Card Number: ");
		sc.nextLine();
		System.out.print("Expiration (MM/YYYY): ");
		sc.nextLine();
		System.out.print("CVV: ");
		sc.nextLine();
		System.out.println();
		System.out.println("Transaction complete. Enjoy your flight!");
		c.hasBooked = true;
		Startup.loggedIn(c);
	}
	
	public static void reviewTickets(User c) {
		
		System.out.println();
		System.out.println("Tickets have been booked to " + c.destination + ", for " 
				+ c.adults + " adults and " + c.children + " children in " + c.flightClass + " class.");
		System.out.println();
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Would you like to write a review? [(1) Yes / (2) No] ");
		if (sc.nextLine().equals("1")) {
			
			SqliteDB db = new SqliteDB();
			System.out.print("From 1 to 5, what would you rate your experience? ");
			String rating = sc.nextLine();
			System.out.print("Write your review: ");
			String review = sc.nextLine();
			
			db.executeUpdate("INSERT INTO Reviews (flightID, firstname, lastname, rating, review) VALUES ('" + c.bookedFlightID 
					+ "', '" + c.firstname + "', '" + c.lastname + "', '" + rating + "', '" + review + "')");
			
			System.out.println("Thank you for your review!");
			Startup.loggedIn(c);
			
		} else {
			System.out.println("Enjoy your trip!");
			Startup.loggedIn(c);
		}
		
	}
	
	

}