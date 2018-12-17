package Utils;

import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import java.io.File;
import java.io.IOException;

public class AttrUtil {
    public static final String[] columnFamilies = {
            "Patient", //[0]
            "Hospital",  //[1]
            "Study",  //[2]
            "Physician",  //[3]
            "Image", //[4]
            "Annotation", //[5]
            "File"  //[6]
    };
    private static org.apache.log4j.Logger logger = Logger.getLogger(AttrUtil.class);
    private static Attributes attrs = null;
    private static String tableName = null;
    private static String fileName = null;
    /**
     * Initial parse-upload process
     *
     * @param file
     * @throws IOException
     */
    public AttrUtil(File file) throws IOException {
        attrs = DicomParseUtil.loadDicomObject(file);
        attrs.setSpecificCharacterSet("GBK");
        fileName = file.getName();
    }

    private void UploadPatientColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientName",
                attrs.getString(Tag.PatientName, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientID",
                attrs.getString(Tag.PatientID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientBirthDate",
                attrs.getString(Tag.PatientBirthDate, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientSex",
                attrs.getString(Tag.PatientSex, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientAge",
                attrs.getString(Tag.PatientAge, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientWeight",
                attrs.getString(Tag.PatientWeight, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "IssuerOfPatientID",
                attrs.getString(Tag.IssuerOfPatientID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "AdditionalPatientHistory",
                attrs.getString(Tag.AdditionalPatientHistory, ""));
    }

    private void UploadHospitalColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "Modality",
                attrs.getString(Tag.Modality, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "Manufacturer",
                attrs.getString(Tag.Manufacturer, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "ManufacturerModelName",
                attrs.getString(Tag.ManufacturerModelName, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "InstitutionName",
                attrs.getString(Tag.InstitutionName, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "InstitutionAddress",
                attrs.getString(Tag.InstitutionAddress, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "StationName",
                attrs.getString(Tag.StationName, ""));

    }

    private void UploadStudyColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyDate",
                attrs.getString(Tag.StudyDate, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "SeriesDate",
                attrs.getString(Tag.SeriesDate, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyTime",
                attrs.getString(Tag.StudyTime, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyDescription",
                attrs.getString(Tag.StudyDescription, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "SeriesDescription",
                attrs.getString(Tag.SeriesDescription, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyInstanceUID",
                attrs.getString(Tag.StudyInstanceUID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "SeriesInstanceUID",
                attrs.getString(Tag.SeriesInstanceUID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyID",
                attrs.getString(Tag.StudyID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[2],
                "StudyStatusID",
                attrs.getString(Tag.StudyStatusID, ""));
    }

    private void UploadPhysicianColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[3],
                "ReferringPhysiciansName",
                attrs.getString(Tag.ReferringPhysicianName, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[3],
                "NameOfPhysiciansReadingStudy",
                attrs.getString(Tag.NameOfPhysiciansReadingStudy, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[3],
                "OperatorsName",
                attrs.getString(Tag.OperatorsName, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[3],
                "PerformingPhysicianName",
                attrs.getString(Tag.PerformingPhysicianName, ""));
    }

    private void UploadImageColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "ImageType",
                attrs.getString(Tag.ImageType, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "ImageDate",
                attrs.getString(Tag.ContentDate, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "ImageTime",
                attrs.getString(Tag.ContentTime, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "ImageNumber",
                attrs.getString(Tag.InstanceNumber, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "SamplesPerPixel",
                attrs.getString(Tag.SamplesPerPixel, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "PhotometricInterpretation",
                attrs.getString(Tag.PhotometricInterpretation, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "NumberOfFrames",
                attrs.getString(Tag.NumberOfFrames, "1"));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "Rows",
                attrs.getString(Tag.Rows, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "Columns",
                attrs.getString(Tag.Columns, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "PixelSpacing",
                attrs.getString(Tag.PixelSpacing, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "BitsAllocated",
                attrs.getString(Tag.BitsAllocated, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "BitsStored",
                attrs.getString(Tag.BitsStored, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "HighBit",
                attrs.getString(Tag.HighBit, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "PixelRepresentation",
                attrs.getString(Tag.PixelRepresentation, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[4],
                "PixelPaddingValue",
                attrs.getString(Tag.PixelPaddingValue, ""));
    }

    // TODO Multiple Grahpic Annotations
    private void UploadAnnotationColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicAnnotationSequence",
                attrs.getString(Tag.GraphicAnnotationSequence, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicAnnotationSequence",
                attrs.getString(Tag.GraphicAnnotationSequence, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "Item",
                attrs.getString(Tag.Item, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "ReferencedSOPClassUID",
                attrs.getString(Tag.ReferencedSOPClassUID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "ReferencedSOPInstanceUID",
                attrs.getString(Tag.ReferencedSOPInstanceUID, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "ReferencedFrameNumber",
                attrs.getString(Tag.ReferencedFrameNumber, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "TextObjectSequence",
                attrs.getString(Tag.TextObjectSequence, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "BoundingBoxAnnotationUnits",
                attrs.getString(Tag.BoundingBoxAnnotationUnits, "PIXEL"));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "UnformattedTextValue",
                attrs.getString(Tag.UnformattedTextValue, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "BoundingBoxTopLeftHandCorner",
                attrs.getString(Tag.BoundingBoxTopLeftHandCorner, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "BoundingBoxBottomRightHandCorner",
                attrs.getString(Tag.BoundingBoxBottomRightHandCorner, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "BoundingBoxTextHorizontalJustification",
                attrs.getString(Tag.BoundingBoxTextHorizontalJustification, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicAnnotationUnits",
                attrs.getString(Tag.GraphicAnnotationUnits, "PIXEL"));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicDimensions",
                attrs.getString(Tag.GraphicDimensions, "2"));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "NumberOfGraphicPoints",
                attrs.getString(Tag.NumberOfGraphicPoints, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicType",
                attrs.getString(Tag.GraphicType, ""));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[5],
                "GraphicFilled",
                attrs.getString(Tag.GraphicFilled, ""));
    }

    private void UploadHBaseColumn(HBaseUtil hBaseUtil, String UID, String HDFS_ROOT_DIR) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[6],
                "DicomFilePath",
                HDFS_ROOT_DIR + tableName + "/" + fileName);
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[6],
                "DicomFileName",
                fileName);
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[6],
                "DicomFileCreateDate",
                tableName);
    }
    /**
     * Upload attributes informations to hbase database
     * @param hBaseUtil
     * @throws IOException
     */

    public void UploadToHBase(HBaseUtil hBaseUtil, String Date, String HDFS_ROOT_DIR) throws IOException {
        tableName = Date;
        hBaseUtil.createTable(tableName, columnFamilies);
        String UID = attrs.getString(Tag.StudyInstanceUID, "Unknown");
        UploadPatientColumn(hBaseUtil, UID);
        UploadHospitalColumn(hBaseUtil, UID);
        UploadStudyColumn(hBaseUtil, UID);
        UploadPhysicianColumn(hBaseUtil, UID);
        UploadImageColumn(hBaseUtil, UID);
        UploadAnnotationColumn(hBaseUtil, UID);
        UploadHBaseColumn(hBaseUtil, UID, HDFS_ROOT_DIR);
    }

    /**
     * JUST FOR LOGGER
     *
     * @throws Exception
     */
    @Deprecated
    public void printAllInfo() throws Exception {
        logger.info(attrs);
        //获取行
        int row = attrs.getInt(Tag.Rows, 1);
        //获取列
        int columns = attrs.getInt(Tag.Columns, 1);
        //窗宽窗位
        float win_center = attrs.getFloat(Tag.WindowCenter, 1);
        attrs.getString(Tag.ImageType);
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
