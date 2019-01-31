package com.vaadin.tutorial;

public class SnackOrder {
  private String name = "";
  private String snack = "";
  private Integer quantity = 1;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSnack() {
    return snack;
  }

  public void setSnack(String snack) {
    this.snack = snack;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }
}
