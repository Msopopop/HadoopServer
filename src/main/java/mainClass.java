import Utils.AttrUtil;
import Utils.DCM2ImageUtil;
import Utils.HBaseUtil;
import Utils.HDFSUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);
    private static String FTP_ROOT_DIR = "/home/hadoop/dicomFile/";
    private static String HDFS_ROOT_DIR = "/dicomFile/";
    private static String HDFS_NODE_NAME = "master";
    private static String HBASE_ZOOKEEPER_QUORUM = "slave";

    public static void main(String[] args) throws Exception {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        HDFSUtil hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
        HBaseUtil HBaseUtil = new HBaseUtil(HBASE_ZOOKEEPER_QUORUM);
        // Initial WatchService on PATH ${FTP_ROOT_DIR}
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(FTP_ROOT_DIR).
                register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        @SuppressWarnings("static-access")
        Thread FTPListenerThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take();
                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        logger.debug("Event:" + event.kind() + " File affected: " + event.context());

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                                && event.context().toString().endsWith(".fin")) {

                            String Date = LocalDate.now().toString(); // "2018-12-25"
                            String FileExName = event.context().toString(); // "a.dcm.fin"
                            String FileName = FileExName.substring(0, FileExName.length() - 4); // "a.dcm"
                            String FullFileName = FTP_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                            File dcmFile = new File(FullFileName);

                            // Create a new dir classified by date
                            // hdfs://${HDFS_NODE_NAME}:9000/dicomFile/yyyy-mm-dd
                            hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                            // Upload files to hdfs://master:9000/dicomFile/yyyy-mm-dd
                            hdfsUtil.uploadFile(FTP_ROOT_DIR + FileName, HDFS_ROOT_DIR + Date);

                            AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                            attrUploadUtil.UploadToHBase(HBaseUtil, Date, HDFS_ROOT_DIR);
                            List<String> tableFullInfo = HBaseUtil.getAllRows(Date);

                            // Parse dcm file and put data to HBase
                            DCM2ImageUtil dcm2ImageUtil = new DCM2ImageUtil(dcmFile);
                            dcm2ImageUtil.setPreferWindow(true);
                            dcm2ImageUtil.setAutoWindowing(true);
                            // Convert and get all Image file names
                            /**
                             * The mode controlling compression settings, which must be set to
                             * one of the four <code>MODE_*</code> values.  The default is
                             * <code>MODE_COPY_FROM_METADATA</code>.
                             *
                             * <p> Subclasses that do not support compression may ignore this
                             * value.
                             *
                             * @see #MODE_DISABLED (for png)
                             * @see #MODE_EXPLICIT (for jpg)
                             * @see #MODE_COPY_FROM_METADATA (default)
                             * @see #MODE_DEFAULT
                             */
                            List<String> jpgFileNameList = dcm2ImageUtil.parseImage(FTP_ROOT_DIR,
                                    "JPEG", ".jpg", null, null, 1l);
                            // TODO Another Usage for generating png files(uncompressed image)
                            // List<String> PngFileNameList = dcm2ImageUtil.parseImage(FTP_ROOT_DIR, "PNG", ".png", null, "MODE_DISABLED", 1l);
                            // Upload jpg file names to HBase
                            dcm2ImageUtil.UploadToHBase(HBaseUtil, Date, HDFS_ROOT_DIR);
                            // Upload Image File to HDFS
                            if (null != jpgFileNameList && (!jpgFileNameList.isEmpty()))
                                for (String filePath : jpgFileNameList) hdfsUtil.uploadFile(filePath, HDFS_ROOT_DIR + Date);

                        }
                    }
                    watchKey.reset();
                }
            } catch (IOException | InterruptedException e) {
                logger.error(e.toString());
            }
        });
        FTPListenerThread.setDaemon(false);
        FTPListenerThread.start();
        //TODO Listener on modification to HBase database

        //TODO Modify the attrs and append to dcm file

        // Destory FTPListner thread before main thread exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                hdfsUtil.close();
                HBaseUtil.close();
                watchService.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }
}
