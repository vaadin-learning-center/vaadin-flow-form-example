package com.vaadin.tutorial;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SnackOrder {
  @NotEmpty(message = "Please enter your name")
  private String name = "";
  @NotNull
  @NotEmpty(message = "Please select a snack")
  private String snack = "";

  @Min(value = 1, message = "Quantity must be 1 or more")
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
