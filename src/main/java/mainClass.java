import ThreadUtil.DicomThread;
import ThreadUtil.JsonThread;
import Utils.XMLUtil;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            String loggerConf = System.getProperty("user.dir") + "/conf/log4j.properties";
            InputStream in = new BufferedInputStream(new FileInputStream(loggerConf));
            properties.load(in);
        } catch (IOException e) {
            logger.error("Unable to log: configuration file missing");
        }
    }

    public static void main(String[] args) throws Exception {
        String confPath = System.getProperty("user.dir") + "/conf/settings.xml";

        XMLUtil xmlUtil = new XMLUtil(confPath);
        XMLUtil.parseParameters();
        xmlUtil.setParameters();

        DicomThread dicomThread = new DicomThread();
        JsonThread jsonThread = new JsonThread();

        dicomThread.setFilePath(XMLUtil.DICOM_ROOT_DIR);
        dicomThread.setHdfsRootDir(XMLUtil.HDFS_ROOT_DIR_DICOM);
        jsonThread.setFilePath(XMLUtil.JSON_ROOT_DIR);
        jsonThread.setHdfsRootDir(XMLUtil.HDFS_ROOT_DIR_GSPS);

        dicomThread.run();
        jsonThread.run();

        // Close listeners thread before main thread exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                dicomThread.close();
                jsonThread.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }
}
