package Utils;

import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DCM2JPGUtil {
    private static org.apache.log4j.Logger logger = Logger.getLogger(DCM2JPGUtil.class);
    private static Attributes attrs = null;
    private static String fileName = null;
    private static File fileDCM = null;
    private static boolean preferWindow = true;
    private static boolean autoWindowing = true;
    private List<String> jpgFilePathList = new ArrayList<>();
    private List<String> jpgFileNameList = new ArrayList<>();

    /**
     * Initial convert process
     *
     * @param file
     * @throws IOException
     */
    public DCM2JPGUtil(File file) throws IOException {
        attrs = DicomParseUtil.loadDicomObject(file);
        fileDCM = file;
        fileName = file.getName();
    }

    public static void setAutoWindowing(boolean autoWindowing) {
        DCM2JPGUtil.autoWindowing = autoWindowing;
    }

    public static void setPreferWindow(boolean preferWindow) {
        DCM2JPGUtil.preferWindow = preferWindow;
    }

    public List<String> parseJPG(String filePath,
                                 String formatName,
                                 String suffix,
                                 String clazz,
                                 String compressionType,
                                 Number quality) {
        int frame = attrs.getInt(Tag.NumberOfFrames, 1);
        if (frame == 1) {
            try {
                File fileJPEG = new File(filePath + getFileNameNoDCM(fileName) + ".jpg");
                Dcm2Jpg dcm2jpg = new Dcm2Jpg();
                dcm2jpg.initImageWriter(formatName, suffix, clazz, compressionType, quality);
                dcm2jpg.convert(fileDCM, fileJPEG);
                jpgFilePathList.add(fileJPEG.getAbsolutePath());
                jpgFileNameList.add(fileJPEG.getName());
                logger.info("Convert single frame jpg File: " + fileJPEG.getName() + ".jpg successfully");
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        // For multiple frames
        else {
            for (int i = 1; i <= frame; i++) {
                try {
                    File fileJPEG = new File(filePath + getFileNameNoDCM(fileName)
                            + "." + i + ".jpg");
                    Dcm2Jpg dcm2jpg = new Dcm2Jpg();
                    dcm2jpg.setFrame(i);
                    dcm2jpg.initImageWriter(formatName, suffix, clazz, compressionType, quality);
                    dcm2jpg.convert(fileDCM, fileJPEG);
                    jpgFilePathList.add(fileJPEG.getAbsolutePath());
                    jpgFileNameList.add(fileJPEG.getName());
                    logger.info("Convert multiple frame" + i + " to jpg file successfully");
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
        }
        return jpgFilePathList;
    }

    public void UploadToHBase(HBaseUtil hBaseUtil, String Date, String HDFS_ROOT_DIR) throws IOException {
        String tableName = Date;
        String UID = attrs.getString(Tag.StudyInstanceUID, "Unknown");
        if (!jpgFileNameList.isEmpty()) {
            for (int i = 0; i < jpgFileNameList.size(); i++) {
                hBaseUtil.addRow(tableName,
                        UID,
                        AttrUtil.columnFamilies[6],
                        "JpgFilePath",
                        HDFS_ROOT_DIR + tableName + jpgFileNameList.get(i));
            }
        } else {
            logger.error("No jpg files found. Upload canceled.");
        }
    }

    /**
     * Delete .dcm to get pure filename
     *
     * @param filename
     * @return
     */
    public String getFileNameNoDCM(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
