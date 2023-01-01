// poop
/*
* Author: Muhammad Mujtaba
* */
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Review {
    public static KoolDB DB = DBModel.DB;
    public static void askReviewForSeller(Seller seller){
        System.out.println("Please write a review for this seller.");
        System.out.print("Your name: ");
        String name = new Scanner(System.in).nextLine();
        double rating;
        while(true) {
            try {
                System.out.print("Rate them out of 5.0: ");
                rating = new Scanner(System.in).nextDouble();
                if(rating > 5.0) throw new InputMismatchException();
                break;
            } catch(InputMismatchException e) { System.out.println("err: invalid rating"); }
        }
        System.out.print("Write review: ");
        String reviewText = new Scanner(System.in).nextLine();
        int reviewID = (Math.abs(name.hashCode() + (int)(Math.random() * 1000) + (LocalDateTime.now().getNano()/8)))/8;
        DB.getTable("REVIEW").insert(new Data[]{
                new Data(reviewID),
                new Data(seller.email),
                new Data(name),
                new Data(rating),
                new Data(reviewText)
    });
    }
}
