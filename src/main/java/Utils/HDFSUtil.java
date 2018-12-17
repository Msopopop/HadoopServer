package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class HDFSUtil {
    private static Logger logger = Logger.getLogger(HDFSUtil.class);
    //Path: hdfs://master:9000/dicomFile/yyyy-mm-dd/file.dcm

    private static Configuration conf = new Configuration();
    private static FileSystem hdfs = null;
    private static String NodeName = null;

    public HDFSUtil(String HDFSNodeName) throws IOException {
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        hdfs = FileSystem.get(conf);
        NodeName = HDFSNodeName;
    }

    /**
     * Create a file
     *
     * @param path
     * @param contents
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private static void createFile(String path, byte[] contents) throws IOException {
        Path newFilePath = new Path(path);
        //Open a output stream
        FSDataOutputStream outputStream = hdfs.create(newFilePath);
        outputStream.write(contents);
        //Close
        outputStream.close();
        logger.info("Create contents success");
    }

    public void close() throws IOException {
        hdfs.close();
    }

    /**
     * Create a new dir in HDFS System
     *
     * @param path
     * @throws IOException
     */
    public void mkdir(String path) throws IOException {
        Path dirPath = new Path(path);
        if (hdfs.mkdirs(dirPath))
            logger.info("Create DIR:" + path + " successful");
        else
            logger.error("Create DIR:" + path + " failure");
    }

    /**
     * Upload files to HDFS from local path
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public void uploadFile(String src, String dst) throws IOException {
        dst = "hdfs://" + NodeName + ":9000" + dst;
        Path srcPath = new Path(src);
        Path dstPath = new Path(dst);
        hdfs.copyFromLocalFile(true, true, srcPath, dstPath);
        logger.info("File " + src + " upload successfully");
    }

    /**
     * Upload files to HDFS from existed file
     *
     * @param file
     * @param dst
     * @throws IOException
     */
    public void uploadFile(File file, String dst) throws IOException {
        dst = "hdfs://" + NodeName + ":9000" + dst;
        Path srcPath = new Path(file.getAbsolutePath());
        Path dstPath = new Path(dst);
        hdfs.copyFromLocalFile(true, true, srcPath, dstPath);
        logger.info("File " + file.getAbsolutePath() + " upload successfully");
    }

    public void renameFile(String oldName, String newName) throws IOException {
        oldName = "hdfs://" + NodeName + ":9000" + oldName;
        newName = "hdfs://" + NodeName + ":9000" + newName;
        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        if (hdfs.rename(oldPath, newPath))
            logger.info("Rename successful");
        else
            logger.error("Rename failed");
    }
}
