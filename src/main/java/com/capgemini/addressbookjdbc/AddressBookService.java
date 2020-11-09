package com.capgemini.addressbookjdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddressBookService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<Contact> contactList = new ArrayList<>();
	private AddressbookDBService addressBookDB;

	public AddressBookService() {
		addressBookDB = AddressbookDBService.getInstance();
	}

	/**
	 * reads all data from database
	 * 
	 * @param ioService
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> readContactData(IOService ioService) throws DatabaseException {
		if (ioService.equals(IOService.DB_IO)) {
			this.contactList = addressBookDB.readData();
		}
		return this.contactList;
	}
	
	public void updatePersonsPhone(String name, long phone) throws DatabaseException {
		int result = addressBookDB.updatePersonsData(name, phone);
		if (result == 0)
			return;
		this.contactList = addressBookDB.readData();
		Contact contact = this.getContact(name);
		if (contact != null)
			contact.setPhoneNumber(phone);
	}

	private Contact getContact(String name) {
		Contact contact = this.contactList.stream().filter(contactData -> contactData.getName().equals(name)).findFirst()
				.orElse(null);
		return contact;
	}
	
	/**
	 * UC 18
	 * returns list of contacts added between given dates
	 * @param start
	 * @param end
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactsByDate(LocalDate start, LocalDate end) throws DatabaseException {
		List<Contact> contactByDateList = addressBookDB.readDataForGivenDateRange(start, end);
		return contactByDateList;
	}
	
	/**
	 * UC 19
	 * returns list of contacts belonging to given city
	 * @param city
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactsByCity(String city) throws DatabaseException {
		return addressBookDB.getContactsByCity(city);
	}

	/**
	 * returns list of contacts belonging to given state
	 * @param state
	 * @return
	 * @throws DatabaseException
	 */
	public List<Contact> getContactsByState(String state) throws DatabaseException {
		return addressBookDB.getContactsByState(state);
	}
	
	/**
	 * Adding new contact to database
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param city
	 * @param state
	 * @param zip
	 * @param phone
	 * @param email
	 * @param addbookName
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	public void addNewContact(String firstName, String lastName, String address, String city, String state, int zip,
							  long phone, String email, List<String> addbookName) throws DatabaseException, SQLException {
		addressBookDB.addContactToDatabase(firstName, lastName, address, city, state, zip, phone, email,
		addbookName, LocalDate.now());
	}

	public boolean checkContactDataSync(String name) throws DatabaseException {
		List<Contact> contactList = addressBookDB.getContactFromDatabase(name);
		return contactList.get(0).equals(getContact(name));

	}

}

