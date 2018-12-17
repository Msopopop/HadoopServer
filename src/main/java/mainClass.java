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
    private static String GSPS_ROOT_DIR = "/home/hadoop/GSPSFile/";
    private static String DICOM_ROOT_DIR = "/home/hadoop/dicomFile/";
    private static String HDFS_ROOT_DIR_DICOM = "/dicomFile/";
    private static String HDFS_ROOT_DIR_GSPS = "/GSPSFile/";
    private static String HDFS_NODE_NAME = "master";
    private static String HBASE_ZOOKEEPER_QUORUM = "slave";
    private static String TABLE_NAME = "DicomAttr";

    public static void main(String[] args) throws Exception {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        HDFSUtil hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
        HBaseUtil HBaseUtil = new HBaseUtil(HBASE_ZOOKEEPER_QUORUM);
        // Listener on DICOM File
        WatchService DicomFileWatchService = FileSystems.getDefault().newWatchService();
        {
            Paths.get(DICOM_ROOT_DIR).
                    register(DicomFileWatchService, StandardWatchEventKinds.ENTRY_CREATE);
        @SuppressWarnings("static-access")
        Thread FTPListenerThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = DicomFileWatchService.take();
                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                                && event.context().toString().endsWith(".fin")) {

                            String Date = LocalDate.now().toString(); // "2018-12-25"
                            String FileExName = event.context().toString(); // "a.dcm.fin"
                            String FileName = FileExName.substring(0, FileExName.length() - 4); // "a.dcm"
                            String FullFileName = DICOM_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                            File dcmFile = new File(FullFileName);

                            // Create a new dir classified by date
                            // hdfs://${HDFS_NODE_NAME}:9000/dicomFile/yyyy-mm-dd
                            hdfsUtil.mkdir(HDFS_ROOT_DIR_DICOM + Date);
                            // Upload files to hdfs://master:9000/dicomFile/yyyy-mm-dd
                            hdfsUtil.uploadFile(DICOM_ROOT_DIR + FileName, HDFS_ROOT_DIR_DICOM + Date);

                            AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                            attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR_DICOM, true);

                            // Parse dcm file and put data to HBase
                            DCM2ImageUtil dcm2ImageUtil = new DCM2ImageUtil(dcmFile);
                            dcm2ImageUtil.setPreferWindow(true);
                            dcm2ImageUtil.setAutoWindowing(true);
                            // Convert and get all Image file names
                            List<String> jpgFileNameList = dcm2ImageUtil.parseImage(DICOM_ROOT_DIR,
                                    "JPEG", ".jpg", null, null, 1l);
                            // TODO Another Usage for generating png files(uncompressed image)
                            // List<String> PngFileNameList = dcm2ImageUtil.parseImage(FTP_ROOT_DIR, "PNG", ".png", null, "MODE_DISABLED", 1l);
                            // Upload jpg file names to HBase
                            dcm2ImageUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR_DICOM);
                            // Upload Image File to HDFS
                            if (null != jpgFileNameList && (!jpgFileNameList.isEmpty()))
                                for (String filePath : jpgFileNameList)
                                    hdfsUtil.uploadFile(filePath, HDFS_ROOT_DIR_DICOM + Date);

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
        }
        // Listener on GSPS File
        WatchService GSPSFileWatchService = FileSystems.getDefault().newWatchService();
        {
            Paths.get(GSPS_ROOT_DIR).
                    register(GSPSFileWatchService, StandardWatchEventKinds.ENTRY_CREATE);
            @SuppressWarnings("static-access")
            Thread GSPSListenerThread = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey watchKey = GSPSFileWatchService.take();
                        for (WatchEvent<?> event : watchKey.pollEvents()) {
                            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                                    && event.context().toString().endsWith(".GSPS")) {

                                String Date = LocalDate.now().toString(); // "2018-12-25"
                                String FileExName = event.context().toString(); // "a.dcm.GSPS"
                                String FileName = FileExName.substring(0, FileExName.length() - 5); // "a.dcm"
                                String FullFileName = GSPS_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                                File dcmFile = new File(FullFileName);
                                hdfsUtil.mkdir(HDFS_ROOT_DIR_GSPS + Date);
                                // Upload files to hdfs://master:9000/GSPSFile/yyyy-mm-dd
                                hdfsUtil.uploadFile(GSPS_ROOT_DIR + FileName, HDFS_ROOT_DIR_GSPS + Date);

                                AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                                attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR_GSPS, false);
                            }
                        }
                        watchKey.reset();
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error(e.toString());
                }
            });
            GSPSListenerThread.setDaemon(false);
            GSPSListenerThread.start();
        }
        // Destory FTPListner thread before main thread exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                hdfsUtil.close();
                HBaseUtil.close();
                DicomFileWatchService.close();
                GSPSFileWatchService.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }
}
