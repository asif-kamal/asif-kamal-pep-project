package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    public MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public List<Message> getMessagesByAccountID(int account_id) {
        return messageDAO.getMessageByAccountID(account_id);
    }

    public Message getMessageByMessageID(int message_id) {
        return messageDAO.getMessageByMessageID(message_id);
    }

    public Message deleteMessageByID(int message_id) {
        return messageDAO.deleteMessageByID(message_id);
    }

    public Message addMessage(Message message) {
        return messageDAO.insertMessage(message);
    }

    public Message updateMessageText(int message_id, String message_text) {
        return messageDAO.patchMessageText(message_id, message_text);
    }
    
}
