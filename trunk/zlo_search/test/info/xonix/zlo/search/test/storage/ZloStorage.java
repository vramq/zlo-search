package info.xonix.zlo.search.test.storage;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;

import java.io.*;
import java.util.*;

/**
 * Author: gubarkov
 * Date: 14.09.2007
 * Time: 15:19:58
 */
@Deprecated
public class ZloStorage implements Serializable {
    private static final Config config = AppSpringContext.get(Config.class);

    public static final String STORAGE_DIR = config.getProp("test.storage");
    public static final int FROM = Integer.parseInt(config.getProp("test.storage.from"));
    public static final int TO = Integer.parseInt(config.getProp("test.storage.to"));
    public static final String SERIALIZE_FILE_NAME = "serialized.bin";

    private Map<Integer, Message> storedMsgs = new HashMap<Integer, Message>(TO - FROM);
    private File serialized = new File(STORAGE_DIR + "/" + SERIALIZE_FILE_NAME);

    private class MsgsHolder implements Serializable {
        public Map<Integer, Message> getMsgs() {
            return storedMsgs;
        }
    }

    public ZloStorage() {
        this(false);
    }

    public ZloStorage(boolean recreate) {
        File dir = new File(STORAGE_DIR);

        if (!serialized.exists() || recreate) {
            if (!dir.exists()) {
                System.out.println("Making storage dir...");
                if (!dir.mkdirs())
                    throw new RuntimeException("Can't create " + STORAGE_DIR);
            }
            // recreate - need to del old:
            if (serialized.exists()) {
                System.out.println("Deleting serialized file...");
                if (!serialized.delete())
                    throw new RuntimeException("Can't delete " + serialized.getAbsolutePath());
            }

            retrieveAndSerialize();
        }
        if (!recreate)
            loadData();
    }

    private void retrieveAndSerialize() {
        System.out.println("Retrieving...");
        try {
            Site site = Site.forName("zlo");
            AppLogic appLogic = AppSpringContext.get(AppLogic.class);
            for (Message m : appLogic.getMessages(site, FROM, TO)) {
                if (m != null)
                    storedMsgs.put(m.getNum(), m);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serialized));
            oos.writeObject(new MsgsHolder());
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serialized));
            storedMsgs = ((MsgsHolder) ois.readObject()).getMsgs();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Message getMessageByNumber(int num) {
/*        for (int key : storedMsgs.keySet()) {
            if (key == num)
                return storedMsgs.get(key);
        }
        return null;*/
        Message message = storedMsgs.get(FROM + num % (TO - FROM));
        message.setNum(num);
        return message;
    }

    public List<Message> getMessages(int from, int to) {
        List<Message> msgs = new ArrayList<Message>(to - from);
/*        for (int key : storedMsgs.keySet()) {
            if (key >= from && key <= to) {
                msgs.add(storedMsgs.get(key));
            }
        }*/

        // hack - just return as many as needed
        for (int i = from; i < to; i++) {
            msgs.add(getMessageByNumber(i));
        }
        return msgs;
    }

    public int getLastMessageNumber() {
        return Collections.max(storedMsgs.keySet());
    }

    static final long serialVersionUID = 5171735320533619859L; // for deserialization
}
