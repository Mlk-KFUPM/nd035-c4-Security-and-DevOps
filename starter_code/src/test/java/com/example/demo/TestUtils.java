package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, toInject);
            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        Cart cart = new Cart();
        user.setCart(cart);
        return user;
    }
}
