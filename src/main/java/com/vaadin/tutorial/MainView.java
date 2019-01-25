package com.vaadin.tutorial;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@StyleSheet("frontend://styles/styles.css")
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("Snack order"));

        SnackForm snackForm = new SnackForm();
        SnacksGrid snacksGrid = new SnacksGrid();

        snackForm.addOrderListener(event -> snacksGrid.addSnack(event.getOrder()));
        add(snackForm, snacksGrid);
    }
}
