package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import junit.framework.TestCase;
import org.junit.Before;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest extends TestCase {

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        User user = new User(1L, "Peter", "thisIsHashed", null);
        Item item = new Item(1L, "testItem", new BigDecimal("0.99"), "desc");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        cart.addItem(item);
        user.setCart(cart);

        UserOrder order = new UserOrder(1L, null, user, new BigDecimal("0.00"));
        List<UserOrder> orders = Stream.of(order).collect(Collectors.toList());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("Peter")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);
    }

    public void testSubmit() {
        ResponseEntity<UserOrder> response = orderController.submit("Peter");
        ResponseEntity<UserOrder> noUser = orderController.submit("Samantha");

        assertNotNull(response);
        assertNotNull(noUser);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(404, noUser.getStatusCodeValue());
    }

    public void testGetOrdersForUser() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Peter");

        assertNotNull(response);

        assertEquals(200, response.getStatusCodeValue());
    }
}