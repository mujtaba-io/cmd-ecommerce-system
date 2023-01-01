// latte
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 19, 2022

 "ECommerce System - Semester 2 Project" by
 - Mujtaba SP22-BSE-036
 - Malaika SP22-BSE-025

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ! OPEN BUG REPORTS:
    - null

 ! NOTES:
    - THE QUALITY OF CODE FROM THE START WAS GOOD AS FAR AS I CAN THINK.
    BUT AFTER THAT, DUE TO LESS TIME, I DECREASED THE CODE QUALITY TO
    COMPLETE THE PROJECT FAST. - YOU CAN SEE THIS IN RENDERER.JAVA FILE
    WHERE THERE IS NO FIXED PATTERN OF DATA FLOW. DATA IS FLOWING TURBULENTLY
    FROM ONE PART TO OTHER, AND MANY UNNECESSARY OBJECTS ARE BEING CREATED
    AND COPIED. ALOT OF MEMORY WASTE. ALOT OF CPU WASTE. THIS CAN BE OPTIMIZED
    BUT ITS NOT WORTH IT. "IT WORKS".

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -


public class Main {
    public static void main(String[] args){
        DBModel.init(); // < important, initialize DB first
        KoolDB DB = DBModel.DB; // < then assign a value to variable, else it will remain null
        Renderer.render();
        DB.save();
    }
}

// - END