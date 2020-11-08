package com.capgemini.addressbookjdbc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public boolean checkContactDataSync(String name) throws DatabaseException {
		List<Contact> contactList = addressBookDB.getContactFromDatabase(name);
		return contactList.get(0).equals(getContact(name));

	}

}

