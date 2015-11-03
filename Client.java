
//Client sends the data to the server and gets result back from the server
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame implements ActionListener {

    private int numOfYears;
    //Text field for receiving annual interest rate,number of years, loan amount
    private JTextField jtfAnnualInterestRate = new JTextField();
    private JTextField jtfNumOfYears = new JTextField();
    private JTextField jtfLoanAmount = new JTextField();
    private JButton jbtSubmit = new JButton("Submit");
    //Text area for displaying contents
    private JTextArea jta = new JTextArea();
    //IO streams
    DataOutputStream outputToServer;
    DataInputStream inputFromServer;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(3, 1));
        p1.add(new JLabel("Annual Interest Rate"));
        p1.add(new JLabel("Number Of Years"));
        p1.add(new JLabel("Loan Amount"));

        Panel p2 = new Panel();
        p2.setLayout(new GridLayout(3, 1));
        p2.add(jtfAnnualInterestRate);
        p2.add(jtfNumOfYears);
        p2.add(jtfLoanAmount);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(p1, BorderLayout.WEST);
        p.add(p2, BorderLayout.CENTER);
        p.add(jbtSubmit, BorderLayout.EAST);

        jtfAnnualInterestRate.setHorizontalAlignment(JTextField.RIGHT);
        jtfNumOfYears.setHorizontalAlignment(JTextField.RIGHT);
        jtfLoanAmount.setHorizontalAlignment(JTextField.RIGHT);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(p, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(jta), BorderLayout.CENTER);

        jbtSubmit.addActionListener(this); //Register listener

        setTitle("Client");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {

            //Create a socket to connect to the server
            Socket connectToServer = new Socket("localhost", 4440);

            //Create an input stream to receive data from the server
            inputFromServer = new DataInputStream(connectToServer.getInputStream());

            //Create an output stream to send data to the server
            outputToServer = new DataOutputStream(connectToServer.getOutputStream());

        } catch (IOException ex) {
            jta.append(ex.toString() + '\n');
        }
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (e.getSource() instanceof JButton) {
            try {

                //Get the annual interest rate from the text field
                double annualInterestRate =
                        Double.parseDouble(jtfAnnualInterestRate.getText().trim());

                //Get the number of years from the text field
                try {
                    numOfYears = Integer.parseInt(jtfNumOfYears.getText());
                } catch (Exception e2) {
                    System.out.println("You must enter an integer value");
                }


                //Get the loan amount from the text field
                double loanAmount =
                        Double.parseDouble(jtfLoanAmount.getText().trim());

                //Send the annual interest rate to the server
                outputToServer.writeDouble(annualInterestRate);

                //Send the number of years to the server
                outputToServer.writeInt(numOfYears);

                //Send the loan amount to the server
                outputToServer.writeDouble(loanAmount);

                outputToServer.flush();

                //Display to the text area
                jta.append("Annual Interest Rate: " + annualInterestRate + "\n");
                jta.append("Number Of Years: " + numOfYears + "\n");
                jta.append("Loan Amount: " + loanAmount + "\n");
                

                //Get monthly payment from the server
                //Get total payment from the server
                jta.append(inputFromServer.readUTF());


            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}
