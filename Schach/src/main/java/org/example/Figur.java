package org.example;

import javax.swing.*;

public class Figur extends JLabel {
    private String pieceType;
    private String color;

    public Figur(ImageIcon icon, String pieceType, String color) {
        super(icon);
        this.pieceType = pieceType;
        this.color = color;
    }

    public String getPieceType() {
        return pieceType;
    }

    public String getColor() {
        return color;
    }
}
