package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseUtil {
    private static Logger logger = Logger.getLogger(HBaseUtil.class);

    private static Configuration conf = null;
    private static Admin admin;
    private static Connection connection;
    private static Table table;
    /**
     * Initial Hbase client
     *
     * @param ZookeeperNodeName
     * @throws IOException
     */
    public HBaseUtil(String ZookeeperNodeName) throws IOException {
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", ZookeeperNodeName);
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        admin = ConnectionFactory.createConnection(conf).getAdmin();
        connection = ConnectionFactory.createConnection(conf);
    }

    /**
     * Test table if exists
     *
     * @param tableName
     * @return boolean
     * @throws IOException
     */
    private boolean isExist(String tableName) throws IOException {
        return admin.tableExists(TableName.valueOf(tableName));
    }

    /**
     * Create a table
     *
     * @param tableName
     * @param columnFamilies
     * @return boolean
     */
    public boolean createTable(String tableName, String[] columnFamilies) throws IOException {
        if (isExist(tableName)) {
            logger.info("Table: " + tableName + " already exists");
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
    public boolean deleteTable(String tableName) throws IOException {
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
    public boolean addRow(String tableName, String row,
                          String columnFamily,
                          String column, String value) throws IOException {
        // Set-up connection first(NEW API)
        if (isExist(tableName)) {
            table = connection.getTable(TableName.valueOf(tableName));
            try {
                // Add data(NEW API)
                Put put = new Put(Bytes.toBytes(row));
                table.put(put.addColumn(
                        Bytes.toBytes(columnFamily),
                        Bytes.toBytes(column),
                        Bytes.toBytes(value)
                ));
            } finally {
                logger.info("Create Row:" + row + "|" + columnFamily + "|" + column + "|" + value + " successfully");
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
    public boolean deleteRow(String tableName, String row) throws IOException {
        // Set-up connection first(NEW API)
        if (isExist(tableName)) {
            table = connection.getTable(TableName.valueOf(tableName));
            // Delete data(NEW API)
            table.delete(new Delete(Bytes.toBytes(row)));
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
    public boolean deleteRows(String tableName, String[] rows) throws IOException {
        // Set-up connection first(NEW API)
        if (isExist(tableName)) {
            table = connection.getTable(TableName.valueOf(tableName));
                List<Delete> deleteList = new ArrayList<Delete>();
                for (String row : rows) {
                    Delete delete = new Delete(Bytes.toBytes(row));
                    deleteList.add(delete);
                }
                // Delete data(NEW API)
                table.delete(deleteList);
            return true;
        } else {
            logger.error("Table not exists. Delete row data failed");
            return false;
        }
    }

    /**
     * Get all records
     * @param tableName
     * @return
     * @throws IOException
     */
    public List<String> getAllRows(String tableName) throws IOException {
        // Set-up connection first(NEW API)
        if (isExist(tableName)) {
            List<String> listString = new ArrayList<>();
            table = connection.getTable(TableName.valueOf(tableName));
                Scan scan = new Scan();
                ResultScanner results = table.getScanner(scan);
                for (Result result : results) {
                    // Each row
                    for (Cell rowKV : result.rawCells()) {
                        // Row name
                        String rowName = CellUtil.cloneRow(rowKV).toString();
                        listString.add(rowName);
                        // Column family
                        String familyName = String.valueOf(CellUtil.cloneFamily(rowKV));
                        listString.add(familyName);
                        // Column name
                        listString.add(CellUtil.cloneQualifier(rowKV).toString());
                        // Value
                        listString.add(CellUtil.cloneValue(rowKV).toString());
                        // Time stamp
                        listString.add(String.valueOf(rowKV.getTimestamp()));
                    }
                }
                results.close();
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
        connection.close();
        admin.close();
        table.close();
    }
}
