package DAO;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlTable extends Table {

    private Connection con;

    public SqlTable(String name, String url, String user, String password) throws SQLException {
        super(name);
        con = DriverManager.getConnection(url, user, password);
    }

    @Override
    public void insert(Dataline selector) {
        StringBuffer request = new StringBuffer();
        request.append("INSERT INTO `").append(this.name).append("`(");
        for (Pair<String, String> el : selector) {
            request.append("`").append(el.getKey()).append("`");
            if (selector.iterator().hasNext()) {
                request.append(",");
            }
        }
        request.delete(request.length() - 1, request.length());

        request.append(") VALUES (");
        for (Pair<String, String> el : selector) {
            request.append('"').append(el.getValue()).append('"');
            request.append(",");
        }
        request.delete(request.length() - 1, request.length());
        request.append(")");

        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(request.toString());

            stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Dataline selector) {
        StringBuffer request = new StringBuffer();
        request.append("UPDATE `").append(this.name).append("` SET ");
        for (Pair<String, String> el : selector) {
            request.append("`").append(el.getKey()).append("`=").append(el.getValue());
            request.append(",");
        }
        request.delete(request.length() - 1, request.length());

        request.append(" WHERE 1");

        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(request.toString());

            stmt.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Dataline> select(Dataline selector) {
        try {
            List<Dataline> data = select("SELECT * FROM `" + name + "`");
            List<Dataline> res = new ArrayList<>();
            for (Dataline line : data) {
                if (IQueryable.checkSelect(line, selector)) {
                    res.add(line);
                }
            }

            return res;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Dataline> getFullTable() {
        try {
            return select("SELECT * FROM `" + name + "` WHERE 1");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Dataline> select(String sqlQuery) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet res = stmt.executeQuery(sqlQuery);
        int columns = res.getMetaData().getColumnCount();
        List<Dataline> ans = new ArrayList<>();
        while (res.next()) {
            Dataline line = new Dataline();
            for (int i = 1; i <= columns; ++i) {
                line.addField(res.getMetaData().getColumnName(i), res.getString(i));
            }
            ans.add(line);
        }

        stmt.close();
        res.close();
        return ans;
    }

    @Override
    public void close() throws Exception {

    }
}
