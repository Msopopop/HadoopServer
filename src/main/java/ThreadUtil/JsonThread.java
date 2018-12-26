package ThreadUtil;

import Utils.AttrUtil;
import org.apache.log4j.Logger;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.tool.json2dcm.Json2Dcm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class JsonThread extends BaseThread implements Runnable {

    private static String HDFS_ROOT_DIR;
    private static Logger logger = Logger.getLogger(JsonThread.class);
    private static String FILE_ROOT_DIR;
    private static WatchService FileWatchService;

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
        try {
            runJson();
        } catch (IOException | InterruptedException e) {
            logger.error(e);
        }
    }

    public void setHdfsRootDir(String hdfsRootDir) {
        HDFS_ROOT_DIR = hdfsRootDir;
    }

    @SuppressWarnings("static-access")
    private void runJson() throws IOException, InterruptedException {
        while (true) {
            WatchKey watchKey = FileWatchService.take();
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && event.context().toString().endsWith(".fin")) {
                    String Date = LocalDate.now().toString(); // "2018-12-25"
                    String FileExName = event.context().toString(); // "a.json.fin"
                    String FileName = FileExName.substring(0, FileExName.length() - 4);

                    String FullFileName = FILE_ROOT_DIR + FileName; // "home/xxx/a.json"
                    File GSPSFile = new File(FullFileName);

                    if (GSPSFile.exists() && GSPSFile.length() != 0) {
                        hdfsUtil.mkdir(HDFS_ROOT_DIR + Date);
                        // Upload files to hdfs://master:9000/GSPSFile/yyyy-mm-dd
                        hdfsUtil.uploadFile(GSPSFile, HDFS_ROOT_DIR + Date);

                        AttrUtil attrUploadUtil = new AttrUtil(GSPSFile);
                        attrUploadUtil.UploadToHBase(HBaseUtil, TABLE_NAME, HDFS_ROOT_DIR, Date, false);
                    } else {
                        // logger.error(FullFileName + " can not be converted to DICOM file");
                        logger.error("Invalid json file");
                    }
                }
            }
            watchKey.reset();
        }
    }

    private File json2DCM(String jsonFileName) throws IOException {
        File jsonFile = new File(jsonFileName);
        //JSON2DCM
        Json2Dcm json2Dcm = new Json2Dcm();
        json2Dcm.parse(new DicomInputStream(jsonFile));

        FileOutputStream out = new FileOutputStream("/tmp/GSPS.dcm");
        try {
            json2Dcm.writeTo(out);
        } finally {
            out.close();
            return new File("/tmp/GSPS.dcm");
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
