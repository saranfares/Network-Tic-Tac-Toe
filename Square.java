//Graphical square in the client window.  Each square is
    // each square -- a white panel containing either:
        // A client calls setIcon() to fill
        // it with an Icon-- an X or O !!
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;

public class Square extends JPanel {
    JLabel label = new JLabel((Icon)null);

    public Square() {
        setBackground(Color.white);
        add(label);
        Dimension dim= new Dimension(100, 100);
        setPreferredSize(dim);
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }
}