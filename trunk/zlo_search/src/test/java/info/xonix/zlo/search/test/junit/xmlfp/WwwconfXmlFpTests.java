package info.xonix.zlo.search.test.junit.xmlfp;

import info.xonix.zlo.search.dao.XmlFpDao;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.impl.XmlFpForumAdapter;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
import info.xonix.zlo.search.xmlfp.XmlFpException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 20.03.12
 * Time: 14:50
 */
public class WwwconfXmlFpTests {
    private static final String DESCRIPTOR_URL = "http://votalka.campus.mipt.ru/?xmlfp";

    private ForumAccessor forumAccessor;
    public static final int N = 10;

    @Before
    public void setup() throws XmlFpException {
        forumAccessor = ForumAccessor.fromDescriptorUrl(DESCRIPTOR_URL);
    }

    @Test
    public void test_descriptor() throws XmlFpException {
        assertEquals("http://votalka.campus.mipt.ru/?index", forumAccessor.getForumUrl());
        assertEquals("свободное™ общение", forumAccessor.getTitle());
        assertEquals("диктатура свободного™ общения без модерации", forumAccessor.getDescription());
        assertEquals(100, forumAccessor.getMessageListMaxDelta());
    }

    @Test
    public void test_list_last_N_msgs() throws XmlFpException {
        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        for (long i = 0; i < N; i++) {
            System.out.println(forumAccessor.getMessage(lastMessageNumber - i));
        }
    }

    @Test
    public void test_list_last_N_msgs_bunch() throws XmlFpException {
        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        final List<Message> messageList = forumAccessor.getMessageList(lastMessageNumber - N + 1, lastMessageNumber);
        assertEquals(N, messageList.size());

        for (Message message : messageList) {
            System.out.println(message);
        }
    }

    @Test
    public void test_list_msgs_bunch1() throws XmlFpException {
        final List<Message> messageList = forumAccessor.getMessageList(101814, 101817);

        assertEquals(4, messageList.size());

        for (Message message : messageList) {
            System.out.println(message);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_list_msgs_bunch2() throws XmlFpException {
        // maxDelta=100
        final List<Message> messageList = forumAccessor.getMessageList(1, 102);
    }

    @Test
    public void test_xmlfp_adaptor1() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = buildAdapter();

        final Message message = xmlFpForumAdapter.getMessage("votalka", 123);

        System.out.println(message);
    }

    @Test
    public void test_xmlfp_adaptor2() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = buildAdapter();

        final List<Message> messages = xmlFpForumAdapter.getMessages("votalka", 1, 333);

        assertEquals(333, messages.size());
    }

    private XmlFpForumAdapter buildAdapter() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = new XmlFpForumAdapter(DESCRIPTOR_URL);
        ReflectionTestUtils.setField(xmlFpForumAdapter, "xmlFpDao", AppSpringContext.get(XmlFpDao.class));

        xmlFpForumAdapter.afterPropertiesSet();
        return xmlFpForumAdapter;
    }
}
