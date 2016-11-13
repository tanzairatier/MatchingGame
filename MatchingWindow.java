/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matchinggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author tanza
 */
public class MatchingWindow extends JPanel {

    private boolean[][] cellsTriedBySolver;

    private long startTime = -1;

    JPanel matching_cells_panel;
    JPanel title_panel;
    JPanel game_status_panel;
    JPanel bottom_panel;

    JLabel title_label;
    JLabel game_status_label;
    JLabel input_width_label;
    JLabel input_height_label;
    JLabel instruction_label;
    JLabel h_label, w_label;

    JTextField input_width_textfield;
    JTextField input_height_textfield;
    JButton new_game_button;
    JButton inc_h, inc_w, dec_h, dec_w;
    JLabel error_message;

    MatchingCore game_logic;

    int cell_selected_x;
    int cell_selected_y;

    BufferedImage[] icons;
    BufferedImage hidden;
    BufferedImage numbers;
    BufferedImage logo;
    BufferedImage checkmark;

    String[] icon_names;
    ArrayList<FadeAnimation> faders;

    boolean[][] hidden_cells;
    boolean[][] completed_cells;
    int test1_x, test1_y, test2_x, test2_y;
    int test_selection;
    boolean hideOnNextSelect = false;

    double time_elapsed, last_time;
    int h_selection = 4;
    int w_selection = 4;
    final int MAX_H_SELECTION = 14;
    final int MAX_W_SELECTION = 14;
    final int MIN_H_SELECTION = 2;
    final int MIN_W_SELECTION = 2;

    //autosolver2stuff
    int first_x, first_y;
    boolean firstMade = false;
    boolean flipMade = false;
    boolean lastMatchSuccess = false;

    public MatchingWindow() throws IOException {

        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.white);

        faders = new ArrayList<>();

        game_logic = new MatchingCore();
        hidden_cells = new boolean[game_logic.num_cells_width][game_logic.num_cells_height];
        completed_cells = new boolean[game_logic.num_cells_width][game_logic.num_cells_height];
        for (int i = 0; i < hidden_cells.length; i++) {
            for (int j = 0; j < hidden_cells[i].length; j++) {
                hidden_cells[i][j] = true;
                completed_cells[i][j] = false;
            }
        }
        hidden = ImageIO.read(getClass().getResource("png/128x128/question.png"));
        icon_names = new MatchingIcons().icons;
        icons = new BufferedImage[icon_names.length * 5];
        for (int i = 0; i < icon_names.length; i++) {
            icons[i] = ImageIO.read(getClass().getResource("png/16x16/" + icon_names[i] + ".png"));
        }
        for (int i = 0; i < icon_names.length; i++) {
            icons[icon_names.length * 1 + i] = ImageIO.read(getClass().getResource("png/32x32/" + icon_names[i] + ".png"));
        }
        for (int i = 0; i < icon_names.length; i++) {
            icons[icon_names.length * 2 + i] = ImageIO.read(getClass().getResource("png/48x48/" + icon_names[i] + ".png"));
        }
        for (int i = 0; i < icon_names.length; i++) {
            icons[icon_names.length * 3 + i] = ImageIO.read(getClass().getResource("png/64x64/" + icon_names[i] + ".png"));
        }
        for (int i = 0; i < icon_names.length; i++) {
            icons[icon_names.length * 4 + i] = ImageIO.read(getClass().getResource("png/128x128/" + icon_names[i] + ".png"));
        }

