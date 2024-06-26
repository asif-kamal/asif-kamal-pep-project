package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.Account;
import Util.ConnectionUtil;


public class AccountDAO {

    public Account insertUser(Account account) {
        Connection conn = ConnectionUtil.getConnection();
    
        try {
            // Check if username already exists
            String checkSql = "SELECT COUNT(*) FROM Account WHERE username = ?";
            PreparedStatement checkStatement = conn.prepareStatement(checkSql);
            checkStatement.setString(1, account.getUsername());
            ResultSet checkResultSet = checkStatement.executeQuery();
    
            if (checkResultSet.next() && checkResultSet.getInt(1) > 0) {
                return null; // Username already exists
            }
    
            // Validate username and password
            if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword() == null || account.getPassword().length() < 4) {
                return null; // Invalid username or password
            }
    
            // Insert new account
            String sql = "INSERT INTO Account (username, password) VALUES (?,?);";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();
    
            // Retrieve the generated account_id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1); // Get the generated account_id
                // Return the new account object with the generated ID
                return new Account(generatedId, account.getUsername(), account.getPassword());
        }
    
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }



    public Account postUserCredentials(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;

        // Validate input
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Invalid username or password");
            return null;
        }
    
        try {
            // Check if username already exists
            String checkSql = "SELECT account_id, username, password FROM Account WHERE username = ? AND password = ?;";
            PreparedStatement checkStatement = conn.prepareStatement(checkSql);
            checkStatement.setString(1, username);
            checkStatement.setString(2, password);
            ResultSet checkResultSet = checkStatement.executeQuery();
    
            if (checkResultSet.next()) {
                // Retrieve account details
                account = new Account(
                    checkResultSet.getInt("account_id"),
                    checkResultSet.getString("username"),
                    checkResultSet.getString("password")
                );
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return account;
    }
}