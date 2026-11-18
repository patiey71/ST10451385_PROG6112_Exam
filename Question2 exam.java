import static org.junit.Assert.assertEquals;

import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.junit.Before;
import org.junit.Test;

/**
 * QUESTION 2 - Mattress sales GUI application.
 * Everything for Q2 is in this one file: the interface, ProductCalculations,
 * the GUI (this class) and the unit tests.
 * Requires the JUnit library to be added to the project.
 */
public class ProductApp extends JFrame {

    private static final String LOG_FILE = "data.txt";
    private static final String DIVIDER = "*".repeat(40);
    private static final String[] PRODUCTS = {
        "Cozy Nights Monarch", "Restonic Barbados", "Sealy Phuket"
    };

    private final JTextField customerField = new JTextField(15);
    private final JComboBox<String> productBox = new JComboBox<>(PRODUCTS);
    private final JTextField priceField = new JTextField(15);

    public ProductApp() {
        super("Product App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(buildMenuBar());
        add(buildForm());
        pack();
        setLocationRelativeTo(null);
    }

    /** Builds File > Exit and Tools > Process / Clear (Q.2.4). */
    private JMenuBar buildMenuBar() {
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());

        JMenuItem processItem = new JMenuItem("Process");
        processItem.addActionListener(e -> processSale());

        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(e -> clearForm());

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(exitItem);
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(processItem);
        toolsMenu.add(clearItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        return menuBar;
    }

    /** Builds the input form with the Process button (Q.2.2). */
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Customer Name:"));
        form.add(customerField);
        form.add(new JLabel("Select Product:"));
        form.add(productBox);
        form.add(new JLabel("Enter Price:"));
        form.add(priceField);

        JButton processButton = new JButton("PROCESS");
        processButton.addActionListener(e -> processSale());
        form.add(new JLabel());
        form.add(processButton);
        return form;
    }

    /** Shared by the Process button and the Process menu item (Q.2.3). */
    private void processSale() {
        String customer = customerField.getText().trim();
        if (customer.isEmpty()) {
            showError("Please enter the customer's name.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid price, e.g. 3500.");
            return;
        }
        if (price < 0) {
            showError("The price cannot be negative.");
            return;
        }

        String product = (String) productBox.getSelectedItem();
        IProductCalculations calc = new ProductCalculations(price);

        String details = "Customer Name: " + customer
                + "\nProduct: " + product
                + "\nVAT: R " + calc.GetVat()
                + "\nCOMMISSION: R " + calc.GetCommission()
                + "\nTOTAL: R " + calc.GetTotal();

        JOptionPane.showMessageDialog(this, details, "Message", JOptionPane.INFORMATION_MESSAGE);
        saveToFile(details);
    }

    /** Appends the sale to data.txt in the layout shown in the brief. */
    private void saveToFile(String details) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println("DATA LOG");
            out.println(DIVIDER);
            out.println(details);
            out.println(DIVIDER);
        } catch (IOException ex) {
            showError("Could not write to " + LOG_FILE + ": " + ex.getMessage());
        }
    }

    /** Clears the text boxes and resets the combo box to the first item. */
    private void clearForm() {
        customerField.setText("");
        priceField.setText("");
        productBox.setSelectedIndex(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductApp().setVisible(true));
    }

    /** Unit tests (Q.2.6). Uses R3500 from the sample screenshot. */
    public static class ProductCalculationsTest {

        private IProductCalculations calc;

        @Before
        public void setUp() {
            calc = new ProductCalculations(3500);
        }

        @Test
        public void GetVat_WithValidData_ReturnsCorrectValue() {
            assertEquals(525.0, calc.GetVat(), 0.001);
        }

        @Test
        public void GetTotal_WithValidData_ReturnsCorrectValue() {
            assertEquals(4200.0, calc.GetTotal(), 0.001);
        }
    }
}

/** Contract for the VAT, commission and total calculations (Q.2.5). */
interface IProductCalculations {

    /** @return VAT amount (15% of the price) */
    double GetVat();

    /** @return commission amount (5% of the price) */
    double GetCommission();

    /** @return total sale amount (price + VAT + commission) */
    double GetTotal();
}

/** Calculates the VAT, commission and total for a single sale. */
class ProductCalculations implements IProductCalculations {

    private static final double VAT_RATE = 0.15;
    private static final double COMMISSION_RATE = 0.05;

    private final double price;

    public ProductCalculations(double price) {
        this.price = price;
    }

    @Override
    public double GetVat() {
        return price * VAT_RATE;
    }

    @Override
    public double GetCommission() {
        return price * COMMISSION_RATE;
    }

    @Override
    public double GetTotal() {
        return price + GetVat() + GetCommission();
    }
}
