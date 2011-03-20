package com.fsck.k9.messagefilter;

import java.util.ArrayList;

import com.fsck.k9.mail.Flag;
import com.fsck.k9.mail.Message;
import com.fsck.k9.mail.store.LocalStore.LocalMessage;
import com.fsck.k9.mail.store.LocalStore.LocalFolder;
import com.fsck.k9.controller.MessagingController;

/**
 * The filter for checking the incoming messages for user-specified criteria.
 * If they are met, user-specified actions are performed.
 */
public class MessageFilter {

    ArrayList<FilteringCriterion> mCriteria = new ArrayList<FilteringCriterion>();
    ArrayList<LocalMessage> mMessagesToDelete = new ArrayList<LocalMessage>();
    ArrayList<Message> mMessagesToMarkAsSeen = new ArrayList<Message>();
    boolean mAll;

    public MessageFilter(boolean all) {
        mAll = all;
    }

    public void addCriterion(FilteringCriterion criteria) {
        mCriteria.add(criteria);
    }

    /**
     * Apply the filter to a message, performing appropriate actions if
     * necessary.
     *
     * @param message Remote message to be checked
     * @param localMessage Local version of the filtered message
     *
     * @return true if the new message should appear in the folder view.
     *         false means there's no point displaying or downloading the message
     */
    public boolean applyToMessage(final Message message, final LocalMessage localMessage) {
        if (mCriteria.isEmpty())
            return true;

        boolean result;
        if (mAll) {
            result = true;
        } else {
            result = false;
        }

        for (FilteringCriterion criterion : mCriteria) {
            boolean isMet = criterion.check(message);
            if (mAll) {
                result &= isMet;
                if (!result) {
                    //should be all, one is unmet
                    mMessagesToDelete.add(localMessage);
                    mMessagesToMarkAsSeen.add(message);
                    return false;
                }
            } else {
                result |= isMet;
                if (result) {
                    //should be any, one is met
                    mMessagesToDelete.add(localMessage);
                    mMessagesToMarkAsSeen.add(message);
                    return false;
                }
            }
        }

        return !result;
    }

    /**
     * Performs actions according to the results of the filtering.
     * @param controller Controller to deal with the message
     * @param folderUpdated Currently display folder. May be null.
     * @return How many messages in the current have been automatically marked as seen.
     */
    public int performActions(final MessagingController controller, final LocalFolder currentFolder) {
        int howManySeen = 0;
        for (Message message : mMessagesToDelete) {
            controller.deleteMessages(new Message[]{message}, null);
        }

        for (Message message : mMessagesToMarkAsSeen) {
            controller.setFlag(new Message[]{message}, Flag.SEEN, true);
            if (currentFolder != null && message.getFolder().getName().equals(currentFolder.getName())) {
                ++howManySeen;
            }
        }
        return howManySeen;
    }
}
