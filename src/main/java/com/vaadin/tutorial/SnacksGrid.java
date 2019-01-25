package com.vaadin.tutorial;

import com.vaadin.flow.component.grid.Grid;

import java.util.LinkedList;
import java.util.List;

public class SnacksGrid extends Grid<SnackOrder> {
  List<SnackOrder> orders = new LinkedList<>();

  public SnacksGrid() {
    super(SnackOrder.class);
  }

  public void addSnack(SnackOrder snackOrder) {
    orders.add(snackOrder);
    setItems(orders);
  }
}
