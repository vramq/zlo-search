package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.db.DbAccessor;
import info.xonix.zlo.search.model.Message;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 22:31:10
 */
@Deprecated
public class DB implements IndexingSource {
    private static Logger logger = Logger.getLogger("DB");

    private DbAccessor dbAccessor;

    public DB(DbAccessor dbAccessor) {
        this.dbAccessor = dbAccessor;
    }

    public void saveMessages(List<Message> msgs) {
        saveMessages(msgs, true);
    }

    public void saveMessages(List<Message> msgs, boolean fast) {
        try {
            logger.info(dbAccessor.getName() + " - Saving (" + msgs.get(0).getNum() + " - " + msgs.get(msgs.size() - 1).getNum() + ") msgs to DB...");
            if (fast)
                dbAccessor.getDbManager().saveMessagesFast(msgs);
            else {
                throw new UnsupportedOperationException();
            }
            logger.info(dbAccessor.getName() + " - Successfully saved " + msgs.size() + " msgs to DB.");
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public Message getMessageByNumber(int num) {
        try {
            return dbAccessor.getDbManager().getMessageByNumber(num);
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public List<Message> getMessages(int start, int end) {
        try {
            return dbAccessor.getDbManager().getMessagesByRange(start, end);
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }

    public int getLastMessageNumber() {
        try {
            return dbAccessor.getDbManager().getLastMessageNumber();
        } catch (DbException e) {
            throw new DAOException(this, e);
        }
    }
}
