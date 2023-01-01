// kool

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ~ Author: Muhammad Mujtaba >>>>> Kool Prsn
 ~ Date: December 21, 2022

 My teacher did not allow the use of OOP and Databases. So I made my own
 toy database-ish thing here. This is a part of my 2nd semester project
 "ECommerce System".

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ! USAGE GUIDE:

    - USE OF '/' SLASH IS NOT ALLOWED IN DATABASE ENTERIES. IT CONFLICTS WITH
    INTERNAL DATABASE OPERATIONS THAT USE SLASH FOR SEPARATING COLUMNS IN TABLES
    OTHERWISE UNDEFINED ERRORS MAY OCCUR.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 ! OPEN BUG REPORTS:

    - AFTER DELETION OF AN ENTRY FROM ONE TABLE, WHAT WILL HAPPEN TO THE RELATED
    ENTERIES IN ANOTHER TABLE? KEEP THAT ORPHAN?

    - DB IS UNABLE TO AUTO-ASSIGN DEFAULT INTEGER PRIMARY KEYS. SO USE ONLY
    MANUALLY-ASSIGNED PRIMARY KEYS LIKE I DID (USED RANDOM NUMBERS/HASH CODES/ETC AS PKs)

 ! TODOs:
    - FOREIGN KEY KINDA USELESS FOR NOW.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// START -

// Data represents actual thing that must be stored.
// This data is (de)serialized based on the table attribute
// Converts all data to String format to be stored in file
// adds slash / between data so to prevent conflict
// among fields in database. DB table name has a /slash attached before its name.

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

enum Type { INTEGER, STRING, DOUBLE, BOOLEAN, ROW };

// Data class should extend from TableAttribute class to inherit its
// important attributes. Since we are not allowed to use OOP concepts
// by our teacher, so I must populate this data class with weird methods.
class Data {
    String sval;
    Type type;
    public Data(int integer){
        sval = Integer.toString(integer);
        type = Type.INTEGER;
    }
    public Data(String string){
        sval =  string;
        type = Type.STRING;
    }
    public Data(double _double){
        sval =  Double.toString(_double);
        type = Type.DOUBLE;
    }
    public Data(boolean _boolean){
        sval =  Boolean.toString(_boolean);
        type = Type.BOOLEAN;
    }
    public Data(Data[] row){
        // - sval =  Double.toString(row); TODO?
    }

    public static int toInteger(String data){
        return Integer.parseInt(data);
    }
    public static String toString(String data){
        return data;
    }
    public static double toDouble(String data){
        return Double.parseDouble(data);
    }
    public static boolean toBoolean(String data){
        return Boolean.parseBoolean(data);
    }

    public int getInteger(){
        //if(type != Type.INTEGER) System.out.println("DB Attempted To Get Integer When DataType is " + type.toString());
        return Integer.parseInt(sval);
    }
    public String getString(){
        //if(type != Type.STRING) System.out.println("DB Attempted To Get String When DataType is " + type.toString());
        return sval;
    }
    public double getDouble(){
        //if(type != Type.DOUBLE) System.out.println("DB Attempted To Get Double When DataType is " + type.toString());
        return Double.parseDouble(sval);
    }
    public boolean getBoolean(){
        //if(type != Type.BOOLEAN) System.out.println("DB Attempted To Get Boolean When DataType is " + type.toString());
        return Boolean.parseBoolean(sval);
    }
}

// Attribute represents a single column in a table
class TableAttribute {
    enum Usage { PRIMARY_KEY, FOREIGN_KEY, STORAGE, };
    String name; // the name of each attribute of table layout
    Type type; // the datatype of each attribute of table layout
    Usage usage;
    public TableAttribute(String name, Type type, Usage usage){
        this.name = name;
        this.type = type;
        this.usage = usage;
    }
}

// Layout represents all the columns of a table
class TableLayout {
    TableAttribute[] tableAttributes;
    int primaryKeyIndex = 0; // default primary key index
    public TableLayout(TableAttribute[] tableAttributes){
        boolean assignedPrimaryKey = false;
        for(int i = 0; i < tableAttributes.length; i++){
            // TODO: MULTIPLE ATTRIBUTES MUST NOT SHARE SAME NAME
            if(tableAttributes[i].usage == TableAttribute.Usage.PRIMARY_KEY) {
                if(!assignedPrimaryKey){ primaryKeyIndex = i; assignedPrimaryKey = true; }
                else { System.out.println("DB Table Attempted Multiple Primary Keys"); }
            }
        }
        this.tableAttributes = tableAttributes;
    }
    public boolean isGivenDataAccordingToTableLayout(String[] data){
        if(data.length != tableAttributes.length) return false;
        for(int i = 0; i < tableAttributes.length; i++){
            if(tableAttributes[i].type == Type.INTEGER){
                try { Data.toInteger(data[i]); } catch(Exception e){ return false; }
            } else if(tableAttributes[i].type == Type.STRING){
                try { Data.toString(data[i]); } catch(Exception e){ return false; }
            } else if(tableAttributes[i].type == Type.DOUBLE){
                try { Data.toDouble(data[i]); } catch(Exception e){ return false; }
            } else if(tableAttributes[i].type == Type.BOOLEAN){
                try { Data.toBoolean(data[i]); } catch(Exception e){ return false; }
            }
        }
        return true;
    }

