/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.checkers.players;

import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author user
 */
public class checkerAI implements IPlayer, IAuto {
    private int depth;
    private String name;
    private GameStatus s;

    public checkerAI(String name, int depth) {
        this.depth = depth;
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(GameStatus s) {


        List<MoveNode> moves =  s.getMoves();

        Random rand = new Random();
        int q = rand.nextInt(moves.size());
        List<Point> points = new ArrayList<>();
        MoveNode node = moves.get(q);
        points.add(node.getPoint());
        
        while(!node.getChildren().isEmpty()) {
            int c = rand.nextInt(node.getChildren().size());
            node = node.getChildren().get(c);
            points.add(node.getPoint());
        }
        return new PlayerMove( points, 0L, 0, SearchType.RANDOM);         
        
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "checkerAI(" + name + ")";
    }
}
