package info.xonix.zlo.search.test.junit.xmlfp;

import info.xonix.forumsearch.xmlfp.XmlFpException;
import info.xonix.forumsearch.xmlfp.XmlFpForum;
import info.xonix.zlo.search.dao.XmlFpDao;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.logic.forum_adapters.impl.XmlFpForumAdapter;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
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
public class WwwconfVotalkaTests {
    private static final String DESCRIPTOR_URL = "http://votalka.campus.mipt.ru/?xmlfp";

    private XmlFpForum xmlFpForum;
    private ForumAccessor forumAccessor;

    public static final int N = 10;

    @Before
    public void setup() throws XmlFpException {
        xmlFpForum = XmlFpForum.fromDescriptorUrl(DESCRIPTOR_URL);
        forumAccessor = new ForumAccessor(xmlFpForum);
    }

    @Test
    public void test_descriptor() throws XmlFpException {
        assertEquals("http://votalka.campus.mipt.ru/?index", xmlFpForum.getForumUrl());
        assertEquals("свободное™ общение", xmlFpForum.getTitle());
        assertEquals("диктатура свободного™ общения без модерации", xmlFpForum.getDescription());
        assertEquals(100, xmlFpForum.getMessageListMaxCount());
    }

    @Test
    public void test_list_last_N_msgs() throws XmlFpException {
        final long lastMessageNumber = xmlFpForum.getLastMessageNumber();

        for (long i = 0; i < N; i++) {
            System.out.println(xmlFpForum.getMessage(lastMessageNumber - i));
        }
    }

    @Test
    public void test_list_last_N_msgs_bunch() throws XmlFpException {
        final long lastMessageNumber = xmlFpForum.getLastMessageNumber();

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
    public void test_xmlfp_adaptor_unicode1() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = buildAdapter();

        final Message message = xmlFpForumAdapter.getMessage("votalka", 29590);

        System.out.println(message);
    }

    @Test
    public void test_xmlfp_adaptor2() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = buildAdapter();

        final List<Message> messages = xmlFpForumAdapter.getMessages("votalka", 1, 333);

        final int size = messages.size();
        assertEquals(332, size);
        assertEquals(1, messages.get(0).getNum());
        assertEquals(332, messages.get(size - 1).getNum());
    }

    @Test
    public void testNl() throws XmlFpException {
        final info.xonix.forumsearch.xmlfp.jaxb_generated.Message message = xmlFpForum.getMessage(101881);
        System.out.println(message.getContent().getBody());
    }

    private XmlFpForumAdapter buildAdapter() throws ForumAccessException {
        final XmlFpForumAdapter xmlFpForumAdapter = new XmlFpForumAdapter(DESCRIPTOR_URL);
        ReflectionTestUtils.setField(xmlFpForumAdapter, "xmlFpDao", AppSpringContext.get(XmlFpDao.class));

        xmlFpForumAdapter.afterPropertiesSet();
        return xmlFpForumAdapter;
    }
}
