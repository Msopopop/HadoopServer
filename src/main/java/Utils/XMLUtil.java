package Utils;

import ThreadUtil.BaseThread;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;

public class XMLUtil {
    // Set through return value
    public static String JSON_ROOT_DIR;
    public static String DICOM_ROOT_DIR;
    public static String HDFS_ROOT_DIR_DICOM;
    public static String HDFS_ROOT_DIR_GSPS;

    // Set through BaseThread Methods
    private static String HDFS_NODE_NAME;
    private static String HBASE_ZOOKEEPER_QUORUM;
    private static String TABLE_NAME;

    private static Logger logger = Logger.getLogger(XMLUtil.class);
    private static Document document;

    public XMLUtil(String filePath) throws DocumentException {
        document = new SAXReader().read(new File(filePath));
    }

    public static void parseParameters() {
        try {
            Element rootElement = document.getRootElement();
            if (rootElement.getName() != "Settings") {
                throw new IllegalArgumentException("Root node must be 'settings");
            } else {
                TABLE_NAME = setTableName(rootElement.element("TableName"));
                setHadoop(rootElement.element("Hadoop"));
                setDicomDir(rootElement.element("Dicom"));
                setGSPSDir(rootElement.element("GSPS"));
            }
        } catch (IllegalArgumentException e) {
            logger.fatal(e.toString());
            System.exit(-1);
        }
    }

    private static String setTableName(Element node) {
        return node.elementText("value");
    }

    private static void setHadoop(Element node) {
        HDFS_NODE_NAME = node.element("NameNode").elementText("value");
        HBASE_ZOOKEEPER_QUORUM = node.element("ZooKeeperQuorum").elementText("value");
    }

    private static void setDicomDir(Element node) {
        HDFS_ROOT_DIR_DICOM = node.element("HDFS").elementText("value") + "/";
        DICOM_ROOT_DIR = node.element("local").elementText("value") + "/";
    }

    private static void setGSPSDir(Element node) {
        HDFS_ROOT_DIR_GSPS = node.element("HDFS").elementText("value") + "/";
        JSON_ROOT_DIR = node.element("local").elementText("value") + "/";
    }

    public void setParameters() throws IOException {
        BaseThread.setHbaseZookeeperQuorum(HBASE_ZOOKEEPER_QUORUM);
        BaseThread.setTableName(TABLE_NAME);
        BaseThread.setHdfsNodeName(HDFS_NODE_NAME);
    }

}
