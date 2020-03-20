package org.rick.client;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import org.rick.auth.KuduKerberosAuth;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class KuduClientDemo {
    /**
     * 使用Kudu API创建一个Kudu表
     *
     * @param client
     * @param tableName
     */
    public static void createTable(KuduClient client, String tableName) {
        List<ColumnSchema> columns = new ArrayList<>();
        //在添加列时可以指定每一列的压缩格式
        columns.add(new ColumnSchema.ColumnSchemaBuilder("id", Type.STRING).key(true).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("sex", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("city", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("occupation", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("tel", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("fixPhoneNum", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("bankName", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("address", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("marriage", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        columns.add(new ColumnSchema.ColumnSchemaBuilder("childNum", Type.STRING).
                compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        Schema schema = new Schema(columns);
        CreateTableOptions createTableOptions = new CreateTableOptions();
        List<String> hashKeys = new ArrayList<>();
        hashKeys.add("id");
        int numBuckets = 8;
        createTableOptions.addHashPartitions(hashKeys, numBuckets);
        try {
            if (!client.tableExists(tableName)) {
                client.createTable(tableName, schema, createTableOptions);
            }
            System.out.println("成功创建Kudu表：" + tableName);
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向指定的Kudu表中upsert数据，数据存在则更新，不存在则新增
     *
     * @param client    KuduClient对象
     * @param tableName 表名
     * @param numRows   向表中插入的数据量
     */
    public static void upsert(KuduClient client, String tableName, int numRows) {
        try {
            KuduTable kuduTable = client.openTable(tableName);
            KuduSession kuduSession = client.newSession();
            //设置Kudu提交数据方式，这里设置的为手动刷新，默认为自动提交
            kuduSession.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
            for (int i = 0; i < numRows; i++) {
                String userInfo_str = "测试数据";
                Upsert upsert = kuduTable.newUpsert();
                PartialRow row = upsert.getRow();
                String[] userInfo = userInfo_str.split(",");
                if (userInfo.length == 11) {
                    row.addString("id", userInfo[0]);
                    row.addString("name", userInfo[1]);
                    row.addString("sex", userInfo[2]);
                    row.addString("city", userInfo[3]);
                    row.addString("occupation", userInfo[4]);
                    row.addString("tel", userInfo[5]);
                    row.addString("fixPhoneNum", userInfo[6]);
                    row.addString("bankName", userInfo[7]);
                    row.addString("address", userInfo[8]);
                    row.addString("marriage", userInfo[9]);
                    row.addString("childNum", userInfo[10]);
                }
                kuduSession.apply(upsert);
            }
            kuduSession.flush();
            kuduSession.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看Kudu表中数据
     *
     * @param client
     * @param tableName
     */
    public static void scanerTable(KuduClient client, String tableName) {
        try {
            KuduTable kuduTable = client.openTable(tableName);
            KuduScanner kuduScanner = client.newScannerBuilder(kuduTable).build();
            while (kuduScanner.hasMoreRows()) {
                RowResultIterator rowResultIterator = kuduScanner.nextRows();
                while (rowResultIterator.hasNext()) {
                    RowResult rowResult = rowResultIterator.next();
                    System.out.println(rowResult.getString("id"));
                }
            }
            kuduScanner.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     *
     * @param client
     * @param tableName
     */
    public static void dropTable(KuduClient client, String tableName) {
        try {
            client.deleteTable(tableName);
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出Kudu下所有的表
     *
     * @param client
     */
    public static void tableList(KuduClient client) {
        try {
            ListTablesResponse listTablesResponse = client.getTablesList();
            List<String> tblist = listTablesResponse.getTablesList();
            for (String tableName : tblist) {
                System.out.println(tableName);
            }
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    public static void schemaList(KuduClient client){
        try {
            ListTablesResponse listTablesResponse = client.getTablesList();
            List<String> tblist = listTablesResponse.getTablesList();
            for (String tableName : tblist) {
                System.out.println(tableName);
            }
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String addresses;
        Properties prop = new Properties();
        try {
            String basePath = "config/kudu.conf";

            InputStream in = new BufferedInputStream(new FileInputStream(
                    new File(basePath)));

            prop.load(in);

            addresses = prop.getProperty("master_addresses");

        } catch (FileNotFoundException e) {
            System.out.println("properties文件路径书写有误，请检查！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String kudu_master = prop.getProperty("master_addresses");
        System.out.println(kudu_master);

        KuduKerberosAuth.initKerberosENV("dl", "config/dl.keytab", "config/krb5.conf", false);
        KuduClient client = KuduKerberosAuth.getKuduClient(kudu_master);

        tableList(client);

        int i=0;

        while (true){
            try {
                KuduTable table = client.openTable("impala::default.test_kudu2");
                KuduScanner kuduScanner = client.newScannerBuilder(table).build();
                while (kuduScanner.hasMoreRows()) {
                    RowResultIterator rowResultIterator = kuduScanner.nextRows();
                    while (rowResultIterator.hasNext()) {
                        RowResult rowResult = rowResultIterator.next();
                        System.out.println(rowResult.getInt("id"));
                    }
                }
                System.out.println(new Date());
                System.out.println("-----------" + ++i + "-----------");
                Thread.sleep(2000);

            } catch (
                    IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }



//        String tableName = "user_info";
//        //删除Kudu的表
//        dropTable(client, tableName);
//        //创建一个Kudu的表
//        createTable(client, tableName);
//        //列出Kudu下所有的表
//        tableList(client);
//        //向Kudu指定的表中插入数据
//        upsert(client, tableName, 100);
//        //扫描Kudu表中数据
//        scanerTable(client, tableName);
//        try {
//            client.close();
//        } catch (KuduException e) {
//            e.printStackTrace();
//        }
    }
}
