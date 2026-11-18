import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;

/**
 * QUESTION 1 - Product sales report (console application).
 * Everything for Q1 is in this one file: the interface, Product,
 * PrintProduct, the main class and the unit tests.
 * Requires the JUnit library to be added to the project.
 */
public class SalesReport {

    private static final String[] PRODUCT_NAMES = {
        "Cozy Nights Monarch", "Restonic Barbados", "Sealy Phuket"
    };
    private static final int MONTHS = 3;
    private static final int REPORT_YEAR = 2026;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int[][] sales = new int[PRODUCT_NAMES.length][MONTHS];

        // Capture the sales figures: one block of prompts per product
        for (int product = 0; product < PRODUCT_NAMES.length; product++) {
            for (int month = 0; month < MONTHS; month++) {
                sales[product][month] = readSales(input, month + 1, PRODUCT_NAMES[product]);
            }
            System.out.println();
        }

        PrintProduct printer = new PrintProduct(new Product(PRODUCT_NAMES, sales));
        System.out.println();
        printer.printHeader(REPORT_YEAR);
        printer.printTotalProductSales();
        printer.printAverageProductSales();
        printer.printMostPopularProduct();
    }

    /** Keeps asking until the user enters a whole number that is not negative. */
    private static int readSales(Scanner input, int month, String productName) {
        while (true) {
            System.out.print("Enter number of sales for month " + month
                    + " for " + productName + " mattress: ");
            String text = input.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // fall through to the error message below
            }
            System.out.println("Please enter a whole number of 0 or more.");
        }
    }

    /** Unit tests (Q.1.4). Uses the sample data from the brief. */
    public static class ProductTest {

        private Product product;

        @Before
        public void setUp() {
            String[] names = {"Cozy Nights Monarch", "Restonic Barbados", "Sealy Phuket"};
            int[][] sales = {
                {25, 18, 20},
                {15, 10, 12},
                {18, 15, 20}
            };
            product = new Product(names, sales);
        }

        @Test
        public void GetTotalProductSales_WithValidData_ReturnsCorrectValue() {
            assertEquals(153, product.GetTotalProductsSold());
        }

        @Test
        public void GetAverageSales_WithValidData_ReturnsCorrectValue() {
            assertEquals(17.0, product.GetAverageProductSold(), 0.001);
        }
    }
}

/** Contract for the product sales report calculations (Q.1.2). */
interface IProduct {

    /** @return the total number of products sold across all products and months */
    int GetTotalProductsSold();

    /** @return the average number of products sold per product, per month */
    double GetAverageProductSold();

    /** @return the name of the product with the highest total sales */
    String GetMostPopularProduct();
}

/**
 * Holds the sales data and performs the report calculations.
 * productNames: single-dimensional array, one entry per product.
 * monthlySales: two-dimensional array, [product][month].
 */
class Product implements IProduct {

    private final String[] productNames;
    private final int[][] monthlySales;

    public Product(String[] productNames, int[][] monthlySales) {
        this.productNames = productNames;
        this.monthlySales = monthlySales;
    }

    /** Adds up every month of every product. */
    @Override
    public int GetTotalProductsSold() {
        int total = 0;
        for (int product = 0; product < monthlySales.length; product++) {
            total += getProductTotal(product);
        }
        return total;
    }

    /** Total sold divided by the number of product/month entries. */
    @Override
    public double GetAverageProductSold() {
        int entries = 0;
        for (int[] months : monthlySales) {
            entries += months.length;
        }
        return entries == 0 ? 0 : (double) GetTotalProductsSold() / entries;
    }

    /**
     * Finds the product with the highest total sales and returns its name
     * followed by its best single-month figure, e.g. "Cozy Nights Monarch(25)".
     */
    @Override
    public String GetMostPopularProduct() {
        int bestProduct = 0;
        for (int product = 1; product < monthlySales.length; product++) {
            if (getProductTotal(product) > getProductTotal(bestProduct)) {
                bestProduct = product;
            }
        }

        int bestMonth = 0;
        for (int sales : monthlySales[bestProduct]) {
            bestMonth = Math.max(bestMonth, sales);
        }
        return productNames[bestProduct] + "(" + bestMonth + ")";
    }

    /** Helper so the "sum one product's months" logic is written only once. */
    private int getProductTotal(int product) {
        int total = 0;
        for (int sales : monthlySales[product]) {
            total += sales;
        }
        return total;
    }
}

/** Responsible only for printing the report data (Q.1.3). */
class PrintProduct {

    private static final String DIVIDER = "*".repeat(40);

    private final IProduct product;

    public PrintProduct(IProduct product) {
        this.product = product;
    }

    public void printHeader(int year) {
        System.out.println("PRODUCT SALES REPORT - " + year);
        System.out.println(DIVIDER);
    }

    public void printTotalProductSales() {
        System.out.println("TOTAL PRODUCT SALES: " + product.GetTotalProductsSold());
    }

    public void printAverageProductSales() {
        // "0.##" prints 17.0 as 17 but keeps decimals such as 17.5
        System.out.println("AVERAGE PRODUCT SALES: "
                + new DecimalFormat("0.##").format(product.GetAverageProductSold()));
    }

    public void printMostPopularProduct() {
        System.out.println("MOST POPULAR PRODUCT: " + product.GetMostPopularProduct());
    }
}
