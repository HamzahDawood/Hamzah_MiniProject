import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    private static final Map<String, User> userDatabase = new HashMap<>();  // Simulating a database for users

    public static void main(String[] args) {
        showSignupForm();  // Start with the signup form
    }


    static class User {
        String username;
        String hashedPassword;
        String name;
        String regNumber;

        User(String username, String hashedPassword, String name, String regNumber) {
            this.username = username;
            this.hashedPassword = hashedPassword;
            this.name = name;
            this.regNumber = regNumber;
        }
    }


    public static void showSignupForm() {
        JFrame signupFrame = new JFrame("Sign-up Page");
        signupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        signupFrame.setSize(500, 500);
        signupFrame.setLayout(new GridBagLayout());
        signupFrame.getContentPane().setBackground(new Color(160, 32, 240));


        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 21));
        titleLabel.setForeground(new Color(255, 255, 0));

        JTextField usernameField = createStyledTextField("Enter username");
        JPasswordField passwordField = createStyledPasswordField("Enter password");
        JTextField nameField = createStyledTextField("Enter full name");
        JTextField regNumberField = createStyledTextField("Enter reg number");

        JButton generatePasswordButton = createStyledButton("Generate Password", new Color(100, 100, 100));
        JButton signupButton = createStyledButton("SIGN UP", new Color(34, 83, 120));
        JButton cancelButton = createStyledButton("CANCEL", new Color(150, 150, 150));

        JLabel messageLabel = new JLabel();
        messageLabel.setForeground(Color.RED);

        // Layout manager for neat alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signupFrame.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        signupFrame.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        signupFrame.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        signupFrame.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        signupFrame.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        signupFrame.add(generatePasswordButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        signupFrame.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        signupFrame.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        signupFrame.add(new JLabel("Reg Number:"), gbc);

        gbc.gridx = 1;
        signupFrame.add(regNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        signupFrame.add(signupButton, gbc);

        gbc.gridx = 1;
        signupFrame.add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        signupFrame.add(messageLabel, gbc);


        signupButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String regNumber = regNumberField.getText();

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || regNumber.isEmpty()) {
                messageLabel.setText("Please fill in all fields.");
            } else if (userDatabase.containsKey(username)) {
                messageLabel.setText("Username already exists.");
            } else if (isWeakPassword(password)) {
                messageLabel.setText("Password is too weak. ");
            } else {
                // Hash the password and store it
                String hashedPassword = hashPassword(password);
                userDatabase.put(username, new User(username, hashedPassword, name, regNumber));
                messageLabel.setText("Signup complete!");
                Timer timer = new Timer(2000, event -> {
                    signupFrame.dispose();
                    showLoginForm();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        cancelButton.addActionListener(e -> {
            signupFrame.dispose();
            showLoginForm();
        });


        generatePasswordButton.addActionListener(e -> {
            String generatedPassword = generateStrongPassword();
            passwordField.setText(generatedPassword);
            messageLabel.setText("Suggested Password: " + generatedPassword);
        });

        signupFrame.setLocationRelativeTo(null);  // Center the frame
        signupFrame.setVisible(true);
    }


    public static void showLoginForm() {
        JFrame loginFrame = new JFrame("Login Page");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 250);
        loginFrame.setLayout(new GridBagLayout());
        loginFrame.getContentPane().setBackground(new Color(160, 32, 240));


        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 50, 50));

        JTextField usernameField = createStyledTextField("Enter username");
        JPasswordField passwordField = createStyledPasswordField("Enter password");

        JButton loginButton = createStyledButton("LOGIN", new Color(34, 83, 120));
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginFrame.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        loginFrame.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        loginFrame.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        loginFrame.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginFrame.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        loginFrame.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginFrame.add(errorLabel, gbc);


        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = userDatabase.get(username);
            if (user != null && verifyPassword(password, user.hashedPassword)) {
                JFrame welcomeFrame = new JFrame("Welcome");
                welcomeFrame.setSize(400, 150);
                welcomeFrame.setLayout(new FlowLayout());
                welcomeFrame.add(new JLabel(
                        "Welcome, " + user.name + "   |   Reg Number: " + user.regNumber
                ));
                welcomeFrame.setLocationRelativeTo(null);
                welcomeFrame.setVisible(true);
                loginFrame.dispose();
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        });

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static boolean verifyPassword(String enteredPassword, String storedHashedPassword) {
        String hashedEnteredPassword = hashPassword(enteredPassword);
        return hashedEnteredPassword.equals(storedHashedPassword);
    }


    private static String generateStrongPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }


    private static boolean isWeakPassword(String password) {
        if (password.length() < 8) {
            return true;
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find() ||  // Missing uppercase
                !Pattern.compile("[a-z]").matcher(password).find() ||  // Missing lowercase
                !Pattern.compile("[0-9]").matcher(password).find() ||  // Missing digit
                !Pattern.compile("[@#$%!^&*()]").matcher(password).find()) {  // Missing special character
            return true;
        }

        String[] commonWeakPasswords = {"123456", "password", "123456789", "qwerty", "abc123", "letmein"};
        for (String weakPassword : commonWeakPasswords) {
            if (password.equalsIgnoreCase(weakPassword)) {
                return true;
            }
        }

        return false;
    }


    private static JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        return textField;
    }


    private static JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        return passwordField;
    }


    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }
}
