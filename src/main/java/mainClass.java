import ThreadUtil.DicomThread;
import ThreadUtil.JsonThread;
import Utils.XMLUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(System.getProperty("user.dir") + "/conf/log4j.properties");
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

        dicomThread.start();
        jsonThread.start();

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