    public boolean hasIllegalCharacters(String[] data){
        for(String dataElem: data){
            if(dataElem.contains("/")) return true;
        }
        return false;
    }

    public int getAttributeIndex(String name){
        for(int i = 0; i < tableAttributes.length; i++){
            if(tableAttributes[i].name.equals(name)) return i;
        }
        System.out.println("DB Get Attribute Index Error");
        return -1;
    }
}

// Table handles all the rows. Rows are directly dependent on the layout of
// a table
class Table {
    String tableName;
    TableLayout tableLayout; // layout of this table

    String[][] rows; // all entries are stored here
    int rows_filledRowsSize = 0;
    public Table(String name, TableLayout tableLayout){
        this.tableName = name;
        this.tableLayout = tableLayout;
        rows = new String[64][tableLayout.tableAttributes.length];
    }

    public boolean isGivenDataPKAlreadyExists(String[] data){
        for(int i = 0; i < rows_filledRowsSize; i++){
            if(data[tableLayout.primaryKeyIndex].equals(rows[i][tableLayout.primaryKeyIndex])) return true;
        }
        return false;
    }


    // This data must be according to the table layout or else
    // error will occur and -1 will be returned. This function
    // also resizes rows[] if needed
    int _addRow(String[] data){
        if(tableLayout.hasIllegalCharacters(data)) {System.out.println("hasIllegalCharacters"); return -1;}
        if(!tableLayout.isGivenDataAccordingToTableLayout(data)) {System.out.println("isGivenDataAccordingToTableLayout"); return -1;}
        if(isGivenDataPKAlreadyExists(data)) {System.out.println("isGivenDataPKAlreadyExists"); return -1;}
        if(rows_filledRowsSize == rows.length - 2){ // TODO: KEEP AN EYE ROWS.LENGTH -1?
            String[][] tmp = new String[rows.length + 64][tableLayout.tableAttributes.length];
            for(int i = 0; i < rows.length; i++){ tmp[i] = rows[i]; } rows = tmp;
        }
        rows[rows_filledRowsSize++] = data; /////////////////////////////////////++...
        return 0;
    }

    // This data must be according to the table layout or else
    // error will occur and -1 will be returned
    int _alterRow(String PK, String[] newData){
        if(!tableLayout.isGivenDataAccordingToTableLayout(newData)) return -1;
        for(int i = 0; i < rows_filledRowsSize; i++){
            if(rows[i][tableLayout.primaryKeyIndex].equals(PK)){
                rows[i] = newData;
                return 0;
            }
        }
        return -1;
    }

    // if row.pk == PK then delete else continue. if no pk found, throw exception
    int _deleteRow(String PK){
        /*boolean deleted = false;
        for(int i = 0; i < rows_filledRowsSize; i++) {
            if (!deleted){
                try { if (rows[i][tableLayout.primaryKeyIndex].equals(PK)) {
                    rows[i] = null;
                    deleted = true;
                } } catch (NullPointerException e){ return -1; }
            }
            if(deleted) rows[i] = rows[i+1];
        }
        if(!deleted) return -1;
        rows_filledRowsSize--;
        return 0;*/
        boolean deleted = false;
        for(int i = 0; i < rows_filledRowsSize; i++) {
            if (!deleted){
                try { if (rows[i][tableLayout.primaryKeyIndex].equals(PK)) {
                    for(int pp = 0; pp < rows[i].length; pp++){
                        if(pp == tableLayout.primaryKeyIndex) continue; rows[i][pp] = "";
                    }
                    deleted = true;
                } } catch (NullPointerException e){ return -1; }
            }
            if(deleted) rows[i] = rows[i+1];
        }
        if(!deleted) return -1;
        rows_filledRowsSize--;
        return 0;
    }

