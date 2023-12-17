package edu.upc.epsevg.prop.checkers.players;


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
    

    public checkerAI(String name, int depth, int jugador1Ojugador2) {
        this.maximizingPlayer = jugador1Ojugador2;
        this.depth = depth;
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    
/**
     * Decideix el points del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el points que fa el jugador.
     */
    /*
    @Override
    public PlayerMove move(GameStatus s) {


        List<MoveNode> moves =  s.getMoves();
        
        List<Point> points = new ArrayList<>();
        MoveNode node = moves.get(0);
        points.add(node.getPoint());
        
        while(!node.getChildren().isEmpty()) {
            node = node.getChildren().get(node.getChildren().size()-1);
            points.add(node.getPoint());
        }
        return new PlayerMove( points, 0L, 0, SearchType.RANDOM);         
        
    }
    */
    @Override
    public PlayerMove move(GameStatus gs) {
        Date date = new Date();
        startTime = date.getTime();
        getBoardStatus(gs);
        Random rand = new Random(); // per si hem d escollir un points random (init)
        List<MoveNode> moviments_possibles =  s.getMoves(); // llista del possibles moviments
        int bestMoveVal = 0;
        int depthReached = 0;
        MoveNode bestMove = null;
        outOfTime = false;
        List<MoveNode> millors_moviments;
        
        // en el cas que nomes hi hagi un points, fem el points
        if(true){
            List<Point> points = new ArrayList<>();
            MoveNode node = moviments_possibles.get(0);
            points.add(node.getPoint());
            while(!node.getChildren().isEmpty()) {
                node = node.getChildren().get(node.getChildren().size()-1);
                points.add(node.getPoint());
            }
            return new PlayerMove( points, 0L, depthReached, SearchType.MINIMAX_IDS);
        }
        
        for (profunditat_actual = 0; profunditat_actual < depth && !outOfTime; profunditat_actual++) {
            List<Point> points = new ArrayList<>();
            millors_moviments = new ArrayList<>();
            int bestVal = Integer.MIN_VALUE;
            for (MoveNode i : moviments_possibles) {
                GameStatus copia = new GameStatus(gs);
                points.add(i.getPoint());
                copia.movePiece(points);
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
        
        List<Point> points = new ArrayList<>();
        MoveNode node = bestMove;
        points.add(node.getPoint());
        while(!node.getChildren().isEmpty()) {
            int c = rand.nextInt(node.getChildren().size());
            node = node.getChildren().get(c);
            points.add(node.getPoint());
        }
        return new PlayerMove( points, 0L, depthReached, SearchType.MINIMAX_IDS);
    }
    
    // check if we've reached leaf nodes or maximum depth
    public boolean cutoffTest(int numMoves, int profunditat) {
        return numMoves == 0 || profunditat == this.depth;
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
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);
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
                            boardVal += numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + (15 * i) + middleBonus(i, j);
                            break;
                        case P1:
                            cntOppPieces++;
                            boardVal -= numDefendingNeighbors(i, j, gs) * 50 + backBonus(i) + (15 * (7 - i)) + middleBonus(i, j);
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
            List<Point> points = new ArrayList<>();
            GameStatus copia = new GameStatus(gameStatus);
            points.add(moveNode.getPoint());
            copia.movePiece(points);

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
            List<Point> points = new ArrayList<>();
            GameStatus copia = new GameStatus(gameStatus);
            points.add(moveNode.getPoint());
            copia.movePiece(points);

            v = Math.min(v, maxVal(copia, alpha, beta, depth + 1));
            if (v <= alpha) return v;
            beta = Math.min(beta, v);
        }
        return v;
    }

    // returns bonus if piece is protecting its king row
    public int backBonus(int row) {
        if (maximizingPlayer == 1 && row == 0) {
            return 100;
        }
        if (maximizingPlayer == 2 && row == 7) {
            return 100;
        }
        return 0;
    }
    
    // returns bonus depending on how close piece is to the middle
    public int middleBonus(int row, int col) {
        return 100 - ((Math.abs(4 - col) + Math.abs(4 - row)) * 10);
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
    
    private void getBoardStatus(GameStatus gs){
        int numRows = 8;
        int numCols = 8;
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
                        case P2:
                            cntOppPieces++;
                        case P1Q:
                            cntAllyKings++;
                        case P2Q:
                            cntOppKings++;
                    }
                } else {
                    switch (peca) {
                        case P2:
                            cntAllyPieces++;
                        case P1:
                            cntOppPieces++;
                        case P2Q:
                            cntAllyKings++;
                        case P1Q:
                            cntOppKings++;
                    }
                }
            }
        }
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

