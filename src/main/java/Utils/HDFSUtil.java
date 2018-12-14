package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;

public class HDFSUtil {
    private static Logger logger = Logger.getLogger(HDFSUtil.class);
    //Path: hdfs://master:9000/dicomFile/a.dcm

    private static Configuration conf = null;
    private static FileSystem hdfs = null;

    public HDFSUtil(String HDFSNodeName) throws IOException {
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        hdfs = FileSystem.get(conf);
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
        logger.info("Create success");
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
        Path srcPath = new Path(src);
        Path dstPath = new Path(dst);
        hdfs.copyFromLocalFile(false, srcPath, dstPath);
        logger.info("Upload to " + conf.get("fs.default.name"));
        logger.info("------------list files------------" + "\n");
        FileStatus[] fileStatus = hdfs.listStatus(dstPath);
        for (FileStatus file : fileStatus) {
            logger.info(file.getPath());
        }
    }

    public void renameFile(String oldName, String newName) throws IOException {
        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        if (hdfs.rename(oldPath, newPath))
            logger.info("Rename successful");
        else
            logger.error("Rename failed");
    }
}
