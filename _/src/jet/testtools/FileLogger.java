package jet.testtools;

import java.io.*;
import java.util.logging.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Used as an example in Chapter 12.
 *
 * @author Lindsay Peters
 */
public class FileLogger {

    public static String logDirName;
    private static Logger logger;
    private static FileHandler fileHandler;
    private static String NL = System.getProperty("line.separator");

    static {
        initialiseLogger(true);
    }

    //Private constructor as this class has only static methods.
    private FileLogger() {}

    private static void initialiseLogger(boolean appendToExistingLog) {
        File currentDir = new File( System.getProperty( "user.dir"));
        File logDir = new File( currentDir, "logs" );
        logDir.mkdirs();
        logDirName = logDir.getPath();
        //Roll the log files if there is already a handler.
        if (fileHandler != null) {
            fileHandler.flush();
            fileHandler.close();
            logger.removeHandler(fileHandler);
        }
        logger = java.util.logging.Logger.getLogger("RippledownLogger");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINEST);
        try {
            fileHandler = new FileHandler(logDir.getPath() + File.separatorChar + "log.txt", 1024 * 10240, 400, appendToExistingLog);
            fileHandler.setFormatter(new LogFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rollLogOver() {
        initialiseLogger(false);
    }

    private static class LogFormatter extends Formatter {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        public String format(LogRecord record) {
            StringBuffer result = new StringBuffer();
            //Date, time
            result.append(dateFormat.format(new Date(record.getMillis())));
            result.append(' ');
            //Level
            result.append(record.getLevel().toString());
            result.append(' ');
            //The actual message.
            result.append(record.getMessage());
            //The exception, if present
            if (record.getThrown() != null) {
                result.append(NL);
                result.append(record.getThrown());
                StackTraceElement[] stes = record.getThrown().getStackTrace();
                for (int i = 0; i < stes.length; i++) {
                    StackTraceElement ste = stes[i];
                    result.append(NL);
                    result.append("\tat ");
                    result.append(ste.toString());
                }
            }
            //Extra newline so that all log records start on a new line.
            result.append(NL);
            return result.toString();
        }
    }

    /**
     * Logs a logger event.
     *
     * @param event the event to log to the System logger.
     */
    public static void log(LogRecord event) {
        logger.log(event);
        //ensure that the event hits the disk immediately.
        fileHandler.flush();
    }
}
