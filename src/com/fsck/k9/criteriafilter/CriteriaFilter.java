package com.fsck.k9.criteriafilter;

import java.util.ArrayList;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.MessagingException;
import com.fsck.k9.controller.MessagingController;

/**
 * The filter for checking the incoming messages for user-specified criteria.
 * If they are met, user-specified actions are performed.
 */
public class CriteriaFilter {
    /**
     * Apply the filter to a message, performing appropriate actions if
     * necessary.
     * 
     * @param message Local message to be checked
     *
     * @return true if the new message should appear in the folder view.
     *         false means there's no point displaying or downloading the message
     * @throws MessagingException from Message.setFlag()
     */
    public boolean ApplyToMessage(Message message) throws MessagingException {
    	if (mMessagesToDelete.contains(message)) {
    		//already's been filtered
    		return false;
    	}
    	
        if (message.getSubject() != null && message.getSubject().contains("spam")) {
	        mMessagesToDelete.add(message);
	        return false;
        }
        return true;
    }
    
    /**
     * Performs actions according to the results of the filtering. 
     * @param controller Controller to deal with the message
     */
    public void PerformActions(final MessagingController controller) {
    	for (Message message : mMessagesToDelete) {
    		controller.deleteMessages(new Message[]{message}, null); 
    	}
    }
    
    ArrayList<Message> mMessagesToDelete = new ArrayList<Message>(); 
}
