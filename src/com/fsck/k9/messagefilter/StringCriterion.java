package com.fsck.k9.messagefilter;

public abstract class StringCriterion extends FilteringCriterion {
	protected String mReferenceValue;

	public StringCriterion(String reference_value) {
		super();
		mReferenceValue = reference_value;
	}

}
