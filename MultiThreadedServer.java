
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.swing.*;

public class MultiThreadedServer extends JFrame {

    private JTextArea jta = new JTextArea();

    public static void main(String[] args) {
        try {
            new MultiThreadedServer().run();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public MultiThreadedServer() {

        //Place text area on the frame
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), BorderLayout.CENTER);

        setTitle("Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); //It is necessary to show the frame here!
    }

    private void run() throws IOException {

        //Create a server socket
        ServerSocket serverSocket = new ServerSocket(4440);
        jta.append("Server started at " + new Date() + '\n');

        //allows multiple connections
        while (true) {
            //Listen for a connection request
            Socket socket = serverSocket.accept();
            Thread thread = new ThreadClass(socket);
            thread.start();
        }
    }
    
    class ThreadClass extends Thread {
        // JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://localhost:3306/bankdatabase";
        //  Database username and password
        static final String USER = "root";
        static final String PASS = "";
        
        private Socket socket;

        public ThreadClass(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            //displays the IP address of the connected client
            InetAddress inetAddress = socket.getInetAddress();

            try {

                //Create data input and output streams
                DataInputStream inputFromClient = new DataInputStream(
                        socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(
                        socket.getOutputStream());

                if (!isRegisteredIP(inetAddress.getHostAddress())) {
                    outputToClient.writeUTF("Sorry. Your IP Address: " + inetAddress.getHostAddress()
                            + " is not registered. \n" + "Only registered client nodes may submit");
                } else {

                    //Receive annual interest rate from the client
                    double annualInterestRate = inputFromClient.readDouble();

                    //Receive number of years from the client
                    int numOfYears = inputFromClient.readInt();

                    //Receive loan amount from the client
                    double loanAmount = inputFromClient.readDouble();


                    //Obtain monthly interest rate
                    double monthlyInterestRate = annualInterestRate / 1200;


                    //Calculate total amount
                    double totalPayment = (loanAmount * annualInterestRate / 100 * numOfYears) + loanAmount;

                    //Calculate monthly amount
                    double monthlyPayment = totalPayment / (numOfYears * 12);

                    //Sends monthly amount back to client
                    //Sends total amount back to client
                    outputToClient.writeUTF("Hello " + inetAddress.getLocalHost().getHostName() + '\n' 
                    		+ "Your registered IP address in the database is " + inetAddress.getHostAddress() + '\n'
                    		+ "The Monthly Payment is " + monthlyPayment + '\n'
                            + "The Total Payment is " + totalPayment + '\n');
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

        private boolean isRegisteredIP(String ip) {

            boolean res = false;
            Connection conn = null;
            Statement stmt = null;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                stmt = conn.createStatement();
                String query = "SELECT IPAddress FROM registeredapplicants";
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        if (ip.equals(rs.getString("IPAddress"))) {
                            res = true;
                            break;
                        }
                    }
                }
                stmt.close();
                conn.close();
            } catch (SQLException | ClassNotFoundException e) {
                System.err.println(e);
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    System.err.println(e);
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
            return res;
        }
    }

}
