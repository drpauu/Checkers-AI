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

    
    private PlayerMove minimaxDecision(GameStatus gs) {
        double bestValue = Double.NEGATIVE_INFINITY;
        PlayerMove bestMove = null;

        for (MoveNode move : gs.getMoves()) {
            double value = minimax(new GameStatus(gs, move), depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move.toPlayerMove();
            }
        }
        return bestMove;
    }

    private double minimax(GameStatus gs, int depth, double alpha, double beta, boolean maximizingPlayer) {
        if (depth == 0 || gs.isGameOver()) {
            return heuristic(gs);
        }
        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (MoveNode move : gs.getMoves()) {
                double eval = minimax(new GameStatus(gs, move), depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (MoveNode move : gs.getMoves()) {
                double eval = minimax(new GameStatus(gs, move), depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    private double heuristic(GameStatus gs) {
        // Implement heuristic logic here based on capturing pieces and board center positioning
        
        double score = 0.0;

        // 1. Safety of team pieces
        score += calculateSafetyScore(gs);

        // 2. Center domination
        score += calculateCenterControlScore(gs);

        // 3. Potential for multiple captures
        score += calculateMultipleCaptureScore(gs);

        // 4. Potential for single captures
        score += calculateSingleCaptureScore(gs);

        return score;
    }

    private double calculateSafetyScore(GameStatus gs) {
        // Implement logic to calculate the safety score
    }

    private double calculateCenterControlScore(GameStatus gs) {
        // Implement logic to calculate the center control score
    }

    private double calculateMultipleCaptureScore(GameStatus gs) {
        // Implement logic to calculate the multiple capture score
    }

    private double calculateSingleCaptureScore(GameStatus gs) {
        // Implement logic to calculate the single capture score
    }


    @Override
    public PlayerMove move(GameStatus gs) {
        return minimaxDecision(gs);
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    /*
    @Override
    public PlayerMove move(GameStatus gs) {
        List<MoveNode> moves = gs.getMoves();
        Point center = new Point(gs.getSize()/ 2, gs.getSize()/ 2); // Assuming a square board
        MoveNode bestMove = null;
        double minDistanceToCenter = Double.MAX_VALUE;

        for (MoveNode move : moves) {
            Point endPoint = getLastPointOfMove(move);
            double distanceToCenter = endPoint.distance(center);

            if (distanceToCenter < minDistanceToCenter) {
                minDistanceToCenter = distanceToCenter;
                bestMove = move;
            }
        }

        return convertMoveNodeToPlayerMove(bestMove);
    }

    private Point getLastPointOfMove(MoveNode move) {
        MoveNode current = move;
        while (!current.getChildren().isEmpty()) {
            current = current.getChildren().get(0); // Assuming the first child is the continuation of the move
        }
        return current.getPoint();
    }

    private PlayerMove convertMoveNodeToPlayerMove(MoveNode move) {
        List<Point> points = new ArrayList<>();
        MoveNode current = move;
        points.add(current.getPoint());

        while (!current.getChildren().isEmpty()) {
            current = current.getChildren().get(0); // Assuming the first child is the continuation of the move
            points.add(current.getPoint());
        }

        return new PlayerMove(points, 0L, 0, SearchType.MINIMAX_IDS);
    }
    */



    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "checkerAI(" + name + ")";
    }
}

