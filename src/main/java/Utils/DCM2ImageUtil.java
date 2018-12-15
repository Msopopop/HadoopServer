package Utils;

import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DCM2ImageUtil {
    private static org.apache.log4j.Logger logger = Logger.getLogger(DCM2ImageUtil.class);
    private static Attributes attrs = null;
    private static String fileName = null;
    private static File fileDCM = null;
    private static boolean preferWindow = true;
    private static boolean autoWindowing = true;
    private List<String> ImageFilePathList = new ArrayList<>();
    private List<String> ImageFileNameList = new ArrayList<>();

    /**
     * Initial convert process
     *
     * @param file
     * @throws IOException
     */
    public DCM2ImageUtil(File file) throws IOException {
        attrs = DicomParseUtil.loadDicomObject(file);
        fileDCM = file;
        fileName = file.getName();
    }

    public static void setAutoWindowing(boolean autoWindowing) {
        DCM2ImageUtil.autoWindowing = autoWindowing;
    }

    public static void setPreferWindow(boolean preferWindow) {
        DCM2ImageUtil.preferWindow = preferWindow;
    }

    public List<String> parseImage(String filePath,
                                   String formatName,
                                   String suffix,
                                   String clazz,
                                   String compressionType,
                                   Number quality) {
        int frame = attrs.getInt(Tag.NumberOfFrames, 1);
        if (frame == 1) {
            try {
                File fileImage = new File(filePath + getFileNameNoDCM(fileName) + suffix);
                Dcm2Jpg dcm2jpg = new Dcm2Jpg();
                dcm2jpg.initImageWriter(formatName, suffix, clazz, compressionType, quality);
                dcm2jpg.convert(fileDCM, fileImage);
                ImageFilePathList.add(fileImage.getAbsolutePath());
                ImageFileNameList.add(fileImage.getName());
                logger.info("Convert single frame image File: " + fileImage.getName() + " successfully");
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        // For multiple frames
        else {
            for (int i = 1; i <= frame; i++) {
                try {
                    File fileJPEG = new File(filePath + getFileNameNoDCM(fileName)
                            + "." + i + suffix);
                    Dcm2Jpg dcm2jpg = new Dcm2Jpg();
                    dcm2jpg.setFrame(i);
                    dcm2jpg.initImageWriter(formatName, suffix, clazz, compressionType, quality);
                    dcm2jpg.convert(fileDCM, fileJPEG);
                    ImageFilePathList.add(fileJPEG.getAbsolutePath());
                    ImageFileNameList.add(fileJPEG.getName());
                    logger.info("Convert multiple frame" + i + " to " + suffix + " file successfully");
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            }
        }
        return ImageFilePathList;
    }

    public void UploadToHBase(HBaseUtil hBaseUtil, String Date, String HDFS_ROOT_DIR) throws IOException {
        String tableName = Date;
        String UID = attrs.getString(Tag.StudyInstanceUID, "Unknown");
        String ImageType = ImageFileNameList.get(0).substring(ImageFileNameList.get(0).length() - 3);
        hBaseUtil.addRow(tableName,
                UID,
                AttrUtil.columnFamilies[6],
                "ImageFileType",
                ImageType
        );
        if (!ImageFileNameList.isEmpty()) {
            for (int i = 0; i < ImageFileNameList.size(); i++) {
                hBaseUtil.addRow(tableName,
                        UID,
                        AttrUtil.columnFamilies[6],
                        "ImageFilePath." + (i + 1),
                        HDFS_ROOT_DIR + tableName + "/" + ImageFileNameList.get(i));
            }
        } else {
            logger.error("No image files found. Upload canceled.");
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
