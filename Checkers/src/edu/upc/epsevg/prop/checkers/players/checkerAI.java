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
    public int profunditat_actual = 0;
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
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param gs Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    
    @Override
    public PlayerMove move(GameStatus gs) {
        
        // mirar el temps
        Date date = new Date();
        startTime = date.getTime();
        
        // init de les coses
        //getBoardStatus(gs);
        
        //init per si no ens queda temps
        //Random rand = new Random(); // per si hem d escollir un moviment random (init)
        
        // init de les llistes
        List<MoveNode> peces =  gs.getMoves();
        List<MoveNode> millors_moviments = new ArrayList<>();
        List <MoveNode> moviments = llista_moves(peces);
        
        // init de les variables
        outOfTime = false;
        
        // en el cas que nomes hi hagi un moviment, fem el moviment
        if(moviments.size() == 1){
            List<Point> path = new ArrayList<>();
            MoveNode node = peces.get(0);
            path.add(node.getPoint());
            int i = 0;
            while(!node.getChildren().isEmpty()) {
                node = node.getChildren().get(i);
                path.add(node.getPoint());
                i++;
            } 
            return new PlayerMove( path, 0L, profunditat_actual, SearchType.MINIMAX_IDS);
        }
        for (profunditat_actual = 0; profunditat_actual < depth && !outOfTime; profunditat_actual++){
            millors_moviments = new ArrayList<>();
            int bestVal = Integer.MIN_VALUE;
            for(MoveNode move : peces){
                int i = 0;
                while(i < move.getChildren().size()) {
                    GameStatus copia = new GameStatus(gs);
                    List<Point> path = new ArrayList<>();
                    path.add(move.getPoint());
                    MoveNode node = move.getChildren().get(i);
                    i++;
                    path.add(node.getPoint());
                    copia.movePiece(path);
                    int min = minVal(copia, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
                    if (min == bestVal) {
                        millors_moviments.add(move);
                        millors_moviments.add(node);
                    }
                    if (min > bestVal) {
                        millors_moviments.clear();
                        millors_moviments.add(move);
                        millors_moviments.add(node);
                        bestVal = min;
                    }
                    if (bestVal == Integer.MAX_VALUE) break; // jo que se, per si hi ha algun cas extrem
                }
            }
        }
        
        List<Point> path = new ArrayList<>();
        path.add(millors_moviments.get(0).getPoint());
        path.add(millors_moviments.get(1).getPoint());
        
        return new PlayerMove(path, 0L, profunditat_actual, SearchType.MINIMAX_IDS);
    }
    
    
    // retorna una llista de moviments (que es poden fer) amb les llista de peces que li has donat 
    public List<MoveNode> llista_moves(List<MoveNode> movimientos){
        List<MoveNode> moviment = new ArrayList<>();
        for(int i = 0; i < movimientos.size(); i++){
            MoveNode node = movimientos.get(i);
            int j = 0;
            while(j < node.getChildren().size()) {
                node = node.getChildren().get(j);
                moviment.add(node);
                j++;
            } 
        }
        return moviment;
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
        int v = Integer.MIN_VALUE;
        for (MoveNode move : gameStatus.getMoves()) {
            int i = 0;
            while(i < move.getChildren().size()) {
                // Apply move to the copy of the game status
                List<Point> path = new ArrayList<>();
                MoveNode node = move.getChildren().get(i);
                i++;
                GameStatus copia = new GameStatus(gameStatus);
                path.add(move.getPoint());
                path.add(node.getPoint());
                copia.movePiece(path);

                v = Math.min(v, maxVal(copia, alpha, beta, depth + 1));
                if (v >= alpha) return v;
                beta = Math.min(beta, v);
                
            }
        }
        return v;
    }

    
    public int minVal(GameStatus gameStatus, int alpha, int beta, int depth) {
        int v = Integer.MAX_VALUE;
        for (MoveNode move : gameStatus.getMoves()) {
            int i = 0;
            while(i < move.getChildren().size()) {
                // Apply move to the copy of the game status
                List<Point> path = new ArrayList<>();
                MoveNode node = move.getChildren().get(i);
                i++;
                GameStatus copia = new GameStatus(gameStatus);
                path.add(move.getPoint());
                path.add(node.getPoint());
                copia.movePiece(path);

                v = Math.min(v, maxVal(copia, alpha, beta, depth + 1));
                if (v <= alpha) return v;
                beta = Math.min(beta, v);
                
            }
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
    
        
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "checkerAI(" + name + ")";
    }
}

