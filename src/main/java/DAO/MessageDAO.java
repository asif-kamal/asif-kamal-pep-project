package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDAO {
    
    public List<Message> getAllMessages() {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM Message;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public List<Message> getMessageByAccountID(int account_id) {
        
        Connection conn = ConnectionUtil.getConnection();

        List<Message> messages = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Message WHERE posted_by = ?;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"),
                rs.getInt("posted_by"),
                rs.getString("message_text"),
                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public Message getMessageByMessageID(int message_id) {
        
        Connection conn = ConnectionUtil.getConnection();
        Message message = null;
        
        try {
            String sql = "SELECT * FROM Message WHERE message_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, message_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    public Message deleteMessageByID(int message_id) {
     
        Connection conn = ConnectionUtil.getConnection();
        Message message = null;

        try {
            String fetchSql = "SELECT * FROM Message WHERE message_id = ?;";
            PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
            fetchPs.setInt(1, message_id);
            ResultSet rs = fetchPs.executeQuery();

            if (rs.next()) {
                message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                );
            }
            if (message != null) {
                String deleteSql = "DELETE * FROM Message WHERE message_id = ?;";
                PreparedStatement deletePs = conn.prepareStatement(deleteSql);
                deletePs.setInt(1, message_id);
                deletePs.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    public Message insertMessage(Message message) {
        Connection conn = ConnectionUtil.getConnection();
    
        try {
            // Check if user exists
            String userCheckSql = "SELECT COUNT(*) FROM Account WHERE account_id = ?";
            PreparedStatement userCheckStatement = conn.prepareStatement(userCheckSql);
            userCheckStatement.setInt(1, message.getPosted_by());
            ResultSet userCheckResultSet = userCheckStatement.executeQuery();
    
            if (!userCheckResultSet.next() || userCheckResultSet.getInt(1) == 0) {
                return null; // User does not exist
            }
    
            // Check if the message text is valid
            if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
                return null; // Message text is invalid
            }
    
            // Insert new message
            String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
            preparedStatement.executeUpdate();
    
            // Retrieve the generated message_id
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1); // Get the generated message_id
                // Return the new message object with the generated ID
                return new Message(generatedId, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
            }
    
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Message patchMessageText(int message_id, String message_text) {
        Connection conn = ConnectionUtil.getConnection();
        Message message = null;
        
        try {
            // Check if the message text is valid
            if (message_text == null || message_text.isBlank() || message_text.length() > 255) {
                return null; // Message text is invalid
            }

            String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, message_text);
            ps.setInt(2, message_id);
            int rows_affected = ps.executeUpdate();

            if (rows_affected > 0) {
                String fetchSql = "SELECT * FROM Message WHERE message_id = ?;";
                PreparedStatement fetchPs = conn.prepareStatement(fetchSql);
                fetchPs.setInt(1, message_id);
                ResultSet rs = fetchPs.executeQuery();

                if (rs.next()) {
                    message = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }
    
}
