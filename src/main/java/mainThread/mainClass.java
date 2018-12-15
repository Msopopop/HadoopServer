package mainThread;

import Utils.AttrUtil;
import Utils.DCM2JPGUtil;
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
        HDFSUtil hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
        HBaseUtil HBaseUtil = new HBaseUtil(HBASE_ZOOKEEPER_QUORUM);
        // Initial WatchService on PATH ${FTP_ROOT_DIR}
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(FTP_ROOT_DIR).
                register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

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

                            //TODO uploaded failed (empty data)
                            // Upload files to hdfs://master:9000/dicomFile/yyyy-mm-dd
                            //hdfsUtil.uploadFile(FTP_ROOT_DIR + FileName, HDFS_ROOT_DIR + Date);

                            AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                            attrUploadUtil.UploadToHBase(HBaseUtil, Date, HDFS_ROOT_DIR);

                            // Parse dcm file and put data to HBase
                            DCM2JPGUtil dcm2JPGUtil = new DCM2JPGUtil(dcmFile);
                            // Convert and get all JPG file names
                            List<String> jpgFileNameList = dcm2JPGUtil.parseJPG(FTP_ROOT_DIR,
                                    null, null, null, null, 1l);
                            // Upload jpg file names to HBase
                            dcm2JPGUtil.UploadToHBase(HBaseUtil, Date, HDFS_ROOT_DIR);
                            // Upload JPG File to HDFS
                            // for (String filePath : jpgFileNameList) hdfsUtil.uploadFile(filePath, HDFS_ROOT_DIR + Date);

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
                watchService.close();
                // Close HDFS and Hbase Clients
                hdfsUtil.close();
                HBaseUtil.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }

    // process file name to delete ".finished"
    private static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
