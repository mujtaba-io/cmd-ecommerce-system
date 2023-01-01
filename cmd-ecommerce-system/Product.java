// tea
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 20, 2022

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


public class Product {
    public static KoolDB DB = DBModel.DB;
    int ID;
    public Seller seller;
    public String name;
    public double price; // $200/-
    public String description;

    // Constructor
    public Product(int ID, String sellerEmail, String name, double price, String description){
        this.ID = ID;
        this.seller = Seller.fromEmail(sellerEmail);
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public static Product[] allOf(String email){
        Table productTable = DB.getTable("PRODUCT");
        Data[][] productsData = productTable.filter("SELLER_EMAIL", new Data(email), Table.FilterType.MATCH);
        Product[] products = new Product[productsData.length];
        for(int i = 0; i < products.length; i++){
            Product product = new Product(
                productsData[i][productTable.tableLayout.getAttributeIndex("ID")].getInteger(),
                productsData[i][productTable.tableLayout.getAttributeIndex("SELLER_EMAIL")].getString(),
                productsData[i][productTable.tableLayout.getAttributeIndex("NAME")].getString(),
                productsData[i][productTable.tableLayout.getAttributeIndex("PRICE")].getDouble(),
                productsData[i][productTable.tableLayout.getAttributeIndex("DESCRIPTION")].getString()
            );
            products[i] = product;
        }
        return products;
    }

    public static Product fromID(int productID){
        Table productTable = DB.getTable("PRODUCT");
        Data[][] products = productTable.all();
        for (Data[] product : products){
            if(product[productTable.tableLayout.getAttributeIndex("ID")].getInteger() == productID){
                return new Product(
                        productID,
                        product[productTable.tableLayout.getAttributeIndex("SELLER_EMAIL")].getString(),
                        product[productTable.tableLayout.getAttributeIndex("NAME")].getString(),
                        product[productTable.tableLayout.getAttributeIndex("PRICE")].getDouble(),
                        product[productTable.tableLayout.getAttributeIndex("DESCRIPTION")].getString()
                );
            }
        }
        return null;
    }

}

// - END

