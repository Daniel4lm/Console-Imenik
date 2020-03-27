package imenik;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class PhoneBook {

	private ArrayList<Entry> entryList;
	private Scanner scanner;

	private static final String userName = "root";
	private static final String password = "43OsLeOlRoEtXe43";
	// localhost//imeBazeNaKojuSeSpajamo
	private static final String CONN_STRING = "jdbc:mysql://localhost:3306/TelefonskiImenik?allowMultiQueries=true&"
											+ "useSSL=false&serverTimezone=UTC";
	// error? dodati na kraj stringa ?useSSL=false&serverTimezone=UTC

	private Connection connDB = null;
	private Statement standardStatement;
	private PreparedStatement preparedStatement;
	private ResultSet setResults;

	// defaultni konstruktor
	public PhoneBook() {
		entryList = new ArrayList<Entry>();
		scanner = new Scanner(System.in);
		openDBConnection();
	}

	/* Ispisivanje svih zapisa iz imenika */
	private void printPersons() {

		String printQuery = "SELECT imenik.ID, osoba.Ime, osoba.Prezime, imenik.Br_tel FROM osoba"
				+ " INNER JOIN imenik ON imenik.ID_Osoba = osoba.ID_Osoba";
		try {
			standardStatement = connDB.createStatement();
			setResults = standardStatement.executeQuery(printQuery);

			System.out.println("\nTabela: " + setResults.getMetaData().getTableName(1));
			
			System.out.printf("%n| %-3s | %-10s | %-15s | %-15s |%n" +
					          "|------------------------------------------------------|%n", setResults.getMetaData().getColumnName(1),
							  setResults.getMetaData().getColumnName(2), setResults.getMetaData().getColumnName(3),
							  setResults.getMetaData().getColumnName(4));

			while (setResults.next()) {
				System.out.printf("| %-3d | %-10s | %-15s | %-15s |%n", setResults.getInt(1), setResults.getString(2),
						setResults.getString(3), setResults.getString(4));
			}

		} catch (SQLException printEx) {
			System.err.println(printEx);
		} finally {
			try {
				standardStatement.close();
				setResults.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Ispisivanje osoba sa svim telefonima vezanim za istu osobu */
	private void printPersonByName() {

		System.out.println("Unesite podatke :");
		System.out.print("Unesite ime >_ ");
		String fName = scanner.next();
		System.out.print("Unesite prezime >_ ");
		String lName = scanner.next();

		String joinQuery = "SELECT Br_tel FROM imenik INNER JOIN osoba ON imenik.ID_Osoba = osoba.ID_Osoba "
				+ "WHERE osoba.Ime = '" + fName + "' AND osoba.Prezime = '" + lName + "'";

		try {

			if (checkPerson(fName, lName) == 0) {
				System.out.println("Osoba ne postoji u bazi podataka!");
				return;
			}

			standardStatement = connDB.createStatement();
			setResults = standardStatement.executeQuery(joinQuery);

			System.out.print("\n" + fName + ", " + lName);

			while (setResults.next()) {
				System.out.print(" : " + setResults.getString(1));
			}
			System.out.println();

		} catch (SQLException joinEx) {
			System.err.println(joinEx);
		} finally {
			try {
				standardStatement.close();
				setResults.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void printEntriesByLetters() {
		
		System.out.println("Unesite jedno ili vise pocetnih slova imena(odvojeno zarezom ili praznim mjestom): ");
		String[] niz = scanner.nextLine().split("(\\s)|(\\,\\s+)|(\\,)");
		
		String letterQuery = ""; 
		
		for(String s : niz) {			
			letterQuery += "SELECT Ime, Prezime, Br_tel FROM imenik INNER JOIN osoba ON imenik.ID_Osoba = osoba.ID_Osoba " + 
						   "WHERE Ime LIKE '" + s + "%';";
		}
				
		try {
			preparedStatement = connDB.prepareStatement(letterQuery);
			preparedStatement.execute();
			//int i = 0;
			System.out.println("\nOsobe pronadjene u bazi sa pocetnim slovima :\n" + 
					   		   "*********************************************");			
            do {
                try (ResultSet rs = preparedStatement.getResultSet()) {
                	  	                	
                    while (rs.next()) {
                        System.out.print(rs.getString(1) + ": " + rs.getString(2) + ": " + rs.getString(3));
                        System.out.println();
                    }
                }
                //i++;
            } while (preparedStatement.getMoreResults());
			
            System.out.println("*********************************************");	
            
		} catch (Exception e) {
			System.err.println(e);
		}		
	}
	
	/* Provjera da li osoba postoji u tabeli 'osoba' */
	private Integer checkPerson(String fname, String lname) throws SQLException {

		String checkPersonQuery = "SELECT ID_Osoba FROM osoba WHERE Ime = '" + fname + "' AND Prezime = '" + lname
				+ "'";
		standardStatement = connDB.createStatement();
		setResults = standardStatement.executeQuery(checkPersonQuery);
		Integer ID;

		if (setResults.next()) {
			ID = setResults.getInt(1);
			standardStatement.close();
			setResults.close();
			return ID;
		}
		standardStatement.close();
		setResults.close();
		return 0;
	}

	/* Provjera da li vec postoji osoba sa istim brojem u zapisu imenika */
	private boolean checkPhoneNumber(String fname, String lname, String number) throws SQLException {

		String checkIDQuery = "SELECT ID_Osoba FROM imenik WHERE ID_Osoba = " + checkPerson(fname, lname)
				+ " AND Br_tel = '" + number + "'";

		standardStatement = connDB.createStatement();
		setResults = standardStatement.executeQuery(checkIDQuery);

		if (setResults.next()) {
			// ID = setResults.getInt(1);
			standardStatement.close();
			setResults.close();
			return true;
		}
		standardStatement.close();
		setResults.close();
		return false;
	}

	/* Funkcija za unos novih osoba i telefona u imenik */
	private void insertNewEntry() {

		System.out.println("Unesite podatke :");
		System.out.print("Unesite ime >_ ");
		String fName = scanner.next();
		System.out.print("Unesite prezime >_ ");
		String lName = scanner.next();
		System.out.print("Unesite broj telefona >_ ");
		scanner.nextLine();
		String telNumber = scanner.nextLine();

		String insert1Query = "INSERT INTO osoba(Ime, Prezime) VALUES ('" + fName + "', '" + lName + "')";

		String insert2Query = "INSERT INTO imenik(ID_Osoba, Br_tel) VALUES (?, ?)";

		// entryList.add(new Entry(new Person(fName, lName), telNumber));

		try {

			preparedStatement = connDB.prepareStatement(insert2Query);

			/* Ako osoba ne postoji u bazi podataka - izvrsiti upis u obje tabele */
			if (checkPerson(fName, lName) == 0) {

				standardStatement = connDB.createStatement();
				if (standardStatement.executeUpdate(insert1Query) > 0) {
					System.out.println("Upis u imenik ... ");
				}

				preparedStatement.setInt(1, checkPerson(fName, lName));
				preparedStatement.setString(2, telNumber);
				preparedStatement.executeUpdate();

			}
			/* Ako osoba vec postoji - dodati samo upis u tabela 'imenik' */
			else {

				if (!checkPhoneNumber(fName, lName, telNumber)) {
					preparedStatement.setInt(1, checkPerson(fName, lName));
					preparedStatement.setString(2, telNumber);
					preparedStatement.executeUpdate();
				} else {
					System.out.println("Osoba sa istim brojem telefona vec postoji u bazi podataka!");
				}
			}

			// standardStatement = connDB.createStatement();

		} catch (SQLException insertEx) {
			System.err.println("Unos u imenik nije uspjesan : " + insertEx);
		} finally {
			try {
				standardStatement.close();
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Funkcija koja vraca max ID iz tabele o studentima */
	private Integer returnMaxID() throws SQLException  {
		
		String maxQuery = "SELECT MAX(ID_Osoba) FROM imenik";				
		Statement maxStatement = connDB.createStatement();
		ResultSet max = maxStatement.executeQuery(maxQuery);
		max.next();
		Integer maxEl = max.getInt(1);
		maxStatement.close();
		max.close();
		return maxEl;	
					
	}
	
	/* Funkcija koja izvrasava reset AUTO_INCREMENT svaki put kada se izvrsi delete statement iz tabele */
	private void auto_increment_Reset() throws SQLException {
		
		String alterQuery = "ALTER TABLE imenik AUTO_INCREMENT = " + returnMaxID();
		Statement alterStatement = connDB.createStatement();
		alterStatement.executeUpdate(alterQuery);		
		alterStatement.close();
	}
	
	private void deletePersonEntry() {

		System.out.println("Unesite podatke :");
		System.out.print("Unesite ime >_ ");
		String fName = scanner.next();
		System.out.print("Unesite prezime >_ ");
		String lName = scanner.next();
		
		String deleteQuery = "DELETE FROM imenik WHERE ID_Osoba = " + 
							 "(SELECT ID_Osoba FROM osoba WHERE Ime = '" + fName + "' AND Prezime = '" + lName + "')";

		try {
						
			if (checkPerson(fName, lName) == 0) {
				System.out.println("Osoba ne postoji u bazi podataka!");
				return;
			}
			
			standardStatement = connDB.createStatement();

			if (standardStatement.executeUpdate(deleteQuery) > 0) {
				System.out.println("Osoba obrisana iz imenika ...");
				
			}
			auto_increment_Reset();

		} catch (SQLException delEx) {
			System.err.println(delEx);
		} finally {
			try {
				standardStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Konekcija za bazom podataka */
	private boolean openDBConnection() {

		try {
			connDB = DriverManager.getConnection(CONN_STRING, userName, password);
			return true;
		} catch (SQLException sqle) {
			System.out.print(" SQL: NEUSPJESNA KONEKCIJA SA BAZOM PODATAKA : > ");
			System.err.println(sqle);
		}

		return false;
	}

	/* Zatvaranje konekcije sa bazom podataka i otpustanje resursa */
	public void closeDBConnection() {
		try {
			connDB.close();
		} catch (SQLException sqle) {
			System.err.println(sqle);
		}
	}

	private void printChoice() {
		System.out.print("\n... Dobrodosli u Web Imenik ...\n\n" + 
						 " Izbornik:\n" + " 1 - Dodajte novi unos u imenik\n" + 
						 " 2 - Izlistajte sve unose iz imenika\n" + 
						 " 3 - Pronadji broj telefona po imenu i prezimenu\n" +
						 " 4 - Pronadji osobe po prvom slovu u imenu\n" + 
						 " 5 - Editujte unos iz imenika\n" + 
						 " 6 - Obrišite unos iz imenika\n" + 
						 " 0 - Izlaz iz programa\n " + 
						 ">_ ");
	}

	/* Glavni menij aplikacije */
	public void appMenu() {

		int choice = 0;

		do {

			printChoice();

			choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {

			case 1:

				insertNewEntry();
				break;

			case 2:

				printPersons();
				break;

			case 3:

				printPersonByName();
				break;

			case 4:

				printEntriesByLetters();
				break;
				
			case 5:
				
				break;
				
			case 6:
				
				deletePersonEntry();
				break;
			case 0:

				closeDBConnection();
				System.out.println("\nIzlaz iz programa ...");

				break;
			}

		} while (choice != 0);

	}

	private void editPerson() {

	}

	public static void main(String[] args) {

		new PhoneBook().appMenu();

	}

}
