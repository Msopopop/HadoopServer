package ThreadUtil;

import Utils.AttrUtil;
import Utils.HBaseUtil;
import Utils.HDFSUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class JsonThread implements Runnable {

    public static String HDFS_NODE_NAME;
    public static String HBASE_ZOOKEEPER_QUORUM;
    public static String TABLE_NAME;
    public static String HDFS_ROOT_DIR;
    private static Logger logger = Logger.getLogger(JsonThread.class);
    private static String FILE_ROOT_DIR;
    private static WatchService FileWatchService;
    private static HDFSUtil hdfsUtil;
    private static HBaseUtil HBaseUtil;


    public JsonThread() {
        try {
            FileWatchService = FileSystems.getDefault().newWatchService();

        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public void setFilePath(String FILE_ROOT_DIR) throws IOException {
        JsonThread.FILE_ROOT_DIR = FILE_ROOT_DIR;
        Paths.get(FILE_ROOT_DIR).register(FileWatchService, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Override
    @SuppressWarnings("static-access")
    public void run() {

    }

    @SuppressWarnings("static-access")
    private void runDicom(File GSPSFile) throws IOException, InterruptedException {
        while (true) {
            WatchKey watchKey = FileWatchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && event.context().toString().endsWith(".fin")) {
                    String Date = LocalDate.now().toString(); // "2018-12-25"
                    hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                    // Upload files to hdfs://master:9000/GSPSFile/yyyy-mm-dd
                    hdfsUtil.uploadFile(GSPSFile, HDFS_ROOT_DIR + Date);

                    AttrUtil attrUploadUtil = new AttrUtil(GSPSFile);
                    attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR, Date, false);
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
