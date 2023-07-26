import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    static boolean loggedin;
    private boolean isExecuted;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    private void start() {
        try {
            socket = new Socket("", 1234);
            out = new ObjectOutputStream(socket.getOutputStream());
            BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            Thread readThread = new Thread(new ReadThread(reader,socket)); //read server responses
            readThread.start();
            handleUserInput(reader); //handle user input

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void executeOnce() {
        if (isExecuted) {
            return;
        } else {
            System.out.println("Please login");
            System.out.println("Enter command:");
            isExecuted = true;
        }
    }
    public synchronized void executeOnce2() {
        if (isExecuted) {
            return;
        } else {
            System.out.println("Provide member number and phone number");

            isExecuted = true;
        }
    }
    private void handleUserInput(BufferedReader reader) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                    executeOnce();
                    String command = scanner.nextLine();
                    loggedin = sendLogin(command,reader);
                    boolean done =true;
                    if (loggedin ){
                        done =false;
                        System.out.println("WELCOME TO UPRISE");
                        System.out.println("COMMANDS: deposit, CheckStatement, requestLoan, LoanRequestStatus, exit");
                        System.out.println("Enter command:");
                        String command2 = scanner.nextLine();
                        sendCommand(command2);
                    }else {
                       executeOnce2();
                       String command3 = scanner.nextLine();
                       signUp(command3);

                    }
            }
        } finally {
            out.close();
            socket.close();
        }

    }

    private void sendCommand(String command) {
        try {
            out.writeObject(command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean sendLogin(String command,BufferedReader reader) {
        try {
            out.writeObject(command);
            out.flush();
            String response = null;
            response = reader.readLine();

                if(response.equalsIgnoreCase("welcome")){
                loggedin = true;

                System.out.println("server Response:"+response);
                return true;
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }
    private void signUp(String command) {
        try {
            out.writeObject("signup "+command);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ReadThread implements Runnable {
        private final BufferedReader reader;
        Socket socket;
        public ReadThread(BufferedReader reader, Socket socket) {
            this.reader = reader;
            this.socket = socket;
        }

        @Override
        public void run() {
            String response;
            try {
                while ((response = reader.readLine()) != null) {
                   System.out.println("Server Response: "+response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