    private String[] _getRow(String PK){
        try {
            for (int i = 0; i < rows_filledRowsSize + 1; i++) {
                if (rows[i][tableLayout.primaryKeyIndex].equals(PK)) return rows[i];
            }
        } catch(NullPointerException e){ return null; }
        return null;
    }

    public void insert(Data[] data){
        String[] sdat = new String[data.length];
        for(int i = 0; i < data.length; i++) sdat[i] = data[i].getString();
        int code = _addRow(sdat);
        if(code == -1) System.out.println("DB Insert Error");
    }


    public void alter(Data PK, Data[] newData){
        String[] sdat = new String[newData.length];
        for(int i = 0; i < newData.length; i++) sdat[i] = newData[i].getString();
        int code = _alterRow(PK.getString(), sdat);
        if(code == -1) System.out.println("DB Alter Row Error");
    }

    public void delete(Data PK){
        int code = _deleteRow(PK.getString());
        if(code == -1) System.out.println("DB Delete Error");
    }

    public Data[] getRowByPK(Data PK){
        String[] thisRow = _getRow(PK.getString());
        if(thisRow == null){ /*System.out.println("DB Get Row Error: Not Found");*/ return null; } // TODO:HANDLE ERROR HERE OR IN LOW LEVEL FUNCTS?
        Data[] data = new Data[thisRow.length];
        for(int i = 0; i < data.length; i++) data[i] = new Data(thisRow[i]);
        return data;
    }

    enum FilterType { MATCH, CONTAINS, }; // what type of filter? return entry if even a part of it matches the value or when it exactly matches the value

    public Data[][] filter(String tableAttributeName, Data correspondingValue, FilterType filterType){
        Data[][] dataRows = null;
        int attributeIndex = tableLayout.getAttributeIndex(tableAttributeName);
        int dataRowsCount = 0;
        if(filterType == FilterType.MATCH){
            for (int i = 0; i < rows_filledRowsSize; i++){
                if(rows[i][attributeIndex].equals(correspondingValue.getString())) dataRowsCount++;
            }
            dataRows = new Data[dataRowsCount][tableLayout.tableAttributes.length];
            for (int i = 0, count = 0; i < rows_filledRowsSize; i++){
                if(rows[i][attributeIndex].equals(correspondingValue.getString())) {
                    // make a row of type Data[]
                    Data[] thisDataRow = new Data[tableLayout.tableAttributes.length];
                    for(int j = 0; j < thisDataRow.length; j++){ thisDataRow[j] = new Data(rows[i][j]); }
                    // then add it to dataRows[][]
                    dataRows[count++] = thisDataRow;
                }
            }
        }

        if(filterType == FilterType.CONTAINS){
            for (int i = 0; i < rows_filledRowsSize; i++){
                if(rows[i][attributeIndex].toUpperCase().contains(correspondingValue.getString().toUpperCase())) dataRowsCount++;
            }
            dataRows = new Data[dataRowsCount][tableLayout.tableAttributes.length];
            for (int i = 0, count = 0; i < rows_filledRowsSize; i++){
                if(rows[i][attributeIndex].toUpperCase().contains(correspondingValue.getString().toUpperCase())) {
                    // make a row of type Data[]
                    Data[] thisDataRow = new Data[tableLayout.tableAttributes.length];
                    for(int j = 0; j < thisDataRow.length; j++){ thisDataRow[j] = new Data(rows[i][j]); }
                    // then add it to daraRows[][]
                    dataRows[count++] = thisDataRow;
                }
            }
        }
        return dataRows;
    }



    // Gets all records from this table
    public Data[][] all(){
        Data[][] dataRows = new Data[rows_filledRowsSize][tableLayout.tableAttributes.length];
        for(int i = 0; i < rows_filledRowsSize; i++){
            for(int j = 0; j < rows[i].length; j++) {
                dataRows[i][j] = new Data(rows[i][j]);
            }
        }
        return dataRows;
    }

}

// uses scanner.nextLine() to read the whole database file line by line. then
// splits each field by using split(DELIM), and then returns a requested part
// of data (filtered).
public class KoolDB {
    String dbFilename; // Database filename
    Table[] tables; // tables associated with this instance of class

    public KoolDB(String dbFilename, Table[] tables){
        this.dbFilename = dbFilename;
        this.tables = tables;
        _load(dbFilename);
    }

    public Table getTable(String name){
        for(Table table: tables) if(table.tableName.equals(name)) return table;
        System.out.println("DB Get Table Error: Not Found");
        return null;
    }

    // Changes made to the database only occur in memory until save()
    // is explicitly called. For load(), the file contents are loaded in memory

