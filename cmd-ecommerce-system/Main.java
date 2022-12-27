
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba
 ~ Date: December 19, 2022

 "ECommerce System - Semester 2 Project" by
 - Mujtaba SP22-BSE-036
 - Malaika SP22-BSE-025

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 !BUG REPORTS:
 
 - null

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -


public class Main {
    public static void main(String[] args){
        DBModel.init(); // < important, initialize DB first
        MujtabaDB DB = DBModel.DB; // < then assign a value to variable, else it will remain null
        Renderer.render();
        DB.save();
    }
}

// - END