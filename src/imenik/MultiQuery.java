package imenik;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultiQuery {
	
	public static void executeUpdateQueries(Connection connDB, String queries) throws SQLException {

		String[] queryArray = queries.split("(\\;)|(\\;\\s+)");

		for (Integer i = 0; i < queryArray.length; i++) {

			if (queryArray[i].startsWith("INSERT ") || queryArray[i].startsWith("DELETE ")
					|| queryArray[i].startsWith("UPDATE ")) {
				connDB.createStatement().executeUpdate(queryArray[i]);
			}
		}
	}

	public static ResultSet[] executeSelectQueries(Connection connDB, String queries) throws SQLException {

		String[] queryArray = queries.split("(\\;)|(\\;\\s+)");

		ResultSet[] resultSet = new ResultSet[queryArray.length];

		for (Integer i = 0; i < queryArray.length; i++) {

			if (queryArray[i].startsWith("SELECT ")) {
				resultSet[i] = connDB.createStatement().executeQuery(queryArray[i]);
			}
		}
		return resultSet;
	}

}
