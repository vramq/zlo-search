package info.xonix.zlo.search.logic.site;

import info.xonix.utils.Check;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.forum_adapters.impl.wwwconf.WwwconfParams;
import info.xonix.zlo.search.model.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Author: gubarkov
 * Date: 01.10.2007
 * Time: 21:18:54
 */
public class MessageRetriever implements InitializingBean {
    private static Logger log = Logger.getLogger(MessageRetriever.class);

    @Autowired
    private Config config;

    @Autowired
    private PageParser pageParser;

    @Autowired
    private PageRetriever pageRetriever;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
        Check.isSet(pageParser, "pageParser");
        Check.isSet(pageRetriever, "pageRetriever");
    }

    public Message getMessage(String forumId, WwwconfParams wwwconfParams, int num) throws RetrieverException, PageParseException {
        return getMessage(forumId, wwwconfParams, num, 3);
    }

    public Message getMessage(String forumId, WwwconfParams wwwconfParams, int num, int retries) throws RetrieverException, PageParseException {
        SiteException lastException = null;

        for (int i = 0; i <= retries; i++) {
            try {
                return pageParser.parseMessage(forumId, wwwconfParams, pageRetriever.getPageContentByNumber(wwwconfParams, num), num);
            } catch (SiteException ex) {
                lastException = ex;

                if (i < retries) {
                    log.warn("Failed to receive msg#: " + num + " from site: (" + forumId +
                            "). Retry # " + (i + 1) + "... Reason: " + ex);
                }
            }
        }

        // TODO: improve--VVV
        if (lastException instanceof RetrieverException) {
            throw (RetrieverException) lastException;
        }
        throw (PageParseException) lastException;
    }

    public List<Message> getMessages(String forumId, WwwconfParams wwwconfParams, int from, int to) throws RetrieverException, PageParseException {
        Set<Integer> nums = new HashSet<Integer>(to - from);
        for (int i = from; i < to; i++) {
            nums.add(i);
        }
        return getMessages(forumId,wwwconfParams, nums);
    }

    public List<Message> getMessages(String forumId, WwwconfParams wwwconfParams, Iterable<Integer> numsToSave) throws RetrieverException, PageParseException {
        return getMessages(forumId, wwwconfParams, numsToSave, config.getRetrieverThreadNum());
    }

    public List<Message> getMessages(String forumId, WwwconfParams wwwconfParams, Iterable<Integer> numsToSave, int threadsNum) throws RetrieverException, PageParseException {
        final List<Message> msgs;

        if (threadsNum == 1) {
            msgs = new ArrayList<Message>();

            for (int num : numsToSave) {
                long t1 = System.currentTimeMillis();

                msgs.add(getMessage(forumId,wwwconfParams, num));

                long delta = System.currentTimeMillis() - t1;
                long toSleep = 1000 / GetForum.params(forumId).getIndexerLimitPerSecond() - delta;
                if (toSleep > 0) {
                    try {
                        Thread.sleep(toSleep);
                    } catch (InterruptedException e) {
                        log.warn("This should not happen!", e);
                    }
                }
            }
        } else {
            msgs = new Vector<Message>();
            List<Thread> threads = new ArrayList<Thread>();

            // starting all threads
            for (int i = 0; i < threadsNum; i++) {
                Thread t = new MessageRetrieverThread(this, forumId, wwwconfParams, numsToSave, msgs);
                t.start();
                threads.add(t);
            }

            // joining them - main thread waits for all
            try {
                for (Thread t : threads)
                    t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Exception exc = MessageRetrieverThread.getException();
            if (exc != null) {
                throw new RetrieverException("Page retrieve problem with " + forumId, exc);
            }
        }

        Collections.sort(msgs, Message.NUM_COMPARATOR);

        return msgs;
    }

    public int getLastMessageNumber(WwwconfParams wwwconfParams) throws RetrieverException {
        return pageRetriever.getLastRootMessageNumber(wwwconfParams);
    }
}
