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
	private static final String CONN_STRING = "jdbc:mysql://localhost/TelefonskiImenik?useSSL=false&serverTimezone=UTC";
	// error? dodati na kraj stringa ?useSSL=false&serverTimezone=UTC

	private Connection connDB = null;
	private Statement standardStatement;
	private PreparedStatement preparedStatement;
	private ResultSet setResults;

	// default constructor
	public PhoneBook() {
		entryList = new ArrayList<Entry>();
		scanner = new Scanner(System.in);
		openDBConnection();
	}

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
				
				if(!checkPhoneNumber(fName, lName, telNumber)) {
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

	private void printPersons() {

		String printQuery = "SELECT imenik.ID, osoba.Ime, osoba.Prezime, imenik.Br_tel FROM osoba"
				+ " INNER JOIN imenik ON imenik.ID_Osoba = osoba.ID_Osoba";
		try {
			standardStatement = connDB.createStatement();
			setResults = standardStatement.executeQuery(printQuery);

			System.out.printf("%n| %-3s | %-10s | %-15s | %-15s |%n", "ID", "First name", "Last name", "Tel. number");

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

	private boolean openDBConnection() {

		try {
			connDB = DriverManager.getConnection(CONN_STRING, userName, password);
			return true;
		} catch (SQLException sqle) {
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
		System.out.print("\n... Dobrodosli u Web Imenik ...\n\n" + " Izbornik:\n" + " 1 - Dodajte novi unos u imenik\n"
				+ " 2 - Izlistajte sve unose iz imenika\n" + " 3 - Editujte unos iz imenika\n"
				+ " 4 - Obrišite unos iz imenika\n" + " 0 - Izlaz iz programa\n " + ">_ ");
	}

	public void appMenu() {

		int choice = 0;

		do {

			printChoice();

			choice = scanner.nextInt();

			switch (choice) {

			case 1:

				insertNewEntry();
				break;

			case 2:

				printPersons();
				break;

			case 3:

				break;
			case 4:

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

	private void deletePerson() {

	}

	public static void main(String[] args) {

		new PhoneBook().appMenu();

	}

}
