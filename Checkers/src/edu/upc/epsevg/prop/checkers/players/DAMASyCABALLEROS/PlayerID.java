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

    public long timeLimit;
    public long startTime;
    public long currentTime;
    public boolean outOfTime;
    
    public int depth;
    public int profunditat_actual = 0;
    public int nodes_explorats = 0;
    public String name;
    public GameStatus s;
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    
    public boolean acabat = false;
    

    public PlayerID(String name, int temps, int jugador1jugador2) {
        this.maximizingPlayer = jugador1jugador2;
        this.depth = 50;
        this.timeLimit = temps;
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
    
    public void game_init(GameStatus gs){
        int numRows = 8;
        int numCols = 8;
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                
                CellType peca = gs.getPos(i, j);
                PlayerType jugador = gs.getCurrentPlayer();
                if(jugador == PlayerType.PLAYER1){
                    switch (peca) {
                        case P1:
                            numAllyPieces++;
                            break;
                        case P2:
                            numOppPieces++;
                            break;
                        case P1Q:
                            numAllyKings++;
                            break;
                        case P2Q:
                            numOppKings++;
                            break;
                    }
                } else {
                    switch (peca) {
                        case P2:
                            numAllyPieces++;
                            break;
                        case P1:
                            numOppPieces++;
                            break;
                        case P2Q:
                            numAllyKings++;
                            break;
                        case P1Q:
                            numOppKings++;
                            break;
                    }
                }
            }
        }
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
        }
        return false;
    }
    
    @Override
    public PlayerMove move(GameStatus gs) {
        int millor_valor;
        List<Point> bestMove = new ArrayList<>();
        List<List<Point>> moviments = llista_moves(gs.getMoves());
        List<List<Point>> millors;
        
        Date date = new Date();
        startTime = date.getTime();
        
        game_init(gs);
   
        // es fa amb ids, i depth es el maxim que es pot baixar
        for(int fons = 0; fons < this.depth; fons++){
            millor_valor = Integer.MAX_VALUE;
            millors = new ArrayList<>();
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);
                
                int max = maxVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                if (max == millor_valor) {
                    bestMove = move;
                    millors.add(move);
                    millor_valor = max;
                    profunditat_actual = fons;
                }
                if (max < millor_valor) {
                    millors.clear();
                    millors.add(move);
                    bestMove = move;
                    millor_valor = max;
                    profunditat_actual = fons;
                }
            }
        }
        
        return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
    
    }
    
    
    public int numDefendingNeighbors(int row, int col, GameStatus gs) {
        
        PlayerType currentPlayer = gs.getCurrentPlayer();
        
        int defensa = 0;
        
        if(row == 0 || row == 7 || col == 0 || col == 7){
            return 2;
        }

        if(currentPlayer == PlayerType.PLAYER1){
            if(gs.getPos(row-1, col-1) == CellType.P1 || gs.getPos(row-1, col-1) == CellType.P1Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P1 || gs.getPos(row-1, col+1) == CellType.P1Q){
                defensa += 1;
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
                defensa += 1;
            }
            if(gs.getPos(row-1, col+1) == CellType.P2 || gs.getPos(row-1, col+1) == CellType.P2Q){
                defensa += 1;
            }
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
        boolean principi = false;
        
        int ap = 0;
        int ak = 0;
        int op = 0;
        int ok = 0;
        
        // valorar el punt de la partida
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                
                CellType peca = gs.getPos(i, j);
                PlayerType jugador = gs.getCurrentPlayer();
                if(jugador == PlayerType.PLAYER1){
                    switch (peca) {
                        case P1:
                            ap++;
                            break;
                        case P2:
                            op++;
                            break;
                        case P1Q:
                            ak++;
                            break;
                        case P2Q:
                            ok++;
                            break;
                    }
                } else {
                    switch (peca) {
                        case P2:
                            ap++;
                            break;
                        case P1:
                            op++;
                            break;
                        case P2Q:
                            ak++;
                            break;
                        case P1Q:
                            ok++;
                            break;
                    }
                }
            }
        }
        
        if(ak + ap + op + ok >= 18){
            principi = true;
        }
        if(ak + ap + op + ok < 14){
            // forçar 1v1
            if (numAllyPieces + numAllyKings > numOppPieces + numOppKings && cntOppPieces + cntOppKings != 0 && numOppPieces + numOppKings != 0 && numOppKings != 1) {
                if ((cntAllyPieces + cntAllyKings)/(cntOppPieces + cntOppKings) > (numAllyPieces + numAllyKings)/(numOppPieces + numOppKings)) {
                    boardVal += 300;
                } else {
                    boardVal -= 300;
                }
            }

            boardVal += 600 * ak + 1000 * ap - 600 * op - 1000 * ok;
        } else {
            for (int i = 0; i < numRows; i++) {
                for (int j = 0; j < numCols; j++) {

                    CellType peca = gs.getPos(i, j);
                    PlayerType jugador = gs.getCurrentPlayer();
                    if(jugador == PlayerType.PLAYER1){
                        switch (peca) {
                            case P1:
                                if(principi){
                                    if(i == 1 && j == 0) boardVal += 200;
                                    if(i == 3 && j == 0) boardVal += 100;
                                    if(i == 5 && j == 0) boardVal += 100;
                                    if(i == 7 && j == 0) boardVal -= 200;
                                }
                                cntAllyPieces++;
                                boardVal += numDefendingNeighbors(i, j, gs) * 10 
                                        + backBonus(i)
                                        + middleBonus(i, j);
                                break;
                            case P2:
                                if(principi){
                                    if(i == 0 && j == 7) boardVal += 200;
                                    if(i == 4 && j == 7) boardVal -= 100;
                                    if(i == 6 && j == 7) boardVal -= 200;
                                    if(i == 2 && j == 7) boardVal -= 100;
                                }
                                cntOppPieces++;
                                boardVal -= numDefendingNeighbors(i, j, gs) * 10 
                                        + backBonus(i)
                                        + middleBonus(i, j);
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
                                if(principi){
                                    if(i == 0 && j == 7) boardVal -= 200;
                                    if(i == 4 && j == 7) boardVal += 100;
                                    if(i == 6 && j == 7) boardVal += 200;
                                    if(i == 2 && j == 7) boardVal += 100;
                                }
                                cntAllyPieces++;
                                boardVal += numDefendingNeighbors(i, j, gs) * 10 
                                        + backBonus(i)
                                        + middleBonus(i, j);
                                break;
                            case P1:
                                if(principi){
                                    if(i == 1 && j == 0) boardVal -= 200;
                                    if(i == 3 && j == 0) boardVal -= 100;
                                    if(i == 5 && j == 0) boardVal -= 100;
                                    if(i == 7 && j == 0) boardVal += 200;
                                }
                                cntOppPieces++;
                                boardVal -= numDefendingNeighbors(i, j, gs) * 10 
                                        + backBonus(i)
                                        + middleBonus(i, j);
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

            boardVal += 600 * cntAllyPieces + 1000 * cntAllyKings - 600 * cntOppPieces - 1000 * cntOppKings;
        }

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
        
        Date newDate = new Date();
        currentTime = newDate.getTime();
        if ((currentTime - startTime) > timeLimit * 900) {
            outOfTime = true;
            return heuristica(gs);
        }
        
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
        
        Date newDate = new Date();
        currentTime = newDate.getTime();
        if ((currentTime - startTime) > timeLimit * 900) {
            outOfTime = true;
            return heuristica(gs);
        }
        
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