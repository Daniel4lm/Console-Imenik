package imenik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBQueries {

	/* Funkcija koja vraca max ID iz tabele o studentima */

	public static Integer returnMaxID(Connection connDB, String column, String tableName) throws SQLException {

		String maxQuery = "SELECT MAX(" + column + ") FROM " + tableName;
		ResultSet max = connDB.createStatement().executeQuery(maxQuery);
		if (max.next()) {
			Integer maxEl = max.getInt(1);
			max.close();
			return maxEl;
		}
		max.close();
		return 0;
	}

	/*
	 * Funkcija koja izvrasava reset AUTO_INCREMENT svaki put kada se izvrsi delete
	 * statement iz tabele
	 */

	public static void auto_increment_Reset(Connection connDB, String column, String tableName) throws SQLException {

		String alterQuery = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + returnMaxID(connDB, column, tableName);
		connDB.createStatement().executeUpdate(alterQuery);

	}

	/*
	 * Izvrsavanje vise upita za unos, brisanje ili update podataka u bazi podataka
	 */

	public static void executeUpdateQueries(Connection connDB, String queries) throws SQLException {

		String[] queryArray = queries.split("(\\;)|(\\;\\s+)");

		for (Integer i = 0; i < queryArray.length; i++) {

			if (queryArray[i].startsWith("INSERT ") && connDB.createStatement().executeUpdate(queryArray[i]) > 0) {
				System.out.println("Query: " + i + " - Upis uspjesno izvrsen ...");
			} else if (queryArray[i].startsWith("DELETE ")
					&& connDB.createStatement().executeUpdate(queryArray[i]) > 0) {
				System.out.println("Query: " + i + " - Zapis uspjesno obrisan ...");
			} else if (queryArray[i].startsWith("UPDATE ")
					&& connDB.createStatement().executeUpdate(queryArray[i]) > 0) {
				System.out.println("Query: " + i + " - Update uspjesno izvrsen ...");
			} else {
				System.out.println("Query: " + i + " - Nepravilan upit ...");
			}
		}
	}

	/* Izvrsavanje vise razlicitih upita za ispis podataka iz baze podataka */

	public static ResultSet[] executeSelectQueries(Connection connDB, String queries) throws SQLException {

		String[] queryArray = queries.split("(\\;)|(\\;\\s+)");

		ResultSet[] resultSet = new ResultSet[queryArray.length];

		for (Integer i = 0; i < queryArray.length; i++) {

			if (queryArray[i].startsWith("SELECT ")) {
				resultSet[i] = connDB.createStatement().executeQuery(queryArray[i]);
			} else {
				System.out.println("Query: " + i + " - Nepravilan upit ...");
			}
		}
		return resultSet;
	}
}
