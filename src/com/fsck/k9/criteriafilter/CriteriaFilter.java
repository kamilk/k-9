package com.fsck.k9.criteriafilter;

import com.fsck.k9.mail.Flag;
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
     * @param controller Controller to deal with the message
     * @param message Local message to be checked
     *
     * @return true if the new message should appear in a folder, otherwise false
     * @throws MessagingException from Message.setFlag()
     */
    public boolean ApplyToMessage(final MessagingController controller, Message message) throws MessagingException {
        if (message.getSubject() != null && message.getSubject().contains("spam")) {
		message.setFlag(Flag.SEEN, true); //mark as seen to avoid notifications
		controller.deleteMessages(new Message[]{message}, null);//delete
		return false;
        }
        return true;
    }
}
