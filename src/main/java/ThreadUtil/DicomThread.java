package ThreadUtil;

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

public class DicomThread implements Runnable {

    public static String HDFS_NODE_NAME;
    public static String HBASE_ZOOKEEPER_QUORUM;
    public static String TABLE_NAME;
    private static Logger logger = Logger.getLogger(DicomThread.class);
    private static String FILE_ROOT_DIR;
    private static String HDFS_ROOT_DIR;
    private static WatchService FileWatchService;
    private static HDFSUtil hdfsUtil;
    private static HBaseUtil HBaseUtil;

    private static boolean isDicomFile;

    public DicomThread() {
        try {
            FileWatchService = FileSystems.getDefault().newWatchService();

        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void setFilePath(String FILE_ROOT_DIR) throws IOException {
        DicomThread.FILE_ROOT_DIR = FILE_ROOT_DIR;
        Paths.get(FILE_ROOT_DIR).register(FileWatchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    public void setParameters(String HDFS_ROOT_DIR,
                              boolean isDicomFile) {
        DicomThread.HDFS_ROOT_DIR = HDFS_ROOT_DIR;
        DicomThread.isDicomFile = isDicomFile;
    }

    @Override
    @SuppressWarnings("static-access")
    public void run() {
        try {
            if (isDicomFile) {
                this.runDicom();
            } else {
                this.runGSPS();
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString());
        }
    }

    @SuppressWarnings("static-access")
    private void runDicom() throws IOException, InterruptedException {
        hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
        HBaseUtil = new HBaseUtil(HBASE_ZOOKEEPER_QUORUM);
        while (true) {
            WatchKey watchKey = FileWatchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                        && event.context().toString().endsWith(".fin")) {

                    String Date = LocalDate.now().toString(); // "2018-12-25"
                    String FileExName = event.context().toString(); // "a.dcm.fin"
                    String FileName = FileExName.substring(0, FileExName.length() - 5);

                    String FullFileName = FILE_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                    File dcmFile = new File(FullFileName);

                    // Create a new dir classified by date
                    // hdfs://${HDFS_NODE_NAME}:9000/dicomFile/yyyy-mm-dd
                    hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                    // Upload files to hdfs://master:9000/dicomFile/yyyy-mm-dd
                    hdfsUtil.uploadFile(dcmFile, HDFS_ROOT_DIR + Date);

                    AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                    attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR, Date, isDicomFile);

                    // Parse dcm file and put data to HBase
                    DCM2ImageUtil dcm2ImageUtil = new DCM2ImageUtil(dcmFile);
                    dcm2ImageUtil.setPreferWindow(true);
                    dcm2ImageUtil.setAutoWindowing(true);
                    // Convert and get all Image file names
                    List<String> jpgFileNameList = dcm2ImageUtil.parseImage(FILE_ROOT_DIR,
                            "JPEG", ".jpg", null, null, 1);
                    // Upload jpg file names to HBase
                    dcm2ImageUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR);
                    // Upload Image File to HDFS
                    if (null != jpgFileNameList && (!jpgFileNameList.isEmpty()))
                        for (String filePath : jpgFileNameList)
                            hdfsUtil.uploadFile(filePath, HDFS_ROOT_DIR + Date);
                }
            }
            watchKey.reset();
        }
    }

    @SuppressWarnings("static-access")
    private void runGSPS() throws IOException, InterruptedException {
        while (true) {
            WatchKey watchKey = FileWatchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && event.context().toString().endsWith(".GSPS")) {
                    String Date = LocalDate.now().toString(); // "2018-12-25"
                    String FileExName = event.context().toString(); // "a.dcm.GSPS"
                    String FileName = FileExName.substring(0, FileExName.length() - 5); // "a.dcm"
                    String FullFileName = FILE_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                    File GSPSFile = new File(FullFileName);
                    hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                    // Upload files to hdfs://master:9000/GSPSFile/yyyy-mm-dd
                    hdfsUtil.uploadFile(GSPSFile, HDFS_ROOT_DIR + Date);

                    AttrUtil attrUploadUtil = new AttrUtil(GSPSFile);
                    attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR, Date, isDicomFile);
                }
            }
            watchKey.reset();
        }
    }

    public void close() {
        try {
            FileWatchService.close();
            hdfsUtil.close();
            HBaseUtil.close();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
