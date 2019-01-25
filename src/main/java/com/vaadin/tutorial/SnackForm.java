package com.vaadin.tutorial;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class SnackForm extends Div {

  class SnackEvent extends ComponentEvent<SnackForm> {
    private SnackOrder order;

    SnackEvent(SnackForm source, boolean fromClient, SnackOrder order) {
      super(source, fromClient);
      this.order = order;
    }

    SnackOrder getOrder() {
      return order;
    }
  }


  Registration addOrderListener(ComponentEventListener<SnackEvent> listener) {
    return addListener(SnackEvent.class, listener);
  }

  SnackForm() {
    setWidth("100%");
    Map<String, List<String>> snacks = new HashMap<String, List<String>>() {{
      put("Fruits", Arrays.asList("Banana", "Apple", "Orange", "Avocado"));
      put("Candy", Arrays.asList("Chocolate bar", "Gummy bears", "Granola bar"));
      put("Drinks", Arrays.asList("Soda", "Water", "Coffee", "Tea"));
    }};
    TextField name = new TextField("Name");
    TextField quantity = new TextField("Quantity");
    quantity.setPreventInvalidInput(true);
    ComboBox<String> snackType = new ComboBox<>("Type", snacks.keySet());
    ComboBox<String> snack = new ComboBox<>("Snack", new LinkedList<>());
    Button orderButton = new Button("Order");
    orderButton.setEnabled(false);
    orderButton.setThemeName("primary");
    Div errors = new Div();

    snack.setEnabled(false);
    snackType.addValueChangeListener(e -> {
      String type = e.getValue();
      snack.setEnabled(type != null && !type.isEmpty());
      if (type != null && !type.isEmpty()) {
        snack.setItems(snacks.get(type));
      }
    });

    Binder<SnackOrder> binder = new Binder<>(SnackOrder.class);
    binder.readBean(new SnackOrder());
    binder.forField(name)
        .asRequired("Name is required")
        .bind("name");
    binder.forField(quantity)
        .asRequired()
        .withConverter(new StringToIntegerConverter("Quantity must be a number"))
        .bind("quantity");
    binder.forField(snack)
        .asRequired("Please choose a snack")
        .bind("snack");

    binder.addStatusChangeListener(status -> {
          // Workaround for https://github.com/vaadin/flow/issues/4988
          boolean emptyFields = Stream.of("name", "quantity", "snack")
              .flatMap(prop -> binder.getBinding(prop).stream())
              .anyMatch(binding -> binding.getField().isEmpty());
          orderButton.setEnabled(!status.hasValidationErrors() && !emptyFields);
        }
    );

    orderButton.addClickListener(click -> {
      try {
        errors.setText("");
        SnackOrder savedOrder = new SnackOrder();
        binder.writeBean(savedOrder);
        fireEvent(new SnackEvent(this, false, savedOrder));
        binder.readBean(new SnackOrder());
        snackType.setValue("");
      } catch (ValidationException e) {
        errors.add(new Html(e.getValidationErrors().stream()
            .map(res -> "<p>" + res.getErrorMessage() +"</p>")
            .collect(Collectors.joining("\n"))));
      }
    });

    add(new Div(
            name,
            quantity,
            snackType,
            snack,
            orderButton){{setClassName("form");}},
        errors);
  }
}
