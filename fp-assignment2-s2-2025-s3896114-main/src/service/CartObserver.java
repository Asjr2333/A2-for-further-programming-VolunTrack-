package service;

import model.CartItem;
import java.util.List;

public interface CartObserver {
    // Called when the cart is updated
    void update(List<CartItem> cartItems);
}