    // Load all table rows from file into var:table.rows
    // TODO:NO WAY TO HANDLE CORRUPTED DATABASE. LIKE, IF WE REMOVE SLASH FROM TABLE NAME
    //  WHAT DOES THIS MEAN? IT IS ENTRY OR A TABLE NAME CORRUPTED? right now, im just
    //  throwing exception with msg="DB maybe corrupted"
    public void _load(String dbFilename) {
        Scanner scanner = null;
        try {
            File inFile = new File(dbFilename);
            if(!inFile.exists()) inFile.createNewFile();
            scanner = new Scanner(new File(dbFilename));
        }
        catch(IOException e){ System.out.println("DB Load Error. scanner read file related."); }

        Table currentTable = null;
        boolean isRow = false;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            for(Table tmpTable: tables){
                if(line.equals("/" + tmpTable.tableName)){currentTable = tmpTable; isRow = false; break; }
                else isRow = true;
            }
            if(isRow) {
                String[] data = line.split("/");
                try { currentTable._addRow(data); } catch (NullPointerException e){ System.out.println("DB Maybe Corrupted. Or Other Unknown Error in _load()"); }
            }
        }
    }

    public void save(){
        String bigString = "";
        for(Table table: this.tables){
            bigString += "/" + table.tableName + "\n";
            for(int i = 0; i < table.rows_filledRowsSize; i++){
                for(int j = 0; j < table.rows[i].length; j++){
                    if(j == table.rows[i].length - 1) bigString += table.rows[i][j];
                    else bigString += table.rows[i][j] + "/";
                }
                bigString += "\n";// < newline important (to distinguish records)
            }
        }
        try {
            FileWriter writer = new FileWriter(this.dbFilename, false);
            writer.write(bigString);
            writer.close();
        } catch (Exception e){ System.out.println("DB Save Error"); }
    }
}



/**************************** HOW DB FILE LOOKS LIKE *************************************
 *
 * /USER
 * muj@mm.com/Muhammad Mujtaba/true/I am a very lovely man.
 * abcabcacajifkgfdg@mm.com/Cool0_0/fasle/Lonliness prevails.
 * /PRODUCT
 * Cement/23.0
 * Kool Hat/10000000000.0
 *
 *
 ****************************************************************************************/

/************************ A SAMPLE MODEL API USE ****************************************
public class Main {

    static TableAttribute[] USER_ATTRIBUTES = {
            new TableAttribute("EMAIL", Type.STRING, TableAttribute.Usage.PRIMARY_KEY),
            new TableAttribute("NAME", Type.STRING, TableAttribute.Usage.STORAGE),
            new TableAttribute("PASSWORD", Type.STRING, TableAttribute.Usage.STORAGE),
    };
    static TableAttribute[] PRODUCT_ATTRIBUTES = {
            new TableAttribute("ID", Type.INTEGER, TableAttribute.Usage.PRIMARY_KEY),
            new TableAttribute("NAME", Type.STRING, TableAttribute.Usage.STORAGE),
            new TableAttribute("DESCRIPTION", Type.STRING, TableAttribute.Usage.STORAGE),
    };

    static Table[] tables = {
            new Table("USER", new TableLayout(USER_ATTRIBUTES)),
            new Table("PRODUCT", new TableLayout(PRODUCT_ATTRIBUTES)),
    };

    public static void main(String[] args){
        System.out.println("- ECommerce System -");
        try {
            Data[] dummy = {
                    new Data("mmm@gmail.com"), new Data("Mujtaba"),
                    new Data("password123"),
            };
            Data[] dummy2 = {
                    new Data("/fgdfgdfgdfgf@gmail.com"), new Data("Mujtdfgdfgdfgaba"),
                    new Data("5"),
            };
            MujtabaDB mujtabaDB = new MujtabaDB("MDB_.txt", tables);
            mujtabaDB.getTable("USER").insert(dummy2);
            mujtabaDB.getTable("USER").insert(dummy);
            //mujtabaDB.getTable("USER").delete(new Data("muhammadmujtaba150@gmail.com"));
            //mujtabaDB.getTable("USER").alter(new Data("muhammadmujtaba150@gmail.com"), dummy2);

            Data[][] dat = mujtabaDB.getTable("USER").filter("NAME", new Data("muj"), Table.FilterType.CONTAINS);
            for(Data[] d: dat)for(Data dd:d){System.out.println(dd.getInteger());}

            mujtabaDB.save();
        } catch(Exception e){ e.printStackTrace(); }
    }
}
****************************************************************************************/

// - END

