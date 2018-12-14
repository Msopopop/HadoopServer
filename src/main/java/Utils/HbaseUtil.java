package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HbaseUtil {
    private static Logger logger = Logger.getLogger(HbaseUtil.class);
    //Path: hdfs://master:9000/dicomFile/a.dcm

    private static Configuration conf = null;
    private static Admin admin;

    /**
     * Initial Hbase client
     *
     * @param ZookeeperNodeName
     * @throws IOException
     */
    public HbaseUtil(String ZookeeperNodeName) throws IOException {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", ZookeeperNodeName);
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        admin = ConnectionFactory.createConnection(conf).getAdmin();
    }

    /**
     * Test table if exists
     *
     * @param tableName
     * @return boolean
     * @throws IOException
     */
    public static boolean isExist(String tableName) throws IOException {
        return admin.tableExists(TableName.valueOf(tableName));
    }

    /**
     * Create a table
     *
     * @param tableName
     * @param columnFamilies
     * @return boolean
     */
    public static boolean createTable(String tableName, String[] columnFamilies) throws IOException {
        if (isExist(tableName)) {
            logger.error("Table: " + tableName + " already exists");
            return false;
        } else {
            // Add a description for a scores table(NEW API)
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            for (String columnFamily : columnFamilies)
                tableDescriptor.addFamily(new HColumnDescriptor(columnFamily));
            // Create a table
            admin.createTable(tableDescriptor);
            logger.info("Create table " + tableName + " successfully");
            return true;
        }
    }

    /**
     * Delete a table
     *
     * @param tableName
     * @throws IOException
     */
    public static boolean deleteTable(String tableName) throws IOException {
        if (isExist(tableName)) {
            // Close the table first
            admin.disableTable(TableName.valueOf(tableName));
            // Delete it then
            admin.deleteTable(TableName.valueOf(tableName));
            logger.info("Delete table " + tableName + " successfully");
            return true;
        } else {
            logger.error("Table not exists. Delete failed");
            return false;
        }
    }

    /**
     * Add data for a single row
     *
     * @param tableName
     * @param row
     * @param columnFamily
     * @param column
     * @param value
     * @return
     * @throws IOException
     */
    public static boolean addRow(String tableName, String row,
                                 String columnFamily,
                                 String column, String value) throws IOException {
        // Set-up connection first(NEW API)
        Connection connection = ConnectionFactory.createConnection(conf);
        if (isExist(tableName)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
                // Add data(NEW API)
                table.put(new Put(Bytes.toBytes(row)).addColumn(
                        Bytes.toBytes(columnFamily),
                        Bytes.toBytes(column),
                        Bytes.toBytes(value)
                ));
            } finally {
                table.close();
                connection.close();
            }
            return true;
        } else {
            logger.error("Table not exists. Add row data failed");
            return false;
        }
    }

    /**
     * Delete single row data
     *
     * @param tableName
     * @param row
     * @return
     * @throws IOException
     */
    public static boolean deleteRow(String tableName, String row) throws IOException {
        // Set-up connection first(NEW API)
        Connection connection = ConnectionFactory.createConnection(conf);
        if (isExist(tableName)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
                // Delete data(NEW API)
                table.delete(new Delete(Bytes.toBytes(row)));
            } finally {
                table.close();
                connection.close();
            }
            return true;
        } else {
            logger.error("Table not exists. Delete row data failed");
            return false;
        }
    }

    /**
     * Delete multiple rows data
     *
     * @param tableName
     * @param rows
     * @return boolean
     * @throws IOException
     */
    public static boolean deleteRows(String tableName, String[] rows) throws IOException {
        // Set-up connection first(NEW API)
        Connection connection = ConnectionFactory.createConnection(conf);
        if (isExist(tableName)) {
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
                List<Delete> deleteList = new ArrayList<Delete>();
                for (String row : rows) {
                    Delete delete = new Delete(Bytes.toBytes(row));
                    deleteList.add(delete);
                }
                // Delete data(NEW API)
                table.delete(deleteList);
            } finally {
                table.close();
                connection.close();
            }
            return true;
        } else {
            logger.error("Table not exists. Delete row data failed");
            return false;
        }
    }

    /**
     * Get row data
     *
     * @param tableName
     * @param row
     * @throws IOException
     */
    public static List<String> getRow(String tableName, String row) throws IOException {
        // Set-up connection first(NEW API)
        Connection connection = ConnectionFactory.createConnection(conf);
        if (isExist(tableName)) {
            List<String> listString = new ArrayList<>();
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
                Get get = new Get(Bytes.toBytes(row));
                Result result = table.get(get);
                // Results
                for (Cell rowKV : result.rawCells()) {
                    // Row name
                    listString.add(CellUtil.cloneRow(rowKV).toString());
                    // Column family
                    listString.add(CellUtil.cloneFamily(rowKV).toString());
                    // Column name
                    listString.add(CellUtil.cloneQualifier(rowKV).toString());
                    // Value
                    listString.add(CellUtil.cloneValue(rowKV).toString());
                    // Time stamp
                    listString.add(String.valueOf(rowKV.getTimestamp()));
                }
            } finally {
                table.close();
                connection.close();
            }
            return listString;
        } else {
            logger.error("Table not exists. Get row data failed");
            return null;
        }
    }

    /**
     * Get all records
     * @param tableName
     * @return
     * @throws IOException
     */
    public static List<String> getAllRows(String tableName) throws IOException {
        // Set-up connection first(NEW API)
        Connection connection = ConnectionFactory.createConnection(conf);
        if (isExist(tableName)) {
            List<String> listString = new ArrayList<>();
            Table table = connection.getTable(TableName.valueOf(tableName));
            try {
                Scan scan = new Scan();
                ResultScanner results = table.getScanner(scan);
                for (Result result : results) {
                    // Each row
                    for (Cell rowKV : result.rawCells()) {
                        // Row name
                        listString.add(CellUtil.cloneRow(rowKV).toString());
                        // Column family
                        listString.add(CellUtil.cloneFamily(rowKV).toString());
                        // Column name
                        listString.add(CellUtil.cloneQualifier(rowKV).toString());
                        // Value
                        listString.add(CellUtil.cloneValue(rowKV).toString());
                        // Time stamp
                        listString.add(String.valueOf(rowKV.getTimestamp()));
                    }
                }
                results.close();
            } finally {

                table.close();
                connection.close();
            }
            return listString;
        } else {
            logger.error("Table not exists. Get all data failed");
            return null;
        }
    }

    /**
     * Close Hbase client
     *
     * @throws IOException
     */
    public void close() throws IOException {
        admin.close();
    }
}
