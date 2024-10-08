import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;

public class IOServices implements Services{
    public static IOServices[] _instance1 = new IOServices[1];
    public static Scanner s = new Scanner(System.in);
    public DataBase db;

    private IOServices(){
        db = DataBase.getInstance();
    }

    public static IOServices getInstance() {
        if (_instance1[0] == null) {
            _instance1[0] = new IOServices();
        }
        return _instance1[0];
    }

    public boolean isInteger(String str){
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble(String str){
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public <T> T getInput(Predicate<T> condition, String mesg) {
        T choice = null;

        try {
            do {
                System.out.println(mesg);
                String input = s.next();
                try {
                    if (condition != null && isInteger(input)) {
                        choice = (T) (Integer) Integer.parseInt(input);
                    } else if (condition != null && isDouble(input)) {
                        choice = (T) (Double) Double.parseDouble(input);
                    } else if (condition != null) {
                        if (input.length() == 1) {
                            choice = (T) (Character) input.charAt(0);
                        }
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please try again");
                    s.next(); // consume the invalid input
                } catch (Exception e) {
                    System.out.println("Please try again");
                }

                if (choice != null && condition.test(choice)) {
                    System.out.println("Invalid input. Please try again");
                }
            } while (choice == null || condition.test(choice));
        }catch (Exception e){
            System.out.println("an error occured, try again");
            return getInput(condition, mesg);
        }

        s.nextLine();
        return choice;
    }

    public Country getCountry(){
        for (Country c: db.getAllCountries()){
            System.out.println(c.toString());
        }
        System.out.println("please enter the country code");
        String code = s.nextLine();
        if(db.getCountry(code) == null){
            System.out.println("Country not found");
            return getCountry();
        }
        return db.getCountry(code);
    }

    public String addCountryToList(){
        System.out.println("please enter the country name");
        String country = s.nextLine();
        System.out.println("please enter the import tax");
        int tax = getInput((Integer i) -> i < 0, "please enter a positive number");
        db.addImportTax(country, tax);
        return country;
    }

    public void printProductTypes(){
        for (int i = 0; i < ProductType.values().length; i++){
            System.out.println(i+1 + ". " + ProductType.values()[i]);
        }
    }

    public Product getProduct() {
        System.out.println(db.AllProductsInStoreToString());
        System.out.println("please enter the product code");
        String code = s.nextLine();
        for (Product p: db.getProducts()){
            if (p.getCode().equals(code)){
                return p;
            }
        }
        return null;
    }

    public String getProductCode(){
        ResultSet rs = null;
        String code = null;
        Product p = null;
        try {
            rs =db.QueryDB("SELECT * FROM " + TN.PRODUCT.tname());
            if (db.getAllProducts().isEmpty())return null;

            System.out.println("The Products: ");
            while (rs.next()){
                System.out.println("Code: " + rs.getString(TN.PRODUCT_CODE.tname()) + "  Name: " + rs.getString(TN.PRODUCT_NAME.tname()) + "  Stock: " + rs.getString(TN.PRODUCT_STOCK.tname()) + "  Type: " + rs.getString(TN.PRODUCT_TYPE.tname()));
            }
            System.out.println("Please enter the Product code: ");
            do {
                code = s.nextLine();
                p = db.getProduct(code);
                if (p == null) System.out.println("No such Product, try again");
            }while (p == null);

        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
        }
        return code;
    }

    public boolean isAlpha(String str){
        int len = str.length();
        int i = 0;
        while (i < len){
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean isNumeric(String str) {
        int len = str.length();
        int i = 0;
        while (i < len){
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
            i++;
        }
        return true;
    }

    public boolean isAlphaNumeric(String str) {
        return isNumeric(str) && isAlpha(str);
    }
}
