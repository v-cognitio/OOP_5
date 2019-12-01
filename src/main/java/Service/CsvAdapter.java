package Service;

import DAO.CsvTable;
import DAO.IQueryable;
import DAO.Table;
import javafx.util.Pair;

import java.io.*;
import java.util.Random;

public class CsvAdapter implements AutoCloseable {

    private String shopsPath;
    private String productsPath;
    private String storehousesPath;

    private Table shopsTable;
    private Table productsTable;
    private Table storehousesTable;

    public CsvAdapter(String shopsPath, String productsPath) throws IOException {
        this.shopsPath = convertShops(shopsPath);
        Pair<String, String> names = convertProducts(productsPath);
        this.productsPath    = names.getKey();
        this.storehousesPath = names.getValue();

        this.shopsTable       = new CsvTable(this.shopsPath, true);
        this.productsTable    = new CsvTable(this.productsPath, true);
        this.storehousesTable = new CsvTable(this.storehousesPath, true);
    }

    public Table getShopsTable() {
        return shopsTable;
    }

    public Table getProductsTable() {
        return productsTable;
    }

    public Table getStorehousesTable() {
        return storehousesTable;
    }

    private String convertShops(String shopsPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(shopsPath))) {

            int rnd = new Random().nextInt();
            String originalName = "resources/shops" + rnd + ".csv";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(originalName))) {
                writer.write("id\n");
                writer.write("id,name");
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.newLine();
                    writer.write(line);
                }

                return originalName;
            }
        }
    }

    //productsName - storehousesName
    private Pair<String, String> convertProducts(String productsPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(productsPath))) {
            int rnd = new Random().nextInt();
            String originalProductsName = "resources/products" + rnd + ".csv";
            String originalStorehousesName = "resources/storehouses" + rnd + ".csv";

            try (BufferedWriter productsWriter    = new BufferedWriter(new FileWriter(originalProductsName));
                 BufferedWriter storehousesWriter = new BufferedWriter(new FileWriter(originalStorehousesName))) {
                productsWriter.write("id\n");
                productsWriter.write("id,name");

                storehousesWriter.write("shop_id,product_id\n");
                storehousesWriter.write("shop_id,product_id,cost,count");

                String line;
                int product_id = 1;
                while ((line = reader.readLine()) != null) {
                    productsWriter.newLine();
                    String[] parsed = line.split(",");
                    productsWriter.write("" + product_id + "," + parsed[0]);
                    for (int i = 1; i < parsed.length; i += 3) {
                        storehousesWriter.newLine();
                        storehousesWriter.write(parsed[i] + "," +
                                product_id + "," + parsed[i + 1] + "," + parsed[i + 2]);
                    }
                    ++product_id;
                }

                return new Pair<>(originalProductsName, originalStorehousesName);
            }
        }
    }

    @Override
    public void close() throws Exception {
        shopsTable.close();
        productsTable.close();
        storehousesTable.close();
    }
}
