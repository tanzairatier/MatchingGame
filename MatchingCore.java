/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matchinggame;

import java.util.Random;

/**
 *
 * @author tanza
 */
public class MatchingCore {
    
    int num_cells_width;
    int num_cells_height;
    int[][] cell_contents;
    int num_unique_contents;
    int numMatchesMade = 0;
    boolean isGameOver = false;
    int numTries = 0;
    int score = 0;
    boolean auto_mode = false;
    
    int num_icons;
    
    public MatchingCore() {
        num_icons = new MatchingIcons().icons.length;
        newGame(4, 4);
    }
    
    public void newGame(int w, int h) {
        num_cells_width = w;
        num_cells_height = h;
        num_unique_contents = (num_cells_width*num_cells_height)/2;
        numMatchesMade = 0;
        isGameOver = false;
        numTries = 0;
        score = 0;
        cell_contents = new int[num_cells_width][num_cells_height];
        /*
        int[] random_contents = new int[num_unique_contents];
        int[] random_contents_used = new int[num_unique_contents];
        boolean[] icons_used = new boolean[num_icons];
        
        Random rnd = new Random();
        for (int i = 0; i < num_unique_contents; i++) {
            random_contents[i] = rnd.nextInt(num_icons);
            while (icons_used[random_contents[i]]) {
                random_contents[i] += 1;
                if (random_contents[i] >= num_icons) random_contents[i] = 0;
            }
            icons_used[random_contents[i]] = true;
            random_contents_used[i] = 0;
        }
        
        int what_content;
        for (int i = 0; i < num_cells_width; i++) {
            for (int j = 0; j < num_cells_height; j++) {
                what_content = rnd.nextInt(num_unique_contents);
                while (random_contents_used[what_content] >= 2) {
                    what_content += 1;
                    if (what_content >= num_unique_contents) what_content = 0;
                }
                cell_contents[i][j] = random_contents[what_content];
                random_contents_used[what_content] += 1;
            }
        }
        */
    
        
        int x,y,content_id;
        boolean[][] already_specified_cell = new boolean[num_cells_width][num_cells_height];
        boolean[] already_used_content = new boolean[num_icons];
        for (int i = 0; i < num_icons; i++) {
            already_used_content[i] = false;
        }
        for (int i = 0; i < num_cells_width; i++) {
            for (int j = 0; j < num_cells_height; j++) {
                already_specified_cell[i][j] = false;
            }
        }
        Random rnd = new Random();
        for (int i = 0; i < num_unique_contents; i++) {
            //pick an initial start x,y that isn't already assigned
            x = rnd.nextInt(num_cells_width);
            y = rnd.nextInt(num_cells_height);
            while (already_specified_cell[x][y]) {
                x = rnd.nextInt(num_cells_width);
                y = rnd.nextInt(num_cells_height);
            }
            
            
            content_id = rnd.nextInt(num_icons);
            while (already_used_content[content_id]) {
                content_id = rnd.nextInt(num_icons);
            }
            already_used_content[content_id] = true;
            already_specified_cell[x][y] = true;
            cell_contents[x][y] = content_id;
            String s = "Added content-pair id " + content_id + " at (" + x + "," + y + "), ";
            
            x = rnd.nextInt(num_cells_width);
            y = rnd.nextInt(num_cells_height);
            while (already_specified_cell[x][y]) {
                x = rnd.nextInt(num_cells_width);
                y = rnd.nextInt(num_cells_height);
            } 
            already_specified_cell[x][y] = true;
            cell_contents[x][y] = content_id;
            s += "(" + x + "," + y + ")";
            System.out.println(s);
        }
        
    }
            
            
}
