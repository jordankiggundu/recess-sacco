import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Server {

    private ServerSocket serverSocket;
    private boolean isRunning;
    Object member_id = 0;
    public static void main(String[] args) {
        Server server = new Server();
        server.start(1234);
        System.out.println("Sacco Server up and running");
    }

    private void start(int port) {
        isRunning = true;
        new Thread(() -> run(port)).start();
    }

    private void run(int port) {
        try {
            serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket client = serverSocket.accept();
                handleClient(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        new Thread(() -> {
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            PrintWriter writer;
            try {
                 out = new ObjectOutputStream(client.getOutputStream());
                 in = new ObjectInputStream(client.getInputStream());
                writer = new PrintWriter(client.getOutputStream(), true);

                String input;
                while ((input = (String) in.readObject()) != null) {
                    // handle the client command according to your requirements
                    String[] parts = input.split(" ");
                    String command = parts[0];
                    writer.println("Attempting to:" + command);
                    //handle client command
                    if (command.equalsIgnoreCase("exit")) {
                        writer.println("Good bye");
                        out.close();
                        in.close();
                        client.close();
                        serverSocket.close();
                        break;

                    }
                    else if (command.equalsIgnoreCase("login")) {
                        String username = parts[1];
                        String password = parts[2];
                        Services services = new Services();
                        member_id = services.Login(username, password);
                        System.out.println(member_id);
                        if ((int) member_id > 0) {

                            writer.println("welcome");
                        } else {
                            writer.println("failed");
                        }
                    }
                    else if (command.equalsIgnoreCase("signup")) {
                        String mem_no = parts[1];
                        String phon_no = parts[2];
                        Services services = new Services();
                        String givePass = services.signup(mem_no, phon_no);

                        if (!(Objects.equals(givePass, "false"))) {

                            writer.println("Your password:" + givePass); //send response to client
                        } else {
                            writer.println("Failed, Try agian after 24 hrs");
                        }
                    }
                    else if (command.equalsIgnoreCase("deposit")) {
                        int amount = Integer.parseInt(parts[1]);
                        String receipt_no = parts[3];
                        Services services = new Services();
                        boolean deposited = services.deposit((Integer) member_id, amount, receipt_no);
                        if (deposited) {
                            writer.println("Deposited successfully");
                        } else {
                            writer.println("Deposit failed");
                        }
                    }
                    else if (command.equalsIgnoreCase("requestLoan")) {
                        int amount = Integer.parseInt(parts[1]);
                        String period = parts[2];
                        Services services = new Services();
                        String loan_id = services.requestLoan((Integer) member_id, amount, Integer.parseInt(period));
                        if (!(Objects.equals(loan_id, "false"))) {
                            writer.println("Your loan Application number: "+loan_id);
                        } else {
                            writer.println("Request failed");
                        }
                    }
                    else if (command.equalsIgnoreCase("LoanRequestStatus")) {
                        String Loan_no = parts[1];
                        Services services = new Services();
                        String status = services.LoanStatus(Loan_no);
                        if (!(Objects.equals(status, "false"))) {
                            writer.println("Your loan "+Loan_no +" has status: "+status);
                        } else {
                            writer.println("Failed, No loans found");
                        }
                    }
                    else if (command.equalsIgnoreCase("CheckStatement")) {
                        String start_date = parts[1];
                        String end_date = parts[2];
                        Services services = new Services();
                        List<String> statements = Collections.singletonList(services.CheckStatement(start_date, end_date).toString());
//                        for (String statement : statements) {
                            writer.println(statements);
                    }
                }

            } catch (EOFException e) {
                // if the end of the stream is reached
                System.out.println("Client has closed the connection.");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    in.close();
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        isRunning = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
