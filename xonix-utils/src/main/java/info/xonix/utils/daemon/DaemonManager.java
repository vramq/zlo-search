package info.xonix.utils.daemon;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: gubarkov
 * Date: 27.04.12
 * Time: 22:21
 * <p/>
 * contains a list of daemons &amp; restarts them in case of unexpected finish
 */
public class DaemonManager {
    private final static Logger log = Logger.getLogger(DaemonManager.class);

    private List<DaemonBase> daemons = new LinkedList<DaemonBase>();

    public static long DEFAULT_CHECK_PERIOD = 1000;// 1 sec
    public static long WAIT_TERMINATION_PERIOD = 1000;// 1 sec

    private long checkPeriod;

    public DaemonManager() {
        this(DEFAULT_CHECK_PERIOD);
    }

    public DaemonManager(long checkPeriod) {
        this.checkPeriod = checkPeriod;

        startCheckingTimer();

        registerShutdownHook();
    }

    private void startCheckingTimer() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                final List<DaemonBase> toRestart = new LinkedList<DaemonBase>();

                final Iterator<DaemonBase> iterator = daemons.iterator();

                while (iterator.hasNext()) {
                    final DaemonBase daemon = iterator.next();
                    if (daemon.finishedAbnormally()) {
                        iterator.remove();
                        daemon.reset();
                        toRestart.add(daemon);
                    }
                }

                for (DaemonBase daemon : toRestart) {
                    log.warn("Daemon " + daemon.describe() + " finished unexpectedly, restarting",
                            daemon.getLastException());

                    startDaemon(daemon);
                }
            }
        }, 0, checkPeriod, TimeUnit.MILLISECONDS);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdownAll(true);
            }
        }));
    }

    public DaemonManager startDaemon(DaemonBase daemon) {
        log.info("Starting daemon: " + daemon.describe());

        daemon.start();

        daemons.add(daemon);

        return this;
    }

    public void shutdownAll(boolean waitAllExit) {
        log.info("Shutting down " + daemons.size() + " daemons...");

        for (DaemonBase daemon : daemons) {
            final DaemonState daemonState = daemon.getDaemonState();

            daemon.setExiting();

//            System.out.println("ds="+ daemonState);
//            System.out.println("ts="+ daemon.getProcess().getState());

            final DaemonBase.Process process = daemon.getProcess();

            if (daemonState == DaemonState.SLEEPING
                    || process.getState() == Thread.State.TIMED_WAITING
                    || process.getState() == Thread.State.WAITING) {
                log.info("Terminating sleeping daemon: " + daemon.describe());

                process.interrupt();
            }
        }

        if (waitAllExit) {
            while (true) {
                int notTerminated = 0;

                for (DaemonBase daemon : daemons) {
                    if (daemon.getProcess().getState() != Thread.State.TERMINATED) {
                        notTerminated++;
                    }
                }

                waitTermOneTime();

                if (notTerminated > 0) {
                    log.info("Still wating " + notTerminated + " daemons to terminate..");

                    waitTermOneTime();
                } else {
                    log.info("All daemons stopped. Exit.");
                    break;
                }
            }
        }

        daemons.clear();
    }

    private void waitTermOneTime() {
        try {
            Thread.sleep(WAIT_TERMINATION_PERIOD);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public List<DaemonBase> listDaemons() {
        return Collections.unmodifiableList(daemons);
    }
}
