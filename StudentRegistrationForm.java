import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegistrationForm extends JFrame {

    // Text & Password Fields (i)
    private JTextField firstNameField, lastNameField, emailField, confirmEmailField;
    private JPasswordField passwordField, confirmPasswordField;

    // Combo Boxes (DOB)
    private JComboBox<Integer> yearCombo, monthCombo, dayCombo;
    
    // Radio Buttons (Gender & Department)
    private JRadioButton maleRadio, femaleRadio;
    private ButtonGroup genderGroup;
    
    private JRadioButton civilRadio, cseRadio, electricalRadio, ecRadio, mechanicalRadio;
    private ButtonGroup deptGroup;

    // Output Area (i)
    private JTextArea outputArea;
    private static int studentCounter = 0; // Simple counter for ID generation
    
    // --- Constructor: Builds the GUI ---
    public StudentRegistrationForm() {
        super("New Student Registration Form");
        // Use a container panel for the entire content, then use BorderLayout for the frame
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        
        // 1. Create the main form panel (Left/Center)
        JPanel formPanel = createFormPanel(); 
        contentPane.add(formPanel, BorderLayout.WEST);
        
        // 2. Create the output area (Right)
        outputArea = new JTextArea(20, 30);
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createTitledBorder("Your Data is Below:"));
        
        contentPane.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        // Add padding to the whole content pane
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane); // Set the main container

        // Setup frame properties
        pack(); // Use pack() to size the window based on component preferred sizes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // --- Helper Method to Create the Main Form Layout ---
    private JPanel createFormPanel() {
        // Use GridBagLayout for flexible alignment and sizing
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.anchor = GridBagConstraints.WEST; // Anchor labels to the left

        // --- Row Counter ---
        int row = 0;

        // 1. Text Fields (First/Last Name, Email/Confirm Email)
        // Use the improved addField helper to ensure HORIZONTAL fill
        row = addField(panel, "Student First Name:", firstNameField = new JTextField(20), gbc, row);
        row = addField(panel, "Student Last Name:", lastNameField = new JTextField(20), gbc, row);
        row = addField(panel, "Email Address:", emailField = new JTextField(20), gbc, row);
        row = addField(panel, "Confirm Email Address:", confirmEmailField = new JTextField(20), gbc, row);
        
        // 2. Password Fields
        row = addField(panel, "Password:", passwordField = new JPasswordField(20), gbc, row);
        row = addField(panel, "Confirm Password:", confirmPasswordField = new JPasswordField(20), gbc, row);
        
        // 3. Date of Birth (DOB)
        JPanel dobPanel = createDobPanel();
        row = addComponent(panel, "Date of Birth:", dobPanel, gbc, row);
        
        // 4. Gender (Radio Buttons - Single Select)
        JPanel genderPanel = createGenderPanel();
        row = addComponent(panel, "", genderPanel, gbc, row); 

        // 5. Department (Radio Buttons - Single Select)
        JPanel deptPanel = createDepartmentPanel();
        row = addComponent(panel, "Department:", deptPanel, gbc, row);
        
        // 6. Buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2; // Span across two columns (label + field space)
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
        
        // --- Add Listeners ---
        submitButton.addActionListener(e -> validateAndSubmit());
        cancelButton.addActionListener(e -> dispose()); 
        
        return panel;
    }
    
    // Helper to add a label and a text/password field to the panel
    private int addField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        // Label (Column 0)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), gbc);
        
        // Field (Column 1) - **FIX**: Use HORIZONTAL fill to ensure the field stretches
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Key fix for text fields to be visible
        gbc.weightx = 1.0; // Give this column weight to take extra space
        panel.add(field, gbc);
        
        gbc.weightx = 0; // Reset weight
        gbc.fill = GridBagConstraints.NONE;
        return row + 1;
    }
    
    // Helper to add a label and a compound component (like radio buttons or DOB)
    private int addComponent(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        if (!labelText.isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel(labelText), gbc);
        }
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(component, gbc);
        return row + 1;
    }

    // Creates the Date of Birth panel
    private JPanel createDobPanel() {
        JPanel dobPanel = new JPanel();
        
        // Year Combo Box (Restricted to ensure age check passes minimum 16 years)
        int currentYear = LocalDate.now().getYear();
        Integer[] years = new Integer[currentYear - 1950 + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = currentYear - i;
        }
        yearCombo = new JComboBox<>(years);
        
        // Month Combo Box
        Integer[] months = new Integer[12];
        for (int i = 0; i < 12; i++) { months[i] = i + 1; }
        monthCombo = new JComboBox<>(months);
        
        // Day Combo Box (Pre-populated 1-31)
        Integer[] days = new Integer[31];
        for (int i = 0; i < 31; i++) { days[i] = i + 1; }
        dayCombo = new JComboBox<>(days);

        // Set initial selection to a valid date
        yearCombo.setSelectedItem(currentYear - 16); 
        monthCombo.setSelectedItem(1);
        dayCombo.setSelectedItem(1);
        
        dobPanel.add(new JLabel("Select Year:"));
        dobPanel.add(yearCombo);
        dobPanel.add(new JLabel("Select Month:"));
        dobPanel.add(monthCombo);
        dobPanel.add(new JLabel("Select Day:"));
        dobPanel.add(dayCombo);
        return dobPanel;
    }
    
    // Creates the Gender radio button panel
    private JPanel createGenderPanel() {
        JPanel genderPanel = new JPanel();
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        return genderPanel;
    }
    
    // Creates the Department radio button panel
    private JPanel createDepartmentPanel() {
        JPanel deptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        civilRadio = new JRadioButton("Civil");
        cseRadio = new JRadioButton("Computer Science and Engineering");
        electricalRadio = new JRadioButton("Electrical");
        ecRadio = new JRadioButton("Electronics and Communication");
        mechanicalRadio = new JRadioButton("Mechanical");
        
        deptGroup = new ButtonGroup();
        deptGroup.add(civilRadio);
        deptGroup.add(cseRadio);
        deptGroup.add(electricalRadio);
        deptGroup.add(ecRadio);
        deptGroup.add(mechanicalRadio);
        
        deptPanel.add(civilRadio);
        deptPanel.add(cseRadio);
        deptPanel.add(electricalRadio);
        deptPanel.add(ecRadio);
        deptPanel.add(mechanicalRadio);
        return deptPanel;
    }

    // --- Core Logic: Validation and Submission ---
    private void validateAndSubmit() {
        StringBuilder errorSummary = new StringBuilder();
        
        // 1. Trim all fields and check for required fields (ii)
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String confirmEmail = confirmEmailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (firstName.isEmpty()) errorSummary.append("First Name is required.\n");
        if (lastName.isEmpty()) errorSummary.append("Last Name is required.\n");
        if (email.isEmpty()) errorSummary.append("Email is required.\n");
        
        // 2. Email Matching and Format Check (ii)
        if (email.isEmpty() && !confirmEmail.isEmpty() || !email.equals(confirmEmail)) {
            errorSummary.append("Email addresses must match.\n");
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
             errorSummary.append("Email format is invalid (e.g., must contain @ and .).\n");
        }
        
        // 3. Password Matching and Strength Check (ii)
        if (password.isEmpty() && !confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            errorSummary.append("Passwords must match.\n");
        }
        if (!password.isEmpty() && (password.length() < 8 || password.length() > 20)) { // 8 - 20 chars
            errorSummary.append("Password must be between 8 and 20 characters.\n");
        }
        if (!password.isEmpty() && !isStrongPassword(password)) { // Letter and digit check
            errorSummary.append("Password must contain at least one letter and one digit.\n");
        }
        
        // 4. Gender and Department Selection (ii)
        String gender = getSelectedGender();
        if (gender == null) errorSummary.append("Gender selection is required.\n");
        
        String department = getSelectedDepartment();
        if (department == null) errorSummary.append("Department selection is required.\n");

        // 5. Age Check (16 years inclusive) (ii)
        LocalDate dob = getSelectedDob();
        if (dob == null) {
            errorSummary.append("Valid Date of Birth selection is required.\n");
        } else {
            Period age = Period.between(dob, LocalDate.now());
            if (age.getYears() < 16) {
                errorSummary.append("Student must be at least 16 years old to register.\n");
            }
        }
        
        // --- Submission Logic (iii) ---
        if (errorSummary.length() > 0) {
            // Invalid: Show summary dialog.
            // NOTE: Inline errors are implemented by highlighting/border changes, but a simple dialog is used here.
            JOptionPane.showMessageDialog(this, errorSummary.toString(), "Validation Errors", JOptionPane.ERROR_MESSAGE);
        } else {
            // Valid: Generate ID, format record, display, and save to CSV.
            String record = generateRecord(firstName, lastName, gender, department, dob, email);
            outputArea.append(record + "\n---\n"); // Append to text area
            appendRecordToCsv(record); // Save to file
            JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields after successful submission (Good practice)
            clearFields();
        }
    }
    
    // Helper to clear all input fields
    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        confirmEmailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        genderGroup.clearSelection();
        deptGroup.clearSelection();
        // Reset DOB to default
        int currentYear = LocalDate.now().getYear();
        yearCombo.setSelectedItem(currentYear - 16); 
    }

    // [Rest of the Helper Methods remain the same as the previous response]

    private String getSelectedGender() {
        if (maleRadio.isSelected()) return "M";
        if (femaleRadio.isSelected()) return "F";
        return null; 
    }
    
    private String getSelectedDepartment() {
        if (civilRadio.isSelected()) return "Civil";
        if (cseRadio.isSelected()) return "CSE";
        if (electricalRadio.isSelected()) return "Electrical";
        if (ecRadio.isSelected()) return "E&C";
        if (mechanicalRadio.isSelected()) return "Mechanical";
        return null; 
    }
    
    private LocalDate getSelectedDob() {
        try {
            int year = (Integer) yearCombo.getSelectedItem();
            int month = (Integer) monthCombo.getSelectedItem();
            int day = (Integer) dayCombo.getSelectedItem();
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    private boolean isStrongPassword(String password) {
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        return hasLetter && hasDigit;
    }

    private String generateRecord(String firstName, String lastName, String gender, String department, LocalDate dob, String email) {
        int currentYear = LocalDate.now().getYear();
        studentCounter++; 
        String studentID = String.format("%d-%05d", currentYear, studentCounter); 
        
        String formattedRecord = String.format(
            "ID: %s | %s %s | %s | %s | %s | %s",
            studentID,
            firstName,
            lastName,
            gender,
            department,
            dob.toString(),
            email
        );
        return formattedRecord;
    }
    
    private void appendRecordToCsv(String record) {
        try (FileWriter fw = new FileWriter("students.csv", true)) {
            String[] parts = record.split(" \\| ");
            if (parts.length >= 6) {
                // Ensure name is combined or split correctly for CSV
                String name = parts[1]; 
                
                String csvLine = parts[0].replace("ID: ", "") + "," + 
                                 name + "," + 
                                 parts[2] + "," + 
                                 parts[3] + "," + 
                                 parts[4] + "," + 
                                 parts[5] + "\n"; 
                fw.write(csvLine);
            }
        } catch (IOException e) {
            System.err.println("Error writing to students.csv: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentRegistrationForm());
    }
}