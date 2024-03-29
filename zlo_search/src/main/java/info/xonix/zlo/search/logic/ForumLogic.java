package info.xonix.zlo.search.logic;


import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.model.Message;

import java.util.List;

/**
 * User: Vovan
 * Date: 12.06.2010
 * Time: 22:00:19
 */
public interface ForumLogic {
    int getLastMessageNumber(String forumId) throws ForumAccessException;

    List<Message> getMessages(String forumId, int from, int to) throws ForumAccessException;

    Message getMessageByNumber(String forumId, int num) throws ForumAccessException;
}
