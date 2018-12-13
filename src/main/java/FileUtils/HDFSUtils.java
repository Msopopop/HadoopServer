package FileUtils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.IOException;

public class HDFSUtils {
    private static Logger logger = Logger.getLogger(HDFSUtils.class);
    //Path: hdfs://master:9000/dicomFile/a.dcm

    /**
     * Create a file
     *
     * @param HDFSNodeName
     * @param path
     * @param contents
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private static void createFile(String HDFSNodeName, String path, byte[] contents) throws IOException {
        // TODO Auto-generated method stub
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem hdfs = FileSystem.get(conf);
        Path newFilePath = new Path(path);
        //Open a output stream
        FSDataOutputStream outputStream = hdfs.create(newFilePath);
        outputStream.write(contents);
        //Close
        outputStream.close();
        hdfs.close();
        logger.info("Create success");
    }

    /**
     * Create a new dir in HDFS System
     *
     * @param HDFSNodeName, path
     * @throws IOException
     */
    public void mkdir(String HDFSNodeName, String path) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem hdfs = FileSystem.get(conf);
        Path dirPath = new Path(path);

        if (hdfs.mkdirs(dirPath))
            logger.info("Create DIR:" + path + " successful");
        else
            logger.error("Create DIR:" + path + " failure");
        hdfs.close();
    }

    /**
     * Upload files to HDFS from local path
     *
     * @param src
     * @param dst
     * @param HDFSNodeName
     * @throws IOException
     */
    public void uploadFile(String src, String dst, String HDFSNodeName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem hdfs = FileSystem.get(conf);

        Path srcPath = new Path(src);
        Path dstPath = new Path(dst);
        hdfs.copyFromLocalFile(false, srcPath, dstPath);
        logger.info("Upload to " + conf.get("fs.default.name"));
        logger.info("------------list files------------" + "\n");
        FileStatus[] fileStatus = hdfs.listStatus(dstPath);
        for (FileStatus file : fileStatus) {
            logger.info(file.getPath());
        }

        hdfs.close();
    }

    public void renameFile(String oldName, String newName, String HDFSNodeName) throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://" + HDFSNodeName + ":9000/");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        FileSystem hdfs = FileSystem.get(conf);

        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        if (hdfs.rename(oldPath, newPath))
            logger.info("Rename successful");
        else
            logger.error("Rename failed");
        hdfs.close();
    }
}
