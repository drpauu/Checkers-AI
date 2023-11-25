package edu.upc.epsevg.prop.checkers;

import java.awt.*;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;


/**
 * UI del tauler de joc.
 *
 * @author Bernat Orellana
 */

public class Board extends MouseAdapter {

    private JFrame mainFrame;
    private BufferedImage image = null;
    int midaTauler;
    int midaCasella;
    int marginW;
    int marginH;
    int timeoutSeconds;

    private IPlayer players[];
    private GameStatus status;
    private boolean pauseInAutomatic=true;
    private Point lastPostAuto;
    private PlayerType curPlayer = PlayerType.PLAYER1;
    private UIStates gameEstatus;
    private JControlsPanel controlPanel;
    private JPanel boardPanel;
    private List<Point> currentClickedPath;
    private List<MoveNode> allowedPositions;

    private IPlayer getCurrentPlayer() {
        return players[PlayerType.to_01(curPlayer)];
    }

    private boolean isCurrentPlayerAuto() {
        return getCurrentPlayer() instanceof IAuto;
    }

    private Point convertScreenToBoard(double x, double y) {

        if (x < marginW || y < marginH || x > (midaTauler + marginW) || y > (midaTauler + marginH)) {
            return null;
        }

        int xx = (int) ((x - marginW) / midaCasella);
        int yy = (int) ((y - marginH) / midaCasella);

        return new Point(xx, yy);
    }


    private void showMessageAndButton(String A, String B, String buttonMessage, boolean buttonEnabled) {

        controlPanel.highlightPlayer(curPlayer);

        if (curPlayer == PlayerType.PLAYER1) {
            controlPanel.setPlayer1Message(A);
            controlPanel.setPlayer2Message(B);

        } else {
            controlPanel.setPlayer2Message(A);
            controlPanel.setPlayer1Message(B);
        }
        controlPanel.setButtonText(buttonMessage);
        controlPanel.setButtonEnabled(buttonEnabled);
    }

    private enum UIStates {
        INIT,
        WAIT_TO_CONTINUE,
        PLAYING_TO,
        END_GAME
    }

    public Board() {

        initComponents();

    }

    Board(IPlayer player1, IPlayer player2, int timeoutSeconds, boolean pauseInAutomatic) {

        this.status = new GameStatus();
        this.timeoutSeconds = timeoutSeconds;
        this.players = new IPlayer[2];
        this.pauseInAutomatic = pauseInAutomatic;

        this.players[0] = player1;
        this.players[1] = player2;
        this.gameEstatus = UIStates.INIT;
        this.curPlayer = PlayerType.PLAYER1;

        this.currentClickedPath = new ArrayList<Point>();
        this.allowedPositions   = new ArrayList<MoveNode>();
        initComponents();
        showCurrentStatus();

    }


    private void showCurrentStatus() {
        controlPanel.setScore1(status.getScore(PlayerType.PLAYER1));
        controlPanel.setScore2(status.getScore(PlayerType.PLAYER2));
        switch (gameEstatus) {
            case INIT: {
                controlPanel.setScore1(0);
                controlPanel.setScore2(0);
                controlPanel.setThinking(false);
                controlPanel.setPlayer1Name(players[0].getName());
                controlPanel.setPlayer2Name(players[1].getName());
                String clicToStart = "Click START !";
                controlPanel.setPlayer1Message(clicToStart);
                controlPanel.setPlayer2Message(clicToStart);
                controlPanel.setButtonText("Start the game");
                controlPanel.setButtonEnabled(true);

            }
            break;
            case END_GAME: {
                controlPanel.setThinking(false);

                if (status.GetWinner() == null) {
                     showMessageAndButton("Game is Draw :-| ", "Game is Draw :-|", "Another game?", true);
                } else 
                if (status.GetWinner() == curPlayer) {
                    showMessageAndButton("YOU WIN ! :-D ", "You lose :_(", "Another game?", true);
                } else {
                    showMessageAndButton("You lose :_(", "YOU WIN ! :-D ", "Another game?", true);
                }
            }
            break;

            case PLAYING_TO: {
             
                controlPanel.setThinking(false);
                String waiting = "Waiting....";
                String yourTurn = isCurrentPlayerAuto() ? "Thinking..." : "Please choose destiny.";
                showMessageAndButton(yourTurn, waiting, "Stop", !isCurrentPlayerAuto());
                
                if(!isCurrentPlayerAuto()){
                    this.allowedPositions = status.getMoves();
                }
            }
            break;
            
            case WAIT_TO_CONTINUE: {
             
                controlPanel.setThinking(false);
                String waiting = "Waiting....";
                String yourTurn =  "click to continue";
                showMessageAndButton(yourTurn, waiting, "Stop", false);
            }
            break;
        }
    }


