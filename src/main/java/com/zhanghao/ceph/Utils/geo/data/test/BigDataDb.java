package com.zhanghao.ceph.Utils.geo.data.test;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BigDataDb {

    private static final String sql_select = "select * from tradi  ";

    /**
     * 27个参数
     */
    private static final String sql_insert = "insert into tradi values( ?,?,?,?,?,?);";

    /**
     * 元数据文件名
     */
    private static final String dbFileName = "D:\\tmpdata\\metedata\\bigdata.tradi";

    /**
     * 从数据库中获取BLOB数据
     *
     * @return
     */
    public static List<BigData> fetchBigData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<BigData> bigDatas = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            if (!(new File(dbFileName).exists())) {
                return null;
            }
            String dbUrl = "jdbc:sqlite:" + dbFileName;
            conn = DriverManager.getConnection(dbUrl);
            ps = conn.prepareStatement(sql_select);
            rs = ps.executeQuery();
            while (rs.next()) {
                BigData bigData = new BigData();
                bigData.setFileName(rs.getString(1));
                bigData.setMd5(rs.getString(2));
                bigData.setPath(rs.getString(3));
                bigData.setPath2(rs.getString(4));
                bigData.setPath3(rs.getString(5));
                bigData.setBuffer(rs.getBytes(6));
                bigDatas.add(bigData);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        } finally {
            BigDataDb.closeDB(conn, rs, ps);
            return bigDatas;
        }
    }


    /**
     * 单个BLOB数据入库
     *
     * @param bigData
     * @throws Exception
     */
    public static Boolean storeBigData(BigData bigData) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connInsert = createConnection(dbFileName);
            PreparedStatement ps = connInsert.prepareStatement(sql_insert);
            ps.setString(1, bigData.getFileName());
            ps.setString(2, bigData.getMd5());
            ps.setString(3, bigData.getPath());
            ps.setString(4, bigData.getPath2());
            ps.setString(5, bigData.getPath3());
            ps.setBytes(6, bigData.getBuffer());
            ps.execute();
            connInsert.close();
            return true;
        } catch (Exception ex) {
            System.out.println("sqlite入库失败。" + ex.toString());
            return false;
        }
    }


    /**
     * BLOB数据批量入库
     *
     * @param bigDatas
     * @throws Exception
     */
    public static Boolean storeBigData(List<BigData> bigDatas) {
        Boolean success = false;
        try {
            if (bigDatas != null && bigDatas.size() > 0) {
                int size = bigDatas.size();
                Class.forName("org.sqlite.JDBC");

                Connection connInsert = createConnection(dbFileName);
                connInsert.setAutoCommit(false);
                for (int i = 0; i < size; i++) {
                    PreparedStatement ps = connInsert.prepareStatement(sql_insert);
                    ps.setString(1, bigDatas.get(i).getFileName());
                    ps.setString(2, bigDatas.get(i).getMd5());
                    ps.setString(3, bigDatas.get(i).getPath());
                    ps.setString(4, bigDatas.get(i).getPath2());
                    ps.setString(5, bigDatas.get(i).getPath3());
                    ps.setBytes(6, bigDatas.get(i).getBuffer());
                    ps.execute();
                    if (i % 500 == 0) {
                        connInsert.commit();
                    }
                }
                connInsert.commit();
                connInsert.close();
                success = true;
            }
        } catch (Exception ex) {
            System.out.println("sqlite入库失败。" + ex.toString());
            success = false;
        } finally {
            return success;
        }
    }


    synchronized private static Connection createConnection(String dbFileName) throws Exception {
        Connection connInsert = null;
        String dbUrl = "jdbc:sqlite:" + dbFileName;
        if (!(new File(dbFileName).exists()) || (new File(dbFileName).length() == 0)) {
            connInsert = DriverManager.getConnection(dbUrl);
            Statement statInsert = connInsert.createStatement();
            statInsert.executeUpdate(BigData.transToCreateSql());
            statInsert.close();
        } else {
            connInsert = DriverManager.getConnection(dbUrl);
        }
        return connInsert;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     * @param rs
     * @param ps
     */
    private static void closeDB(Connection conn, ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
