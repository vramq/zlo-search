package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;

import java.util.Date;
import java.util.List;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:22:50
 */
public interface AppLogic {
    void setLastIndexedNumber(Site site, int num);

    int getLastIndexedNumber(Site site);

    Date getLastIndexedDate(Site site);

    void setLastSavedDate(Site site, Date d);

    Date getLastSavedDate(Site site);

    void saveMessages(Site site, List<Message> msgs);

    /**
     * get message, saved in DB
     *
     * @param site site
     * @param num  message id
     * @return message obj
     */
    Message getMessageByNumber(Site site, int num);

    /**
     * get messages, saved in DB for site in given range
     *
     * @param site  site
     * @param start start index
     * @param end   end index
     * @return result messages
     */
    List<Message> getMessages(Site site, int start, int end);

    int getLastSavedMessageNumber(Site site);

    void saveSearchTextForAutocomplete(Site site, String text);

    List<String> autoCompleteText(Site site, String test, int limit);
}
