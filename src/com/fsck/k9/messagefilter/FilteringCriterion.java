package com.fsck.k9.messagefilter;

import com.fsck.k9.mail.Message;

/**
 * Filtering criteria based on comparision of a specified string field with 
 * a given value
 */
public class FilteringCriterion {
	/**
	 * Enumaration type representing operations which can be used in this criteria
	 */
	public enum Operation {
		EQUALS, CONTAINS
	}
	
	FilteringCriterion.Operation mOperation;
	String mReferenceValue; 
	
	/**
	 * Constructor creating a criteria performing operation: [field] [operation] [value] 
	 *   (for example "subject contains 'spam'"
	 * @param operation Operation to be performed on the field
	 * @param value Reference value for the criteria
	 */
	public FilteringCriterion(FilteringCriterion.Operation operation, String value) {
	    mOperation = operation; 
		mReferenceValue = value; 
	}
	
	public boolean check(final Message message) {
		String subject = message.getSubject(); 
		if (subject != null)
		{
			switch(mOperation)
			{
			case CONTAINS:
				return subject.contains(mReferenceValue); 
			case EQUALS:
				return subject.equals(mReferenceValue); 
			}
		}
		return false; 
	}
}
