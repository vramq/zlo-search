package info.xonix.utils.daemon;

import org.apache.log4j.Logger;

import java.util.Random;

/**
 * User: gubarkov
 * Date: 01.05.12
 * Time: 22:37
 */
public class RandomlyFailingDaemon extends DaemonBase {
    private final static Logger log = Logger.getLogger(RandomlyFailingDaemon.class);
    private float failingRate;

    private final static Random random = new Random();

    protected RandomlyFailingDaemon(String forumId, float failingRate) {
        super(forumId);
        this.failingRate = failingRate;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void perform() {
        try {
            while (random.nextFloat() >= failingRate) {
                stopIfExiting();

                log.info(describe() + " - working...");
                work(1000);
            }

            log.info(describe() + " - ups: FAIL occured");
        } catch (InterruptedException e) {
            log.info(describe() + " - Exiting...");
        }
    }

    @Override
    public void cleanUp() {
        log.info(describe() + " - clean up...");
    }

    private void work(final long interval) {
        log.info(describe() + " - working " + interval + "...");
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            log.info(describe() + " - work interrupted.");
        }
    }
}
