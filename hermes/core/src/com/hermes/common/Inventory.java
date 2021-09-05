package com.hermes.common;

import com.badlogic.gdx.utils.Logger;

import java.util.ArrayList;

public class Inventory {

    private static final Logger log = new Logger(Inventory.class.getName(), Logger.DEBUG);

    private ArrayList<Item> inventory = new ArrayList<>();

    public void addItem(Item item) {
        if (item.isHealth()) {

        } else {
            Item it = findItem(item.name);
            if (it != null) {
                it.amount++;
            } else {
                inventory.add(item);
            }
            log.debug("added item: " + item);
            log.debug("items in inventory: " + inventory.size());
        }

    }

    public Item findItem(String name) {
        for (Item item : inventory) {
            if (item.name.equals(name)) return item;
        }
        return null;
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public void removeItem(String name) {
        inventory.removeIf(item -> item.name.equals(name));
    }

    public void emptyInventory() {
        inventory.clear();
    }



}
