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
public class PlayerID implements IPlayer, IAuto {

    public int depth;
    public int profunditat_actual = 0;
    public int nodes_explorats = 0;
    public String name;
    public GameStatus s;
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    

    public PlayerID(String name, int depth, int jugador1jugador2) {
        this.maximizingPlayer = jugador1jugador2;
        this.depth = depth;
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }
    
    @Override
    public String getName() {
        return "PlayerID(" + name + ")";
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
    
    public boolean opening(GameStatus gs){
        
        if(PlayerType.PLAYER2 == gs.getCurrentPlayer() && gs.getScore(gs.getCurrentPlayer()) == 12){
            if(gs.getPos(0,5) == CellType.P2 && gs.getPos(2, 5) == CellType.P2 && 
                    gs.getPos(4,5) == CellType.P2 && gs.getPos(6,5) == CellType.P2 && 
                    gs.getPos(1,6) == CellType.P2 && gs.getPos(3,6) == CellType.P2 && 
                    gs.getPos(5,6) == CellType.P2 && gs.getPos(7,6) == CellType.P2 &&
                    gs.getPos(0,7) == CellType.P2 && gs.getPos(2,7) == CellType.P2 && 
                    gs.getPos(4,7) == CellType.P2 && gs.getPos(6,7) == CellType.P2){
                return true;
            }
        } else {
            if(gs.getPos(0,5) == CellType.P2 && gs.getPos(2, 5) == CellType.P2 && 
                    gs.getPos(4,5) == CellType.P2 && gs.getPos(6,5) == CellType.P2 && 
                    gs.getPos(1,6) == CellType.P2 && gs.getPos(3,6) == CellType.P2 && 
                    gs.getPos(5,6) == CellType.P2 && gs.getPos(7,6) == CellType.P2 &&
                    gs.getPos(0,7) == CellType.P2 && gs.getPos(2,7) == CellType.P2 && 
                    gs.getPos(4,7) == CellType.P2 && gs.getPos(6,7) == CellType.P2){
                return true;
            }
        }
        return false;
    }
    
    
    public boolean opening2(GameStatus gs){
        
        if(PlayerType.PLAYER2 == gs.getCurrentPlayer() && gs.getScore(gs.getCurrentPlayer()) == 12){
            if(gs.getPos(0,5) == CellType.P2 && gs.getPos(3, 4) == CellType.P2 && 
                    gs.getPos(4,5) == CellType.P2 && gs.getPos(6,5) == CellType.P2 && 
                    gs.getPos(1,6) == CellType.P2 && gs.getPos(3,6) == CellType.P2 && 
                    gs.getPos(5,6) == CellType.P2 && gs.getPos(7,6) == CellType.P2 &&
                    gs.getPos(0,7) == CellType.P2 && gs.getPos(2,7) == CellType.P2 && 
                    gs.getPos(4,7) == CellType.P2 && gs.getPos(6,7) == CellType.P2){
                return true;
            }
        } else {
            if(gs.getPos(0,5) == CellType.P2 && gs.getPos(2, 5) == CellType.P2 && 
                    gs.getPos(4,5) == CellType.P2 && gs.getPos(6,5) == CellType.P2 && 
                    gs.getPos(1,6) == CellType.P2 && gs.getPos(3,6) == CellType.P2 && 
                    gs.getPos(5,6) == CellType.P2 && gs.getPos(7,6) == CellType.P2 &&
                    gs.getPos(0,7) == CellType.P2 && gs.getPos(2,7) == CellType.P2 && 
                    gs.getPos(4,7) == CellType.P2 && gs.getPos(6,7) == CellType.P2){
                return true;
            }
        }
        return false;
    }

    
    @Override
    public PlayerMove move(GameStatus gs) {
        int millor_valor;
        List<Point> bestMove = new ArrayList<>();
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        List<MoveNode> peces = gs.getMoves();
        MoveNode node;
        
        // menja
        if(moviments.size() == 1){
            bestMove.add(peces.get(0).getPoint());
            node = peces.get(0).getChildren().get(0);
            bestMove.add(node.getPoint());
            return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
        }
        // primer moviment
        if(opening(gs)){
            bestMove.add(peces.get(1).getPoint());
            node = peces.get(1).getChildren().get(1);
            bestMove.add(node.getPoint());
            return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
            
        }
        
        // segon moviment
        if(opening2(gs)){
            bestMove.add(peces.get(4).getPoint());
            node = peces.get(4).getChildren().get(0);
            bestMove.add(node.getPoint());
            return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
            
        }
        
        
        // es fa amb ids, i depth es el maxim que es pot baixar
        for(int fons = 0; fons < 15; fons++){
            millor_valor = Integer.MIN_VALUE;
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);
                
                int min = minVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                
                if (min == millor_valor) {
                    bestMove = move;
                    millor_valor = min;
                    profunditat_actual = fons;
                }
                if (min > millor_valor) {
                    bestMove = move;
                    millor_valor = min;
                    profunditat_actual = fons;
                }
            }
        }
        
