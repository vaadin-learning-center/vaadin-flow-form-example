package com.vaadin.tutorial;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("")
public class MainView extends VerticalLayout {

  private List<SnackOrder> snackOrders = new LinkedList<>();
  private Grid<SnackOrder> snackOrderGrid = new Grid<>(SnackOrder.class);

  public MainView() {
    add(
        new H1("Snack order"),
        buildForm(),
        snackOrderGrid);
  }

  private Component buildForm() {
    // The snacks we can choose from. In real life, these would
    // probably come from a service of some sort.
    Map<String, List<String>> snacks = new HashMap<>();
    snacks.put("Fruits", Arrays.asList("Banana", "Apple", "Orange", "Avocado"));
    snacks.put("Candy", Arrays.asList("Chocolate bar", "Gummy bears", "Granola bar"));
    snacks.put("Drinks", Arrays.asList("Soda", "Water", "Coffee", "Tea"));

    // Create UI components
    TextField nameField = new TextField("Name");
    TextField quantityField = new TextField("Quantity");
    ComboBox<String> snackTypeSelect = new ComboBox<>("Type", snacks.keySet());
    ComboBox<String> snackSelect = new ComboBox<>("Snack", Collections.emptyList());
    Button orderButton = new Button("Order");
    Div errorsLayout = new Div();

    // Configure UI components
    quantityField.setPreventInvalidInput(true);
    orderButton.setEnabled(false);
    orderButton.setThemeName("primary");

    // Only enable snack selection after a type has been selected.
    // Populate the snack alternatives based on the type.
    snackSelect.setEnabled(false);
    snackTypeSelect.addValueChangeListener(e -> {
      String type = e.getValue();
      snackSelect.setEnabled(type != null && !type.isEmpty());
      if (type != null && !type.isEmpty()) {
        snackSelect.setValue("");
        snackSelect.setItems(snacks.get(type));
      }
    });

    // Create bindings between UI fields and the SnackOrder data model
    Binder<SnackOrder> binder = new Binder<>(SnackOrder.class);
    binder.forField(nameField)
        .asRequired("Name is required")
        .bind("name");
    binder.forField(quantityField)
        .asRequired()
        .withConverter(new StringToIntegerConverter("Quantity must be a number"))
        .withValidator(new IntegerRangeValidator("Quantity must be at least 1", 1, Integer.MAX_VALUE))
        .bind("quantity");
    binder.forField(snackSelect)
        .asRequired("Please choose a snack")
        .bind("snack");

    // Only enable submit button when the form is valid.
    binder.addStatusChangeListener(status -> {
          // Workaround for https://github.com/vaadin/flow/issues/4988
          boolean emptyFields = Stream.of("name", "quantity", "snack")
              .flatMap(prop -> binder.getBinding(prop).stream())
              .anyMatch(binding -> binding.getField().isEmpty());
          orderButton.setEnabled(!status.hasValidationErrors() && !emptyFields);
        }
    );


    // Process order
    orderButton.addClickListener(click -> {
      try {
        errorsLayout.setText("");
        SnackOrder savedOrder = new SnackOrder();
        binder.writeBean(savedOrder);
        addOrder(savedOrder);
        binder.readBean(new SnackOrder());
        snackTypeSelect.setValue("");
      } catch (ValidationException e) {
        errorsLayout.add(new Html(e.getValidationErrors().stream()
            .map(res -> "<p>" + res.getErrorMessage() + "</p>")
            .collect(Collectors.joining("\n"))));
      }
    });

    // Wrap components in layouts
    HorizontalLayout formLayout = new HorizontalLayout(nameField, quantityField, snackTypeSelect, snackSelect, orderButton);
    Div wrapperLayout = new Div(formLayout, errorsLayout);
    formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
    wrapperLayout.setWidth("100%");

    return wrapperLayout;
  }

  private void addOrder(SnackOrder order) {
    snackOrders.add(order);
    snackOrderGrid.setItems(snackOrders);
  }
}
