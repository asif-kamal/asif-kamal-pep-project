package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    public AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account addUser(Account account) {
        return accountDAO.insertUser(account);
    }

    public Account userLogin(String username, String password) {
        return accountDAO.postUserCredentials(username, password);  
    }
    
    
}
