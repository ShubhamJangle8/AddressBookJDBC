package com.capgemini.addressbookjdbc;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.capgemini.addressbookjdbc.AddressBookService.IOService;

public class AddressBookServiceTest {
	/**
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactCount() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactData = addressBookService.readContactData(IOService.DB_IO);
		assertEquals(7, contactData.size());
	}
}
