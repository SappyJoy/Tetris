import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

public class Tetris extends JPanel {
    private final Point[][][] myPoint= {
            {
                    // I
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3)},

            },
            {
                    // J
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 0)},

            },
            {
                    // L
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 0)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2)},
                    {new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 0)},

            },
            {
                    // O
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 0), new Point(1, 1)},

            },
            {
                    // T
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 0)},
                    {new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(1, 0)},
                    {new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2)},
                    {new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2, 1)},

            },
            {
                    // Z
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(2, 0)},
                    {new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2)},
                    {new Point(0, 1), new Point(1, 1), new Point(1, 0), new Point(2, 0)},

            },
            {
                    // Z
                    {new Point(0, 2), new Point(0, 1), new Point(1, 1), new Point(1, 0)},
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},
                    {new Point(0, 2), new Point(0, 1), new Point(1, 1), new Point(1, 0)},
                    {new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1)},

            },

    };

    private final Color[] myColor = {Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.YELLOW,
                                    Color.BLUE, Color.GREEN, Color.RED};

    private Point pt;
    private int currentPiece;
    private int rotation;
    private ArrayList<Integer> nextPiece = new ArrayList<>();
    private long score;
    private Color[][] well;
    static boolean pressedSpace = false;
    private double time = 1.0;

    private void init() {
        well = new Color[12][24];
        score = 0;
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if (i == 0 || i == 11 || j ==22) {
                    well[i][j] = Color.PINK;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    public void newPiece() {
        pt = new Point(5, 1);
        rotation = 0;
        if (nextPiece.isEmpty()) {
            Collections.addAll(nextPiece, 0, 1, 2, 3, 4, 5, 6);
        }
        Collections.shuffle(nextPiece);
        currentPiece = nextPiece.get(0);
    }

    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : myPoint[currentPiece][rotation]) {
            if (well[p.x+x][p.y+y+1] != Color.BLACK) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    private void rotate(int i) {
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0)
            newRotation = 3;
        if (!collidesAt(pt.x, pt.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    public void move(int i) {
        boolean can = true;
        for (Point p : myPoint[currentPiece][rotation]) {
            if(well[p.x + pt.x + i][p.y + pt.y] != Color.BLACK)
                can = false;
        }
        if (can && !collidesAt(pt.x, pt.y, rotation)) {
            pt.x += i;
        }
        repaint();
    }

    public void drop() {
        if (!collidesAt(pt.x, pt.y, rotation)) {
            pt.y += 1;
        } else {
            if (pt.x == 5 && pt.y == 1)
                init();
            else {
                fixToWell();
            }
        }
        repaint();
    }

    public void fixToWell() {
        for (Point p : myPoint[currentPiece][rotation]) {
            well[pt.x + p.x][pt.y + p.y] = myColor[currentPiece];
        }
        clearRows();
        newPiece();
    }

    public void deleteRow(int row) {
        for (int j = row; j > 0; j--)
            for (int i = 1; i < 11; i++) {
                well[i][j] = well[i][j-1];
            }
    }

    public void clearRows() {
        boolean gap;
        int numClear = 0;
        for (int j = 21; j > 0; j--) {
            gap = false;
            for (int i = 1; i < 11; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClear += 1;
                time *= 1.3;
            }
        }
        switch (numClear) {
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }


    private void drawPiece(Graphics g) {
        g.setColor(myColor[currentPiece]);
        for (Point p : myPoint[currentPiece][rotation]) {
            g.fillRect((p.x + pt.x)*26, (pt.y + p.y)*26, 25, 25);
        }
    }

    public void paintComponent(Graphics g) {
        g.fillRect(0, 0, 26*12, 26*23);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                g.setColor(well[i][j]);
                g.fillRect(26*i, 26*j, 25, 25);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Score is " + score, 19*2, 25);
        drawPiece(g);
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Tetris");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(12*26+10, 26*23+25);
        f.setVisible(true);

        final Tetris tetris = new Tetris();
        tetris.init();
        f.add(tetris);

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    tetris.drop();
                    tetris.score += 1;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    tetris.move(-1);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    tetris.move(+1);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        tetris.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        tetris.rotate(+1);
                        break;
                }
            }
        });
        new Thread() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(400 - (int)tetris.time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tetris.drop();
                }
            }
        }.start();
    }
}
