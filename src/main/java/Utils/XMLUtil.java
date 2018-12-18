package Utils;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

public class XMLUtil {
    private static final String GSPS_ROOT_DIR = "/home/verizonwu/GSPSFile/";
    private static final String DICOM_ROOT_DIR = "/home/hadoop/dicomFile/";
    private static final String HDFS_ROOT_DIR_DICOM = "/dicomFile/";
    private static final String HDFS_ROOT_DIR_GSPS = "/GSPSFile/";
    private static final String TABLE_NAME = "DicomAttr";
    private static final String HDFS_NODE_NAME = "master";
    private static final String HBASE_ZOOKEEPER_QUORUM = "slave";
    private static Logger logger = Logger.getLogger(XMLUtil.class);
    private static Document document;

    public XMLUtil(File file) throws DocumentException {
        document = new SAXReader().read(file);
    }

    public XMLUtil(String filePath) throws DocumentException {
        document = new SAXReader().read(new File(filePath));
    }

    //TODO Not finished yet
    public void parseParameters() {
        try {
            Element node = document.getRootElement();
            if (node.getName() != "settings") {
                throw new IllegalArgumentException("Root node must be 'settings");
            }
        } catch (IllegalArgumentException e) {
            logger.fatal(e.toString());
            System.exit(-1);
        } finally {

        }
    }
}
