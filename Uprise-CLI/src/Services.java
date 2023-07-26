import java.sql.*;
import java.util.Objects;
import java.util.Random;

public class Services {
    private Connection connection;
    private Statement statement;
    public Services() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sacco", "root", "");
            statement = connection.createStatement();
            System.out.println("connected to Database");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int Login(String username, String password) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM accounts");
            ResultSetMetaData metadata = resultSet.getMetaData();
            int columnCount = metadata.getColumnCount();
            int member_id =0;
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metadata.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    if (columnName.equals("username") && columnValue.equals(username)) {
                        String pswd = resultSet.getString("password");
                        if (password.equals(pswd)) {
                            System.out.println(username + " logged in");
                            member_id = resultSet.getInt("id");
                            break;
                        }
                    }
                }
            }
            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
            return member_id;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String signup(String mem_no, String phone_no) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM members");
            while (resultSet.next()) {
                String mn = resultSet.getString("member_number");
                String pn = resultSet.getString("phone_number");
                String name = resultSet.getString("name");

                if(Objects.equals(mn, mem_no) && Objects.equals(pn, phone_no)) {
                    String insertQuery = "INSERT INTO accounts (username, password) VALUES (?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                    preparedStatement.setString(1, name);
                    Random rand = new Random();
                    int rand_int = rand.nextInt(1000);
                    preparedStatement.setString(2, "pass@"+rand_int);
                    return "pass@"+rand_int;
                }else {
                    return "false";
                }
            }
            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }

    public boolean deposit(int member_id,int amount, String receipt_no){
        try{
            //Prepare the INSERT statement
            String insertQuery = "INSERT INTO deposits (amount, receipt_number, member_id) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            //Get member_id
            // Insert data into the table using prepared statement
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, receipt_no);
            preparedStatement.setInt(3, member_id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Deposited successfully");
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public String requestLoan(int member_id,int amount,int period){
        try{
            //Prepare the INSERT statement
            String insertQuery = "INSERT INTO loans (loan_id,amount, payment_period, member_id, status) VALUES (?, ?, ?, ?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            //Generate loan number
            Random rand = new Random();
            int num = rand.nextInt(9000) + 1000;
            String loan_no = "LOAN"+num;
            // Insert data into the table using prepared statement
            preparedStatement.setString(1, loan_no);
            preparedStatement.setInt(2, amount);
            preparedStatement.setInt(3, period);
            preparedStatement.setInt(4, member_id);
            preparedStatement.setString(5, "pending");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            System.out.println("Request successfully");
            connection.close();
            return loan_no;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }

    public String LoanStatus(String loan_no) {
        try {
            String selectQuery = "SELECT * FROM loans WHERE loan_id = ? ";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, loan_no);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String status = resultSet.getString("status");
                return status;
            }
            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "false";
    }
}
