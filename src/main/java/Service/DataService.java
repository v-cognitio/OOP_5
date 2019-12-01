package Service;

import DAO.Dataline;
import DAO.IQueryable;
import DAO.SqlTable;
import DAO.Table;
import javafx.util.Pair;

import java.util.List;

public class DataService implements AutoCloseable {

    private Table products;
    private Table storehouses;
    private Table shops;

    public DataService(String url, String user, String password) {
        try {
            this.products    = new SqlTable("products", url, user, password);
            this.storehouses = new SqlTable("storehouses", url, user, password);
            this.shops       = new SqlTable("shops", url,user, password);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataService(String shopsPath, String productsPath) {
        try {
            CsvAdapter adapter = new CsvAdapter(shopsPath, productsPath);

            this.shops       = adapter.getShopsTable();
            this.products    = adapter.getProductsTable();
            this.storehouses = adapter.getStorehousesTable();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createShop(String name, String street) {
        shops.insert(new Dataline()
                .addField("name",    name)
                .addField("address", street));
    }

    public void createProduct(String name) {
        products.insert(new Dataline()
                .addField("name", name));
    }

    public void doDelivery(String shopName, String productName, Integer cost, Integer count) {
        String  shop_id    = getShopId(shopName);
        String  product_id = getProductId(productName);


        List<Dataline> storehouseResponse = selectFrom(storehouses, new Dataline()
                .addField("shop_id",    shop_id)
                .addField("product_id", product_id));

        if (!storehouseResponse.isEmpty()) {
            int newCount = Integer.parseInt(storehouseResponse.get(0).getValue(3)) + count;
            Dataline updateQuery = new Dataline()
                    .addField("shop_id",    shop_id)
                    .addField("product_id", product_id)
                    .addField("cost",       cost.toString())
                    .addField("count",      Integer.toString(newCount));
            storehouses.update(updateQuery);
        }
        else {
            Dataline updateQuery = new Dataline()
                    .addField("shop_id",    shop_id)
                    .addField("product_id", product_id)
                    .addField("cost",       cost.toString())
                    .addField("count",      count.toString());
            storehouses.insert(updateQuery);
        }
    }

    public String bestPrice(String productName) {
        String product_id = getProductId(productName);

        List<Dataline> storehousesResponse = selectFrom(storehouses, new Dataline()
                .addField("product_id", product_id));

        int    minCost    = Integer.MAX_VALUE;
        String minShop_id = "-1";
        for (Dataline line : storehousesResponse) {
            int currentCost = Integer.parseInt(line.getValue(2));
            if (currentCost < minCost) {
                minCost    = currentCost;
                minShop_id = line.getValue(0);
            }
        }

        return getShopName(minShop_id);
    }

    //set: product_name - count
    public Integer buyProductSet(String shopName, Dataline set) {
        String shop_id = getShopId(shopName);

        int totalCost = 0;
        for (Pair<String, String> line : set) {
            List<Dataline> productsResponse = selectFrom(products, new Dataline()
                    .addField("name", line.getKey()));
            String currentProduct_id = productsResponse.get(0).getValue(0);
            List<Dataline> storehouseResponce = selectFrom(storehouses, new Dataline()
                    .addField("shop_id",    shop_id)
                    .addField("product_id", currentProduct_id));
            int currentCount  = Integer.parseInt(storehouseResponce.get(0).getValue(3));
            int expectedCount = Integer.parseInt(line.getValue());
            if (currentCount < expectedCount) {
                return null;
            }
            int currentCost = Integer.parseInt(storehouseResponce.get(0).getValue(2));
            totalCost += currentCost * expectedCount;
        }

        return totalCost;
    }

    public String bestSetPrice(Dataline set) {
        List<Dataline> shopsTable   = shops.getFullTable();
        int            minCost      = Integer.MAX_VALUE;
        String         minShop_name = null;
        for (Dataline line : shopsTable) {
            String currentShop_name = line.getValue(1);
            try {
                int currentCost = buyProductSet(currentShop_name, set);

                if (currentCost < minCost) {
                    minCost = currentCost;
                    minShop_name = currentShop_name;
                }
            }
            catch (Exception e) {
                //
            }
        }

        return minShop_name;
    }

    public Dataline buyWithLimit(String shopName, int limit) {
        Dataline res = new Dataline();

        String shop_id = getShopId(shopName);

        List<Dataline> storehouseResponse = selectFrom(storehouses, new Dataline()
                .addField("shop_id", shop_id));

        for (Dataline line : storehouseResponse) {
            Integer currentPossibleCount = Math.min(
                    limit / Integer.parseInt(line.getValue(2)),
                    Integer.parseInt(line.getValue(3)));
            res.addField(getProductName(line.getValue(1)), currentPossibleCount.toString());
        }

        return res;
    }

    private List<Dataline> selectFrom(IQueryable table, Dataline query) {
        List<Dataline> response = table.select(query);
        /*if (response.isEmpty()) {
            throw new IllegalArgumentException();
        }*/

        return response;
    }

    private String getProductName(String id) {
        List<Dataline> response = selectFrom(products, new Dataline()
                .addField("id", id));
        return response.get(0).getValue(1);
    }

    private String getProductId(String name) {
        List<Dataline> productsResponse = selectFrom(products, new Dataline()
                .addField("name", name));
        return productsResponse.get(0).getValue(0);
    }

    private String getShopName(String id) {
        List<Dataline> shopResponse = selectFrom(shops, new Dataline()
                .addField("id", id));
        return shopResponse.get(0).getValue(1);
    }

    private String getShopId(String name) {
        List<Dataline> shopResponse = selectFrom(shops, new Dataline()
                .addField("name", name));
        return shopResponse.get(0).getValue(0);
    }

    @Override
    public void close() throws Exception {
        shops.close();
        products.close();
        storehouses.close();
    }
}
