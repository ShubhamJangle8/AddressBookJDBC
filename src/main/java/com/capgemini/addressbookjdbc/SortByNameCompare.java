package com.capgemini.addressbookjdbc;

import java.util.Comparator;

public class SortByNameCompare implements Comparator<Contact> {
	public int compare(Contact a, Contact b) {
		return (a.getFirstName() + " " + a.getLastName()).compareTo(b.getFirstName() + " " + b.getLastName());
	}
}