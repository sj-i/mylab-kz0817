package anbutter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import java.lang.RuntimeException;

public class App {

    private static final Logger logger = LogManager.getLogger();

    class ArgParser {
        int numLoops = 0;

        private Iterator<String> argIter;
        private Map<String, Runnable> handlers = new HashMap<>();

        ArgParser(String[] args) {
            argIter = Arrays.asList(args).iterator();

            handlers.put("-u", () -> {
                if (!argIter.hasNext()) {
                    throw new RuntimeException("Need integer option");
                }
                numLoops = Integer.parseInt(argIter.next());
            });

            while (argIter.hasNext()) {
                final var opt = argIter.next();
                final var handler = handlers.get(opt);
                if (handler == null) {
                    throw new RuntimeException(
                        String.format("Unknown option: %s", opt));
                }
                handler.run();
            }
        }
    }

    void start(String[] args) {
        var option = new ArgParser(args);

        var q = new LinkedBlockingQueue<Integer>();
        var an = new An(q, option.numLoops);
        var butter = new Butter(q);
        an.start();
        butter.start();

        try {
            an.join();
            butter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        logger.info("App started.");
        try {
            (new App()).start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
