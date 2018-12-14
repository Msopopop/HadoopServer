package mainThread;

import Utils.DicomParseUtil;
import Utils.HDFSUtil;
import Utils.HbaseUtil;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);
    private static String USER_HOME = "E:\\hadoop";
    private static String HDFS_NODE_NAME = "master.msopopop.cn";
    private static String HBASE_ZOOKEEPER_QUORUM = "slave.msopopop.cn";

    public static void main(String[] args) throws Exception {
        HDFSUtil hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
        HbaseUtil hbaseUtil = new HbaseUtil(HBASE_ZOOKEEPER_QUORUM);

        // Initial WatchService on PATH ${USER_HOME}
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Paths.get(USER_HOME).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        Thread FTPListenerThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take();
                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        //TODO 判断测试完成(通过finished后缀)
                        logger.debug("Event:" + event.kind() + " File affected: " + event.context());

                        if (event.kind() ==
                                StandardWatchEventKinds.ENTRY_CREATE
                                && event.context().toString().endsWith(".finished")) {
                            hdfsUtil.mkdir("/dicomFile/" +
                                    LocalDate.now());
                            // Upload files to HDFS hdfs://master:9000/dicomFile/yyyy-mm-dd
                            String fileName = getFileNameNoEx(event.context().toString());
                            File file = new File(USER_HOME + "\\" + fileName);
                            //hdfsUtils.uploadFile(USER_HOME+"/"+fileName, "/dicomFile/" + LocalDate.now());
                            DicomParseUtil d = new DicomParseUtil(file);

                            @SuppressWarnings("static-access")
                            Attributes attrs = d.loadDicomObject(file);
                            //输出所有属性信息
                            logger.debug("所有信息: " + attrs);
                            //获取行
                            int row = attrs.getInt(Tag.Rows, 1);
                            //获取列
                            int columns = attrs.getInt(Tag.Columns, 1);
                            //窗宽窗位
                            float win_center = attrs.getFloat(Tag.WindowCenter, 1);
                            float win_width = attrs.getFloat(Tag.WindowWidth, 1);
                            logger.debug("" + "row=" + row + ",columns=" + row + ",row*columns = " + row * columns);
                            String patientName = attrs.getString(Tag.PatientName, "");
                            logger.debug("姓名：" + patientName);
                            //生日
                            String patientBirthDate = attrs.getString(Tag.PatientBirthDate, "");
                            logger.debug("生日：" + patientBirthDate);
                            //机构
                            String institution = attrs.getString(Tag.InstitutionName, "");
                            logger.debug("机构：" + institution);
                            //站点
                            String station = attrs.getString(Tag.StationName, "");
                            logger.debug("站点：" + station);
                            //制造商
                            String Manufacturer = attrs.getString(Tag.Manufacturer, "");
                            logger.debug("制造商：" + Manufacturer);
                            //制造商模型
                            String ManufacturerModelName = attrs.getString(Tag.ManufacturerModelName, "");
                            logger.debug("制造商模型：" + ManufacturerModelName);
                            //描述--心房
                            String description = attrs.getString(Tag.StudyDescription, "");
                            logger.debug("描述--心房：" + description);
                            //描述--具体
                            String SeriesDescription = attrs.getString(Tag.SeriesDescription, "");
                            logger.debug("描述--具体：" + SeriesDescription);
                            //描述时间
                            String studyData = attrs.getString(Tag.StudyDate, "");
                            logger.debug("描述时间：" + studyData);
                            byte[] bytename = attrs.getBytes(Tag.PatientName);
                            logger.debug("姓名: " + new String(bytename, "gb18030"));
                            byte[] bytesex = attrs.getBytes(Tag.PatientSex);
                            logger.debug("性别: " + new String(bytesex, "gb18030"));
                        }
                    }
                    watchKey.reset();
                }
            } catch (IOException | InterruptedException e) {
                logger.fatal(e.toString());
            }
        });
        FTPListenerThread.setDaemon(false);
        FTPListenerThread.start();

        // Close HDFS Client
        hdfsUtil.close();
        // Close Hbase Client
        hbaseUtil.close();
        // Destory FTPListner thread before main thread exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watchService.close();
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
