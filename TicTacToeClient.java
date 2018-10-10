import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;

public class TicTacToeClient {

    private JFrame frame = new JFrame("Tic Tac Toe");
    private JLabel messageLabel = new JLabel("");
    private ImageIcon icon;
    private ImageIcon opponentIcon;

    private Square[] board = new Square[9];
    private Square currentSquare;
    static String status_of_win= "";
    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    //Constructs the client by connecting to a server, laying out the
    //GUI and registering GUI listeners.

    public TicTacToeClient(String serverAddress) throws Exception {

        // Setup networking
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Layout of the UI
        messageLabel.setBackground(Color.lightGray);
        frame.getContentPane().add(messageLabel, "South");

        JPanel boardPanel = new JPanel();

        boardPanel.setBackground(Color.black);
        boardPanel.setLayout(new GridLayout(3, 3, 2, 2));
        for (int i = 0; i < board.length; i++) {
            final int j = i;
            board[i] = new Square();
            board[i].addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentSquare = board[j];
                    out.println("MOVE " + j);}});

            boardPanel.add(board[i]);
        }

        frame.getContentPane().add(boardPanel, "Center");
    }

    public void play() throws Exception {
        String response;
        try {
            response = in.readLine();
            if (response.startsWith("WELCOME")) {
                char mark = response.charAt(8);
                ImageIcon iconinit = new ImageIcon(mark == 'X' ? "x.png" : "o.png");
                icon = new ImageIcon (iconinit.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                ImageIcon opponentIconinit  = new ImageIcon(mark == 'X' ? "o.png" : "x.png");
                opponentIcon= new ImageIcon (opponentIconinit.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));

                frame.setTitle("Tic Tac Toe - Player " + mark);
            }
            while (true) {
                response = in.readLine();
                if (response.startsWith("VALID_MOVE")) {
                    messageLabel.setText("Valid move, please wait");
                    currentSquare.setIcon(icon);
                    currentSquare.repaint();
                } else if (response.startsWith("OPPONENT_MOVED")) {
                    int loc = Integer.parseInt(response.substring(15));
                    board[loc].setIcon(opponentIcon);
                    board[loc].repaint();
                    messageLabel.setText("Opponent moved, your turn");
                } else if (response.startsWith("VICTORY")) {
                    messageLabel.setText("You win");
                    status_of_win="You win";
                    break;
                } else if (response.startsWith("DEFEAT")) {
                    messageLabel.setText("You lose");
                    status_of_win="You lose";
                    break;
                } else if (response.startsWith("TIE")) {
                    messageLabel.setText("You tied");
                    status_of_win="You tied";
                    break;
                } else if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
            }
            out.println("QUIT");
        }
        finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain(String win_status) {
        String play_again_msg= win_status + "! Want to play again?";
        int response = JOptionPane.showConfirmDialog(frame,
            play_again_msg,
            "Tic Tac Toe is a fun game!",
            JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    
   // Runs the client as an application. MUST PASS SERVER INFO
    public static void main(String[] args) throws Exception {
        while (true ) {
            String serverAddress;
            if (args.length == 0) 
                serverAddress= "localhost";
            else
                serverAddress=args[1];

            TicTacToeClient client = new TicTacToeClient(serverAddress);
            client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.frame.setSize(450, 450);
            client.frame.setVisible(true);
            client.frame.setResizable(false);
            client.play();


            if (!client.wantsToPlayAgain(status_of_win)) 
            {
                break;
            }
        }
    }
}