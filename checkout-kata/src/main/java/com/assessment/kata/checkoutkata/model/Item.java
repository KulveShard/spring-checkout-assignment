package com.assessment.kata.checkoutkata.model;

public enum Item {
  APPLE("apple", "Apple"),
  BANANA("banana", "Banana"),
  PEACH("peach", "Peach"),
  KIWI("kiwi", "Kiwi");

  private final String key;
  private final String displayName;

  Item(String key, String displayName) {
    this.key = key;
    this.displayName = displayName;
  }

  public String getKey() {
    return key;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static Item fromKey(String key) {
    for (Item item : Item.values()) {
      if (item.key.equalsIgnoreCase(key)) {
        return item;
      }
    }
    throw new IllegalArgumentException("No item found with key: " + key);
  }
}