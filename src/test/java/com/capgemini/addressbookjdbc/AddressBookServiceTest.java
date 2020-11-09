package com.capgemini.addressbookjdbc;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.time.LocalDate;
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

	/**
	 * checking Updated info of a persons is in sync with database
	 * 
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	@Test
	public void givenNewDataForContact_WhenUpdated_ShouldBeInSync() throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.updatePersonsPhone("Shubham", 222222222);
		addressBookService.readContactData(IOService.DB_IO);
		boolean result = addressBookService.checkContactDataSync("Shubham Jangale");
		assertEquals(true, result);
	}
	
	/**
	 * UC 18
	 * checking if the getContactsByDate() method returns list of persons added
	 * between given dates
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrieved_ShouldMatchContactAddedInGivenDateRangeCount()
			throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactByDateList = null;
		LocalDate start = LocalDate.of(2018, 11, 12);
		LocalDate end = LocalDate.now();
		contactByDateList = addressBookService.getContactsByDate(start, end);
		assertEquals(2, contactByDateList.size());
	}
	
	/**
	 * UC 19
	 * @throws DatabaseException
	 */
	@Test
	public void givenContactDataInDB_WhenRetrievedByCity_ShouldMatchContactInCityCount()
			throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactByCity = addressBookService.getContactsByCity("Mumbai");
		assertEquals(3, contactByCity.size());
	}
	
	@Test
	public void givenContactDataInDB_WhenRetrievedByState_ShouldMatchContactInStateCount()
			throws DatabaseException {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> contactByState = addressBookService.getContactsByState("Maharashtra");
		assertEquals(7, contactByState.size());
	}
}
