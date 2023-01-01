// pee
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 18, 2022

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 !BUG REPORTS:
  - null

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -

// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
// < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < <
// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
// < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < <
// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

public class Seller {
    public static KoolDB DB = DBModel.DB;
    // Seller-specific variables
    public String email, password, name;
    public double rating = 0.0;
    public String bio;

    // Constructor
    public Seller(String email, String password, String name, double rating, String bio){
        this.email = email;
        this.password = password;
        this.name = name;
        this.rating = rating;
        this.bio = bio;
    }

    public static Seller[] all(){
        Table sellerTable = DB.getTable("SELLER");
        Data[][] sellersData = sellerTable.all();
        Seller[] sellers = new Seller[sellersData.length];
        for(int i = 0; i < sellers.length; i++){
            Seller seller = new Seller(
                    sellersData[i][sellerTable.tableLayout.getAttributeIndex("EMAIL")].getString(),
                    sellersData[i][sellerTable.tableLayout.getAttributeIndex("PASSWORD")].getString(),
                    sellersData[i][sellerTable.tableLayout.getAttributeIndex("NAME")].getString(),
                    sellersData[i][sellerTable.tableLayout.getAttributeIndex("RATING")].getDouble(),
                    sellersData[i][sellerTable.tableLayout.getAttributeIndex("BIO")].getString()
            );
            sellers[i] = seller;
        }
        return sellers;
    }

    public static Seller fromEmail(String sellerEmail){
        Table sellerTable = DB.getTable("SELLER");
        Data[] sellerInfo = sellerTable.getRowByPK(new Data(sellerEmail));
        if(sellerInfo == null) return null;
        return new Seller(
                sellerInfo[sellerTable.tableLayout.getAttributeIndex("EMAIL")].getString(),
                sellerInfo[sellerTable.tableLayout.getAttributeIndex("PASSWORD")].getString(),
                sellerInfo[sellerTable.tableLayout.getAttributeIndex("NAME")].getString(),
                sellerInfo[sellerTable.tableLayout.getAttributeIndex("RATING")].getDouble(),
                sellerInfo[sellerTable.tableLayout.getAttributeIndex("BIO")].getString()
        );
    }

    public static void signup(String email, String password, String name, String bio){
        Data[] sellerInfo = {
                new Data(email), new Data(password),
                new Data(name), new Data(0.0),
                new Data(bio),
        };
        DB.getTable("SELLER").insert(sellerInfo);
    }

    public String allProductsStringified(){
        Product[] products = Product.allOf(this.email);
        String str = "";
        for(Product product: products){
            str += String.format("""
                    
                    %-16d %-16s %-16.1f %-16s
                    """, product.ID, product.name, product.price,product.description);
        }
        return str;
    }

}


// - END