        return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
    
    }
    
    
    // estrategia X Y alpha 
    public int numDefendingNeighbors(int row, int col, GameStatus gs) {
        
        PlayerType currentPlayer = gs.getCurrentPlayer();
        
        int defensa = 0;
        
        if(row == 0 || row == 7 || col == 0 || col == 7){
            return 0;
        }

        if(currentPlayer == PlayerType.PLAYER1){
            if(gs.getPos(row-1, col-1) == CellType.P1 || gs.getPos(row-1, col-1) == CellType.P1Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                if(defensa == 2){
                    defensa = 4;
                }
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                if(defensa > 2){
                    defensa = 5;
                }
                if(defensa == 2){
                    defensa = 4;
                } else defensa += 1;
            }
        }
        
        if(currentPlayer == PlayerType.PLAYER2){
            if(gs.getPos(row-1, col-1) == CellType.P2 || gs.getPos(row-1, col-1) == CellType.P2Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P2 || gs.getPos(row-1, col+1) == CellType.P2Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P2 || gs.getPos(row-1, col+1) == CellType.P2Q){
                if(defensa == 2){
                    defensa = 4;
                }
            }
            if(gs.getPos(row-1, col+1) == CellType.P2 || gs.getPos(row-1, col+1) == CellType.P2Q){
                if(defensa > 2){
                    defensa = 5;
                }
                if(defensa == 2){
                    defensa = 4;
                } else defensa += 1;
            }
        }
        return defensa;
    }
    
    public int patata_calenta(int jugador, int x, int y, GameStatus gs){
        if(x == 0 && y == 7 && jugador == 1 && gs.getPos(x+1, y-1) == CellType.EMPTY){
            return Integer.MAX_VALUE;
        }
        if(x == 7 && y == 0 && jugador == 2 && gs.getPos(x-1, y+1) == CellType.EMPTY){
            return Integer.MAX_VALUE;
        }
        return 0;
    }
    
    public int jugades(GameStatus gs){
        int defensa1 = 0;
        
        int defensa2 = 0;
        
        int cantonada = 0;
        
        int defensa = 0;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(gs.getCurrentPlayer() == PlayerType.PLAYER1){
                    if(gs.getPos(7,2) == CellType.P1 || gs.getPos(7,2) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                    }
                    if(gs.getPos(7,4) == CellType.P1 || gs.getPos(7,4) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                    }
                    if(gs.getPos(7,6) == CellType.P1 || gs.getPos(7,6) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                        cantonada++;
                    }
                    if(gs.getPos(6,3) == CellType.P1 || gs.getPos(6,3) == CellType.P1Q){
                        defensa1++;
                    }
                    if(gs.getPos(6,5) == CellType.P1 || gs.getPos(6,5) == CellType.P1Q){
                        defensa1++;
                    }
                    if(gs.getPos(6,7) == CellType.P1 || gs.getPos(6,7) == CellType.P1Q){
                        cantonada++;
                    }
                } else{
                    if(gs.getPos(0,3) == CellType.P1 || gs.getPos(0,3) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                    }
                    if(gs.getPos(0,5) == CellType.P1 || gs.getPos(0,5) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                    }
                    if(gs.getPos(0,1) == CellType.P1 || gs.getPos(0,1) == CellType.P1Q){
                        defensa1++;
                        defensa2++;
                        cantonada++;
                    }
                    if(gs.getPos(1,4) == CellType.P1 || gs.getPos(1,4) == CellType.P1Q){
                        defensa1++;
                    }
                    if(gs.getPos(1,2) == CellType.P1 || gs.getPos(1,2) == CellType.P1Q){
                        defensa1++;
                    }
                    if(gs.getPos(1,0) == CellType.P1 || gs.getPos(1,0) == CellType.P1Q){
                        defensa1++;
                    }
                }
            }
        }
        if(defensa1 == 5){
            defensa += 100;
        }
        if(defensa2 == 3){
            defensa += 300;
        }
        if(cantonada == 2){
            defensa += 500;
        }
        
        return defensa;
    }
    
    
    public int heuristica(GameStatus gs){
        int numRows = 8;
        int numCols = 8;
        int boardVal = 0;
        int cntAllyPieces = 0;
        int cntAllyKings = 0;
        int cntOppPieces = 0;
        int cntOppKings = 0;
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                
                CellType peca = gs.getPos(i, j);
                PlayerType jugador = gs.getCurrentPlayer();
                if(jugador == PlayerType.PLAYER1){
                    switch (peca) {
                        case P1:
                            cntAllyPieces++;
                            boardVal += numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + (15 * i) + middleBonus(i, j) + jugades(gs) + patata_calenta(1, i, j, gs);
                            break;
                        case P2:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j)+jugades(gs);
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
                            boardVal += numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * i) + middleBonus(i, j)+jugades(gs)+patata_calenta(1, i, j, gs);
                            break;
                        case P1:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j)+jugades(gs);
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

        boardVal += 600 * cntAllyPieces + 1000 * cntAllyKings - 600 * cntOppPieces - 100 * cntOppKings;

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
            return 200;
        }
        if (maximizingPlayer == 2 && row == 7) {
            return 200;
        }
        return 0;
    }
    
    public int maxVal(GameStatus gs, int alpha, int beta, int depth) {
        
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        
        if(moviments.isEmpty() || depth == this.depth){
            return heuristica(gs);
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
            return heuristica(gs);
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