    void OnStartClicked() {
        status = new GameStatus();
        boardPanel.repaint();
        curPlayer = PlayerType.PLAYER1;
        if (gameEstatus == UIStates.PLAYING_TO) { //wish to STOP
            gameEstatus = UIStates.INIT;
            showCurrentStatus();
        } else if (gameEstatus == UIStates.INIT || gameEstatus == UIStates.END_GAME) {
            gameEstatus = UIStates.PLAYING_TO;
            showCurrentStatus();
            startTurn();
        }

    }

    /**
     * Inici del torn
     */
    private void startTurn() {
        if (isCurrentPlayerAuto()) {
            this.controlPanel.setThinking(true);
            Mover m = new Mover();
            Watchdog w = new Watchdog(m, timeoutSeconds);
            m.setWatchdog(w);
            w.execute();
            m.execute();
            //(new Mover()).doInBackground();
        } else {

        }
    }
    /**
     * Fi del torn
     */
    private void endTurn() {
        currentClickedPath.clear();
        if (status.isGameOver()) {
            gameEstatus = UIStates.END_GAME;
            showCurrentStatus();
        } else {
 
            curPlayer = PlayerType.opposite(curPlayer);
 
            gameEstatus = UIStates.PLAYING_TO;
            showCurrentStatus();
            startTurn();
        }
    }

    /**
     * Vigilant del timeout
     */
    class Watchdog extends SwingWorker<Void, Object> {

        Mover m;
        int timeoutSeconds;

        Watchdog(Mover m, int timeoutSeconds) {
            this.m = m;
            this.timeoutSeconds = timeoutSeconds;
        }

        @Override
        public Void doInBackground() {
            try {
                Thread.sleep(timeoutSeconds * 1000);
            } catch (InterruptedException ex) {
            }
            return null;
        }

