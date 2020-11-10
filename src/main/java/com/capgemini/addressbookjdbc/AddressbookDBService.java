package com.capgemini.addressbookjdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AddressbookDBService {
	private static AddressbookDBService addressBookDB;
	private static PreparedStatement contactPrepareStatement;

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
		try(Connection connection = this.getConnection();) {
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
	
	public List<Contact> getContactFromDatabase(String name) throws DatabaseException {
		String[] fullName = name.split("[ ]");
		String sql = String.format("select contacts.contact_id, address_book.addressbookName, addressbookType.type, contacts.fName, contacts.lName,"
				+ "contacts.address, zipCityState.city, zipCityState.state, contacts.zip, contacts.phone, contacts.email "
				+ "from contacts inner join zipCityState on contacts.zip = zipCityState.zip "
				+ "inner join address_book on contacts.contact_id = address_book.contact_id "
				+ "inner join addressbookType on addressbookType.addressbookName = address_book.addressbookName "
				+ "where fName = '%s' and lName = '%s';",
				fullName[0], fullName[1]);
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = this.getContactDataFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return contactList;
	}
	
	/**
	 * UC 18
	 * returns list of contacts added between given dates
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> readDataForGivenDateRange(LocalDate start, LocalDate end) throws DatabaseException {
		String sql = String.format("select contacts.contact_id, address_book.addressbookName, addressbookType.type, contacts.fName, contacts.lName, contacts.address, zipCityState.city, zipCityState.state, contacts.zip, contacts.phone, contacts.email "
				+ "from contacts inner join zipCityState on contacts.zip = zipCityState.zip "
				+ "inner join address_book on contacts.contact_id = address_book.contact_id "
				+ "inner join addressbookType on addressbookType.addressbookName = address_book.addressbookName "
				+ "where dateAdded between '%s' and '%s'", Date.valueOf(start), Date.valueOf(end));
		return this.getContactData(sql);
	}
	
	/**
	 * returns list of contacts belonging to given city
	 * @param city
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactsByCity(String city) throws DatabaseException {
		String sql = String.format("select contacts.contact_id, address_book.addressbookName, addressbookType.type, contacts.fName, contacts.lName, "
				+ "contacts.address, zipCityState.city, zipCityState.state, contacts.zip, contacts.phone, contacts.email, contacts.dateAdded "
				+ "from contacts "
				+ "inner join zipCityState on contacts.zip = zipCityState.zip "
				+ "inner join address_book on contacts.contact_id = address_book.contact_id "
				+ "inner join addressbookType on addressbookType.addressbookName = address_book.addressbookName WHERE city = '%s'",city);
		return getContactData(sql).stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * returns list of contacts belonging to given state
	 * @param state
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactsByState(String state) throws DatabaseException {
		String sql =String.format("select contacts.contact_id, address_book.addressbookName, addressbookType.type, contacts.fName, contacts.lName, "
				+ "contacts.address, zipCityState.city, zipCityState.state, contacts.zip, contacts.phone, contacts.email, contacts.dateAdded "
				+ "from contacts "
				+ "inner join zipCityState on contacts.zip = zipCityState.zip "
				+ "inner join address_book on contacts.contact_id = address_book.contact_id "
				+ "inner join addressbookType on addressbookType.addressbookName = address_book.addressbookName WHERE state = '%s'",state);
		return getContactData(sql).stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * Updating contact details
	 * @param name
	 * @param phone
	 * @return
	 * @throws DatabaseException
	 */
	@SuppressWarnings("static-access")
	public int updatePersonsData(String name, long phone) throws DatabaseException {
		String sql = "update contacts set phone = ? where fName = ?";
		int result = 0;
		try {
			if (this.contactPrepareStatement == null) {
				Connection connection = this.getConnection();
				contactPrepareStatement = connection.prepareStatement(sql);
			}
			contactPrepareStatement.setLong(1, phone);
			contactPrepareStatement.setString(2, name);
			result = contactPrepareStatement.executeUpdate();
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
		return result;
	}
	
	/**
	 * Adding contact to the address book database and returning added records
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @param phone
	 * @param email
	 * @param addbookName
	 * @param dateAdded
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@SuppressWarnings("static-access")
	public List<Contact> addContactToDatabase(String firstName, String lastName, String address, String city,
			String state, int zip, long phone, String email, List<String> addbookName, LocalDate dateAdded)
			throws DatabaseException, SQLException {
		int contactId = -1;
		Connection connection = null;
		List<Contact> addedContacts = new ArrayList<>();
		connection = this.getConnection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException exception) {
			throw new DatabaseException(exception.getMessage());
		}

		try (Statement statement = connection.createStatement()) { 
			String sql = String.format(
					"insert into contacts (fName, lName, address, zip, phone, email, dateAdded) values ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
					firstName, lastName, address, zip, phone, email, Date.valueOf(dateAdded));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					contactId = resultSet.getInt(1);
			}
		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to contacts");
		}

		try (Statement statement = connection.createStatement()) {
			String sqlGetZip = String.format("select zip from zipCityState where zip = %s", zip);
			ResultSet resultSet = statement.executeQuery(sqlGetZip);
			int existingZip = 0;
			while (resultSet.next()) {
				existingZip = resultSet.getInt("zip");
			}
			if (existingZip == 0) {
				String sql = String.format("insert into zipCityState (zip, city, state) values ('%s', '%s', '%s')", zip,
						city, state);
				statement.executeUpdate(sql);
			}

		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to zipCityState table");
		}

		Map<String, String> addbookNameTypeMap = new HashMap<>();
		try (Statement tempStatement = connection.createStatement()) {
			String sqlGetType = String.format("select * from addressbooktype");
			ResultSet resultSet = tempStatement.executeQuery(sqlGetType);
			while (resultSet.next()) {
				addbookNameTypeMap.put(resultSet.getString("addressbookName"), resultSet.getString("type"));
			}
		} catch (Exception e) {
			throw new DatabaseException(e.getMessage());
		}
		final int contId = contactId;
		try (Statement statement = connection.createStatement()) {
			addbookName.forEach(name -> {
				String sql = String.format(
						"insert into address_book (contact_id, addressbookName) values ('%s', '%s')", contId, name);
				try {
					statement.executeUpdate(sql);
				} catch (SQLException e) {
				}
			});
			addbookName.forEach(name -> addedContacts.add(new Contact(contId, firstName, lastName, address, city, state,
					zip, phone, email, name, addbookNameTypeMap.get(name), dateAdded)));

		} catch (SQLException exception) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new DatabaseException(e.getMessage());
			}
			throw new DatabaseException("Unable to add to address_book");
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return addedContacts;
	}
	
}
