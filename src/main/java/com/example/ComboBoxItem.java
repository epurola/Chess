package com.example;



public class ComboBoxItem {
    private final String text;
    private final int value;

    public ComboBoxItem(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return text + " " + value;
    }
}

