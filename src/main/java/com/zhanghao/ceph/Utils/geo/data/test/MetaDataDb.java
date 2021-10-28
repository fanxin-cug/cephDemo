package com.zhanghao.ceph.Utils.geo.data.test;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDataDb {

    private static final String sql_select = "select * from tradi  ";

    /**
     * 27个参数
     */
    private static final String sql_insert = "insert into tradi values( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    /**
     * 元数据文件名
     */
    private static final String dbFileName = "D:\\tmpdata\\metedata\\metedata.tradi";

    /**
     * 从数据库中获取元数据
     *
     * @return
     */
    public static List<MetaData> fetchMetaData() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<MetaData> metaDatas = new ArrayList<>();
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
                MetaData metaData = new MetaData();
                metaData.setFileName(rs.getString(1));
                metaData.setMd5(rs.getString(2));
                metaData.setPath(rs.getString(3));
                metaData.setUlLon(rs.getDouble(4));
                metaData.setUlLat(rs.getDouble(5));
                metaData.setDlLon(rs.getDouble(6));
                metaData.setDlLat(rs.getDouble(7));
                metaData.setDrLon(rs.getDouble(8));
                metaData.setDrLat(rs.getDouble(9));
                metaData.setUrLon(rs.getDouble(10));
                metaData.setUrLat(rs.getDouble(11));
                metaData.setResolution(rs.getDouble(12));
                metaData.setWidth(rs.getInt(13));
                metaData.setHeight(rs.getInt(14));
                metaData.setBandCount(rs.getInt(15));
                metaData.setMaxLevel(rs.getInt(16));
                metaData.setProjectionType(rs.getString(17));
                metaData.setProWKT(rs.getString(18));
                metaData.setGeoTrans1(rs.getDouble(19));
                metaData.setGeoTrans2(rs.getDouble(20));
                metaData.setGeoTrans3(rs.getDouble(21));
                metaData.setGeoTrans4(rs.getDouble(22));
                metaData.setGeoTrans5(rs.getDouble(23));
                metaData.setGeoTrans6(rs.getDouble(24));
                metaData.setGeoHashCode1(rs.getString(25));
                metaData.setPath2(rs.getString(26));
                metaData.setPath3(rs.getString(27));
                metaDatas.add(metaData);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        } finally {
            MetaDataDb.closeDB(conn, rs, ps);
            return metaDatas;
        }
    }


    /**
     * 单个元数据入库
     *
     * @param metaData
     * @throws Exception
     */
    public static Boolean storeMetaData(MetaData metaData) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connInsert = createConnection(dbFileName);
            PreparedStatement ps = connInsert.prepareStatement(sql_insert);
            ps.setString(1, metaData.getFileName());
            ps.setString(2, metaData.getMd5());
            ps.setString(3, metaData.getPath());
            ps.setDouble(4, metaData.getUlLon());
            ps.setDouble(5, metaData.getUlLat());
            ps.setDouble(6, metaData.getDlLon());
            ps.setDouble(7, metaData.getDlLat());
            ps.setDouble(8, metaData.getDrLon());
            ps.setDouble(9, metaData.getDrLat());
            ps.setDouble(10, metaData.getUrLon());
            ps.setDouble(11, metaData.getUrLat());
            ps.setDouble(12, metaData.getResolution());
            ps.setInt(13, metaData.getWidth());
            ps.setInt(14, metaData.getHeight());
            ps.setInt(15, metaData.getBandCount());
            ps.setInt(16, metaData.getMaxLevel());
            ps.setString(17, metaData.getProjectionType());
            ps.setString(18, metaData.getProWKT());
            ps.setDouble(19, metaData.getGeoTrans1());
            ps.setDouble(20, metaData.getGeoTrans2());
            ps.setDouble(21, metaData.getGeoTrans3());
            ps.setDouble(22, metaData.getGeoTrans4());
            ps.setDouble(23, metaData.getGeoTrans5());
            ps.setDouble(24, metaData.getGeoTrans6());
            ps.setString(25, metaData.getGeoHashCode1());
            ps.setString(26, metaData.getPath2());
            ps.setString(27, metaData.getPath3());
            ps.execute();
            connInsert.close();
            return true;
        } catch (Exception ex) {
            System.out.println("sqlite入库失败。" + ex.toString());
            return false;
        }
    }


    /**
     * 元数据批量入库
     *
     * @param metaDatas
     * @throws Exception
     */
    public static Boolean storeMetaData(List<MetaData> metaDatas) {
        Boolean success = false;
        try {
            if (metaDatas != null && metaDatas.size() > 0) {
                int size = metaDatas.size();
                Class.forName("org.sqlite.JDBC");

                Connection connInsert = createConnection(dbFileName);
                connInsert.setAutoCommit(false);
                for (int i = 0; i < size; i++) {
                    PreparedStatement ps = connInsert.prepareStatement(sql_insert);
                    ps.setString(1, metaDatas.get(i).getFileName());
                    ps.setString(2, metaDatas.get(i).getMd5());
                    ps.setString(3, metaDatas.get(i).getPath());
                    ps.setDouble(4, metaDatas.get(i).getUlLon());
                    ps.setDouble(5, metaDatas.get(i).getUlLat());
                    ps.setDouble(6, metaDatas.get(i).getDlLon());
                    ps.setDouble(7, metaDatas.get(i).getDlLat());
                    ps.setDouble(8, metaDatas.get(i).getDrLon());
                    ps.setDouble(9, metaDatas.get(i).getDrLat());
                    ps.setDouble(10, metaDatas.get(i).getUrLon());
                    ps.setDouble(11, metaDatas.get(i).getUrLat());
                    ps.setDouble(12, metaDatas.get(i).getResolution());
                    ps.setInt(13, metaDatas.get(i).getWidth());
                    ps.setInt(14, metaDatas.get(i).getHeight());
                    ps.setInt(15, metaDatas.get(i).getBandCount());
                    ps.setInt(16, metaDatas.get(i).getMaxLevel());
                    ps.setString(17, metaDatas.get(i).getProjectionType());
                    ps.setString(18, metaDatas.get(i).getProWKT());
                    ps.setDouble(19, metaDatas.get(i).getGeoTrans1());
                    ps.setDouble(20, metaDatas.get(i).getGeoTrans2());
                    ps.setDouble(21, metaDatas.get(i).getGeoTrans3());
                    ps.setDouble(22, metaDatas.get(i).getGeoTrans4());
                    ps.setDouble(23, metaDatas.get(i).getGeoTrans5());
                    ps.setDouble(24, metaDatas.get(i).getGeoTrans6());
                    ps.setString(25, metaDatas.get(i).getGeoHashCode1());
                    ps.setString(26, metaDatas.get(i).getPath2());
                    ps.setString(27, metaDatas.get(i).getPath3());
                    ps.execute();
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
            statInsert.executeUpdate(MetaData.transToCreateSql());
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
