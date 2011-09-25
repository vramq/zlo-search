package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageShallow;
import info.xonix.zlo.search.model.Topic;

import java.util.List;
import java.util.Map;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface MessagesDao {
    void saveMessagesFast(Site site, List<Message> msgs);

    void saveMessagesFast(Site site, List<Message> msgs, boolean updateIfExists);

    Message getMessageByNumber(Site site, int num);

    List<Message> getMessagesByRange(Site site, int start, int end);

    List<Message> getMessages(Site site, int[] nums);

    List<MessageShallow> getShallowMessages(Site site, int[] nums);

    int getLastMessageNumber(Site site);

    // TODO: introduce right dmo

    List<Topic> getTopicList(Site site);

    String[] getTopics(Site site);

    Map<String, Integer> getTopicsHashMap(Site site);

    void saveSearchTextForAutocomplete(Site site, String text);

    List<String> autoCompleteText(Site site, String text, int limit);
}
