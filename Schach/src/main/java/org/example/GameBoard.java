package org.example;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;

public class GameBoard extends JPanel {
    public static final int WIDTH = 1100;
    public static final int Height = 800;
    private JFrame frame;
    private JPanel[][] board = new JPanel[8][8];
    private String[][] initialSetup = {
            {"b-rook", "b-knight", "b-bishop", "b-queen", "b-king", "b-bishop", "b-knight", "b-rook"},
            {"b-pawn", "b-pawn", "b-pawn", "b-pawn", "b-pawn", "b-pawn", "b-pawn", "b-pawn"},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {"w-pawn", "w-pawn", "w-pawn", "w-pawn", "w-pawn", "w-pawn", "w-pawn", "w-pawn"},
            {"w-rook", "w-knight", "w-bishop", "w-queen", "w-king", "w-bishop", "w-knight", "w-rook"}
    };

    private JLabel selectedPiece;  // the selected piece
    private int selectedRow = -1;  // the row of the selected piece
    private int selectedCol = -1;  // the column of the selected piece
    private int lastselectedRow = -2; // to store th last position
    private int lastselectedCol = -2;
    private boolean GameStateTurn = true; // true =  whites turn || false = Blacks turn
    private int GameState = 0; // 0 = normal 1= matt // 2 = schachmatt
    private boolean Check = false;

    public GameBoard() {
        frame = new JFrame();
        frame.setBounds(0, 0, WIDTH, Height);
        frame.setLayout(new GridLayout(8, 8));

        // add board and add mouse listeners
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new JPanel();
                board[i][j].setBackground((i + j) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
                int row = i;
                int col = j;

                // Adds mouse listener to each square
                board[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleMouseClick(row, col);
                    }
                });

