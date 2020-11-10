package com.capgemini.addressbookjdbc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class AddressBookIO {
	public static String ADDRESSBOOK_FILE_NAME = "AddressBook.txt";

	public void writeData(Map<String, AddressBook> addressBookMap) {
		StringBuffer contactBuffer = new StringBuffer();
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			entry.getValue().getBook().forEach(contact -> {
				String contactString = contact.toString().concat("\n");
				contactBuffer.append(contactString);
			});
		}

		try {
			Files.write(Paths.get(ADDRESSBOOK_FILE_NAME), contactBuffer.toString().getBytes());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readData() {
		try {
			Files.lines(new File(ADDRESSBOOK_FILE_NAME).toPath()).forEach(System.out::println);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
