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
    public int depth;
    public String name;
    public GameStatus s;
    List<Point> points = new ArrayList<>();
    // maximizingPlayer specifies which player the computer is playing for.
    public int maximizingPlayer;
    // default time limit
    public int timeLimit = 1;
    // startTime and currentTime used to ensure search doesn't exceed time limit
    public long startTime;
    public long currentTime;
    public boolean outOfTime;
    // used to keep track of number of pieces on board prior to search
    public int numAllyPieces, numAllyKings, numOppPieces, numOppKings;
    

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
    public PlayerMove move(GameStatus gs) {
        List<MoveNode> moves =  s.getMoves(); // agafa els possibles moviments
        
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

