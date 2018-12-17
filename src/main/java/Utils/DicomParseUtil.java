package Utils;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;

import java.io.File;
import java.io.IOException;

public class DicomParseUtil {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DicomParseUtil.class);
    private static Attributes obj = null;

    private DicomParseUtil(File file) {
        try {
            setObject(loadDicomObject(file));
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    /**
     * Read metadata of Dicom 3.0
     *
     * @param f : input file
     * @return Attributes
     * @throws IOException
     */
    public static Attributes loadDicomObject(File f) throws IOException {
        if (f == null) {
            return null;
        } else {
            DicomInputStream dis = new DicomInputStream(f);
            return dis.readDataset(-1, -1);
        }
    }

    /**
     * Put attribute
     *
     * @param obJ : DicomObject
     */
    @SuppressWarnings("static-access")
    public static void setObject(Attributes obJ) {
        obj = obJ;
    }

}
