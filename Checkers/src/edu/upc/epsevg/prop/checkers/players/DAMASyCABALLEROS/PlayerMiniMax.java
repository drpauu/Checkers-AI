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
public class PlayerMiniMax implements IPlayer, IAuto {

    public int depth;
    public int profunditat_actual = 0;
    public int nodes_explorats = 0;
    public String name;
    public GameStatus s;
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    
    public boolean acabat = false;
    
    /**
    * Constructor de la classe PlayerMiniMax.
    * 
    * @param name Nom del jugador.
    * @param depth Profunditat màxima de cerca en l'algorisme Minimax.
    * @param jugador1jugador2 Indica si el jugador és el jugador 1 o 2.
    */
    public PlayerMiniMax(String name, int depth, int jugador1jugador2) {
        this.maximizingPlayer = jugador1jugador2;
        this.depth = depth;
        this.name = name;
    }
    /**
    * Mètode cridat quan s'acaba el temps de joc.
    * No es realitza cap acció addicional ja que el jugador és ràpid i no excedeix el temps.
    */
    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }
    /**
    * Retorna el nom del jugador.
    * 
    * @return Una cadena amb el nom del jugador.
    */
    @Override
    public String getName() {
        return "PlayerID(" + name + ")";
    }
    /**
    * Inicialitza el recompte de peces i reines al començament del joc.
    * 
    * @param gs Estat actual del joc.
    */
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
    
    /**
    * Funció auxiliar per convertir una llista de MoveNode en una llista de llistes de Point.
    * 
    * @param movimientos Llista de MoveNode amb els moviments possibles.
    * @return Llista de llistes de Point representant els moviments.
    */
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
    /**
    * Determina si el joc està en la fase d'obertura.
    * 
    * @param gs Estat actual del joc.
    * @return true si el joc està en la fase d'obertura, false en cas contrari.
    */
    // aquesta funcio al final no l'utilitzem
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
    /**
    * Avalua l'estat actual del tauler.
    * 
    * @param gs Estat actual del joc.
    * @return Valoració heurística de l'estat del tauler.
    */
    @Override
    public PlayerMove move(GameStatus gs) {
        int millor_valor;
        List<Point> bestMove = new ArrayList<>();
        List<List<Point>> moviments = llista_moves(gs.getMoves());
   
        game_init(gs);
        
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
                }
            }
        }
        
        return new PlayerMove(bestMove, nodes_explorats, profunditat_actual, SearchType.MINIMAX_IDS);
    
    }
    
    /**
    * Calcula el nombre de veïns que defensen una peça en una posició donada.
    * 
    * @param row Fila de la peça.
    * @param col Columna de la peça.
    * @param gs Estat actual del joc.
    * @return Nombre de veïns que defensen la peça.
    */
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
    
    /**
    * Avalua l'estat actual del tauler.
    * 
    * @param gs Estat actual del joc.
    * @return Valoració heurística de l'estat del tauler.
    */
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
                                boardVal += numDefendingNeighbors(i, j, gs) * 50 
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
                                boardVal -= numDefendingNeighbors(i, j, gs) * 50 
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
                                boardVal += numDefendingNeighbors(i, j, gs) * 50 
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
                                boardVal -= numDefendingNeighbors(i, j, gs) * 50 
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
    /**
    * Calcula un bonus basat en la posició central de la peça.
    * 
    * @param row Fila de la peça.
    * @param col Columna de la peça.
    * @return Puntuació de bonus per la posició de la peça.
    */
    public int middleBonus(int row, int col) {
        
        return 100 - ((Math.abs(4 - col) + Math.abs(4 - row)) * 10);
    }
    /**
    * Calcula un bonus per les peces situades a la fila de darrere.
    * 
    * @param row Fila de la peça.
    * @return Puntuació de bonus per la posició de la peça.
    */
    public int backBonus(int row) {
        if (maximizingPlayer == 1 && row == 0) {
            return 100;
        }
        if (maximizingPlayer == 2 && row == 7) {
            return 100;
        }
        return 0;
    }
    /**
    * Funció de l'algorisme Minimax per maximitzar el valor.
    * 
    * @param gs Estat actual del joc.
    * @param alpha Valor alpha per a la poda Alpha-Beta.
    * @param beta Valor beta per a la poda Alpha-Beta.
    * @param depth Profunditat actual de la cerca.
    * @return El valor màxim obtingut.
    */
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
    /**
    * Funció de l'algorisme Minimax per minimitzar el valor.
    * 
    * @param gs Estat actual del joc.
    * @param alpha Valor alpha per a la poda Alpha-Beta.
    * @param beta Valor beta per a la poda Alpha-Beta.
    * @param depth Profunditat actual de la cerca.
    * @return El valor mínim obtingut.
    */
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