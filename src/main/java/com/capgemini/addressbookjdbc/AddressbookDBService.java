package com.capgemini.addressbookjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressbookDBService {
	private static AddressbookDBService addressBookDB;

	public AddressbookDBService() {
	}

	public static AddressbookDBService getInstance() {
		if (addressBookDB == null) {
			addressBookDB = new AddressbookDBService();
		}
		return addressBookDB;
	}

	private Connection getConnection() throws DatabaseException {
		Connection connection = null;
		String jdbcURL = "jdbc:mysql://localhost:3306/addressbookservice?useSSL=false";
		String userName = "root";;
		String password = "1234";
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	private List<Contact> getContactDataFromResultSet(ResultSet resultSet) {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int contactId = resultSet.getInt("contact_id");
				String fname = resultSet.getString("fName");
				String lname = resultSet.getString("lName");
				String address = resultSet.getString("address");
				int zip = resultSet.getInt("zip");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				long phoneNumber = resultSet.getLong("phone");
				String email = resultSet.getString("email");
				String addbookName = resultSet.getString("addressBookName");
				String type = resultSet.getString("type");
				contactList.add(new Contact(contactId, fname, lname, address, city, state, zip, phoneNumber, email,
						addbookName, type));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}
	
	private List<Contact> getContactData(String sql) throws DatabaseException {
		List<Contact> contactList = new ArrayList<>();
		try {
			Connection connection = this.getConnection();
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = this.getContactDataFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return contactList;
	}

	public List<Contact> readData() throws DatabaseException {
		String sql = "select contacts.contact_id, address_book.addressbookName, addressbookType.type, contacts.fName, contacts.lName,"
				+ "contacts.address, zipCityState.city, zipCityState.state, contacts.zip, contacts.phone, contacts.email "
				+ "from contacts "
				+ "inner join zipCityState on contacts.zip = zipCityState.zip "
				+ "inner join address_book on contacts.contact_id = address_book.contact_id "
				+ "inner join addressbookType on addressbookType.addressbookName = address_book.addressbookName;";
		return this.getContactData(sql);
	}
}
