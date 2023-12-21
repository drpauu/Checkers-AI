/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.checkers.players.DAMASyCABALLEROS;

import edu.upc.epsevg.prop.checkers.CellType;
import static edu.upc.epsevg.prop.checkers.CellType.P1;
import static edu.upc.epsevg.prop.checkers.CellType.P1Q;
import static edu.upc.epsevg.prop.checkers.CellType.P2;
import static edu.upc.epsevg.prop.checkers.CellType.P2Q;
import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.PlayerType;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.*;

/**
 *
 * @author user
 */
public class jugador implements IPlayer, IAuto {

    public int depth;
    public int profunditat_actual = 0;
    public String name;
    public GameStatus s;
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    

    public jugador(String name, int depth, int jugador1jugador2) {
        this.maximizingPlayer = jugador1jugador2;
        this.depth = depth;
        this.name = name;
    }

    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }
    
    public String getName() {
        return "jugador(" + name + ")";
    }
    
    
    // funcio auxiliar per fer la llista de moviments 
    public List<List<Point>> llista_moves(List<MoveNode> movimientos){
        List<List<Point>> moviment = new ArrayList<>();
        int i = 0;
        for(MoveNode move : movimientos){
            int j = 0;
            while(j < move.getChildren().size()) {
                List<Point> path = new ArrayList<>();
                
                MoveNode node = move.getChildren().get(j);
                path.add(move.getPoint()); 
                while(!node.getChildren().isEmpty()){
                    path.add(node.getPoint());
                    node = node.getChildren().get(0);
                }
                path.add(node.getPoint());
                
                moviment.add(path);
                j++;
            } 
            i++;
        }
        return moviment;
    }
    
    public PlayerMove move(GameStatus gs) {
        int millor_valor = 0;
        int profunditat_arribada = 0;
        List<Point> bestMove = new ArrayList<>();
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        
        
        
        // es fa amb ids, i depth es el maxim que es pot baixar
        for(int fons = 0; fons < depth; fons++){
            millor_valor = Integer.MIN_VALUE;
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);
                
                int min = minVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                
                if (min == millor_valor) {
                    bestMove = move;
                }
                if (min > millor_valor) {
                    bestMove = move;
                    millor_valor = min;
                }
            }
        }
        
        return new PlayerMove(bestMove, 0L, profunditat_actual, SearchType.MINIMAX_IDS);
    
    }
    
    public int heuristica1(GameStatus gs){
        
        int files = 8;
        int columnes = 8;
        int valor = 0;
        
        for(int i = 0; i < files; i++){
            for(int j = 0; j < columnes; j++){
                
                if(gs.getCurrentPlayer() == PlayerType.PLAYER1){
                    switch(gs.getPos(i, j)){
                        case P1:
                            valor -= 3;
                            break;
                        case P2:
                            valor += 3;
                            break;
                        case P1Q:
                            valor -= 5;
                            break;
                        case P2Q:
                            valor += 5;
                            break;
                    }       
                } else {
                    switch(gs.getPos(i, j)){
                        case P2:
                            valor -= 3;
                            break;
                        case P1:
                            valor += 3;
                            break;
                        case P2Q:
                            valor -= 5;
                            break;
                        case P1Q:
                            valor += 5;
                            break;
                    } 
                }
            }
        }
        
        return valor;
    }
    
    public int heuristica2(GameStatus gs){
        
        int files = 8;
        int columnes = 8;
        int valor = 0; 
        
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columnes; j++) {
                if(gs.getCurrentPlayer() == PlayerType.PLAYER1){
                    switch(gs.getPos(i, j)){
                        case P2:
                            valor += 3 + (i * 0.5) + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor += 1;
                            }
                            if (i == 0) {
                                valor += 2;
                            }
                            break;
                        case P1:
                            valor -= 3 + ((7 - i) * 0.5) + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor -= 1;
                            }
                            if (i == 7) {
                                valor -= 2;
                            }
                            break;
                        case P2Q:
                            valor += 5 + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor += 1;
                            }
                            if (i == 0) {
                                valor += 2;
                            }
                            break;
                        case P1Q:
                            valor -= 5 + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor -= 1;
                            }
                            if (i == 7) {
                                valor -= 2;
                            }
                            break;
                    }
                } else {
                    switch(gs.getPos(i, j)) {
                        case P1:
                            valor -= 3 + (i * 0.5) + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor -= 1;
                            }
                            if (i == 0) {
                                valor -= 2;
                            }
                            break;
                        case P2:
                            valor += 3 + ((7 - i) * 0.5) + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor += 1;
                            }
                            if (i == 7) {
                                valor += 2;
                            }
                            break;
                        case P1Q:
                            valor -= 5 + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor -= 1;
                            }
                            if (i == 0) {
                                valor -= 2;
                            }
                            break;
                        case P2Q:
                            valor += 5 + numDefendingNeighbors(i, j, gs);
                            if (j == 0 || j == 8) {
                                valor += 1;
                            }
                            if (i == 7) {
                                valor += 2;
                            }
                            break;
                    }
                }
            }
        }
        return valor;
    }
    
    // gets number of neighbors for a piece on the board
    public int numDefendingNeighbors(int row, int col, GameStatus gs) {
        int defense = 0;
        CellType currentPiece = gs.getPos(row, col);
        PlayerType currentPlayer = gs.getCurrentPlayer();

        // Define checks based on piece type and player
        if (currentPlayer == PlayerType.PLAYER1) {
            // For Player 1 pieces
            if (currentPiece == CellType.P1 || currentPiece == CellType.P1Q) {
                // Check for defending neighbors for Player 1 pieces
                defense += checkNeighbor(gs, row + 1, col + 1, CellType.P1, CellType.P1Q);
                defense += checkNeighbor(gs, row + 1, col - 1, CellType.P1, CellType.P1Q);
            }
        } else if (currentPlayer == PlayerType.PLAYER2) {
            // For Player 2 pieces
            if (currentPiece == CellType.P2 || currentPiece == CellType.P2Q) {
                // Check for defending neighbors for Player 2 pieces
                defense += checkNeighbor(gs, row - 1, col + 1, CellType.P2, CellType.P2Q);
                defense += checkNeighbor(gs, row - 1, col - 1, CellType.P2, CellType.P2Q);
            }
        }

        return defense;
    }

    // Helper method to check if a neighbor is a defending piece
    private int checkNeighbor(GameStatus gs, int row, int col, CellType... validTypes) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            CellType neighbor = gs.getPos(row, col);
            for (CellType type : validTypes) {
                if (neighbor == type) {
                    return 1;
                }
            }
        }
        return 0;
    }
    
    public int heuristica3(GameStatus gs){
        int numRows = 8;
        int numCols = 8;
        int boardVal = 0;
        int cntAllyPieces = 0;
        int cntAllyKings = 0;
        int cntOppPieces = 0;
        int cntOppKings = 0;
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                // Assuming there's a method in GameStatus to get the piece type at a given position
                CellType peca = gs.getPos(i, j);
                PlayerType jugador = gs.getCurrentPlayer();
                if(jugador == PlayerType.PLAYER1){
                    switch (peca) {
                        case P1:
                            cntAllyPieces++;
                            boardVal += numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + (15 * i) + middleBonus(i, j);
                            break;
                        case P2:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);
                            break;
                        case P1Q:
                            cntAllyKings++;
                            boardVal += middleBonus(i,j);
                            break;
                        case P2Q:
                            cntOppKings++;
                            boardVal -= middleBonus(i,j);
                            break;
                    }
                } else {
                    switch (peca) {
                        case P2:
                            cntAllyPieces++;
                            boardVal += numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * i) + middleBonus(i, j);
                            break;
                        case P1:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);
                            break;
                        case P2Q:
                            cntAllyKings++;
                            boardVal += middleBonus(i,j);
                            break;
                        case P1Q:
                            cntOppKings++;
                            boardVal -= middleBonus(i,j);
                            break;
                    }
                }
            }
        }
        
        // force trades when ahead
        if (numAllyPieces + numAllyKings > numOppPieces + numOppKings && cntOppPieces + cntOppKings != 0 && numOppPieces + numOppKings != 0 && numOppKings != 1) {
            if ((cntAllyPieces + cntAllyKings)/(cntOppPieces + cntOppKings) > (numAllyPieces + numAllyKings)/(numOppPieces + numOppKings)) {
                boardVal += 150;
            } else {
                boardVal -= 150;
            }
        }

        boardVal += 600 * cntAllyPieces + 1000 * cntAllyKings - 600 * cntOppPieces - 1000 * cntOppKings;

        if (cntOppPieces + cntOppKings == 0 && cntAllyPieces + cntAllyKings > 0) {
            boardVal = Integer.MAX_VALUE;
        }

        if (cntAllyPieces + cntAllyKings == 0 && cntOppPieces + cntOppKings > 0) {
            boardVal -= Integer.MIN_VALUE;
        }
                
        return boardVal;
    }
    
    public int middleBonus(int row, int col) {
        return 100 - ((Math.abs(4 - col) + Math.abs(4 - row)) * 10);
    }
    
    public int backBonus(int row) {
        if (maximizingPlayer == 1 && row == 0) {
            return 100;
        }
        if (maximizingPlayer == 2 && row == 7) {
            return 100;
        }
        return 0;
    }
    
    public int maxVal(GameStatus gs, int alpha, int beta, int depth) {
        
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        
        if(moviments.isEmpty() || depth == this.depth){
            return heuristica3(gs);
        }
        
        int v = Integer.MIN_VALUE;
        for(List<Point> move : moviments){
            
            GameStatus copia = new GameStatus(gs);
            copia.movePiece(move);
            
            v = Math.max( v, minVal(copia, alpha, beta, depth + 1));
            
            if(v >= beta) return v;
            alpha = Math.max(alpha, v);
            
        }
        
        return v;
        
    }
    
    public int minVal(GameStatus gs, int alpha, int beta, int depth){
        
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        
        if(moviments.isEmpty() || depth == this.depth){
            return heuristica3(gs);
        }
        
        int v = Integer.MAX_VALUE;
        for(List<Point> move : moviments){
            
            GameStatus copia = new GameStatus(gs);
            copia.movePiece(move);
            
            v = Math.min( v, maxVal(copia, alpha, beta, depth + 1));
            
            if(v <= alpha) return v;
            beta = Math.min(beta, v);
            
        }
        
        return v;
        
    }
}
