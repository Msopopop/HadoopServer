package ThreadUtil;

import Utils.AttrUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class DicomThread extends BaseThread implements Runnable {

    private static Logger logger = Logger.getLogger(DicomThread.class);
    private static String FILE_ROOT_DIR;

    private static String HDFS_ROOT_DIR;

    public void setHdfsRootDir(String hdfsRootDir) {
        HDFS_ROOT_DIR = hdfsRootDir;
    }
    private static WatchService FileWatchService;

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

    @Override
    @SuppressWarnings("static-access")
    public void run() {
        try {
            this.runDicom();
        } catch (IOException | InterruptedException e) {
            logger.error(e.toString());
        }
    }

    @SuppressWarnings("static-access")
    private void runDicom() throws IOException, InterruptedException {
        while (true) {
            WatchKey watchKey = FileWatchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                        && event.context().toString().endsWith(".fin")) {

                    String Date = LocalDate.now().toString(); // "2018-12-25"
                    String FileExName = event.context().toString(); // "a.dcm.fin"
                    String FileName = FileExName.substring(0, FileExName.length() - 4);

                    String FullFileName = FILE_ROOT_DIR + FileName; // "home/xxx/a.dcm"
                    File dcmFile = new File(FullFileName);

                    // Create a new dir classified by date
                    // hdfs://${HDFS_NODE_NAME}:9000/dicomFile/yyyy-mm-dd
                    hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                    // Upload files to hdfs://master:9000/dicomFile/yyyy-mm-dd
                    hdfsUtil.uploadFile(dcmFile, HDFS_ROOT_DIR + Date);

                    AttrUtil attrUploadUtil = new AttrUtil(dcmFile);
                    attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR, Date, true);

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
