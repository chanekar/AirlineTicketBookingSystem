package app;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
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
		
		db.listFlights();
		
		db.closeConnection();
		
		viewFlights(c);

	}
	
	public static void viewFlights(User c) {
		
		// TODO sorting, reviews to each flight
		
		System.out.println();
		System.out.println("Viewing flights to " + c.destination + ".");
		System.out.println();
		
		SqliteDB db = new SqliteDB();
		// db.listFlights("price");
		Connection conn = db.c;
		DBTablePrinter.printTable(conn, "Flights", "airline");
		
		Scanner sc = new Scanner(System.in);
		System.out.println();
		System.out.print("Enter Preferred Flight ID: ");
		c.bookedFlightID = sc.nextLine(); 
		db.enterInfo(c, "Users", "flightID", c.bookedFlightID);
		
		db.closeConnection();
		System.exit(0);
	}
	
	public static void reviewTickets(User c) {
		System.out.println();
		System.out.println("Reviewing booked tickets to " + c.destination + ".");
		
		System.exit(0);
	}

}