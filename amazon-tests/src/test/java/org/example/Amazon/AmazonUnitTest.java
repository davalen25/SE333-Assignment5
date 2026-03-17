package org.example.Amazon;

import org.example.Amazon.Amazon;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AmazonUnitTest {

    private ShoppingCart mockCart;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        mockCart = mock(ShoppingCart.class);
    }

    // ─── Specification-based ───────────────────────────────────────────────

    @Test
    @DisplayName("specification-based")
    void regularCost_calculatesCorrectTotal() {
        Item item = new Item(ItemType.OTHER, "Book", 2, 10.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        amazon = new Amazon(mockCart, List.of(new RegularCost()));
        assertEquals(20.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPrice_returnsZero_whenCartEmpty() {
        when(mockCart.getItems()).thenReturn(List.of());
        amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPrice_returnsFive_whenOneToThreeItems() {
        List<Item> items = List.of(
            new Item(ItemType.OTHER, "Book", 1, 5.0),
            new Item(ItemType.OTHER, "Pen", 1, 1.0)
        );
        when(mockCart.getItems()).thenReturn(items);
        amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));
        assertEquals(5.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPrice_returnsTwelvePointFive_whenFourToTenItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }
        when(mockCart.getItems()).thenReturn(items);
        amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));
        assertEquals(12.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPrice_returnsTwenty_whenMoreThanTenItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            items.add(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }
        when(mockCart.getItems()).thenReturn(items);
        amazon = new Amazon(mockCart, List.of(new DeliveryPrice()));
        assertEquals(20.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void extraCostForElectronics_returnsSevenFifty_whenHasElectronic() {
        Item item = new Item(ItemType.ELECTRONIC, "Laptop", 1, 500.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        amazon = new Amazon(mockCart, List.of(new ExtraCostForElectronics()));
        assertEquals(7.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void extraCostForElectronics_returnsZero_whenNoElectronics() {
        Item item = new Item(ItemType.OTHER, "Book", 1, 10.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        amazon = new Amazon(mockCart, List.of(new ExtraCostForElectronics()));
        assertEquals(0.0, amazon.calculate());
    }

    // ─── Structural-based ─────────────────────────────────────────────────

    @Test
    @DisplayName("structural-based")
    void amazon_calculate_aggregatesMultipleRules() {
        Item item = new Item(ItemType.ELECTRONIC, "Phone", 1, 100.0);
        when(mockCart.getItems()).thenReturn(List.of(item));
        amazon = new Amazon(mockCart, List.of(new RegularCost(), new ExtraCostForElectronics(), new DeliveryPrice()));
        // 100 + 7.5 + 5 = 112.5
        assertEquals(112.5, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void amazon_addToCart_callsCartAdd() {
        Item item = new Item(ItemType.OTHER, "Book", 1, 10.0);
        amazon = new Amazon(mockCart, List.of());
        amazon.addToCart(item);
        verify(mockCart, times(1)).add(item);
    }

    @Test
    @DisplayName("structural-based")
    void regularCost_returnsZero_whenCartEmpty() {
        when(mockCart.getItems()).thenReturn(List.of());
        amazon = new Amazon(mockCart, List.of(new RegularCost()));
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void regularCost_multipleItems_correctTotal() {
        List<Item> items = List.of(
            new Item(ItemType.OTHER, "Book", 3, 5.0),
            new Item(ItemType.OTHER, "Pen", 2, 2.0)
        );
        when(mockCart.getItems()).thenReturn(items);
        amazon = new Amazon(mockCart, List.of(new RegularCost()));
        assertEquals(19.0, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void amazon_calculate_returnsZero_whenNoRules() {
        when(mockCart.getItems()).thenReturn(List.of());
        amazon = new Amazon(mockCart, List.of());
        assertEquals(0.0, amazon.calculate());
    }
}
