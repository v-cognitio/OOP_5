package Service;

import DAO.Dataline;
import DAO.IQueryable;
import DAO.SqlTable;
import javafx.util.Pair;

import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.crypto.Data;
import java.util.List;

public class SqlService {

    //private String url;
    //private String user;
    //private String password;
    private IQueryable products;
    private IQueryable storehouses;
    private IQueryable shops;

    public SqlService(String url, String user, String password) {
        //this.url = url;
        //this.user = user;
        //this.password = password;

        try {
            products    = new SqlTable("products", url, user, password);
            storehouses = new SqlTable("storehouses", url, user, password);
            shops       = new SqlTable("shops", url,user, password);
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
        List<Dataline> productsResponse = selectFrom(products, new Dataline()
                .addField("name", productName));

        List<Dataline> shopResponse     = selectFrom(shops, new Dataline()
                .addField("name", shopName));

        String  shop_id    = shopResponse.get(0).getValue(0);
        String  product_id = productsResponse.get(0).getValue(0);


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
        List<Dataline> productsResponse = selectFrom(products, new Dataline()
                .addField("name", productName));

        String product_id = productsResponse.get(0).getValue(0);

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

        List<Dataline> shopResponse = selectFrom(shops, new Dataline()
                .addField("id", minShop_id));

        return shopResponse.get(0).getValue(1);
    }

    //set: product_name - count
    public Integer buyProductSet(String shopName, Dataline set) {
        List<Dataline> shopResponse     = selectFrom(shops, new Dataline()
                .addField("name", shopName));

        String shop_id = shopResponse.get(0).getValue(0);

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

    private List<Dataline> selectFrom(IQueryable table, Dataline query) {
        List<Dataline> response = table.select(query);
        /*if (response.isEmpty()) {
            throw new IllegalArgumentException();
        }*/

        return response;
    }
}
