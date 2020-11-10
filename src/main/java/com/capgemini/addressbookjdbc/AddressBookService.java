package com.capgemini.addressbookjdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddressBookService {
	private static final Logger LOG = LogManager.getLogger(AddressBookService.class);
	
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
	
	/**
	 * adding multiple new contacts in database using threads
	 * @param newContactsList
	 * @throws DatabaseException
	 */
	public void addMultipleContacts(List<Contact> newContactsList) throws DatabaseException {
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<Integer, Boolean>();
		newContactsList.forEach(person -> {
			Runnable task = () -> {
				contactAdditionStatus.put(person.hashCode(), false);
				LOG.info("Contact Being Added: " + Thread.currentThread().getName());
				try {
					addNewContact(person.getFirstName(), person.getLastName(), person.getAddress(), person.getCity(),
							person.getState(), person.getZip(), person.getPhoneNumber(), person.getEmail(),
							Arrays.asList(person.getType()));
				} catch (DatabaseException | SQLException exception) {
					exception.printStackTrace();
				}
				contactAdditionStatus.put(person.hashCode(), true);
				LOG.info("Contact Added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, person.getName());
			thread.start();
		});
		while (contactAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException exception) {
				throw new DatabaseException(exception.getMessage());
			}
		}
	}

	public boolean checkContactDataSync(String name) throws DatabaseException {
		List<Contact> contactList = addressBookDB.getContactFromDatabase(name);
		return contactList.get(0).equals(getContact(name));
	}
	
	/**
	 * checking if data added is in sync for multiple contacts
	 * @param namesList
	 * @return
	 */
	public boolean checkMultipleContactDataSync(List<String> namesList) {
		List<Boolean> resultList = new ArrayList<>();
		namesList.forEach(name -> {
			try {
				resultList.add(checkContactDataSync(name));
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		});
		if (resultList.contains(false)) {
			return false;
		}
		return true;
	}

}

