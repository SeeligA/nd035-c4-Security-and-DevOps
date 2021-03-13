package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        Item item = new Item(1L, "testItem", new BigDecimal("0.99"), "desc");
        User user = new User(1L, "Peter", "thisIsHashed", new Cart());

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        cart.setTotal(new BigDecimal("0.00"));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("Peter")).thenReturn(user);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
    }

    @Test
    public void testAddToCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(10);
        request.setUsername("Peter");

        ResponseEntity<Cart> response = cartController.addTocart(request);
        request.setUsername("Samantha");
        ResponseEntity<Cart> noUser = cartController.removeFromcart(request);

        request.setUsername("Peter");
        request.setItemId(0);
        ResponseEntity<Cart> noItem = cartController.addTocart(request);

        assertNotNull(response);
        assertNotNull(noItem);
        assertNotNull(noUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(10, response.getBody().getItems().size());
        assertEquals(9.90, response.getBody().getTotal().doubleValue(), 0.001);

        assertEquals(404, noUser.getStatusCodeValue());
        assertEquals(404, noItem.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCart() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(10);
        request.setUsername("Peter");
        cartController.addTocart(request);

        request.setQuantity(9);
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        request.setUsername("Samantha");
        ResponseEntity<Cart> noUser = cartController.removeFromcart(request);

        request.setUsername("Peter");
        request.setItemId(0);
        ResponseEntity<Cart> noItem = cartController.removeFromcart(request);

        assertNotNull(response);
        assertNotNull(noItem);
        assertNotNull(noUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getItems().size());
        assertEquals(0.99, response.getBody().getTotal().doubleValue(), 0.001);

        assertEquals(404, noUser.getStatusCodeValue());
        assertEquals(404, noItem.getStatusCodeValue());
    }


    @Test
    @Ignore
    public void verify_that_item_number_and_total_not_negative() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(0);
        request.setUsername("Peter");
        ResponseEntity<Cart> addZeroResponse = cartController.addTocart(request);

        request.setQuantity(-1);
        ResponseEntity<Cart> addNegativeResponse = cartController.addTocart(request);

        request.setQuantity(10);
        ResponseEntity<Cart> deductToNegative = cartController.removeFromcart(request);

        request.setQuantity(-1);
        ResponseEntity<Cart> removeNegativeResponse = cartController.removeFromcart(request);

        assertEquals(400, addZeroResponse.getStatusCodeValue());
        assertEquals(400, addNegativeResponse.getStatusCodeValue());
        assertEquals(400, deductToNegative.getStatusCodeValue());
        assertEquals(400, removeNegativeResponse.getStatusCodeValue());
    }
}