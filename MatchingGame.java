/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matchinggame;

import java.io.IOException;
import javax.swing.JFrame;

/**
 *
 * @author tanza
 */
public class MatchingGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame ("Matching Game");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        

        MatchingWindow panel = new MatchingWindow();
        frame.getContentPane().add(panel);

        frame.pack();
        frame.setVisible(true);
    }
    
}
