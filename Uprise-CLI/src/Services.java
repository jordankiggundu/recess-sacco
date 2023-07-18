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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean Login(String username, String password) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM accounts");
            while (resultSet.next()) {
                String name = resultSet.getString("username");
                String pass = resultSet.getString("password");
                if(Objects.equals(name, username) && Objects.equals(pass, password)) {
                    System.out.println(username + " logged in");
                    return true;
                }else {
                    return false;
                }
            }
            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String checkMembership(String mem_no, String phone_no) {
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

//    public boolean signUp(String username, String passwd){
//        try{
//            //Prepare the INSERT statement
//            String insertQuery = "INSERT INTO members (username, password) VALUES (?, ?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
//
//            // Insert data into the table using prepared statement
//            preparedStatement.setString(1, username);
//            preparedStatement.setString(2, passwd);
//            preparedStatement.executeUpdate();
//            preparedStatement.close();
//            System.out.println(username+" signed up");
//            connection.close();
//            return true;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//
//    }

}
