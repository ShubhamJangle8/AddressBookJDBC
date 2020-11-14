package com.capgemini.addressbookjdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddressBook{
	Scanner sc = new Scanner(System.in);
	public List<Contact> contactArray = new ArrayList<Contact>();	
	public String city;
	
	public AddressBook(String city) {
		this.city = city;
	}
	public List<Contact> getBook(){
		return contactArray;
	}
	public void addContact(Contact c) {
		for(int i = 0; i<contactArray.size(); i++) {	
			if(contactArray.get(i).equals(c)) {
				System.out.println("The person already exists!!!");
				return;
			}
		}
		contactArray.add(c);
	}
	
	public void editContact(String name){
		Scanner scanner = new Scanner(System.in);
		String check;
		for (int i = 0; i < contactArray.size(); i++) {
			Contact contact = contactArray.get(i);
			String editName = contactArray.get(i).getFirstName() + contactArray.get(i).getLastName();
			if (name.equalsIgnoreCase(editName)) {
				Scanner sc = new Scanner(System.in);
				do {
					System.out.println("Enter what you want to change : ");
					int choice = sc.nextInt();
					switch (choice) {
					case 1:
						System.out.println("Enter your new address : ");
						String newAddress = sc.next();
						sc.nextLine();
						contact.setAddress(newAddress);
						break;
					case 2:
						System.out.println("Enter your new city name : ");
						String newCity = sc.next();
						sc.nextLine();
						contact.setCity(newCity);
						break;
					case 3:
						System.out.println("Enter your new state name : ");
						String newState = sc.next();
						sc.nextLine();
						contact.setState(newState);
						break;
					case 4:
						System.out.println("Enter your new zip : ");
						int newZip = sc.nextInt();
						contact.setZip(newZip);
						break;
					case 5:
						System.out.println("Enter your new phone number : ");
						long newPNo = sc.nextLong();
						contact.setPhoneNumber(newPNo);
						break;
					case 6:
						System.out.println("Enter your new Email : ");
						String newEmail = sc.next();
						contact.setEmail(newEmail);
						break;
					default:
						break;
					}
					System.out.println("Do you want to edit more ? ");
					check = scanner.next();
				} while (check.equalsIgnoreCase("Yes"));
			}
		}
	}
	public void deleteContact(String name){
		for (int i = 0; i < contactArray.size(); i++) {
			String delName = contactArray.get(i).getFirstName() + contactArray.get(i).getLastName();
			if (name.equals(delName)) {
				contactArray.remove(contactArray.get(i));
			}
		}
	}
	
	public void viewList() {
		
		for(Contact c : contactArray) {
			System.out.println(c + "\n");
		}
	}
	
	public List<Contact> getContactList() {
		return contactArray;
	}

}