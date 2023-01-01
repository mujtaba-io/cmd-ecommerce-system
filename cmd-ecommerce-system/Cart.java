// cup

/*
* Author: Mujtaba
* */

import java.io.FileWriter;
import java.time.LocalDateTime;

class CartItem {
    int productID;
    int quantity;
    public CartItem(int product, int quantity){
        this.productID = product;
        this.quantity = quantity;
    }
}

public class Cart {
    public static KoolDB DB = DBModel.DB;
    CartItem[] items = new CartItem[8];
    int items_filledSize = 0;

    int invoiceID;

    public boolean has(int productID){
        for(int i = 0; i < items_filledSize; i++){
            if(items[i].productID == productID) return true;
        }
        return false;
    }

    public void addItem(int productID, int quantity){
        // if already exists in cart, then just update quantity
        if(has(productID)) for(int i = 0; i < items_filledSize; i++) if(items[i].productID == productID){ items[i].quantity = quantity; return ; }
        // else create new entry in cart
        if(items_filledSize < items.length){
            items[items_filledSize++] = new CartItem(productID, quantity);
        } else {
            CartItem[] tmp = new CartItem[items_filledSize + 8];
            for(int i = 0; i < items_filledSize; i++){ tmp[i] = items[i]; } items = tmp;
            items[items_filledSize++] = new CartItem(productID, quantity);
        }
    }

    public void removeItem(int productID){
        boolean removed = false;
        for(int i = 0; i < items_filledSize; i++){
            if(items[i].productID == productID) {
                items[i] = null; removed = true; items_filledSize--;
            }
            if(removed){
                items[i] = items[i+1];
            }
        }
    }

    public void buyCartItems(){
        if(items == null){ System.out.println("Cart is empty"); return; }
        Table invoiceTable = DB.getTable("INVOICE");
        Table invoiceItemTable = DB.getTable("INVOICE_ITEM");
        invoiceID = items.length + (int)(Math.random() * 10) + LocalDateTime.now().getNano();
        for(int i = 0; i < items_filledSize; i++){
            invoiceItemTable.insert(new Data[]{
                    new Data((items[i].hashCode() + invoiceID)), new Data(invoiceID),
                    new Data(items[i].productID), new Data(items[i].quantity)
            });
        }
        invoiceTable.insert(
                new Data[]{
                        new Data(invoiceID),
                        new Data(Product.fromID(items[0].productID).seller.email),
                        new Data(LocalDateTime.now().toString()),
                }
        );

        generateInvoice();
        Review.askReviewForSeller(Product.fromID(items[0].productID).seller);
    }

    public void generateInvoice(){
        double totalCost = 0.0;
        String bigString = String.format("""
                Invoice ID # %d
                Sold by seller: %s (%s)
                Date: %s
                Items:
                %-16s  %-16s  %-16s
                """, invoiceID, Product.fromID(items[0].productID).seller.name,
                Product.fromID(items[0].productID).seller.email, LocalDateTime.now().toString(),
                "Product ID", "Product Name", "Quantity");
        for(int i = 0; i < items_filledSize; i++){
            totalCost += items[i].quantity * Product.fromID(items[i].productID).price;
            bigString += String.format("""
                    %-16d %-16s %-16d
                    """, items[i].productID, Product.fromID(items[i].productID).name, items[i].quantity);
        }
        bigString += String.format("""
                Total Cost: %-8f
                """, totalCost);
        try {
            FileWriter wr = new FileWriter("Invoice#"+invoiceID, false);
            wr.write(bigString);
            wr.close();
        } catch(Exception e){
            System.out.println("Invoice cannot be saved to disk.");
        }
        Renderer.clrscr();
        System.out.println("Thank you for visiting us, your invoice ID # " + invoiceID + " is created\nand is saved to the disk. Have a nice Dayy!");
        try { Thread.sleep(500); } catch(InterruptedException e) { }
    }

    public String cartItemsStringified(){
        String str = "";
        if(items[0] == null) return "";
        for(int i = 0; i < items_filledSize; i++){
            str += String.format("""
                    
                    %-16d %-16s %-16d
                    """, items[i].productID, Product.fromID(items[i].productID).name, items[i].quantity);
        }
        return str;
    }

}

