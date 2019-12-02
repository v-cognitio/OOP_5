import DAO.Dataline;
import Service.DataService;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String connectionURL = "jdbc:mysql://stoorx.beget.tech:3306/stoorx_vcognitio";

        Properties properties = new Properties();
        try (InputStream is = new FileInputStream("resources/config.properties")) {
            properties.load(is);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String databaseType = properties.getProperty("databaseType");

        DataService service = null;
        if (databaseType.equals("csv")) {
            try {
                service = new DataService("resources/shops.csv",
                        "resources/products.csv");
                System.out.println("connected with csv");
            }
            catch (Exception e) {
                service.close();
                e.printStackTrace();
                return;
            }
        }
        else if (databaseType.equals("sql")) {
            try {
                service = new DataService(connectionURL,
                        "stoorx_vcognitio", "123456787654321");
                System.out.println("connected with sql");
            }
            catch (Exception e) {
                service.close();
                e.printStackTrace();
                return;
            }
        }
        else {
            System.out.println("unknown database type");
            return;
        }

        //*** actions ***//


        Dataline line = service.buyWithLimit("Auchan", 200);
        for (Pair<String, String> pair : line) {
            System.out.println(pair.getKey() + " - " + pair.getValue());
        }

        service.close();

    }
}
