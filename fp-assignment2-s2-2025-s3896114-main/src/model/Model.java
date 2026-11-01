package model;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

public class Model {
    private static Model instance;
    private UserDao userDao = new UserDaoImpl() ;
    private ProjectDao projectDao = new ProjectDaoImpl() ;
    private RegistrationDao registrationDao = new RegistrationDaoImpl() ;
    private User currentUser;
    private ObservableList<CartItem> cart = FXCollections.observableArrayList();

    private Model(){

    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public void setup() throws SQLException {
        this.userDao.setup();
        this.projectDao.setup();
        this.registrationDao.setup();
    }

    public UserDao getUserDao() {
        return this.userDao;
    }

    public ProjectDao getProjectDao() {
        return this.projectDao;
    }

    public RegistrationDao getRegistrationDao() {
        return this.registrationDao;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public ObservableList<CartItem> getCart() {
        return this.cart;
    }

    public void clearCart() {
        this.cart.clear();
    }


}
