package com.capgemini.addressbookjdbc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class AddressBookService {
	private static final Logger LOG = LogManager.getLogger(AddressBookService.class);
	public static String FILE_NAME = "C:\\Users\\DELL\\eclipse-workspace\\AddressBookJDBCPro\\AddressBook.txt";
	public static String CSV_FILE_NAME = "C:\\Users\\DELL\\eclipse-workspace\\AddressBookJDBCPro\\addressbook.csv";
	public static String GSON_FILE_NAME = "C:\\Users\\DELL\\eclipse-workspace\\AddressBookJDBCPro\\addressbook.json";
	
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<Contact> contactList = new ArrayList<>();
	private AddressbookDBService addressBookDB;

	public AddressBookService() {
		addressBookDB = AddressbookDBService.getInstance();
	}
	
	public AddressBookService(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}
	
	public void writeData(Map<String, AddressBook> stateAddressBookMap) {
		StringBuffer personBuffer = new StringBuffer();
		stateAddressBookMap.values().stream().map(book -> book.getPersonList()).forEach(list -> {
			list.forEach(person -> {
				String empString = person.toString().concat("\n");
				personBuffer.append(empString);
			});
		});
		try {
			Files.write(Paths.get(FILE_NAME), personBuffer.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readData() {
		try {
			Files.lines(new File(FILE_NAME).toPath()).forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeDataCSV(Map<String, AddressBook> stateAddressBookMap) {

		File file = new File(CSV_FILE_NAME);

		try {
			FileWriter outputfile = new FileWriter(file);
			CSVWriter writer = new CSVWriter(outputfile);
			List<String[]> data = new ArrayList<String[]>();
			String[] header = { "First Name", "Last Name", "Address", "City", "State", "ZIP", "Phone Number",
					"Email ID" };
			data.add(header);
			stateAddressBookMap.values().stream().map(entry -> entry.getPersonList())
					.forEach(listEntry -> listEntry.forEach(person -> {
						String[] personData = { person.getFirstName(), person.getLastName(), person.getAddress(),
								person.getCity(), person.getState(), Integer.toString(person.getZip()),
								Long.toString(person.getPhoneNumber()), person.getEmail() };
						data.add(personData);
					}));

			writer.writeAll(data);
			writer.close();
			System.out.println("Data entered successfully to addressbook.csv file.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void readDataCSV() {
		try {
			FileReader filereader = new FileReader(CSV_FILE_NAME);
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;

			// we are going to read data line by line
			while ((nextRecord = csvReader.readNext()) != null) {
				for (String cell : nextRecord) {
					System.out.print(cell + "\t");
				}
				System.out.println();
			}
			csvReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeDataGSON(Map<String, AddressBook> stateAddressBookMap) {
		try {
			Gson gson = new Gson();
			FileWriter writer = new FileWriter(GSON_FILE_NAME);
			for (Map.Entry<String, AddressBook> entry : stateAddressBookMap.entrySet()) {
				entry.getValue().getContactList().forEach(contact -> {
					String json = gson.toJson(contact);
					try {
						writer.write(json);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
			writer.close();
			System.out.println("Data entered successfully to addressbook.json file.");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void readDataGSON() {
		Gson gson = new Gson();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(GSON_FILE_NAME));
			JsonStreamParser parser = new JsonStreamParser(bufferedReader);
			while (parser.hasNext()) {
				JsonElement json = parser.next();
				if (json.isJsonObject()) {
					Contact person = gson.fromJson(json, Contact.class);
					System.out.println(person);
				}
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}


	/**
	 * reads all data from database
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
	
	public void updatePersonsPhone(String name, long phone, IOService ioService) throws DatabaseException {
		if (ioService.equals(IOService.DB_IO)) {
			int result = addressBookDB.updatePersonsData(name, phone);
			if (result == 0)
				return;
		}
		if (ioService.equals(IOService.REST_IO)) {
			Contact contact = this.getContact(name);
			if (contact != null)
				contact.setPhoneNumber(phone);
		}
	}

	public Contact getContact(String name) {
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
	
	public int countEntries() {
		return contactList.size();
	}
	
	/**
	 * adds new contacts to application memory
	 * @param newContacts
	 */
	public void addToApplicationMemory(Contact contact) {
		contactList.add(contact);
	}
	
	/**
	 * deleting from application memory
	 * @param contact
	 */
	public void deleteFromApplicationMemory(Contact contact) {
		List<Contact> newList = contactList.stream().filter(p -> !p.getName().equals(contact.getName()))
				.collect(Collectors.toList());
		contactList = newList;
	}

}