        numbers = ImageIO.read(getClass().getResource("images/numbers3_spritesheet.png"));
        logo = ImageIO.read(getClass().getResource("images/jklogo.png"));
        checkmark = ImageIO.read(getClass().getResource("images/check_mark.png"));
        matching_cells_panel = new JPanel() {

            public synchronized void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                int cw = (this.getWidth() - 60) / game_logic.num_cells_width;
                int ch = (this.getHeight() - 60) / game_logic.num_cells_height;
                cw = Math.min(cw, ch);
                ch = cw;
                int spacingx = (40 / (game_logic.num_cells_width - 1));
                int spacingy = spacingx;
                int offx = (this.getWidth() - cw * game_logic.num_cells_width - spacingx * game_logic.num_cells_width) / 2;
                int offy = (this.getHeight() - ch * game_logic.num_cells_height - spacingy * game_logic.num_cells_height) / 2;

                int scaler;
                if (cw >= 128) {
                    scaler = icon_names.length * 4;
                } else if (cw >= 64) {
                    scaler = icon_names.length * 3;
                } else if (cw >= 48) {
                    scaler = icon_names.length * 2;
                } else if (cw >= 32) {
                    scaler = icon_names.length * 1;
                } else {
                    scaler = 0;
                }

                //iterate faders
                try {
                    time_elapsed = System.currentTimeMillis() - last_time;
                    for (FadeAnimation f : faders) {
                        if (f.getAlpha() > 0) {
                            g.drawImage(icons[game_logic.cell_contents[(int) f.getCoord().getX()][(int) f.getCoord().getY()] + scaler], (int) f.getCoord().getX() * cw + offx + (int) f.getCoord().getX() * spacingx + cw / 2 - (int) (cw * f.getAlpha()) / 2, (int) f.getCoord().getY() * ch + offy + (int) f.getCoord().getY() * spacingy + ch / 2 - (int) (ch * f.getAlpha()) / 2, (int) (cw * f.getAlpha()), (int) (f.getAlpha() * ch), this);
                            f.setAlpha(f.getAlpha() - time_elapsed / 750.0);
                        } else {
                            g.drawImage(checkmark, (int) f.getCoord().getX() * cw + offx + (int) f.getCoord().getX() * spacingx, (int) f.getCoord().getY() * ch + offy + (int) f.getCoord().getY() * spacingy, cw, ch, this);
                        }
                    }
                    last_time = System.currentTimeMillis();
                } catch (ConcurrentModificationException cme) {
                    System.out.println("Whoops, synch issues.  Ignoring.");
                } catch (NullPointerException npe) {
                    System.out.println("Huh, this null pointer thing happens sometimes.");
                }

                for (int i = 0; i < game_logic.num_cells_width; i++) {
                    for (int j = 0; j < game_logic.num_cells_height; j++) {
                        if (!hidden_cells[i][j] && !completed_cells[i][j]) {
                            g.drawImage(icons[game_logic.cell_contents[i][j] + scaler], i * cw + offx + i * spacingx, j * ch + offy + j * spacingy, cw, ch, this);
                            g.drawRect(i * cw + offx + i * spacingx, j * ch + offy + j * spacingy, cw, ch);
                            //startx = i*cw + offx + i*spacingx
                            //x = any position inside the square
                            //     x = i*cw + offx + i*spacingx
                            //     x = i(cw+spacingx) + offx
                            //     x - offx = i(cw+spacingx)
                            //     (x-offx)/(cw+spacingx) = i
                            //starty = j*ch + offy + j*spacingy
                            //endx = startx + cw
                            //endy = starty + ch
                        } else if (completed_cells[i][j]) {
                            g.drawRect(i * cw + offx + i * spacingx, j * ch + offy + j * spacingy, cw, ch);

                        } else {
                            //g.fillRect(i*cw + offx + i*spacingx, j*ch + offy + j*spacingy, cw, ch);
                            g.drawImage(hidden, i * cw + offx + i * spacingx, j * ch + offy + j * spacingy, cw, ch, this);
                        }
                    }
                }

                //draw the selected cell
                g.setColor(Color.YELLOW);
                g.drawRect(cell_selected_x * cw + offx + cell_selected_x * spacingx - 1, cell_selected_y * ch + offy + cell_selected_y * spacingy - 1, cw + 2, ch + 2);
                g.drawRect(cell_selected_x * cw + offx + cell_selected_x * spacingx - 2, cell_selected_y * ch + offy + cell_selected_y * spacingy - 2, cw + 4, ch + 4);
                g.drawRect(cell_selected_x * cw + offx + cell_selected_x * spacingx - 3, cell_selected_y * ch + offy + cell_selected_y * spacingy - 3, cw + 6, ch + 6);

                repaint();
            }
        };

        setFocusable(true);
        requestFocusInWindow();
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (!game_logic.auto_mode) {
                    if (hideOnNextSelect) {
                        hideOnNextSelect = false;
                        hidden_cells[test2_x][test2_y] = true;
                        hidden_cells[test1_x][test1_y] = true;
                    }

                    //map x and y to grid
                    int cw = (matching_cells_panel.getWidth() - 60) / game_logic.num_cells_width;
                    int ch = (matching_cells_panel.getHeight() - 60) / game_logic.num_cells_height;
                    cw = Math.min(cw, ch);
                    ch = cw;
                    int spacingx = (40 / (game_logic.num_cells_width - 1));
                    int spacingy = spacingx;
                    int offx = (matching_cells_panel.getWidth() - cw * game_logic.num_cells_width - spacingx * game_logic.num_cells_width) / 2;
                    int offy = (matching_cells_panel.getHeight() - ch * game_logic.num_cells_height - spacingy * game_logic.num_cells_height) / 2;

                    double i = (e.getX() - offx) / (cw + spacingx);
                    double j = (e.getY() - offy - matching_cells_panel.getY()) / (ch + spacingy);
                    if (i >= 0 && j >= 0 && i < game_logic.num_cells_width && j < game_logic.num_cells_height) {
                        cell_selected_x = (int) Math.floor(i);
                        cell_selected_y = (int) Math.floor(j);
                        flipSelectedCard();
                    }
                    System.out.println("Mouse clicked on " + e.getX() + "," + e.getY() + " this maps to coord " + i + "," + j);

                    requestFocusInWindow();
                }
            }

        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!game_logic.auto_mode) {
                    int key = e.getKeyCode();

                    if (hideOnNextSelect) {
                        hideOnNextSelect = false;
                        hidden_cells[test2_x][test2_y] = true;
                        hidden_cells[test1_x][test1_y] = true;
                    }
                    if (key == KeyEvent.VK_KP_LEFT || key == KeyEvent.VK_LEFT) {
                        cell_selected_x -= 1;
                        if (cell_selected_x < 0) {
                            cell_selected_x = game_logic.num_cells_width - 1;
                        }
                    } else if (key == KeyEvent.VK_KP_RIGHT || key == KeyEvent.VK_RIGHT) {
                        cell_selected_x += 1;
                        if (cell_selected_x > game_logic.num_cells_width - 1) {
                            cell_selected_x = 0;
                        }
                    } else if (key == KeyEvent.VK_KP_UP || key == KeyEvent.VK_UP) {
                        cell_selected_y -= 1;
                        if (cell_selected_y < 0) {
                            cell_selected_y = game_logic.num_cells_height - 1;
                        }
                    } else if (key == KeyEvent.VK_KP_DOWN || key == KeyEvent.VK_DOWN) {
                        cell_selected_y += 1;
                        if (cell_selected_y > game_logic.num_cells_height - 1) {
                            cell_selected_y = 0;
                        }
                    } else if (key == KeyEvent.VK_ENTER) {
                        flipSelectedCard();

                    } else if (key == KeyEvent.VK_G) {

                        autoSolver();

                    } else if (key == KeyEvent.VK_J) {
                        autoSolver2();
                    }
                }
            }
        });
        //matching_cells_panel.setOpaque(false);
        //matching_cells_panel.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        title_panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = (int) Math.min(logo.getWidth() * 2, matching_cells_panel.getWidth() * 0.55);
                int height = (int) (title_panel.getHeight() * 0.85);
                g.drawImage(logo, (matching_cells_panel.getWidth() - width) / 2, (title_panel.getHeight() - height) / 2, width, height, this);
                repaint();
            }
        ;
        };
        game_status_panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (game_logic.isGameOver) {
                    g.drawString("Congratulations!  Final Score: ", (getWidth() - 7 * 22) / 2 + -10 * 22, 20);
                    if (game_logic.score >= 1000) {
                        int temp = game_logic.score;
                        int thousands_digit = temp / 1000;
                        temp = temp - thousands_digit * 1000;
                        int hundreds_digit = temp / 100;
                        temp = temp - hundreds_digit * 100;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(thousands_digit * 22 + thousands_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + -1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(hundreds_digit * 22 + hundreds_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 0 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else if (game_logic.score >= 100) {
                        int temp = game_logic.score;
                        int hundreds_digit = temp / 100;
                        temp = temp - hundreds_digit * 100;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(hundreds_digit * 22 + hundreds_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 0 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else if (game_logic.score >= 10) {
                        int temp = game_logic.score;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else {
                        int ones_digit = game_logic.score;
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    }
                } else {
                    g.drawString("Tries Made: ", (getWidth() - 7 * 22) / 2 + -4 * 22, 20);
                    if (game_logic.numTries > 9999) {
                        game_logic.numTries = 9999;
                    }
                    if (game_logic.numTries >= 1000) {
                        int temp = game_logic.numTries;
                        int thousands_digit = temp / 1000;
                        temp = temp - thousands_digit * 1000;
                        int hundreds_digit = temp / 100;
                        temp = temp - hundreds_digit * 100;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(thousands_digit * 22 + thousands_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + -1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(hundreds_digit * 22 + hundreds_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 0 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else if (game_logic.numTries >= 100) {
                        int temp = game_logic.numTries;
                        int hundreds_digit = temp / 100;
                        temp = temp - hundreds_digit * 100;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(hundreds_digit * 22 + hundreds_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 0 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else if (game_logic.numTries >= 10) {
                        int temp = game_logic.numTries;
                        int tens_digit = temp / 10;
                        temp = temp - tens_digit * 10;
                        int ones_digit = temp;
                        g.drawImage(numbers.getSubimage(tens_digit * 22 + tens_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 1 * 22, 2, null);
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    } else {
                        int ones_digit = game_logic.numTries;
                        g.drawImage(numbers.getSubimage(ones_digit * 22 + ones_digit * 1, 0, 22, 30), (getWidth() - 7 * 22) / 2 + 2 * 22, 2, null);
                    }
                }
                repaint();
            }
        };
        bottom_panel = new JPanel();

        /* GRID BAG LAYOUT STUFF */
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1.0;
        c.weighty = 0.15;
        this.add(title_panel, c);
        //title_panel.setBorder(BorderFactory.createLineBorder(Color.RED));
        title_label = new JLabel("Joe's Matching Game");
        //title_panel.add(title_label);

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        this.add(matching_cells_panel, c);

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1.0;
        c.weighty = 0.05;
        this.add(game_status_panel, c);
        //game_status_panel.setBorder(BorderFactory.createLineBorder(Color.PINK));
        game_status_label = new JLabel("Number of Tries: " + game_logic.numTries + ".  Matches made: " + game_logic.numMatchesMade + "/" + (game_logic.num_cells_height * game_logic.num_cells_width) / 2);
        //game_status_panel.add(game_status_label);

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;
        c.weightx = 1.0;
        c.weighty = 0.05;
        this.add(bottom_panel, c);
        //bottom_panel.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        input_width_label = new JLabel("Number of Squares Wide: ");
        input_width_textfield = new JTextField("5", 5);
        input_height_label = new JLabel("Number of Square High: ");
        input_height_textfield = new JTextField("4", 5);
        error_message = new JLabel("Error: No error message defined yet.");
        error_message.setVisible(false);
        error_message.setForeground(Color.red);

        inc_h = new JButton("+");
        inc_h.setPreferredSize(new Dimension(40, 20));
        inc_w = new JButton("+");
        inc_w.setPreferredSize(new Dimension(40, 20));
        dec_h = new JButton("-");
        dec_h.setPreferredSize(new Dimension(40, 20));
        dec_w = new JButton("-");
        dec_w.setPreferredSize(new Dimension(40, 20));
        h_label = new JLabel("Height: 4");
        w_label = new JLabel("Width: 4");
        instruction_label = new JLabel("< Adjust Width and Size in New Game >");

        inc_w.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                w_selection += 1;
                if (w_selection > MAX_W_SELECTION) {
                    w_selection = MAX_W_SELECTION;
                } else {
                    w_label.setText("Width: " + w_selection);
                }
            }
        });
        inc_h.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                h_selection += 2;
                if (h_selection > MAX_H_SELECTION) {
                    h_selection = MAX_H_SELECTION;
                } else {
                    h_label.setText("Height: " + h_selection);
                }
            }
        });
        dec_h.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                h_selection -= 2;
                if (h_selection < MIN_H_SELECTION) {
                    h_selection = MIN_H_SELECTION;
                } else {
                    h_label.setText("Height: " + h_selection);
                }
            }
        });
        dec_w.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                w_selection -= 1;
                if (w_selection < MIN_W_SELECTION) {
                    w_selection = MIN_W_SELECTION;
                } else {
                    w_label.setText("Width: " + w_selection);
                }

            }
        });

        new_game_button = new JButton("New Game");
        new_game_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!game_logic.auto_mode) {
                    int w = w_selection;
                    int h = h_selection;

                    game_logic.newGame(w, h);
                    hidden_cells = new boolean[w][h];
                    completed_cells = new boolean[w][h];
                    for (int i = 0; i < hidden_cells.length; i++) {
                        for (int j = 0; j < hidden_cells[i].length; j++) {
                            hidden_cells[i][j] = true;
                            completed_cells[i][j] = false;
                        }
                    }
                    cell_selected_x = 0;
                    cell_selected_y = 0;
                    game_status_label.setText("Number of Tries: " + game_logic.numTries + ".  Matches made: " + game_logic.numMatchesMade + "/" + (game_logic.num_cells_height * game_logic.num_cells_width) / 2);
                    hideOnNextSelect = false;
                    test_selection = 0;
                    faders = new ArrayList<>();
                    requestFocusInWindow();
                }
            }
        });

        bottom_panel.add(dec_w);
        bottom_panel.add(w_label);
        bottom_panel.add(inc_w);
        bottom_panel.add(instruction_label);
        bottom_panel.add(dec_h);
        bottom_panel.add(h_label);
        bottom_panel.add(inc_h);
        //bottom_panel.add(input_width_label);
        //bottom_panel.add(input_width_textfield);
        //bottom_panel.add(input_height_label);
        //bottom_panel.add(input_height_textfield);
        bottom_panel.add(new_game_button);
        //bottom_panel.add(error_message);

    }

    public void initiateFadingIcons(int test1_x, int test1_y, int test2_x, int test2_y) {

        final java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                faders.add(new FadeAnimation(test1_x, test1_y));
                faders.add(new FadeAnimation(test2_x, test2_y));
                completed_cells[test1_x][test1_y] = true;
                completed_cells[test2_x][test2_y] = true;
            }
        }, 500);

    }

    public void flipSelectedCard() {
        if ((hidden_cells[cell_selected_x][cell_selected_y])) {
            test_selection += 1;
            if (test_selection == 1) {
                test1_x = cell_selected_x;
                test1_y = cell_selected_y;
                hidden_cells[test1_x][test1_y] = false;
            } else if (test_selection == 2) {
                test2_x = cell_selected_x;
                test2_y = cell_selected_y;
                hidden_cells[test2_x][test2_y] = false;
            }
            if (test_selection >= 2) {
                test_selection = 0;
                game_logic.numTries += 1;
                if (game_logic.cell_contents[test1_x][test1_y] == game_logic.cell_contents[test2_x][test2_y]) {
                    //successful match
                    lastMatchSuccess = true;
                    initiateFadingIcons(test1_x, test1_y, test2_x, test2_y);
                    game_logic.numMatchesMade += 1;
                    game_status_label.setText("Number of Tries: " + game_logic.numTries + ".  Matches made: " + game_logic.numMatchesMade + "/" + (game_logic.num_cells_height * game_logic.num_cells_width) / 2);
                    if (game_logic.numMatchesMade == (game_logic.num_cells_height * game_logic.num_cells_width) / 2) {
                        //victory
                        int score = Math.max(0, 100 * (game_logic.num_cells_height * game_logic.num_cells_width) / 2 - 10 * game_logic.numTries);
                        game_logic.score = score;
                        game_status_label.setText("You've won the game!  Your score: " + score + ".  Play a new one now!");
                        game_logic.isGameOver = true;
                    }
                } else {
                    lastMatchSuccess = false;
                    game_status_label.setText("Number of Tries: " + game_logic.numTries + ".  Matches made: " + game_logic.numMatchesMade + "/" + (game_logic.num_cells_height * game_logic.num_cells_width) / 2);
                    hideOnNextSelect = true;
                }
            }
        }
    }

    public void autoSolver() {
        System.out.println("Reached Auto Solver mode!");
        game_logic.auto_mode = true;
        test_selection = 0;

        final java.util.Timer t = new java.util.Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            public synchronized void run() {
                if (game_logic.isGameOver) {
                    game_logic.auto_mode = false;
                    cancel();
                } else {

                    //make a move
                    if (hideOnNextSelect) {
                        hideOnNextSelect = false;
                        hidden_cells[test2_x][test2_y] = true;
                        hidden_cells[test1_x][test1_y] = true;
                    }

                    //try a random unsolved and hidden square
                    Random rgen = new Random();
                    cell_selected_x = rgen.nextInt(game_logic.num_cells_width);
                    cell_selected_y = rgen.nextInt(game_logic.num_cells_height);
                    while (completed_cells[cell_selected_x][cell_selected_y] || !hidden_cells[cell_selected_x][cell_selected_y]) {
                        cell_selected_x = rgen.nextInt(game_logic.num_cells_width);
                        cell_selected_y = rgen.nextInt(game_logic.num_cells_height);
                        if (game_logic.isGameOver) {
                            game_logic.auto_mode = false;
                            cancel();
                        }
                    }
                    //System.out.println("selected " + cell_selected_x + "," + cell_selected_y);

                    flipSelectedCard();
                }
            }
        }, 800, 1);
        System.out.println("exiting auto solver mode");

    }

    public void autoSolver2() {
        System.out.println("Reached Auto Solver 2 mode!");
        game_logic.auto_mode = true;
        test_selection = 0;
        firstMade = false;
        cellsTriedBySolver = new boolean[game_logic.num_cells_width][game_logic.num_cells_height];
        for (int i = 0; i < cellsTriedBySolver.length; i++) {
            for (int j = 0; j < cellsTriedBySolver[i].length; j++) {
                cellsTriedBySolver[i][j] = false;
            }
        }
        final java.util.Timer t = new java.util.Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            public synchronized void run() {
                if (game_logic.isGameOver) {
                    game_logic.auto_mode = false;
                    cancel();
                } else {

                    //make a move
                    if (hideOnNextSelect) {
                        hideOnNextSelect = false;
                        hidden_cells[test2_x][test2_y] = true;
                        hidden_cells[test1_x][test1_y] = true;
                    }

                    //System.out.println(test_selection + ", " + firstMade);
                    if (test_selection == 0 && !firstMade) {
                        //try a random unsolved and hidden square
                        Random rgen = new Random();
                        cell_selected_x = rgen.nextInt(game_logic.num_cells_width);
                        cell_selected_y = rgen.nextInt(game_logic.num_cells_height);
                        while (completed_cells[cell_selected_x][cell_selected_y] || !hidden_cells[cell_selected_x][cell_selected_y]) {
                            cell_selected_x = rgen.nextInt(game_logic.num_cells_width);
                            cell_selected_y = rgen.nextInt(game_logic.num_cells_height);
                            if (game_logic.isGameOver || nothing_available_to_select()) {
                                game_logic.auto_mode = false;
                                cancel();
                            }
                        }
                        first_x = cell_selected_x;
                        first_y = cell_selected_y;
                        if (game_logic.isGameOver) {
                            game_logic.auto_mode = false;
                            cancel();
                        }
                        flipSelectedCard();
                        firstMade = true;
                    } else if (test_selection == 0 && firstMade) {
                        cell_selected_x = first_x;
                        cell_selected_y = first_y;
                        flipSelectedCard();
                    } else {
                        //test1_x and test1_y is defined.  find its match
                        //cycle over i and j until match found
                        flipMade = false;
                        for (int j = 0; j < game_logic.num_cells_width; j++) {
                            for (int i = 0; i < game_logic.num_cells_height; i++) {
                                if (!cellsTriedBySolver[i][j] && !completed_cells[i][j] && hidden_cells[i][j]) {
                                    cell_selected_x = i;
                                    cell_selected_y = j;
                                    flipSelectedCard();
                                    cellsTriedBySolver[i][j] = true;
                                    flipMade = true;
                                    break;
                                }
                            }
                            if (flipMade) {
                                break;
                            }
                        }

                        if (lastMatchSuccess) {
                            //reset trier
                            for (int i = 0; i < cellsTriedBySolver.length; i++) {
                                for (int j = 0; j < cellsTriedBySolver[i].length; j++) {
                                    cellsTriedBySolver[i][j] = false;
                                }
                            }
                            firstMade = false;
                        }
                    }

                }
            }
        }, 500, (int) (2000.0 / (double) (game_logic.num_cells_width * game_logic.num_cells_height / 2)));
        System.out.println("exiting auto solver mode");
    }

    public boolean nothing_available_to_select() {
        for (int i = 0; i < game_logic.num_cells_width; i++) {
            for (int j = 0; j < game_logic.num_cells_height; j++) {
                if (!cellsTriedBySolver[i][j] && !completed_cells[i][j] && hidden_cells[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

}
