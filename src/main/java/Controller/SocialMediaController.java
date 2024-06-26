package Controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    /**
     * 
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postAccountHandler);
        app.post("/login", this::userLoginHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getAllMessagesForUserHandler);
        app.get("/messages/{message_id}", this::getMessageByMessageIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageTextHandler);
        //app.start(8080);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

    private void postAccountHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account accountAdded = accountService.addUser(account);
        
        if(accountAdded != null) {
            ctx.json(mapper.writeValueAsString(accountAdded));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    private void getAllMessagesHandler (Context ctx) {
        List<Message> messages = messageService.getAllMessages();
        ctx.json(messages);
    }

    private void postMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper objm = new ObjectMapper();
        Message message = objm.readValue(ctx.body(), Message.class);
        Message addedMessage = messageService.addMessage(message);

        if(addedMessage != null) {
            ctx.json(objm.writeValueAsString(addedMessage));
            ctx.status(200);
        } else {
            ctx.status(400);
        }

    }

    private void getAllMessagesForUserHandler(Context ctx) {
        String account_id = ctx.pathParam("account_id");
        List<Message> messages = messageService.getMessagesByAccountID(Integer.parseInt(account_id));
        ctx.json(messages);
    }

    private void getMessageByMessageIdHandler(Context ctx) {
        String message_id = ctx.pathParam("message_id");
        try {
            int messageId = Integer.parseInt(message_id);
            Message message = messageService.getMessageByMessageID(messageId);
            if (message != null) {
                ctx.json(message);
               
            } 
            ctx.status(200);
        } catch (Exception e) {
            ctx.status(500).result("Internal Server Error: " + e.getMessage());
            e.printStackTrace(); // Log the exception for debugging purposes
        }
    }

    private void deleteMessageByIdHandler(Context ctx) {
        String message_id = ctx.pathParam("message_id");
        Message deletedMessage = messageService.deleteMessageByID(Integer.parseInt(message_id));

        if (deletedMessage != null) {
            ctx.json(deletedMessage);
            ctx.status(200);
        } else {
            ctx.status(200);
        }
    }

    private void updateMessageTextHandler(Context ctx) throws SQLException {
        String messageIdStr = ctx.pathParam("message_id");
        String messageText;
        
        try {
            messageText = ctx.bodyAsClass(Map.class).get("message_text").toString();
        } catch (Exception e) {
            ctx.status(400).result("");
            return;
        }
    
        int messageId;
    
        try {
            messageId = Integer.parseInt(messageIdStr);
        } catch (NumberFormatException e) {
            ctx.status(400).result("");
            return;
        }
    
        try {
            Message message = messageService.updateMessageText(messageId, messageText);
            if (message != null) {
                ctx.json(message);
                ctx.status(200);
            } else {
                ctx.status(400).result("");
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("");
        }
    }
    
    
    
    
    
    
    
    

    private void userLoginHandler(Context ctx) throws JsonMappingException, JsonProcessingException {
        
            // Parse the request body JSON into an Account object
            ObjectMapper mapper = new ObjectMapper();
            Account account = mapper.readValue(ctx.body(), Account.class);
    
            // Extract username and password from the Account object
            String username = account.getUsername();
            String password = account.getPassword();
    
            // Perform login validation
            Account loggedInUser = accountService.userLogin(username, password);
    
            // Check if login was successful
            if (loggedInUser != null) {
                ctx.json(loggedInUser);
                ctx.status(200); // Successful login, return account details
            } else {
                ctx.status(401); // Unauthorized, invalid credentials
            }
        
    }

}