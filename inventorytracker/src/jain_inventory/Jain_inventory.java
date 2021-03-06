package jain_inventory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Jain_inventory extends Application {
   
    public static boolean loggedIn;
    public final static String MASTER_PASSWORD = "abc123";
    
    public static Stage mainStage;
    public final static String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
    public final static String ALPHANUM_REGEX = "[A-Z][A-Z][0-9][0-9][0-9]";
    public static Pattern idPattern = Pattern.compile(ALPHANUM_REGEX);
    public final static String PHONE_REGEX = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]";
    public static Pattern phonePattern = Pattern.compile(PHONE_REGEX);
    
    public static String RECEIVER = "vanshjain1224@gmail.com";
    private final static String SENDER = "duplicate1224@gmail.com";
    private final static String PASSWORD = "vanshjain";
    
    private static Properties PROPERTIES = new Properties();
    private final static Authenticator AUTHENTICATOR = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER, PASSWORD);
            }
    };
    
    public static Connection connection;
    
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    
    @Override
    public void start(Stage stage) throws Exception {
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        connection = DriverManager.getConnection("jdbc:derby:jainTextiles;create=true;user=app;password=app", "app", "app");
         
        try {
            PreparedStatement st = connection.prepareStatement("CREATE TABLE APP.INVENTORY(PRODUCT_ID char(5), NAME varchar(255), QUANTITY_METRES int, PRICE_METRES int)");
            st.executeUpdate();
            st = connection.prepareStatement("CREATE TABLE APP.ACCOUNTS(EMAIL varchar(255), PASSWORD varchar(255))");
            st.executeUpdate();
            st = connection.prepareStatement("CREATE TABLE APP.CUSTOMERS(PERSON_ID char(5), NAME varchar(255), ADDRESS varchar(255), PHONE_NUMBER varchar(255))");
            st.executeUpdate();
            st = connection.prepareStatement("CREATE TABLE APP.SUPPLIERS(PERSON_ID char(5), NAME varchar(255), ADDRESS varchar(255), PHONE_NUMBER varchar(255))");            
            st.executeUpdate();
            st = connection.prepareStatement("CREATE TABLE APP.PURCHASE_ORDERS(ORDER_ID char(5), PRODUCT_ID char(5), PERSON_ID char(5), QUANTITY_METRES int, DELIVERY_DATE date)");
            st.executeUpdate();
            st = connection.prepareStatement("CREATE TABLE APP.SALES_ORDERS(ORDER_ID char(5), PRODUCT_ID char(5), PERSON_ID char(5), QUANTITY_METRES int, DELIVERY_DATE date)");
            st.executeUpdate();
        } catch(SQLException e) {System.out.println(e);}
        
        PROPERTIES.put("mail.transport.protocol", "smtp");
        PROPERTIES.put("mail.smtp.user", SENDER);
        PROPERTIES.put("mail.smtp.host", "smtp.gmail.com");
        PROPERTIES.put("mail.smtp.port", "587");
        PROPERTIES.put("mail.smtp.starttls.enable","true");
        PROPERTIES.put("mail.smtp.auth", "true");
        
        mainStage = stage;
        mainStage.setResizable(false);
        mainStage.initStyle(StageStyle.UNIFIED);
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void infoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public static boolean confirmAlert(String title, String header, String content) {
        boolean confirm = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            confirm = true;
        }
        
        return confirm;
    }
    
    public static void sendEmail(String subject, String body) throws AddressException{
        Session session = Session.getInstance(PROPERTIES, AUTHENTICATOR);
        Message message = new MimeMessage(session); 
        
        try{ 
            message.setFrom(new InternetAddress(SENDER));  
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(RECEIVER));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("message sent");
            Jain_inventory.infoAlert("Email", null, "An email has been sent to your account.");
        } catch (MessagingException exc) {System.out.println(exc);}
    }
    
    public static boolean isInt(String test) {
        try {
            int n = Integer.parseInt(test);
            return (n > 0);
        } catch(NumberFormatException exc) {
            return false;
        }
    }
    
    public static boolean isText(String test) {
        return (!test.trim().isEmpty());
    }
    
    public static String toStringDate(LocalDate date) {
        String strDate = null;
        if (date != null) {
            strDate = DATE_FORMAT.format(date);
        }
        
        return strDate;
    }
    
    public static LocalDate fromStringDate(String strDate) {
        LocalDate date = null;
        if (strDate != null && !strDate.trim().isEmpty()) {
            date = LocalDate.parse(strDate, DATE_FORMAT);
        }
        
        return date;
    }
    
    public static void exitApplication() {
        if (confirmAlert("Exit Application", null, "Are you sure you want to exit?")) {System.exit(1);}
    }
}