        @Override
        protected void done() {
            m.timeout();
        }
    }
    /**
     * This method guarantees that garbage collection is done unlike
     * <code>{@link System#gc()}</code>
     */
    public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
        }
    }
    /**
     * Fil per realitzar el moviment
     */
    class Mover extends SwingWorker<PlayerMove, Object> {

        Watchdog w;
        boolean hasMoved = false;

        Mover() {

        }

        public void timeout() {
            if (!hasMoved) {
                getCurrentPlayer().timeout();
            }
        }

        @Override
        public PlayerMove doInBackground() {
            try {
                PlayerMove m = getCurrentPlayer().move(new GameStatus(status));//, curPlayer); 
                String info = "Profunditat màxima:" + m.getMaxDepthReached() + "\n";
                info += "Node explorats:    " + m.getNumerOfNodesExplored();
                Board.this.controlPanel.setInfo(info);
                hasMoved = true;
                gc();
                return m;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                System.out.println(sw.toString());
                e.printStackTrace();
            }
            return null;
        }

        public void setWatchdog(Watchdog w) {
            this.w = w;
        }

        @Override
        protected void done() {
            try {
                PlayerMove m = get();
                if (w != null) {
                    w.cancel(true);
                }
                if (m != null && m.getPath()!= null) {

                    
                    status.movePiece(m.getPath());
                    lastPostAuto = m.getPath().get(m.getPath().size()-1);  // posició de destí (la última del path)
                    Board.this.controlPanel.setThinking(false);
                    //System.out.println(">" + status.toString());
                    boardPanel.repaint();
                    
                    if(Board.this.pauseInAutomatic){
                        
                        gameEstatus = UIStates.WAIT_TO_CONTINUE;
                        showCurrentStatus();
                    } else {                                        
                        endTurn();
                    }
                } else {
                    Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, "Player is returning a null move or a null position ("+m+"), game is lost...",
                            "");
                    status.forceLoser();
                    Board.this.controlPanel.setThinking(false);
                    endTurn();

                }
            } catch (Exception ignore) {
                if(ignore!=null) ignore.printStackTrace();
                Logger.getLogger(Board.class.getName()).log(java.util.logging.Level.SEVERE, "Player is throwing an Exception, game is lost...",
                        "");
                status.forceLoser();
                Board.this.controlPanel.setThinking(false);
                endTurn();

            }
        }

    }

    private int getX(int col) {
        return (int) (marginW + midaCasella * (col + 0.5));
    }

    private int getY(int fil) {
        return (int) (marginH + midaCasella * (fil + 0.5));
    }

    private void initComponents() {
        try {
            image = ImageIO.read(getClass().getResource("/resources/back.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Color greenColor = new Color(200, 200, 200, 255);
                Color blackColor = new Color(20, 30, 80, 255);
                Color highlightColor = new Color(255, 255, 0, 100);
                Color lastColor = new Color(255, 255, 0, 255);
                

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                              

                g.drawImage(image, 0,0,getWidth(), getHeight(), this);                

                midaTauler = (int) (Math.min(getWidth(), getHeight()) * 0.9);
                midaCasella = midaTauler / status.getSize();
                midaTauler = midaCasella * status.getSize();
                marginW = (int) ((getWidth() - midaTauler) * 0.5);
                marginH = (int) ((getHeight() - midaTauler) * 0.5);


                //Draw the board square
                g2d.setColor(blackColor);
                //g2d.setStroke(new BasicStroke(6));
                g2d.fillRect(marginW - 3, marginH - 3, midaTauler + 6, midaTauler + 6);

                g.setColor(blackColor);
                g2d.fillRect(marginW, marginH, midaTauler, midaTauler);


                //Draw cells                
                int oh = 0;
                for (int i = 0; i < status.getSize(); i++) {
                    int ow = 0;
                    for (int j = 0; j < status.getSize(); j++) {
                        // -----------------------------------
                        // Draw green cells 
                        g2d.setColor(((i+j)%2==1)?blackColor:greenColor);//greenColor);
                        
                        g2d.fillRect(marginW + ow, marginH + oh, midaCasella-1, midaCasella-1);
                        // -----------------------------------
                        // Show highlight       

                        if(gameEstatus==UIStates.PLAYING_TO ) { //&& status.isHighlighted(pp)) {
                            Point pp = new Point(j, i);
                            for(MoveNode m: Board.this.allowedPositions) {
                                if(m.getPoint().equals(pp)){
                                    g2d.setColor(highlightColor);
                                    g2d.fillRect(marginW + ow, marginH + oh, midaCasella, midaCasella);
                                    break;
                                }
                            }
                            
                        }
                        //else if(gameEstatus ==  UIStates.WAIT_TO_CONTINUE ) 
                        {
                            if(lastPostAuto!=null && lastPostAuto.x==j&&lastPostAuto.y==i){
                                g2d.setColor(lastColor);
                                g2d.fillRect(marginW + ow, marginH + oh, midaCasella, midaCasella);
                            }
                        }
                        
                        CellType cell = (status.getPos(j, i));
                        if(cell!=null && cell!=CellType.EMPTY){
                            switch (cell.getPlayer()) {
                                case PLAYER1:
                                    paintPiece(g2d, true, cell.isQueen(), Board.this.getX(j), Board.this.getY(i), midaCasella / 2);
                                    break;
                                case PLAYER2:
                                    paintPiece(g2d, false,cell.isQueen(), Board.this.getX(j), Board.this.getY(i), midaCasella / 2);
                                    break;
                            }
                        }

                        ow += midaCasella;
                    }                                                                          
                    oh += midaCasella;
                }
                int radius = 10;
                int N2 = status.getSize()-2;
                g2d.setColor(blackColor);        

            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 500);//(int) (n * dx + (n - 1) * h) + 200, (int) (n * dy + 200));
            }
        };

        boardPanel.addMouseListener(this);

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        controlPanel = new JControlsPanel(this);
        controlPanel.setThinking(true);
        mainPane.add(controlPanel, BorderLayout.WEST);
        mainPane.add(boardPanel, BorderLayout.CENTER);

        Dimension dB = boardPanel.getPreferredSize();
        Dimension dP = controlPanel.getMinimumSize();
        Dimension d = new Dimension(dB.width + dP.width, dB.height);
        mainFrame.setMinimumSize(d);
        mainFrame.add(mainPane);
        mainFrame.pack();
        mainFrame.setVisible(true);

    }


    
    
    @Override
    public void mouseClicked(MouseEvent me) {


        if (gameEstatus == UIStates.PLAYING_TO && !isCurrentPlayerAuto()) {
 
            Point to = convertScreenToBoard(me.getX(), me.getY());
            if(to!=null){
                    for(MoveNode m: Board.this.allowedPositions) {
                        if(m.getPoint().equals(to)){
                            currentClickedPath.add(to);
                            lastPostAuto=to;
                                                        
                            this.allowedPositions = m.getChildren();
                            boardPanel.repaint();
                            if(allowedPositions.size()==0){
                                status.movePiece(this.currentClickedPath);
                                endTurn();
                            }                            
                            break;
                        }
                    }
            
            }
            /*if (to != null && status.isHighlighted(to)) {
                
            }*/
        } else if(gameEstatus == UIStates.WAIT_TO_CONTINUE) {
            boardPanel.repaint();
            endTurn();            
        }
    }

    protected void paintPiece(Graphics2D g2, boolean isWhitePiece, boolean isQueen, int x, int y, int radius) {
        boolean isWhite = true;
        int pieceRadius = (int) (radius * 0.85);
        int x1 = x - pieceRadius;
        int y1 = y - pieceRadius;
        x -= radius;
        y -= radius;
        int size = radius * 2;

        // Retains the previous state
        Paint oldPaint = g2.getPaint();

        // Fills the circle with solid blue color
        int backColor = !isWhitePiece ? 0xFFFFFFFF : 0xFF111111;
        g2.setColor(new Color(backColor, true));
        g2.fillOval(x, y, size - 1, size - 1);
        g2.setColor(new Color(0x000000, true));
        g2.drawOval(x, y, size - 1, size - 1);
        
        // Adds shadows at the top
        Paint p;
        p = new GradientPaint(x, y, new Color(0.0f, 0.0f, 0.0f, 0.1f),
                x, y + size, new Color(0.0f, 0.0f, 0.0f, 0.0f));
        g2.setPaint(p);
        g2.fillOval(x, y, size - 1, size - 1);

        // Adds highlights at the bottom 
        {
            Color i = isWhite ? new Color(160, 160, 160, 127) : new Color(1.0f, 1.0f, 1.0f, 0.0f);
            Color f = isWhite ? new Color(0.0f, 0.0f, 0.0f, 0.1f) : new Color(1.0f, 1.0f, 1.0f, 0.4f);

            p = new GradientPaint(x, y, i,
                    x, y + size, f);
            g2.setPaint(p);
            g2.fillOval(x, y, size - 1, size - 1);
        }
        // Creates dark edges for 3D effect
        {
            Color i = isWhite ? new Color(250, 250, 250, 127) : new Color(6, 76, 160, 127);
            Color f = isWhite ? new Color(0.0f, 0.0f, 0.0f, 0.2f) : new Color(0.0f, 0.0f, 0.0f, 0.8f);
            p = new RadialGradientPaint(new Point2D.Double(x + size / 2.0,
                    y + size / 2.0), size / 2.0f,
                    new float[]{0.0f, 1.0f},
                    new Color[]{i,
                        f});
            g2.setPaint(p);
            g2.fillOval(x, y, size - 1, size - 1);
        }


        // Adds oval specular highlight at the top left
        p = new RadialGradientPaint(new Point2D.Double(x + size / 2.0,
                y + size / 2.0), size / 1.4f,
                new Point2D.Double(45.0, 25.0),
                new float[]{0.0f, 0.5f},
                new Color[]{new Color(1.0f, 1.0f, 1.0f, 0.4f),
                    new Color(1.0f, 1.0f, 1.0f, 0.0f)},
                RadialGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(p);
        g2.fillOval(x, y, size - 1, size - 1);

        
        if(isQueen){
            int textColor = isWhitePiece ? 0xFF000000 : 0xFFFFFFFF;
            g2.setColor(new Color(textColor, true));
            int textSize = (int)(pieceRadius*1.5);
            g2.setFont(new Font("TimesRoman", Font.BOLD, textSize)); 
            g2.drawString("♔", (int)(x+textSize*0.3), (int)(y+textSize*1.2));
        }
        // Restores the previous state
        g2.setPaint(oldPaint);        
    }

}
