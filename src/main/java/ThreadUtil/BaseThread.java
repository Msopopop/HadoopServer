package ThreadUtil;

import Utils.HBaseUtil;
import Utils.HDFSUtil;

import java.io.IOException;

public class BaseThread {
    protected static String TABLE_NAME;
    protected static String HDFS_NODE_NAME;
    protected static String HBASE_ZOOKEEPER_QUORUM;
    protected static HDFSUtil hdfsUtil;
    protected static HBaseUtil HBaseUtil;

    public static void setTableName(String tableName) {
        TABLE_NAME = tableName;
    }

    public static void setHdfsNodeName(String hdfsNodeName) throws IOException {
        HDFS_NODE_NAME = hdfsNodeName;
        hdfsUtil = new HDFSUtil(HDFS_NODE_NAME);
    }

    public static void setHbaseZookeeperQuorum(String hbaseZookeeperQuorum) throws IOException {
        HBASE_ZOOKEEPER_QUORUM = hbaseZookeeperQuorum;
        HBaseUtil = new HBaseUtil(HBASE_ZOOKEEPER_QUORUM);
    }
}
