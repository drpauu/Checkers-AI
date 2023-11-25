package edu.upc.epsevg.prop.checkers;

import edu.upc.epsevg.prop.checkers.players.HumanPlayer;
import edu.upc.epsevg.prop.checkers.players.RandomPlayer;
import edu.upc.epsevg.prop.checkers.IPlayer;
import edu.upc.epsevg.prop.checkers.players.OnePiecePlayer;



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
                
                IPlayer player1 = new HumanPlayer("Human");
                IPlayer player2 = new OnePiecePlayer(1);
                //IPlayer player1 = new RandomPlayer("Kamikaze 1");
                //IPlayer player2 = new RandomPlayer("Kamikaze 2");
                                
                new Board(player1 , player2, 1, false);
             }
        });
    }
}
