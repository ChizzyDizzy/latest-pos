package com.syos.cli.ui.menu;

public interface MenuComponent {
    void display();
    void execute();
    String getName();
    boolean isComposite();
}