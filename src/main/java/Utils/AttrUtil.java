package Utils;

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

    // TODO Append Grahpic Annotations Tags
    private void UploadAnnotationColumn(HBaseUtil hBaseUtil, String UID, String HDFS_ROOT_DIR, String Date) throws IOException {
        if (hBaseUtil.getRow(tableName, UID, columnFamilies[5], "Version") != null) {
            String GSPSFilePath = hBaseUtil.getRow(
                    tableName, UID, columnFamilies[5], "GSPSFilePath");
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "GSPSFilePath",
                    GSPSFilePath + "," +
                            HDFS_ROOT_DIR + Date + "/" + fileName);

            String GSPSFileName = hBaseUtil.getRow(
                    tableName, UID, columnFamilies[5], "GSPSFileName");
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "GSPSFileName",
                    GSPSFileName + "," + fileName);
            int Version = Integer.valueOf(
                    hBaseUtil.getRow(
                            tableName, UID, columnFamilies[5], "Version"));
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "Version",
                    String.valueOf(Version + 1));
        } else {
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "GSPSFilePath",
                    HDFS_ROOT_DIR + Date + "/" + fileName);
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "GSPSFileName",
                    fileName);
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[5],
                    "Version",
                    "1");
            /*
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
                    */
        }
    }

    private void UploadHBaseColumn(HBaseUtil hBaseUtil, String UID, String HDFS_ROOT_DIR, String Date) throws IOException {
        if (hBaseUtil.getRow(tableName, UID, columnFamilies[6], "Version") != null) {
            String DicomFilePath = hBaseUtil.getRow(
                    tableName, UID, columnFamilies[6], "DicomFilePath");
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "DicomFilePath",
                    DicomFilePath + "," +
                            HDFS_ROOT_DIR + Date + "/" + fileName);

            String DicomFileName = hBaseUtil.getRow(
                    tableName, UID, columnFamilies[6], "DicomFileName");
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "DicomFileName",
                    DicomFileName + "," + fileName);

            int Version = Integer.valueOf(
                    hBaseUtil.getRow(
                            tableName, UID, columnFamilies[6], "Version"));
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "Version",
                    String.valueOf(Version + 1));
        } else {
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "DicomFilePath",
                    HDFS_ROOT_DIR + Date + "/" + fileName);
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "DicomFileName",
                    fileName);
            hBaseUtil.addRow(tableName,
                    UID,
                    columnFamilies[6],
                    "Version",
                    "1");
        }
    }
    /**
     * Upload attributes informations to hbase database
     * tag:1 DicomFile
     * tag:0 GSPSFile
     * @param hBaseUtil :
     * @param TABLENAME :
     * @param HDFS_ROOT_DIR : Root DIR of HDFS Path. For example: "/dicomFile/2018-12-20"
     * @param Date : a String when dicom file creates
     * @throws IOException
     */

    public void UploadToHBase(HBaseUtil hBaseUtil, String TABLENAME, String HDFS_ROOT_DIR, String Date, boolean isDcmImgFile) throws IOException {
        tableName = TABLENAME;
        String UID = attrs.getString(Tag.StudyInstanceUID, "Unknown");
        if (isDcmImgFile) {
            hBaseUtil.createTable(tableName, columnFamilies);
            UploadPatientColumn(hBaseUtil, UID);
            UploadHospitalColumn(hBaseUtil, UID);
            UploadStudyColumn(hBaseUtil, UID);
            UploadPhysicianColumn(hBaseUtil, UID);
            UploadImageColumn(hBaseUtil, UID);
            UploadHBaseColumn(hBaseUtil, UID, HDFS_ROOT_DIR, Date);
        } else {
            UploadAnnotationColumn(hBaseUtil, UID, HDFS_ROOT_DIR, Date);
        }
    }

}
