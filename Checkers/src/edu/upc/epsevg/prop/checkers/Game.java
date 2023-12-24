package edu.upc.epsevg.prop.checkers;

import edu.upc.epsevg.prop.checkers.players.HumanPlayer;
import edu.upc.epsevg.prop.checkers.players.RandomPlayer;
import edu.upc.epsevg.prop.checkers.players.OnePiecePlayer;
import edu.upc.epsevg.prop.checkers.players.DAMASyCABALLEROS.PlayerID;
import edu.upc.epsevg.prop.checkers.players.DAMASyCABALLEROS.PlayerMiniMax;



import javax.swing.SwingUtilities;
import javax.swing.text.html.MinimalHTMLWriter;

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
                
                //IPlayer player1 = new OnePiecePlayer(0);
                
                IPlayer player1 = new RandomPlayer("random");

                IPlayer player2 = new PlayerID("DAMASyCABALLEROS",1,1);
                
                //IPlayer player2 = new PlayerMiniMax("DAMASyCABALLEROS",5,2);

                Board board = new Board(player1 , player2, 1, false);
                
             }
        });
    }
}
