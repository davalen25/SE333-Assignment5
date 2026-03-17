package org.example.Amazon;

import org.example.Amazon.Amazon;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.example.Amazon.Database;
import org.example.Amazon.Item;
import org.example.Amazon.ShoppingCartAdaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonIntegrationTest {

    private Database database;
    private ShoppingCartAdaptor cart;
    private Amazon amazon;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.resetDatabase();
        cart = new ShoppingCartAdaptor(database);
    }

    // ─── Specification-based ───────────────────────────────────────────────

    @Test
    @DisplayName("specification-based")
    void calculate_regularCost_singleItem() {
        amazon = new Amazon(cart, List.of(new RegularCost()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 2, 10.0));
        assertEquals(20.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void calculate_deliveryPrice_emptyCart() {
        amazon = new Amazon(cart, List.of(new DeliveryPrice()));
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void calculate_deliveryPrice_oneToThreeItems() {
        amazon = new Amazon(cart, List.of(new DeliveryPrice()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 5.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 1, 1.0));
        assertEquals(5.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void calculate_extraCost_withElectronic() {
        amazon = new Amazon(cart, List.of(new ExtraCostForElectronics()));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Laptop", 1, 500.0));
        assertEquals(7.5, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void calculate_extraCost_withNoElectronic() {
        amazon = new Amazon(cart, List.of(new ExtraCostForElectronics()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 10.0));
        assertEquals(0.0, amazon.calculate());
    }

    @Test
    @DisplayName("specification-based")
    void calculate_allRules_electronicItem() {
        amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 100.0));
        // 100 + 5 + 7.5 = 112.5
        assertEquals(112.5, amazon.calculate());
    }

    // ─── Structural-based ─────────────────────────────────────────────────

    @Test
    @DisplayName("structural-based")
    void addToCart_itemPersistedInDatabase() {
        amazon = new Amazon(cart, List.of(new RegularCost()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 10.0));
        assertEquals(1, cart.getItems().size());
    }

    @Test
    @DisplayName("structural-based")
    void database_reset_clearsAllItems() {
        amazon = new Amazon(cart, List.of(new RegularCost()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 1, 10.0));
        database.resetDatabase();
        assertEquals(0, cart.getItems().size());
    }

    @Test
    @DisplayName("structural-based")
    void calculate_multipleItems_correctRegularCost() {
        amazon = new Amazon(cart, List.of(new RegularCost()));
        amazon.addToCart(new Item(ItemType.OTHER, "Book", 3, 5.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Pen", 2, 2.0));
        assertEquals(19.0, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void calculate_deliveryPrice_fourToTenItems() {
        amazon = new Amazon(cart, List.of(new DeliveryPrice()));
        for (int i = 0; i < 5; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }
        assertEquals(12.5, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void calculate_deliveryPrice_moreThanTenItems() {
        amazon = new Amazon(cart, List.of(new DeliveryPrice()));
        for (int i = 0; i < 11; i++) {
            amazon.addToCart(new Item(ItemType.OTHER, "Item" + i, 1, 1.0));
        }
        assertEquals(20.0, amazon.calculate());
    }

    @Test
    @DisplayName("structural-based")
    void calculate_mixedItems_allRules() {
        amazon = new Amazon(cart, List.of(new RegularCost(), new DeliveryPrice(), new ExtraCostForElectronics()));
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 200.0));
        amazon.addToCart(new Item(ItemType.OTHER, "Case", 1, 20.0));
        // 220 + 5 + 7.5 = 232.5
        assertEquals(232.5, amazon.calculate());
    }
}
