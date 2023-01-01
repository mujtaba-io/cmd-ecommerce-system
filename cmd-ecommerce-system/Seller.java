// pee
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 18, 2022

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 !BUG REPORTS:
 - AT METHOD:delete():
   The array:users resizes to existingSize+32 when its element reach
   existingSize, but in delete() method, I have written the code that
   the next element takes the space of now-deleted element and so on.
   Now if the element deleted at index=arraySize-1, accessing eleement
   at existingSize would invoke IndexOutOfBoundsException.

 - AT METHOD:load():
   The array:data is initialoized bsed on the fields we foun in a file
   rather than the schema of database that we explicitly defined in class.
   So if we alter the file and make less enteries externally, the
   IndexOutOfBoundsException occurs. So we must initialize the array
   beforehand and use it later.

 - null

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -

// name, email, password, seller,rating, bio

// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
// < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < <
// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
// < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < < <
// > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

// !NOTE:
// OnStart: Program will call User.load() to lead all necessary data from file. --> load()
// OnUse: All data manipulation will occur with array:users --> doStuff()
// OnClose: Program will call User.save() to save the current state of array:users
//  in a file again to be read back next time OnStart. --> save()

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

