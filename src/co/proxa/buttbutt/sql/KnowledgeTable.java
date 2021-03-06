package co.proxa.buttbutt.sql;


import co.proxa.buttbutt.Buttbutt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KnowledgeTable {

    private Buttbutt butt;

    public KnowledgeTable(Buttbutt butt) {
        this.butt = butt;
    }

    public void insertKnowledge(String item, String data, String grabber) throws SQLException {
        if (butt.getSqlManager().isConnected()) {
            java.util.Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(date);
            String update = "INSERT INTO `" + butt.getSqlManager().getTablePrefix() + "_knowledge` (item,data,added_by,timestamp) VALUES(?,?,?,?)";
            PreparedStatement ps = butt.getSqlManager().getConnection().prepareStatement(update);
            ps.setString(1, item);
            ps.setString(2, data);
            ps.setString(3, grabber);
            ps.setString(4, timestamp);
            ps.executeUpdate();
        }  else {
            butt.getSqlManager().reconnect();
            queryKnowledge(item);
        }
    }

    public String queryKnowledge(String item) {
        if (butt.getSqlManager().isConnected()) {
            String query = "SELECT * FROM `" + butt.getSqlManager().getTablePrefix() + "_knowledge` WHERE item=?";
            PreparedStatement ps = butt.getSqlManager().getPreparedStatement(query);
            Object[] objects = { item };
            butt.getSqlManager().prepareStatement(ps, objects);
            ResultSet rs = butt.getSqlManager().getResultSet(ps);
            if (rs != null) {
                try {
                    if (rs.next()) {
                        return rs.getString("data");
                    }
                } catch (SQLException ex) {
                    System.out.println("sql exception :(");
                }
            }
        } else {
            butt.getSqlManager().reconnect();
            queryKnowledge(item);
        }
        return null;
    }

    public boolean deleteKnowledge(String item) {
        if (butt.getSqlManager().isConnected()) {
            System.out.println(item);
            String update = "DELETE FROM `" + butt.getSqlManager().getTablePrefix() + "_knowledge` WHERE item=?";
            PreparedStatement ps = butt.getSqlManager().getPreparedStatement(update);
            try {
                ps.setString(1, item.trim());
                int rows = ps.executeUpdate();
                return (rows > 0); // if no rows have been updated then we haven't actually deleted anything
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getRandomData() {
        if (butt.getSqlManager().isConnected()) {
            String query = "SELECT * FROM `" + butt.getSqlManager().getTablePrefix() + "_knowledge` ORDER BY RAND() LIMIT 1";
            PreparedStatement ps = butt.getSqlManager().getPreparedStatement(query);
            ResultSet rs = butt.getSqlManager().getResultSet(ps);
            try {
                if (rs.next()) {
                    return rs.getString("data");
                }
            } catch (SQLException ex) {
                butt.getLogger().severe("SQL Exception has occurred.  StackTrace:");
                ex.printStackTrace();
            }
        } else {
            butt.getSqlManager().reconnect();
            getRandomData();
        }
        return null;
    }

}
