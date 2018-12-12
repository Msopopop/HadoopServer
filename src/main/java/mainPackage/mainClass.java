package mainPackage;

import FileUtils.DicomParseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import java.io.File;
import java.nio.file.*;
import java.util.List;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://{IP}:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem hdfs = FileSystem.get(conf);

        WatchService watchService =
                FileSystems.getDefault().newWatchService();
        Path path = Paths.get(System.getProperty("user.home") + "/dicomFile");
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
        );

        Thread FTPGetthread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey watchKey = watchService.take();
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        //TODO 判断测试完成(通过finished后缀)
                        logger.debug("Event:" + event.kind() + " File affected: " + event.context());
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                                && event.context().toString().endsWith(".finished")) {
                            String fileName = getFileNameNoEx(event.context().toString());

                            File file = new File(fileName);
                            DicomParseUtil d = new DicomParseUtil(file);
                            @SuppressWarnings("static-access")
                            Attributes attrs = d.loadDicomObject(file);
                            //输出所有属性信息
                            System.out.println("所有信息: " + attrs);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        FTPGetthread.setDaemon(false);
        FTPGetthread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watchService.close();
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }));
    }

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
