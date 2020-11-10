package com.capgemini.addressbookjdbc;

import java.util.Comparator;

public class SortByZipCompare implements Comparator<Contact>{
	public int compare(Contact a, Contact b) {
		return a.getZip() - b.getZip();
	}
}