package com.aidn5.mcqa.tests.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;


public class UseTest {
	// This test is copied from the library's github
	// it's only used to quick-test the library
	@Test
	public void defaultLibTestUnit() {
		Connection connection = null;
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite::memory:");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 seconds.

			statement.executeUpdate("drop table if exists person");
			statement.executeUpdate("create table person (id integer, name string)");
			statement.executeUpdate("insert into person values(1, 'leo')");
			statement.executeUpdate("insert into person values(2, 'yui')");
			ResultSet rs = statement.executeQuery("select * from person");

			rs.next();
			Assert.assertEquals("leo", rs.getString("name"));
			Assert.assertEquals(1, rs.getInt("id"));

			rs.next();
			Assert.assertEquals("yui", rs.getString("name"));
			Assert.assertEquals(2, rs.getInt("id"));

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		} finally {
			try {
				if (connection != null) connection.close();
			} catch (SQLException e) {
				// connection close failed.
				System.err.println(e.getMessage());
			}
		}
	}
}
