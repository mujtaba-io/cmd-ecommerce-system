

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba >>> Kooler than ever
 ~ Date: December 24, 2022

 Implements the database layout for this ecommerce system.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -

public class DBModel {
    public static MujtabaDB DB;
    public static void init(){
        Table[] tables = {
            // User-table
            new Table("SELLER", new TableLayout(
                new TableAttribute[]{
                    new TableAttribute("EMAIL", Type.STRING, TableAttribute.Usage.PRIMARY_KEY),
                    new TableAttribute("PASSWORD", Type.STRING, TableAttribute.Usage.STORAGE),
                    new TableAttribute("NAME", Type.STRING, TableAttribute.Usage.STORAGE),
                    //new TableAttribute("IS_SELLER", Type.BOOLEAN, TableAttribute.Usage.STORAGE),
                    new TableAttribute("RATING", Type.DOUBLE, TableAttribute.Usage.STORAGE),
                    new TableAttribute("BIO", Type.STRING, TableAttribute.Usage.STORAGE),
                })
            ),
            // Review-table
            new Table("REVIEW", new TableLayout(
                new TableAttribute[]{
                    new TableAttribute("ID", Type.STRING, TableAttribute.Usage.PRIMARY_KEY),
                    new TableAttribute("TO_SELLER_EMAIL", Type.STRING, TableAttribute.Usage.FOREIGN_KEY),
                    new TableAttribute("BY_USER", Type.STRING, TableAttribute.Usage.STORAGE),
                    new TableAttribute("RATING", Type.DOUBLE, TableAttribute.Usage.STORAGE),
                    new TableAttribute("TEXT", Type.STRING, TableAttribute.Usage.STORAGE),
                })
            ),
            // Product-table
            new Table("PRODUCT", new TableLayout(
                new TableAttribute[]{
                    new TableAttribute("ID", Type.INTEGER, TableAttribute.Usage.PRIMARY_KEY),
                    new TableAttribute("SELLER_EMAIL", Type.STRING, TableAttribute.Usage.FOREIGN_KEY),
                    new TableAttribute("NAME", Type.STRING, TableAttribute.Usage.STORAGE),
                    new TableAttribute("PRICE", Type.DOUBLE, TableAttribute.Usage.STORAGE),
                    new TableAttribute("DESCRIPTION", Type.STRING, TableAttribute.Usage.STORAGE),
                })
            ),
            // InvoiceItem-table
            new Table("INVOICE_ITEM", new TableLayout(
                new TableAttribute[]{
                    new TableAttribute("ID", Type.INTEGER, TableAttribute.Usage.PRIMARY_KEY),
                    new TableAttribute("INVOICE_ID", Type.INTEGER, TableAttribute.Usage.FOREIGN_KEY), // Associated invoice ID
                    new TableAttribute("PRODUCT_ID", Type.INTEGER, TableAttribute.Usage.FOREIGN_KEY),
                    new TableAttribute("QUANTITY", Type.DOUBLE, TableAttribute.Usage.STORAGE),
                })
            ),
            // Invoice-table
            new Table("INVOICE", new TableLayout(
                new TableAttribute[]{
                    new TableAttribute("ID", Type.INTEGER, TableAttribute.Usage.PRIMARY_KEY),
                    new TableAttribute("SELLER_EMAIL", Type.STRING, TableAttribute.Usage.FOREIGN_KEY), // SELLER
                    new TableAttribute("DATE", Type.STRING, TableAttribute.Usage.STORAGE),
                })
            ),
        };
        DB = new MujtabaDB("DB.db", tables);
    }
}

