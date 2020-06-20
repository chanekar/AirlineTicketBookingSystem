package app;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * 
 * 
 * @author eluthro
 *
 */

public class User {
	
	public String username;
	public String firstname; 
	public String lastname; 
	
	public String destination;
	public String startDate;
	public String endDate;
	public String typeTrip;
	public boolean hasBooked;
	public int adults;
	public int children;
	
	public String bookedFlightID;
	public String flightClass;
	
	User(String username) {
		this.username = username;
	}
	
	public int getIndex() {
		int count = 0;
		try {
			Path path = Paths.get("users.txt");
			Scanner sc = new Scanner(path);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (this.username.equals(line)) return count;
				else count++;
			}
			
			sc.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
		return count;
	}
	
	public String getPass() {
		int index = this.getIndex();
		int count = 0;
		try {
			Path path = Paths.get("passwords.txt");
			Scanner sc = new Scanner(path);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (count == index) return line;
				else count++;
			}
			
			sc.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
		
		return null;
		
	}
	
	public String getSalt() {
		int index = this.getIndex();
		int count = 0;
		try {
			Path path = Paths.get("salt.txt");
			Scanner sc = new Scanner(path);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (count == index) return line;
				else count++;
			}
			
			sc.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
		
		return null;
		
	}
	
	// retrieves information from database and stores in User object
	public User getInfo(String username) {
		
		try {
			SqliteDB db = new SqliteDB();
			ResultSet rs = db.executeQuery("SELECT * FROM USERS WHERE username='" + username + "'");
			while (rs.next()) {
				this.firstname = rs.getString("firstname");
				this.lastname = rs.getString("lastname");
				this.destination = rs.getString("destination");
				this.adults = rs.getInt("adults");
				this.children = rs.getInt("children");
				this.bookedFlightID = rs.getString("flightID");
				
				int classval = rs.getInt("class");
				if (classval == 1) this.flightClass = "First";
				else if (classval == 2) this.flightClass = "Business";
				else if (classval == 3) this.flightClass = "Economy";
				
				int booked = rs.getInt("booked");
				if (booked == 1) this.hasBooked = true;
				else this.hasBooked = false;
			}
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		return this;
	
	}
	
	public String getName() {
		return this.username;
	}
	
}