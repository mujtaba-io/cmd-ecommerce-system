// frends

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 24, 2022

 The real thing starts happening from here!

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


/*
*
* !BUG REPORTS:
* - AT LINE 278: REVIEWS MUST BE FILTERED FOR EACH USER. IT DISPLAYS ALL REVIEWS FOR EVERY USER.
*
* !NOTES:
* - var:PK and var:ID are used interchangeably
*
* */

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class Renderer {
    public static KoolDB DB = DBModel.DB;
    public static void render(){
        System.out.println("""
                -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                ------------------------ E COMMERCE SYSTEM --------------------------
                -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                """);
        while(true){
            clrscr();
            System.out.print("""
                    To Sell: Type "sell"
                    To Buy: Type "buy"
                    To Exit: Type "exit"
                    > """);
            String command = new Scanner(System.in).next();
            if(command.equalsIgnoreCase("EXIT")) return ;
            if(command.equalsIgnoreCase("BUY")) buy();
            if(command.equalsIgnoreCase("SELL")) sell();
        }
    }

    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

    private static void buy(){
        String sellers = allSellersStringified();
        while(true){
            clrscr();
            System.out.printf("""
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    Top 10 Sellers:
                    %-24s %-24s %-24s %-24s
                    """ + sellers/*.replaceAll(".{72}", "$0\n")*/ + """
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                    
                    """,
                    "Email", "Name", "Rating", "Bio");
            System.out.print("""
                    To search a seller: Type "search [EMAIL | NAME]"
                    To view all sellers: Type "all"
                    To visit a seller: Type "visit [EMAIL]"
                    To exit: Type "back"
                    > """);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            if(command.equalsIgnoreCase("back")){ break; }
            if(command.equalsIgnoreCase("search")){ sellers = searchedSellersStringified(scanner.next()); }
            if(command.equalsIgnoreCase("all")){ sellers = allSellersStringified(); }
            if(command.equalsIgnoreCase("visit")){ visitSeller(scanner.next()); }

        }
    }

    public static void visitSeller(String sellerEmail){
        Table sellerTable = DB.getTable("SELLER");
        Seller seller = Seller.fromEmail(sellerEmail);
        if(seller == null){ System.out.println("no such seller"); return ; }
        Cart myCart = new Cart();
        while(true){
            clrscr();
            System.out.printf("""
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    Seller, %s (%s) | %d Reviews
                    
                    Their Products:
                    %-16s %-16s %-16s %-16s
                    """ + seller.allProductsStringified()/*.replaceAll(".{72}", "$0\n")*/ + """
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                    
                    """, seller.name, seller.email, DB.getTable("REVIEW").filter("TO_SELLER_EMAIL", new Data(seller.email), Table.FilterType.MATCH).length,
                    "ID", "Product Name", "Price $", "Description");
            System.out.printf("""
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    Cart
                    
                    %d items:
                    %-16s %-16s %-16s
                    """ + myCart.cartItemsStringified()/*.replaceAll(".{72}", "$0\n")*/ + """
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    """, myCart.items_filledSize, "Item ID", "Item Name", "Item Quantity");
            System.out.print("""
                    Add-to-cart a Product: Type "addcart [PRODUCT_ID] [QUANTITY]"
                    Remove-from-cart a Product: Type "removecart [PRODUCT_ID] [QUANTITY]"
                    To buy cart items: Type "buy"
                    View their Reviews: Type "reviews"
                    To go back: Type "back"
                    > """);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            if(command.equalsIgnoreCase("back")){ break; }
            if(command.equalsIgnoreCase("addcart")){
                try{ addToCart(scanner.nextInt(), scanner.nextInt(), myCart); }
                catch(InputMismatchException e){ System.out.println("err: enter integers"); }
            }
            if(command.equalsIgnoreCase("removecart")){
                try{ removeFromCart(scanner.nextInt(), myCart); }
                catch(InputMismatchException e){ System.out.println("err: enter integer"); }
            }
            if(command.equalsIgnoreCase("buy")){ buyCartItems(myCart); myCart = new Cart(); }
            if(command.equalsIgnoreCase("reviews")){ viewReviews(seller); }
        }

    }

    public static void addToCart(int productPK, int quantity, Cart cart){
        if(DB.getTable("PRODUCT").getRowByPK(new Data(productPK)) == null){
            System.out.println("Product does not exist"); return ;
        }
        cart.addItem(productPK, quantity);
    }
    public static void removeFromCart(int productPK, Cart cart){
        if(!cart.has(productPK)){
            System.out.println("Product does not exist in cart"); return ;
        }
        cart.removeItem(productPK);
    }

    // creates DB invoice enteries from cat and generates invoice.
    // invoice is saved to DB, but it has customer cpoy saved to
    // another txt file.
    public static void buyCartItems(Cart cart){
        if (cart.items_filledSize == 0){System.out.println("Empty cart cannot be brought"); return; }
        cart.buyCartItems();
    }

    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

    public static String searchedSellersStringified(String keywords){
        Seller[] sellers = Seller.all();
        String str = "";
        for(Seller seller : sellers){
            if(seller.name.toUpperCase().contains(keywords.toUpperCase())
            || seller.email.toUpperCase().contains(keywords.toUpperCase())){
                str += String.format("""
                    
                    %-24s %-24s %-24.1f %-24s
                    """, seller.email, seller.name, seller.rating,seller.bio);
            }
        }
        return str;
    }

    public static String allSellersStringified(){
        Seller[] sellers = Seller.all();
        String str = "";
        for(Seller seller: sellers){
            str += String.format("""
                    
                    %-24s %-24s %-24.1f %-24s
                    """, seller.email, seller.name, seller.rating,seller.bio);
        }
        return str;
    }

    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

    private static void sell(){
        // Until seller variable != null, we are logged in as seller. When it becomes
        // null, that means we are logged out.
        Seller seller = _awaitLogin();
        while(true){
            clrscr();
            System.out.printf("""
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    Welcome, %s (%s) | %d Reviews
                    
                    Your Products:
                    %-16s %-16s %-16s %-16s
                    """ + seller.allProductsStringified()/*.replaceAll(".{72}", "$0\n")*/ + """
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                    
                    """, seller.name, seller.email, DB.getTable("REVIEW").filter("TO_SELLER_EMAIL", new Data(seller.email), Table.FilterType.MATCH).length,
            "ID", "Product Name", "Price $", "Description");
            System.out.print("""
                    Add a new Product: Type "add"
                    Edit a product: Type "edit [ID]"
                    Delete a product: Type "delete [ID]"
                    View your Reviews: Type "reviews"
                    To Logout: Type "logout"
                    > """);
            Scanner scanner = new Scanner(System.in);
            String command = scanner.next();
            if(command.equalsIgnoreCase("logout")){ seller = null; break; }
            if(command.equalsIgnoreCase("add")){ addProduct(seller); }
            if(command.equalsIgnoreCase("edit")){ editProduct(seller, new Data(scanner.next())); }
            if(command.equalsIgnoreCase("delete")){ deleteProduct(seller, new Data(scanner.next())); }
            if(command.equalsIgnoreCase("reviews")){ viewReviews(seller); }
        }
    }

    public static void addProduct(Seller seller){
        Table productTable = DB.getTable("PRODUCT");
        ////System.out.print("Product ID (you cannot change it later): ");
        ////int ID = Integer.parseInt(new Scanner(System.in).nextLine());
        System.out.print("Product name: ");
        String name = new Scanner(System.in).nextLine();
        double price;
        while(true) {
            try {
                System.out.print("Product price: "); price = new Scanner(System.in).nextDouble(); break;
            } catch(InputMismatchException e){ System.out.println("err: invalid price"); } }
        System.out.print("Product description: ");
        String description = new Scanner(System.in).nextLine();
        int ID = Math.abs(name.hashCode() + (int)(Math.random() * 10) + (LocalDateTime.now().getNano()/8));
        if(productTable.getRowByPK(new Data(ID)) != null) { System.out.print("This ID already exists"); return; }
        Data[] data = {
                new Data(ID), new Data(seller.email), new Data(name),
                new Data(price), new Data(description),
        };
        productTable.insert(data);
    }
    public static void editProduct(Seller seller, Data PK){
        Table productTable = DB.getTable("PRODUCT");
        if(productTable.getRowByPK(PK) == null){ System.out.print("This product does not exist."); return ; }
        System.out.print("Product new name: ");
        String name = new Scanner(System.in).nextLine();
        double price;
        while(true) {
            try {
                System.out.print("Product new price: "); price = new Scanner(System.in).nextDouble(); break;
            } catch(InputMismatchException e){ System.out.println("err: invalid price"); } }
        System.out.print("Product new description: ");
        String description = new Scanner(System.in).nextLine();
        Data[] data = {
                PK, new Data(seller.email),
                new Data(name), new Data(price), new Data(description),
        };
        productTable.alter(PK, data);
    }
    public static void deleteProduct(Seller seller, Data PK){
        Table productTable = DB.getTable("PRODUCT");
        if(productTable.getRowByPK(PK) == null){ System.out.print("This product does not exist."); return ; }
        productTable.delete(PK);
    }

    public static void viewReviews(Seller seller){
        Table reviewsTable = DB.getTable("REVIEW");
        Data[][] reviews = reviewsTable.all();
        String reviewsText = "";
        for(Data[] reviewsDataRow: reviews){
            reviewsText += String.format("""
                    
                    %-16s %-16.1f %-16s
                    """,
                    reviewsDataRow[reviewsTable.tableLayout.getAttributeIndex("BY_USER")].getString(),
                    reviewsDataRow[reviewsTable.tableLayout.getAttributeIndex("RATING")].getDouble(),
                    reviewsDataRow[reviewsTable.tableLayout.getAttributeIndex("TEXT")].getString());
        }
        clrscr();
        System.out.printf("""
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
                    
                    Reviews to: %s (%s)
                    
                    Reviews:
                    %-16s %-16s %-16s
                    """ + reviewsText + """
                    
                    -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
                    
                    Press any key then enter to go back.
                    """, seller.name, seller.email, "Name", "Rating", "Thoughts");
        new Scanner(System.in).next();
    }

    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >
    // > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > > >

    private static Seller _awaitLogin(){
        Table sellerTable = DB.getTable("SELLER");
        String email, password, name, bio;
        while(true) {
            System.out.print("Enter Email: ");
            email = new Scanner(System.in).next();
            if (email.isBlank()){ System.out.println(" is an incorrect email"); continue; }
            break;
        }
        Data[] sellerInfo = sellerTable.getRowByPK(new Data(email));
        while(true) {
            System.out.print("Enter Password: ");
            password = new Scanner(System.in).nextLine();
            // Login
            if (sellerInfo != null) {
                if (sellerInfo[sellerTable.tableLayout.getAttributeIndex("PASSWORD")].getString().equals(password)) {
                    return Seller.fromEmail(email);
                } else { System.out.println(" is an incorrect password"); continue; }
            }
            // Signup
            else {
                System.out.println("This account does not exist, so we are creating one for you.");
                System.out.print("Enter Name: ");
                name = new Scanner(System.in).nextLine();
                System.out.print("Enter Bio: ");
                bio = new Scanner(System.in).nextLine();
                Seller.signup(email, password, name, bio);
                return Seller.fromEmail(email);
            }
        }
    }

    public static void clrscr(){
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

}
