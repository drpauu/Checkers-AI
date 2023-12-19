/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.checkers.players;

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

    // used to keep track of number of pieces on board prior to search
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
        return "checkerAI(" + name + ")";
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
        
        if(moviments.size() == 1){
            
            return new PlayerMove( moviments.get(0), 0L, profunditat_actual, SearchType.MINIMAX_IDS);
        
        }
        
        // es fa amb ids, i depth es el maxim que es pot baixar
        for(int fons = 0; fons < depth; fons++){
            millor_valor = Integer.MIN_VALUE;
            for(List<Point> move : moviments){
                GameStatus copia = new GameStatus(gs);
                copia.movePiece(move);
                
                int min = heuristica1(copia);
                
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
                            valor += 3;
                            break;
                        case P2:
                            valor -= 3;
                            break;
                        case P1Q:
                            valor += 5;
                            break;
                        case P2Q:
                            valor -= 5;
                            break;
                    }       
                } else {
                    switch(gs.getPos(i, j)){
                        case P1:
                            valor += 3;
                            break;
                        case P2:
                            valor -= 3;
                            break;
                        case P1Q:
                            valor += 5;
                            break;
                        case P2Q:
                            valor -= 5;
                            break;
                    } 
                }
            }
        }
        
        return valor;
    }
    
    public int heuristica2(GameStatus gs){
        return 0;
    }
    
    public int numDefendingNeighbors(int row, int col, GameStatus gs) {
        
        int defense = 0;
        
        int files = 8;
        int columnes = 8;
        
        for(int i = 0; i < files; i++){
            for(int j = 0; j < columnes; j++){

            }
        }
        
        return defense;
        
    }
    
    public int heuristica3(GameStatus gs){
        return 0;
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
}
