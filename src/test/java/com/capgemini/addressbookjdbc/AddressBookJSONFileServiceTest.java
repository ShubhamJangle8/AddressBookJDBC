package com.capgemini.addressbookjdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.capgemini.addressbookjdbc.AddressBookService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

class AddressBookJSONFileServiceTest {
	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	/**
	 * sending get request and retrieving all data from JSON server
	 * 
	 * @return
	 */
	private Contact[] getContactList() {
		Response response = RestAssured.get("/contacts");
		System.out.println("Contact entries in JSONServer:\n" + response.asString());
		String responseBody = response.getBody().asString();
		System.out.println("Response Body is =>  " + responseBody);
		Contact[] arrayOfContact = new Gson().fromJson(response.asString(), Contact[].class);
		return arrayOfContact;
	}
	
	/**
	 * adds new contacts to JSON server and returns response
	 * @param newContacts
	 * @return
	 */
	private Response addContactToJsonServer(Contact contact) {
		String json = new Gson().toJson(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(json);
		return request.post("/contacts");
	}

	@Test
	public void givenContactDataInJSONServer_WhenRetrieved_ShouldMatchTheCount() {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContact));
		long entries = addressBookService.countEntries();
		assertEquals(15, entries);
	}
	
	@Test
	public void givenMultipleNewContacts_WhenAdded_ShouldMatch201ResponseAndCount() {
		List<Contact> newContacts = Arrays.asList(
				new Contact("Joe", "Bidden", "Karad", "Satara","Maharashtra", 525252, 000000000, "joeb@gmail.com",
						LocalDate.now()),
				new Contact("saurabh", "raut", "Panchgani", "Satara", "Maharashtra", 525253, 0000000000,
						"saurabhr@gmail.com", LocalDate.now()),
				new Contact("Kamala", "Harris", "Vita", "Sangli", "Maharashtra", 858585, 0000000000, "kamalah@gmail.com",
						LocalDate.now()));
		AddressBookService addressBookFileService = new AddressBookService(Arrays.asList(getContactList()));
		newContacts.forEach(contact -> {
			Runnable task = () -> {
				Response response = addContactToJsonServer(contact);
				int statusCode = response.getStatusCode();
				assertEquals(201, statusCode);
				addressBookFileService.addToApplicationMemory(contact);
			};
			Thread thread = new Thread(task, contact.getFirstName());
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		long entries = addressBookFileService.countEntries();
		assertEquals(18, entries);
	}
	
	private Response updatePhone(String name, long phone) throws DatabaseException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressbookService = new AddressBookService(Arrays.asList(arrayOfContact));
		addressbookService.updatePersonsPhone(name, phone, IOService.REST_IO);
		Contact contactToUpdate = addressbookService.getContact(name);
		String contactJson = new Gson().toJson(contactToUpdate);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(contactJson);
		return request.put("/contacts/" + contactToUpdate.getContactId());

	}
	
	@Test
	public void givenMultipleEmployees_WhenUpdatedSalary_ShouldSyncWithDB() throws DatabaseException {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(arrayOfContact));
		int statusCode = updatePhone("Joe Bidden", 8888855555L).getStatusCode();
		assertEquals(200, statusCode);
	}
	
	/**
	 * deleting contact from JSON server and application memory
	 * @param name
	 * @return
	 */
	private Response deleteContact(String name) {
		Contact[] arrayOfContact = getContactList();
		AddressBookService addressbookService = new AddressBookService(Arrays.asList(arrayOfContact));
		Contact contact = addressbookService.getContact(name);
		addressbookService.deleteFromApplicationMemory(contact);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		return request.delete("/contacts/" + contact.getContactId());
	}

	@Test
	public void givenContactToDelete_WhenDeleted_ShouldMatch200ResponseAndCount() {
		int statusCode = deleteContact("Kamala Harris").getStatusCode();
		assertEquals(200, statusCode);
		Contact[] arrayOfContact = getContactList();
		int count = arrayOfContact.length;
		assertEquals(17, count);
	}

}
