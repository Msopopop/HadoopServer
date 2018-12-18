import ThreadUtil.DicomThread;
import Utils.XMLUtil;
import org.apache.log4j.Logger;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);
    private static final String GSPS_ROOT_DIR = "/home/verizonwu/GSPSFile/";
    private static final String DICOM_ROOT_DIR = "/home/hadoop/dicomFile/";

    private static final String HDFS_ROOT_DIR_DICOM = "/dicomFile/";
    private static final String HDFS_ROOT_DIR_GSPS = "/GSPSFile/";

    private static final String TABLE_NAME = "DicomAttr";

    private static final String HDFS_NODE_NAME = "master";
    private static final String HBASE_ZOOKEEPER_QUORUM = "slave";

    public static void main(String[] args) throws Exception {
        String path = mainClass.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
        int lastIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
        path = path.substring(firstIndex, lastIndex);

        String confPath = path + "settings.xml";

        XMLUtil xmlUtil = new XMLUtil(confPath);
        //TODO Not finished yet -- set all Strings
        xmlUtil.parseParameters();

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        DicomThread.HDFS_NODE_NAME = HDFS_NODE_NAME;
        DicomThread.HBASE_ZOOKEEPER_QUORUM = HBASE_ZOOKEEPER_QUORUM;
        DicomThread.TABLE_NAME = TABLE_NAME;

        DicomThread dicomThread = new DicomThread();
        DicomThread gspsThread = new DicomThread();

        dicomThread.setFilePath(DICOM_ROOT_DIR);
        gspsThread.setFilePath(GSPS_ROOT_DIR);

        dicomThread.setParameters(HDFS_ROOT_DIR_DICOM, true);
        gspsThread.setParameters(HDFS_ROOT_DIR_GSPS, false);

        dicomThread.run();
        gspsThread.run();

        // Close listeners thread before main thread exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                dicomThread.close();
                gspsThread.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }
}