                frame.add(board[i][j]);
            }
        }

        // Load initial chess pieces on the board
        loadInitialPieces();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void loadInitialPieces() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String pieceKey = initialSetup[i][j];
                if (pieceKey != null) {
                    String color = pieceKey.charAt(0) == 'b' ? "Black" : "White"; // Determine the color based on the first character
                    String type = pieceKey.substring(2).substring(0, 1).toUpperCase() + pieceKey.substring(3); // Convert to "Rook", "Knight", etc.

                    String imagePath = "src/main/java/chessPieces/" + pieceKey + ".png"; // Construct the path for the image

                    try {
                        BufferedImage myPicture = ImageIO.read(new File(imagePath));

                        // Create a ChesspieceLabel instead of a regular JLabel
                        Figur chessPiece = new Figur(new ImageIcon(myPicture), type, color);
                        board[i][j].add(chessPiece); // Add the chess piece to the board
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    private void handleMouseClick(int row, int col) {

        String kingcolor;
        // If ther a piece already selected, attempt to move it
        if (selectedPiece != null) {
            // If the click is on a different square
            if (selectedRow != row || selectedCol != col) {
                // Move the selected piece to the new square
                if (board[row][col].getBackground().equals(new Color(255, 164, 164))) {
                    board[selectedRow][selectedCol].setBackground((selectedRow + selectedCol) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
                    movePiece(row, col);
                    // unselect the piece
                    selectedPiece = null;
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
            else{
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++){
                        board[i][j].setBackground((i + j) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
                    }
                }
                selectedPiece = null;
                selectedRow = -1;
                selectedCol = -1;

            }
        }
        else {
            Check = false;
            if (GameStateTurn == true){
                kingcolor = "White";}
            else{
                kingcolor = "Black";}
            if (isKingInCheck(kingcolor) ){
                Check = true;
                JOptionPane.showMessageDialog(frame, "You have been Checked", "Hopss", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("check");
            }

            // If this panel has a piece select it
            if (board[row][col].getComponentCount() > 0) {
                selectedPiece = (JLabel) board[row][col].getComponent(0);
                Figur selectedPieceForColor = (Figur) board[row][col].getComponent(0);
                String pieceColor = selectedPieceForColor.getColor();
                // player turns
                switch (pieceColor){
                    case "White":
                        if (GameStateTurn == true){
                            board[row][col].setBackground(new Color(224, 0, 0));
                            selectedRow = row;
                            selectedCol = col;
                            ChessPieceLogic(selectedRow, selectedCol);
                        }
                        else{
                            selectedPiece = null;
                            selectedRow = -1;
                            selectedCol = -1;
                        }
                        break;
                    case "Black":
                        if (GameStateTurn == false) {
                            board[row][col].setBackground(new Color(224, 0, 0));
                            selectedRow = row;
                            selectedCol = col;
                            ChessPieceLogic(selectedRow, selectedCol);// determine which piece  was moved Bauer oder König
                        }
                        else{
                            selectedPiece = null;
                            selectedRow = -1;
                            selectedCol = -1;
                        }
                        break;
                }

            }
        }
    }
    private void ChessPieceLogic(int row, int col){
        Figur movedPiece = (Figur) board[row][col].getComponent(0);
        String pieceType = movedPiece.getPieceType();
        String pieceColor = movedPiece.getColor();
        switch(pieceType){
            case "Pawn":
                pawnPieceLogic(row, col,pieceColor);
                break;
            case "Rook":
                rookPieceLogic(row, col , pieceColor);
                break;
            case "Bishop":
                bishopPieceLogic(row , col, pieceColor);
                break;
            case "Knight":
                knightPieceLogic(row, col, pieceColor);
                break;
            case "Queen":
                rookPieceLogic(row , col, pieceColor);
                bishopPieceLogic(row , col, pieceColor);
                break;
            case "King":
                kingPieceLogic(row, col, pieceColor);
                break;

        }


    }
    private void pawnPieceLogic(int row, int col, String pieceColor) {
        if (pieceColor.equals("White")) {
            // Move forward
            if (row - 1 >= 0 && (board[row - 1][col].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row - 1][col].setBackground(new Color(255, 164, 164));
            }
            // Left capture
            if (col - 1 >= 0 && row - 1 >= 0 && board[row - 1][col - 1].getComponentCount() > 0) { // Check bounds
                Figur movedPieceLeft = (Figur) board[row - 1][col - 1].getComponent(0);
                if (movedPieceLeft.getColor().equals("Black")) { // Check if the piece is an enemy
                    board[row - 1][col - 1].setBackground(new Color(255, 164, 164));
                }
            }
            // Right capture
            if (col + 1 < 8 && row - 1 >= 0 && board[row - 1][col + 1].getComponentCount() > 0) { // Check bounds
                Figur movedPieceRight = (Figur) board[row - 1][col + 1].getComponent(0);
                if (movedPieceRight.getColor().equals("Black")) { // Check if the piece is an enemy
                    board[row - 1][col + 1].setBackground(new Color(255, 164, 164));
                }
            }

        } else if (pieceColor.equals("Black")) {
            // Move forward
            if (row + 1 < 8 && (board[row + 1][col].getComponentCount() <= 0)) { // Check bounds
                board[row + 1][col].setBackground(new Color(255, 164, 164));
            }

            // Left capture
            if (col - 1 >= 0 && row + 1 < 8 && board[row + 1][col - 1].getComponentCount() > 0) { // Check bounds
                Figur movedPieceLeft = (Figur) board[row + 1][col - 1].getComponent(0);
                if (movedPieceLeft.getColor().equals("White")) { // Check if the piece is an enemy
                    board[row + 1][col - 1].setBackground(new Color(255, 164, 164));
                }
            }

            // Right capture
            if (col + 1 < 8 && row + 1 < 8 && board[row + 1][col + 1].getComponentCount() > 0) { // Check bounds
                Figur movedPieceRight = (Figur) board[row + 1][col + 1].getComponent(0);
                if (movedPieceRight.getColor().equals("White")) { // Check if the piece is an enemy
                    board[row + 1][col + 1].setBackground(new Color(255, 164, 164));
                }
            }
        }
    }


    private void bishopPieceLogic(int row, int col, String pieceColor){
        for (int i = 1; i < 8; i++) {
            if (0 <= row - i && col + i <= 7){// doesn't not need to check for upper + beacause it only goes down same for row
                if ((board[row - i][col+ i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row - i][col + i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row- i][col + i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row - i][col + i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }
        for (int i = 1; i < 8; i++) {
            if (0 <= row - i && col - i >=0){//
                if ((board[row - i][col - i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row - i][col - i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row- i][col - i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row - i][col - i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

        for (int i = 1; i < 8; i++) {
            if ( row + i <= 7 && col - i >= 0){ //
                if ((board[row + i][col - i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row + i][col - i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row + i][col - i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row + i][col - i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

        for (int i = 1; i < 8; i++) {
            if (row + i <= 7 && col + i <=7){ //
                if ((board[row + i][col + i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row + i][col + i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row + i][col + i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row + i][col + i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

    }


    private void rookPieceLogic(int row ,int col, String pieceColor){
        for (int i = 1; i < 8; i++) {
            if (col + i <= 7){
                if ((board[row][col + i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row][col + i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row][col + i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row][col + i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }
        for (int i = 1; i < 8; i++) {
            if (col - i >= 0){
                if ((board[row][col - i].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row][col - i].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row][col - i].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row][col - i].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

        for (int i = 1; i < 8; i++) {
            if (row - i >= 0){
                if ((board[row- i][col].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row - i][col].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row - i][col].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row - i][col].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

        for (int i = 1; i < 8; i++) {
            if (row + i <= 7){
                if ((board[row + i][col].getComponentCount() <= 0)) { // Check bounds before accessing
                    board[row + i][col].setBackground(new Color(255, 164, 164));
                }
                else{
                    Figur movedPiece = (Figur) board[row + i][col].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();
                    if (!pieceColorEnemy.equals(pieceColor)){
                        board[row + i][col].setBackground(new Color(255, 164, 164));
                        break;
                    }
                    else{
                        break;
                    }
                }
            }
        }

    }

    private void knightPieceLogic(int row, int col, String pieceColor){
        if (row - 2 >= 0 && col - 1 >= 0) {
            if ((board[row - 2][col - 1].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row - 2][col - 1].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row - 2][col -1].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row -2][col - 1].setBackground(new Color(255, 164, 164));
                }
            }
        }

        if (row - 1 >= 0 && col - 2 >= 0) {
            if ((board[row - 1][col - 2].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row - 1][col - 2].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row - 1][col -2].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row - 1][col - 2].setBackground(new Color(255, 164, 164));
                }
            }
        }
        if (row - 1 >= 0 && col + 2 <= 7) {
            if ((board[row - 1][col + 2].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row - 1][col + 2].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row - 1][col + 2].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row - 1][col + 2].setBackground(new Color(255, 164, 164));
                }
            }
        }
        if (row - 2 >= 0 && col + 1 <= 7) {
            if ((board[row - 2][col + 1].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row - 2][col + 1].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row - 2][col + 1].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row - 2][col + 1].setBackground(new Color(255, 164, 164));
                }
            }
        }
        //// lower side
        if (row + 2 <= 7 && col - 1 >= 0) {
            if ((board[row + 2][col - 1].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row + 2][col - 1].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row + 2][col -1].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row + 2][col - 1].setBackground(new Color(255, 164, 164));
                }
            }
        }

        if (row + 1 <= 7 && col - 2 >= 0) {
            if ((board[row + 1][col - 2].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row + 1][col - 2].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row + 1][col -2].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row + 1][col - 2].setBackground(new Color(255, 164, 164));
                }
            }
        }
        if (row + 1 <= 7 && col + 2 <= 7) {
            if ((board[row + 1][col + 2].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row + 1][col + 2].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row + 1][col + 2].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row + 1][col + 2].setBackground(new Color(255, 164, 164));
                }
            }
        }
        if (row + 2 <= 7 && col + 1 <= 7) {
            if ((board[row + 2][col + 1].getComponentCount() <= 0)) { // Check bounds before accessing
                board[row + 2][col + 1].setBackground(new Color(255, 164, 164));
            }
            else{
                Figur movedPiece = (Figur) board[row + 2][col + 1].getComponent(0);
                String pieceColorEnemy = movedPiece.getColor();
                if (!pieceColorEnemy.equals(pieceColor)){
                    board[row + 2][col + 1].setBackground(new Color(255, 164, 164));
                }
            }
        }

    }
    private void kingPieceLogic(int row, int col, String pieceColor) {
        // Define the possible moves for a king (8 possible directions)
        int[][] moves = {
                {-1, -1}, {-1, 0}, {-1, 1},  // Upper row
                {0, -1},          {0, 1},     // Same row
                {1, -1}, {1, 0}, {1, 1}       // Lower row
        };

        // Iterate through all possible moves
        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            // Check if the new position is within the bounds of the board
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                // Check if the square is empty
                if (board[newRow][newCol].getComponentCount() == 0) {
                    board[newRow][newCol].setBackground(new Color(255, 164, 164)); // Highlight the square
                } else {
                    // There is a piece on the square, check its color
                    Figur movedPiece = (Figur) board[newRow][newCol].getComponent(0);
                    String pieceColorEnemy = movedPiece.getColor();

                    // If the piece is an opponent's piece, highlight the square
                    if (!pieceColorEnemy.equals(pieceColor)) {
                        board[newRow][newCol].setBackground(new Color(255, 164, 164)); // Highlight the square
                    }
                }
            }
        }
    }



    private boolean isKingInCheck(String kingColor) {
        int kingRow = -1;
        int kingCol = -1;

        // Locate the king's position on the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getComponentCount() > 0) {
                    Figur piece = (Figur) board[i][j].getComponent(0);
                    if (piece.getPieceType().equals("King") && piece.getColor().equals(kingColor)) {
                        kingRow = i;
                        kingCol = j;
                        break;
                    }
                }
            }
        }

        // Check if any opponent piece can attack the king
        String opponentColor = kingColor.equals("White") ? "Black" : "White";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getComponentCount() > 0) {
                    Figur piece = (Figur) board[i][j].getComponent(0);
                    if (piece.getColor().equals(opponentColor)) {
                        int row = i;
                        int col = j;
                        ChessPieceLogic(row, col);
                        if (board[kingRow][kingCol].getBackground().equals(new Color(255, 164, 164))) {
                            // Reset all squares to original color
                            for (int k = 0; k < 8; k++) {
                                for (int l = 0; l < 8; l++) {
                                    board[k][l].setBackground((k + l) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
                                }
                            }
                            return true;
                        }
                        // Reset colors regardless of the check state
                        for (int k = 0; k < 8; k++) {
                            for (int l = 0; l < 8; l++) {
                                board[k][l].setBackground((k + l) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
                            }
                        }
                    }
                }
            }
        }

        return false;
    }



    private void movePiece(int row, int col) {
        if (board[row][col].getComponentCount() > 0) {
            for (Component component : board[row][col].getComponents()) {
                board[row][col].remove(component);
            }

        }
        // Remove the piece from the selected square
        board[selectedRow][selectedCol].remove(selectedPiece);
        board[selectedRow][selectedCol].revalidate();
        board[selectedRow][selectedCol].repaint();

        // Add the piece to the new square
        board[row][col].add(selectedPiece);
        board[row][col].revalidate();
        board[row][col].repaint();

        GameStateTurn = !GameStateTurn;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++){
                board[i][j].setBackground((i + j) % 2 == 0 ? new Color(242, 202, 92) : new Color(248, 231, 187));
            }
        }
    }



    public static void main(String[] args) {
        new GameBoard();
    }
}
