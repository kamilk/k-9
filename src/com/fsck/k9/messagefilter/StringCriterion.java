package com.fsck.k9.messagefilter;

/**
 * Base class for a filtering criteria comparing the specified string field
 *   with a given value
 */
public abstract class StringCriterion extends FilteringCriterion {
	protected String mReferenceValue;

	public StringCriterion(String reference_value) {
		super();
		mReferenceValue = reference_value;
	}

}
