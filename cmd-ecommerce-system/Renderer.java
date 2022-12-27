

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 24, 2022

 The real thing starts happening from here!

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

import java.io.IOException;
import java.util.*;

public class Renderer {
    public static MujtabaDB DB = DBModel.DB;
    public static void render(){
        System.out.println("""
                [][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]
                ------------------------ E COMMERCE SYSTEM --------------------------
                [][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][][]
                """);
        while(true){
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


    private static void buy(){
//TODO:DURING INVOICE CREATION/BUYCALL(), WE WILL BE ASKED TO WRITE A REVIEW ON SELLER
    }

    private static void sell(){
        // Until seller variable != null, we are logged in as seller. When it becomes
        // null, that means we are logged out.
        Seller seller = _awaitLogin();
        while(true){
            //clrscr();
            System.out.printf("""
                    
                    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    -
                    - Welcome, %s (%s) | %d Reviews
                    -
                    - Your Products:
                    """ + seller.allProductsStringified().replaceAll(".{72}", "$0\n-    ") + """
                    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
                    
                    """, seller.name, seller.email, DB.getTable("REVIEW").filter("TO_SELLER_EMAIL", new Data(seller.email), Table.FilterType.MATCH).length);
            System.out.print("""
                    Add a new Product: Type "new"
                    Delete a product: Type "delete [ID]"
                    Edit a product: Type "edit [ID]"
                    View your Reviews: Type "reviews"
                    To Logout: Type "logout"
                    > """);
            String command = new Scanner(System.in).next();
            if(command.equalsIgnoreCase("logout")){ seller = null; break; }
        }
    }

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
