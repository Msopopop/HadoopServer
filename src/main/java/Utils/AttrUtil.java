package Utils;

import org.apache.log4j.Logger;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AttrUtil {
    private static final String[] columnFamilies = {
            "Patient",
            "Hospital",
            "Study",
            "Physician",
            "Image",
            "File"
    };
    private static org.apache.log4j.Logger logger = Logger.getLogger(AttrUtil.class);
    private static Attributes attrs = null;
    private static String tableName = LocalDate.now().toString();

    /**
     * Initial parse-upload process
     *
     * @param file
     * @throws IOException
     */
    public AttrUtil(File file) throws IOException {
        attrs = DicomParseUtil.loadDicomObject(file);
    }

    private static void UploadHosptialColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "Modality",
                attrs.getString(Tag.Modality));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "Manufacturer",
                attrs.getString(Tag.Manufacturer));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "ManufacturerModelName",
                attrs.getString(Tag.ManufacturerModelName));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "InstitutionName",
                attrs.getString(Tag.InstitutionName));
        /*
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "InstitutionAddress",
                attrs.getString(Tag.InstitutionAddress));
                */
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[1],
                "StationName",
                attrs.getString(Tag.StationName));

    }

    //TODO NOT WRITTEN YET
    @SuppressWarnings("static-access")
    private static void UploadStudyColumn(HBaseUtil hBaseUtil, String UID) {
    }

    //TODO NOT WRITTEN YET
    @SuppressWarnings("static-access")
    private static void UploadPhysicianColumn(HBaseUtil hBaseUtil, String UID) {
    }

    //TODO NOT WRITTEN YET
    @SuppressWarnings("static-access")
    private static void UploadImageColumn(HBaseUtil hBaseUtil, String UID) {
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

    /**
     * Upload attributes informations to hbase database
     *
     * @param hBaseUtil
     * @throws IOException
     */

    public void UploadToHBase(HBaseUtil hBaseUtil) throws IOException {
        hBaseUtil.createTable(tableName, columnFamilies);
        String UID = attrs.getString(Tag.MediaStorageSOPClassUID);
        UploadPatientColumn(hBaseUtil, UID);
        UploadHosptialColumn(hBaseUtil, UID);
    }

    private void UploadPatientColumn(HBaseUtil hBaseUtil, String UID) throws IOException {
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientName",
                attrs.getString(Tag.PatientName));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientID",
                attrs.getString(Tag.PatientID));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientBirthDate",
                attrs.getString(Tag.PatientBirthDate));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientSex",
                attrs.getString(Tag.PatientSex));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientAge",
                attrs.getString(Tag.PatientAge));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "PatientWeight",
                attrs.getString(Tag.PatientWeight));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "IssuerOfPatientID",
                attrs.getString(Tag.IssuerOfPatientID));
        hBaseUtil.addRow(tableName,
                UID,
                columnFamilies[0],
                "AdditionalPatientHistory",
                attrs.getString(Tag.AdditionalPatientHistory));
    }
}
