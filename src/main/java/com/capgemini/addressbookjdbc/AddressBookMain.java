package com.capgemini.addressbookjdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class AddressBookMain {
	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	public static Map<String, AddressBook> addressBookMap = new HashMap<>();

	public static Contact getDetails(Scanner scanner) {
		System.out.println("Enter the first name");
		String firstName = scanner.next();
		System.out.println("Enter the last name");
		String lastName = scanner.next();
		System.out.println("Enter the address");
		String address = scanner.next();
		System.out.println("Enter the city name");
		String city = scanner.next();
		System.out.println("Enter the state name");
		String state = scanner.next();
		System.out.println("Enter the ZIP code");
		int zip = scanner.nextInt();
		System.out.println("Enter the phone number");
		long phoneNumber = scanner.nextLong();
		scanner.nextLine();
		System.out.println("Enter the email");
		String email = scanner.next();
		Contact c = new Contact(firstName, lastName, address, city, state, zip, phoneNumber, email);
		return c;
	}

	public static void addAddressBook(String city) {
		AddressBook addBook = new AddressBook(city);
		addressBookMap.put(city, addBook);
	}

	public void searchPersonByCity(String name, String city) {
		List<Contact> list = new ArrayList<Contact>();
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			List<Contact> tempList = entry.getValue().getBook().stream().filter(c -> c.getCity().equals(city))
					.filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(name)).collect(Collectors.toList());
			list.addAll(tempList);
		}
		System.out.println("Contact of person with " + name + " in this city are :");
		for (Contact c : list) {
			System.out.println(c);
		}
	}

	public void searchPersonByState(String name, String state) {
		List<Contact> list = new ArrayList<Contact>();
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			List<Contact> tempList = entry.getValue().getBook().stream().filter(c -> c.getState().equals(state))
					.filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(name)).collect(Collectors.toList());
			list.addAll(tempList);
		}
		System.out.println("Contact of person with " + name + " in this state are :");
		for (Contact c : list) {
			System.out.println(c);
		}
	}

	public void viewPersonByCity(String city) {
		List<Contact> list = new ArrayList<Contact>();
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			List<Contact> tempList = entry.getValue().getBook().stream().filter(c -> c.getCity().equals(city))
					.collect(Collectors.toList());
			list.addAll(tempList);
		}
		System.out.println("Contacts in this city are :");
		for (Contact c : list) {
			System.out.println(c);
		}
	}

	public void viewPersonByState(String state) {
		List<Contact> list = new ArrayList<Contact>();
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			List<Contact> tempList = entry.getValue().getBook().stream().filter(c -> c.getState().equals(state))
					.collect(Collectors.toList());
			list.addAll(tempList);
		}
		System.out.println("Contacts in this state are :");
		for (Contact c : list) {
			System.out.println(c);
		}
	}

	public void countPersonsByCity(String city) {
		long count = 0;
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			count = entry.getValue().getBook().stream().filter(c -> c.getCity().equals(city)).count();
		}
		System.out.println("Number of persons in this city are : " + count);
	}

	public void countPersonsByState(String state) {
		long count = 0;
		for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			count = entry.getValue().getBook().stream().filter(c -> c.getState().equals(state))
					.count();
		}
		System.out.println("Number of persons in this state are : " + count);
	}
	
	public void sortByName() {
		for(Map.Entry<String,AddressBook> entry : addressBookMap.entrySet()) {
			Collections.sort(entry.getValue().getBook(),new SortByNameCompare());
		}
	}
	
	public void sortByZip() {
		for(Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
			Collections.sort(entry.getValue().getBook(), new SortByZipCompare());
		}
	}
	
	public void writeDataToFile(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookIO().writeData(addressBookMap);
		}
	}

	public void readDataFromFile(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookIO().readData();
		}
	}
	
	public void readDataCSV(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookFileService().readDataCSV();
		}
	}

	public void writeDataCSV(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookFileService().writeDataCSV(addressBookMap);
		}

	}

	public void readDataGSON(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookFileService().readDataGSON();
		}
	}

	public void writeDataGSON(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO)) {
			new AddressBookFileService().writeDataGSON(addressBookMap);
		}
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		AddressBookMain addBookMain = new AddressBookMain();
		int v;
		while (true) {
			System.out.println("1.to create addbook");
			System.out.println("2.to add person");
			System.out.println("3.to edit contact");
			System.out.println("4.to delete contact");
			System.out.println("5.to view addbook");
			System.out.println("6.to search contact in city");
			System.out.println("7.to search contact in state");
			System.out.println("8.to view contacts in city");
			System.out.println("9.to view contacts in state");
			System.out.println("10.to count contacts in city");
			System.out.println("11.to count contacts in state");
			System.out.println("12.to sort the addressBook by Name");
			System.out.println("13.to sort the addressBook by Zip");
			System.out.println("14.to write to file");
			System.out.println("15.to read from file");
			System.out.println("16.to write to csv");
			System.out.println("17.to read from csv");
			System.out.println("18.to write to gson");
			System.out.println("19.to read from gson");
			System.out.println("20.exit");
			System.out.println("Enter what you want to do : ");
			v = scanner.nextInt();
			scanner.nextLine();
			switch (v) {
				case 1:
					System.out.println("Enter the city name to create addressBook");
					String City = scanner.nextLine();
					addAddressBook(City);
					break;
				case 2:
					System.out.println("Enter name of the addressBook you want to add contact in : ");
					String city = scanner.next();
					System.out.println("Enter the details of the contact you want to add : ");
					Contact c = getDetails(scanner);
					for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
						if (entry.getKey().equalsIgnoreCase(city)) {
							entry.getValue().addContact(c);
						}
					}
					break;
				case 3:
					System.out.println("Enter the name to edit contact");
					String name = scanner.nextLine();
					System.out.println("Enter the city");
					city = scanner.nextLine();
					for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
						if (entry.getKey().equalsIgnoreCase(city)) {
							entry.getValue().editContact(name);
						} else {
							System.out.println("The addressbook does not exist, please create addressbook for that city");
						}
					}
					break;
				case 4:
					System.out.println("Enter the name to delete");
					name = scanner.nextLine();
					System.out.println("Enter the city");
					city = scanner.nextLine();
					for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
						if (entry.getKey().equalsIgnoreCase(city)) {
							entry.getValue().deleteContact(name);
						} else {
							System.out.println("The addressbook does not exist, please create addressbook for that city");
						}
					}
					break;
				case 5:
					for (Map.Entry<String, AddressBook> entry : addressBookMap.entrySet()) {
						System.out.println("The addressbook for city " + entry.getKey() + " is :");
						entry.getValue().viewList();
					}
					break;
				case 6:
					System.out.println("Enter the name to search");
					String person = scanner.nextLine();
					System.out.println("Enter the city");
					city = scanner.nextLine();
					addBookMain.searchPersonByCity(person, city);
					break;
				case 7:
					System.out.println("Enter the name to search");
					person = scanner.nextLine();
					System.out.println("Enter the state");
					String state = scanner.nextLine();
					addBookMain.searchPersonByState(person, state);
					break;
				case 8:
					System.out.println("Enter the city");
					city = scanner.nextLine();
					addBookMain.viewPersonByCity(city);
					break;
				case 9:
					System.out.println("Enter the state");
					state = scanner.nextLine();
					addBookMain.viewPersonByState(state);
					break;
				case 10:
					System.out.println("Enter the city");
					city = scanner.nextLine();
					addBookMain.countPersonsByCity(city);
					break;
				case 11:
					System.out.println("Enter the state");
					state = scanner.nextLine();
					addBookMain.countPersonsByState(state);
					break;
				case 12:
					addBookMain.sortByName();
					break;
				case 13:
					addBookMain.sortByZip();
					break;
				case 14:
					addBookMain.writeDataToFile(IOService.FILE_IO);
					break;
				case 15:
					addBookMain.readDataFromFile(IOService.FILE_IO);
					break;
				case 16:
					addBookMain.writeDataCSV(IOService.FILE_IO);
					break;
				case 17:
					addBookMain.readDataCSV(IOService.FILE_IO);
					break;
				case 18:
					addBookMain.writeDataGSON(IOService.FILE_IO);
					break;
				case 19:
					addBookMain.readDataGSON(IOService.FILE_IO);
					break;
				case 20:
					System.exit(0);
					break;

			}
		}
	}
}