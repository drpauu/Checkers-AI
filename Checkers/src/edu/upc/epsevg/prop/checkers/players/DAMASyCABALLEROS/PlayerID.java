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

    public boolean outOfTime;
    public int timeLimit;
    private Timer timer;
    
    public String name;
    public GameStatus s;
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    

    public PlayerID(String name, int jugador1jugador2, int time) {
        this.maximizingPlayer = jugador1jugador2;
        this.name = name;
        this.timeLimit = time;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
        outOfTime = false;
        Timer timer = new Timer();
        
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                outOfTime = true;
            }
        };
        
        int time = timeLimit * 1000;
        timer.schedule(task, time);
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
<<<<<<< Updated upstream
        
=======
    
>>>>>>> Stashed changes
    @Override
    public PlayerMove move(GameStatus gs) {
        timeout();
        nodes_explorats = 0;
        int millor_valor;
        List<Point> bestMove = new ArrayList<>();
        List<List<Point>> moviments = llista_moves(gs.getMoves());
<<<<<<< Updated upstream
        List<MoveNode> peces = gs.getMoves();
        MoveNode node;
        
        game_init(gs);
        
        // primer moviment
        if(opening(gs) && moviments.size() == 7){
            bestMove.add(peces.get(1).getPoint());
            node = peces.get(1).getChildren().get(1);
            bestMove.add(node.getPoint());
            return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX);
        }
        
        int prof_max = 0;
        while(!outOfTime){
            millor_valor = Integer.MIN_VALUE;
            prof_max++;
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);

                int min = minVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                if (min >= millor_valor) {
                    bestMove = move;
                    millor_valor = min;
                    profunditat_actual = prof_max;
=======
   
        // es fa amb ids, i depth es el maxim que es pot baixar
        for(int fons = 0; fons < this.depth; fons++){
            millor_valor = Integer.MAX_VALUE;
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);
                
                int max = maxVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                if (max == millor_valor) {
                    bestMove = move;
                    millor_valor = max;
                    profunditat_actual = fons;
                }
                if (max < millor_valor) {
                    bestMove = move;
                    millor_valor = max;
                    profunditat_actual = fons;
>>>>>>> Stashed changes
                }
            }
        }
        //if (timer != null) timer.cancel();
        return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
    }
    
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

            boardVal += 600 * cntAllyPieces + 1000 * cntAllyKings - 600 * cntOppPieces - 1000 * cntOppKings;
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
                                boardVal += numDefendingNeighbors(i, j, gs) * 25 
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
                                boardVal -= numDefendingNeighbors(i, j, gs) * 25 
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
                                boardVal += numDefendingNeighbors(i, j, gs) * 25 
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
                                boardVal -= numDefendingNeighbors(i, j, gs) * 25 
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
        if (outOfTime) return 0;
        
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
        nodes_explorats++;
        return v;
        
    }
    
    public int minVal(GameStatus gs, int alpha, int beta, int depth){
        if (outOfTime) return 0;
        
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
        nodes_explorats++;
        return v; 
    }
}