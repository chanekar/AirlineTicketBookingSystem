package app;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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

public class Startup {
	
	public static void main(String args[]) throws Exception {
		
//		SqliteDB db = new SqliteDB();
//		
//		db.listFlights();
//		
//		db.closeConnection();
		User c = new User("jsmith");
		c.destination = "Toronto";
		c.bookedFlightID = "1";
		Booking.flightInfo(c);

//		System.out.println("Airline Ticket Booking System");
//		welcome();	
		
	}
	
	// first screen that user views as program is started
	public static void welcome() throws NoSuchAlgorithmException {
		
		System.out.println();
		System.out.println("1. Log In");
		System.out.println("2. Sign Up");
		System.out.println("3. Quit");
		System.out.println();
		System.out.print("To select an option, enter number: ");
	
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine(); 
		
		if (input.equals("1")) login(0);
		if (input.equals("2")) signup(0);
		if (input.equals("3")) {
			System.out.println("Logged out.");
			System.exit(0);
		} else welcome();
		
	}
	
	// login in screen
	public static void login(int count) throws NoSuchAlgorithmException {
		
		if (count == 3) welcome();
		
		Scanner sc = new Scanner(System.in);
		System.out.println();
		System.out.print("Username: ");
		String username = sc.nextLine();
		
		
		if (userExists(username)) {
			System.out.print("Password: ");
			String password = sc.nextLine();
			
			User cur = new User(username);
			cur.getIndex();
			String stored = cur.getPass();
			String entered = generateHash(password, "SHA-256", hexStringToByteArray(cur.getSalt()));
			
			if (!stored.equals(entered)) {
				System.out.println("Password does not match. Try again.");
				login(count += 1);
			}
			
			
		} else {
			System.out.println("Username does not exist. Try again.");
			login(count += 1);
		}
		
		User cur = new User(username);
		loggedIn(cur);
		
	}
	
	// sign up screen
	public static void signup(int count) throws NoSuchAlgorithmException {
		
		if (count == 3) {
			welcome();
		}
		
		System.out.println();
		System.out.println("Please enter the following information: ");
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Username: ");
		String username = sc.nextLine();
		
		
		// username does not exist
		if (userExists(username) == false) {
			User cur = new User(username);
			System.out.print("Password: ");
			String pwd = sc.nextLine();
			
			appendString("passwords.txt", generateHash(pwd, "SHA-256", createSalt()));
			
			System.out.println();
			System.out.println("Account created.");
			
			SqliteDB db = new SqliteDB();
			db.newUser(username);
			db.closeConnection();
			
			appendString("users.txt", username);
			loggedIn(cur);
			
		} else {
			System.out.println("Username already exists. Enter new username.");
			while (userExists(username)) signup(count += 1);
		}
		
		
	}
	
	// check to see if username exists in users.txt
	public static boolean userExists(String username) {
		
		try {
			Path path = Paths.get("users.txt");
			Scanner sc = new Scanner(path);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.equals(username)) return true;
			}
			
			sc.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
	
	return false;
	
	}
	
	// adds String information to .txt file
	public static void appendString(String file, String info) {
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.append("\r\n" + info);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loggedIn(User cur) {
		
		// System.out.println("Logged in as " + cur.getName() + ".");
		// System.out.println();
		
		System.out.println();
		System.out.println("1. Book Tickets");
		if (cur.hasBooked) {
			System.out.println("2. Review Booked Tickets");
			System.out.println("3. Log Out");
		} else {
			System.out.println("2. Log Out");
		}
		System.out.println();
		System.out.print("To select an option, enter number: ");
		
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine(); 
		
		if (input.equals("1")) {
			Booking.selectOptions(cur);
		} else if (input.equals("2") && !cur.hasBooked) {
			System.out.println("Logged out.");
			System.exit(0);
		} else if (input.equals("2") && cur.hasBooked){
			Booking.reviewTickets(cur);
		} else if (input.equals("3")){
			System.out.println("Logged out.");
			System.exit(0);
		} else loggedIn(cur);
		
	}
	
	
	// --------- HASH & SALT ---------- // 
	
	private static String generateHash(String data, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		digest.reset();
		digest.update(salt);
		byte[] hash = digest.digest(data.getBytes());
		return bytesToStringHex(hash);
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToStringHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length*2];
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			hexChars[i*2] = hexArray[v >>> 4];
			hexChars[i*2+1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte[] createSalt() {
		byte[] bytes = new byte[20];
		SecureRandom random = new SecureRandom();
		random.nextBytes(bytes);
		appendString("salt.txt", bytesToStringHex(bytes));
		return bytes;
	}
	
}