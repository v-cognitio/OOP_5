import DAO.*;
import Service.SqlService;

import java.sql.*;

public class Application {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String connectionURL = "jdbc:mysql://stoorx.beget.tech:3306/stoorx_vcognitio";

        SqlService service = new SqlService(connectionURL,
                "stoorx_vcognitio", "123456787654321");
        Dataline line = new Dataline()
                .addField("Mango", "80");
        System.out.println(service.bestSetPrice(line));


/*
        try  {
            Connection con = DriverManager.getConnection(connectionURL,
                    "stoorx_vcognitio", "123456787654321");
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM `shops`");
            ResultSetMetaData rm = res.getMetaData();


            while (res.next()) {
                System.out.println(res.getString(3));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
}
