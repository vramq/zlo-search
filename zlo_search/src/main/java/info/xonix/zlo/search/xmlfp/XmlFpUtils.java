package info.xonix.zlo.search.xmlfp;

import info.xonix.forumsearch.xmlfp.XmlFpContext;
import info.xonix.forumsearch.xmlfp.XmlFpException;
import info.xonix.forumsearch.xmlfp.jaxb_generated.Forum;
import info.xonix.forumsearch.xmlfp.jaxb_generated.ObjectFactory;
import info.xonix.forumsearch.xmlfp.utils.MarshalUtils;
import info.xonix.forumsearch.xmlfp.utils.XmlFpMarshalException;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;
import info.xonix.zlo.search.logic.forum_adapters.impl.XmlFpForumAdapter;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfForumAdapter;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfUtils;
import info.xonix.zlo.search.model.Message;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * User: gubarkov
 * Date: 12.02.12
 * Time: 1:59
 */
public class XmlFpUtils {
    public static String messageToXml(String forumId, Message message) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMessageMarshaller(),
                    Convert.toJaxbMessage(forumId, message));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static String messagesToXml(String forumId, List<Message> messages) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getMessagesMarshaller(),
                    Convert.toJaxbMessages(forumId, messages));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message messageFromXml(String xml) throws XmlFpException {
        try {
            return Convert.fromJaxbMessage(
                    MarshalUtils.<info.xonix.forumsearch.xmlfp.jaxb_generated.Message>unmarshal(
                            XmlFpContext.getMessageUnmarshaller(), xml));
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static Forum descriptorFromXml(String xml) throws XmlFpException {
        try {
            return MarshalUtils.unmarshal(
                    XmlFpContext.getDescriptorUnmarshaller(), xml);
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static String lastMessageNumberToXml(int num) {
        try {
            return MarshalUtils.marshal(XmlFpContext.getLastMessageNumberMarshaller(),
                    new ObjectFactory().createLastMessageNumber((long) num));
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }

    public static long lastMessageNumberFromXml(String xml) throws XmlFpException {
        try {
            final JAXBElement<Long> res = MarshalUtils.unmarshal(
                    XmlFpContext.getLastMessageNumberUnmarshaller(), xml);
            return res.getValue();
        } catch (XmlFpMarshalException e) {
            throw new XmlFpException(e);
        }
    }

    public static String siteDescriptorToXml(String forumId) {
        try {
            // TODO: improve
            final ForumAdapter adapter = GetForum.adapter(forumId);
            if (adapter instanceof WwwconfForumAdapter) {
                return MarshalUtils.marshal(XmlFpContext.getDescriptorMarshaller(),
                        Convert.toJaxbForum(forumId, WwwconfUtils.getWwwconfParams(forumId)));
            } else {
                return ((XmlFpForumAdapter) adapter).getXmlFpForum().formDescriptorXml();
            }
        } catch (XmlFpMarshalException e) {
            throw new RuntimeException(e);
        }
    }
}
