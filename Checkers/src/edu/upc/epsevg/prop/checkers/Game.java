package edu.upc.epsevg.prop.checkers;

import edu.upc.epsevg.prop.checkers.players.HumanPlayer;
import edu.upc.epsevg.prop.checkers.players.RandomPlayer;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.players.OnePiecePlayer;
import edu.upc.epsevg.prop.checkers.players.checkerAI;
import edu.upc.epsevg.prop.checkers.players.jugador;



import javax.swing.SwingUtilities;

/**
 * Checkers: el joc de taula.
 * @author bernat
 */
public class Game {
        /**
     * @param args
     */
    public static void main(String[] args) { 
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                //IPlayer player1 = new OnePiecePlayer(3);
                //IPlayer player1 = new HumanPlayer("pau");
                IPlayer player2 = new checkerAI("codi", 4, 2);
                IPlayer player1 = new RandomPlayer("Kamikaze 1");
                IPplayer player2 = new jugador("jugador", 4, 2);
                //IPlayer player2 = new RandomPlayer("Kamikaze 2");
                                
                new Board(player1 , player2, 1, false);
             }
        });
    }
}
