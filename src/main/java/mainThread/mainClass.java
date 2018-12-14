package mainThread;

import Utils.AttrUtil;
import Utils.HBaseUtil;
import Utils.HDFSUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.log4j.Logger;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Iterator;

public class mainClass {
    private static Logger logger = Logger.getLogger(mainClass.class);
    private static String FTP_ROOT_DIR = "E:\\hadoop";
    private static String HDFS_NODE_NAME = "master.msopopop.cn";
    private static String HBASE_ZOOKEEPER_QUORUM = "slave.msopopop.cn";

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
                                && event.context().toString().endsWith(".finished")) {
                            // Create a new dir classified by date
                            // hdfs://${HDFS_NODE_NAME}:9000/dicomFile/yyyy-mm-dd
                            hdfsUtil.mkdir("/dicomFile/" + LocalDate.now());

                            //TODO upload failed (slave datanode unaccessable)
                            // Upload files to HDFS hdfs://master:9000/dicomFile/yyyy-mm-dd
                            String fileName = getFileNameNoEx(event.context().toString());
                            //hdfsUtil.uploadFile(FTP_ROOT_DIR+"\\"+ fileName, "/dicomFile/" + LocalDate.now());
                            // dcm2jpg
                            DICOM2JPG(fileName);
                            //hdfsUtil.uploadFile(FTP_ROOT_DIR+"\\"+ getFileNameNoDCM(fileName) + ".jpg", "/dicomFile/" + LocalDate.now());
                            // Parse dcm file and put data to HBase
                            AttrUtil attrUploadUtil = new AttrUtil(new File(FTP_ROOT_DIR + "\\" + fileName));
                            attrUploadUtil.UploadToHBase(HBaseUtil);

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

    // process file name to delete ".dcm"
    private static String getFileNameNoDCM(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    // Conver DICOM to JPG File
    // TODO Multiple-frame DCM Convert
    private static void DICOM2JPG(String fileName) {
        File dcmFile = new File(FTP_ROOT_DIR + "\\" + fileName);
        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName("DICOM");
        BufferedImage jpegImage = null;
        while (iterator.hasNext()) {
            // Read the dcm file
            ImageReader imageReader = iterator.next();
            DicomImageReadParam dicomImageReadParam = (DicomImageReadParam) imageReader.getDefaultReadParam();
            try {
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(dcmFile);
                imageReader.setInput(imageInputStream, false);
                jpegImage = imageReader.read(0, dicomImageReadParam);
                imageInputStream.close();
                if (jpegImage == null) {
                    logger.error("Can't read image file");
                }
            } catch (IOException e) {
                logger.error(e.toString());
            }
            // Save the jpg file
            File jpgFile = new File(FTP_ROOT_DIR + "\\" + getFileNameNoDCM(fileName) + ".jpg");
            try {
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(jpgFile));
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
                encoder.encode(jpegImage);
                outputStream.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
            logger.info("Convert complete: " + fileName);
        }
    }
}
