package com.hermes.common;

public class  Item {
    public enum ItemType {
        UNIQUE, COLLECTABLE, COIN, HEALTH;
        public boolean isHealth() {return this == HEALTH;}
    }

    public Item(ItemType type, String name, int amount) {
        this.type = type;
        this.name = name;
        this.amount = amount;
    }
    public Item(ItemType type, String name) {
        this(type, name, 1);
    }

    public ItemType type;
    public String name;
    public int amount;

    public boolean isHealth() {
        return type.isHealth();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (type != item.type) return false;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
