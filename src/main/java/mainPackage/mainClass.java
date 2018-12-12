package mainPackage;

import FileUtils.DicomParseUtil;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import java.io.File;

public class mainClass {
    public static void main(String[] args) throws Exception {
        //ftpServer ftpServer = new ftpServer(21);
        //ftpServer.run();

        File file = new File("test.dcm");
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
        System.out.println("" + "row=" + row + ",columns=" + row + ",row*columns = " + row * columns);
        String patientName = attrs.getString(Tag.PatientName, "");
        System.out.println("姓名：" + patientName);
        //生日
        String patientBirthDate = attrs.getString(Tag.PatientBirthDate, "");
        System.out.println("生日：" + patientBirthDate);
        //机构
        String institution = attrs.getString(Tag.InstitutionName, "");
        System.out.println("机构：" + institution);
        //站点
        String station = attrs.getString(Tag.StationName, "");
        System.out.println("站点：" + station);
        //制造商
        String Manufacturer = attrs.getString(Tag.Manufacturer, "");
        System.out.println("制造商：" + Manufacturer);
        //制造商模型
        String ManufacturerModelName = attrs.getString(Tag.ManufacturerModelName, "");
        System.out.println("制造商模型：" + ManufacturerModelName);
        //描述--心房
        String description = attrs.getString(Tag.StudyDescription, "");
        System.out.println("描述--心房：" + description);
        //描述--具体
        String SeriesDescription = attrs.getString(Tag.SeriesDescription, "");
        System.out.println("描述--具体：" + SeriesDescription);
        //描述时间
        String studyData = attrs.getString(Tag.StudyDate, "");
        System.out.println("描述时间：" + studyData);
        byte[] bytename = attrs.getBytes(Tag.PatientName);
        System.out.println("姓名: " + new String(bytename, "gb18030"));
        byte[] bytesex = attrs.getBytes(Tag.PatientSex);
        System.out.println("性别: " + new String(bytesex, "gb18030"));
        //ftpServer.stop();
    }

}
