package edu.upc.epsevg.prop.checkers.players;


import edu.upc.epsevg.prop.checkers.GameStatus;
import edu.upc.epsevg.prop.checkers.IAuto;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.MoveNode;
import edu.upc.epsevg.prop.checkers.PlayerMove;
import edu.upc.epsevg.prop.checkers.SearchType;
import java.awt.Point;
import java.util.*;

/**
 *
 * @author user
 */
public class checkerAI implements IPlayer, IAuto {
    public int depth;
    public int profunditat_actual;
    public String name;
    public GameStatus s;
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
        Date date = new Date();
        startTime = date.getTime();
        Random rand = new Random(); // per si hem d escollir un moviment random (init)
        List<MoveNode> moviments_possibles =  s.getMoves(); // llista del possibles moviments
        int bestMoveVal = 0;
        int depthReached = 0;
        MoveNode bestMove = null;
        outOfTime = false;
        List<MoveNode> millors_moviments;
        
        // en el cas que nomes hi hagi un moviment, fem el moviment
        if(moviments_possibles.size() == 1){
            List<Point> moviment = new ArrayList<>();
            MoveNode node = moviments_possibles.get(0);
            moviment.add(node.getPoint());
            return new PlayerMove( moviment, 0L, depthReached, SearchType.MINIMAX_IDS);
        }
        
         for (profunditat_actual = 0; profunditat_actual < depth && !outOfTime; profunditat_actual++) {
            List<Point> moviment = new ArrayList<>();
            millors_moviments = new ArrayList<>();
            int bestVal = Integer.MIN_VALUE;
            for (MoveNode i : moviments_possibles) {
                GameStatus copia = new GameStatus(gs);
                moviment.add(i.getPoint());
                copia.movePiece(moviment);
                int min = minVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                if (outOfTime) break;
                if (min == bestVal) {
                    millors_moviments.add(i);
                }
                if (min > bestVal) {
                    millors_moviments.clear();
                    millors_moviments.add(i);
                    bestVal = min;
                }
                if (bestVal == Integer.MAX_VALUE) break;
            }
            if (!outOfTime) {
                int c = rand.nextInt(millors_moviments.size());
                bestMove = millors_moviments.get(c);
                depthReached = profunditat_actual;
                bestMoveVal = bestVal;
            }
            if (bestMoveVal == Integer.MAX_VALUE) break;
        }
        
        List<Point> moviment = new ArrayList<>();
        MoveNode node = bestMove;
        moviment.add(node.getPoint());
        return new PlayerMove( moviment, 0L, depthReached, SearchType.MINIMAX_IDS);
    }
    
    // check if we've reached leaf nodes or maximum depth
    public boolean cutoffTest(int numMoves, int profunditat) {
        return numMoves == 0 || profunditat == this.depth;
    }
    
    public int heuristica(GameStatus gs){
        return 0;
        /*int numRows = game.board.length;
        int numCols = game.board[0].length;
        int boardVal = 0;
        int cntAllyPieces = 0;
        int cntAllyKings = 0;
        int cntOppPieces = 0;
        int cntOppKings = 0;*/
    }
    
    public int maxVal(GameStatus gameStatus, int alpha, int beta, int depth) {
        // Check if ran out of time for search
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) >= timeLimit * 990) {
            outOfTime = true;
            return 0;
        }

        // Actual max algorithm
        List<MoveNode> listLegalMoves = gameStatus.getMoves();
        if (cutoffTest(listLegalMoves.size(), depth)) {
            return heuristica(gameStatus);
        }

        int v = Integer.MIN_VALUE;
        for (MoveNode moveNode : listLegalMoves) {
            // Apply move to the copy of the game status
            List<Point> moviment = new ArrayList<>();
            GameStatus copia = new GameStatus(gameStatus);
            moviment.add(moveNode.getPoint());
            copia.movePiece(moviment);

            v = Math.max(v, minVal(copia, alpha, beta, depth + 1));
            if (v >= beta) return v;
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    
    public int minVal(GameStatus gameStatus, int alpha, int beta, int depth) {
        // Check if ran out of time for search
        long currentTime = System.currentTimeMillis();
        if ((currentTime - startTime) > timeLimit * 990) {
            outOfTime = true;
            return 0;
        }

        // Actual min algorithm
        List<MoveNode> listLegalMoves = gameStatus.getMoves();
        if (cutoffTest(listLegalMoves.size(), depth)) {
            return heuristica(gameStatus);
        }

        int v = Integer.MAX_VALUE;
        for (MoveNode moveNode : listLegalMoves) {
            // Apply move to the copy of the game status
            List<Point> moviment = new ArrayList<>();
            GameStatus copia = new GameStatus(gameStatus);
            moviment.add(moveNode.getPoint());
            copia.movePiece(moviment);

            v = Math.min(v, maxVal(copia, alpha, beta, depth + 1));
            if (v <= alpha) return v;
            beta = Math.min(beta, v);
        }
        return v;
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

