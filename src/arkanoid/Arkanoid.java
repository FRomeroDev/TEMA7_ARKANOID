/**
 * 
 */
package arkanoid;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class Arkanoid extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;

    /* CONSTANTS */
	/* Añado esta chorradilla pra probar github*/

    public static final int SCREEN_WIDTH = 1050; // Cambiado tamaño de pantalla ancho y alto
    public static final int SCREEN_HEIGHT = 700;

    public static final double BALL_RADIUS = 10.0; // Radio de la bola
    public static final double BALL_VELOCITY = 0.5; // Velocidad de la bola

    public static final double PADDLE_WIDTH = 70.0; // Anchura de nave
    public static final double PADDLE_HEIGHT = 10.0; // Grosor de nave
    public static final double PADDLE_VELOCITY = 0.8; // Aumentada velocidad de la nave

    public static final double BLOCK_WIDTH = 60.0;
    public static final double BLOCK_HEIGHT = 20.0;

    public static final int COUNT_BLOCKS_X = 15; // Cambiado el número de bloques o ladrillos por columnas y filas
    public static final int COUNT_BLOCKS_Y = 6;

    public static final int PLAYER_LIVES = 5;

    public static final double FT_SLICE = 1.0; // Ralentización bola
    public static final double FT_STEP = 1.0; // Aceleración bola

    private static final String FONT = "Courier New";

    /* GAME VARIABLES */

    private boolean tryAgain = false;
    private boolean running = false;
    private int scoreRed = 0;
    private int scoreBlue = 0;
    private int scoreGreen = 0;
    private int scoreYellow = 0;
    int destroyedBlocks = 0;

    /* OBJECTS */

    private Paddle paddle = new Paddle(SCREEN_WIDTH / 2, SCREEN_HEIGHT - 50);
    private Ball ball = new Ball(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
    private List<Brick> bricks = new ArrayList<Arkanoid.Brick>();
    private Color[] colors = { Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE };

    private ScoreBoard scoreboard = new ScoreBoard();

    private double lastFt;
    private double currentSlice;

    /**
     * Declaration of game object which is the block of bricks
     * 
     * @author
     */
    abstract class GameObject {

	abstract double left();

	abstract double right();

	abstract double top();

	abstract double bottom();
    }

    /**
     * Descending of abstract superclass GameObject creates block of bricks
     * according to coordinates x and y on screen
     * 
     * @author
     */
    class Rectangle extends GameObject {

	double x, y;
	double sizeX;
	double sizeY;

	double left() {
	    return x - sizeX / 2.0;
	}

	double right() {
	    return x + sizeX / 2.0;
	}

	double top() {
	    return y - sizeY / 2.0;
	}

	double bottom() {
	    return y + sizeY / 2.0;
	}

    }

    /**
     * Creates individual bricks adjusting the size, color etc.
     * 
     * @author
     */
    class Brick extends Rectangle {

	Random azar = new Random();
	int colorIndex = azar.nextInt(4);

	boolean destroyed = false;

	/**
	 * 
	 * @param x
	 * @param y
	 */
	Brick(double x, double y) {
	    this.x = x;
	    this.y = y;
	    this.sizeX = BLOCK_WIDTH;
	    this.sizeY = BLOCK_HEIGHT;
	}

	/**
	 * Creates the color of the bricks according the coordinates X and Y and if pair
	 * or odd.
	 * 
	 * @param g
	 */

	void draw(Graphics g) {

	    g.setColor(colors[colorIndex]);
	    g.fillRect((int) left(), (int) top(), (int) sizeX, (int) sizeY);

	    /*
	     * if (this.x % 2 == 0) { g.setColor(Color.RED); if (this.y % 2 == 0) {
	     * g.setColor(Color.YELLOW); } } else { g.setColor(Color.BLUE); if (this.y % 2
	     * == 0) { g.setColor(Color.GREEN); } }
	     */

	}

	/**
	 * 
	 * @param colorIndex
	 * @return
	 */
	int calculateScoreByColorBrick(int index) {

	    int redPoints = 0;
	    int greenPoints = 0;
	    int yellowPoints = 0;
	    int bluePoints = 0;

	    index = colorIndex;
	    switch (index) {
	    case 0:
		redPoints++;
		break;
	    case 1:
		greenPoints++;
		break;
	    case 2:
		yellowPoints++;
		break;
	    default:
		bluePoints++;
		break;
	    }

	    return redPoints + greenPoints + yellowPoints + bluePoints;
	}
    }

    /**
     * 
     * @author
     */
    class Paddle extends Rectangle {

	double velocity = 0.0;

	/**
	 * Constructor which takes the size from constant variables PADDLE_WIDTH and
	 * PADDLE_HEIGHT.
	 * 
	 * @param x x coordinate to create rectangle
	 * @param y y coordinate to create rectangle
	 */
	public Paddle(double x, double y) {
	    this.x = x;
	    this.y = y;
	    this.sizeX = PADDLE_WIDTH;
	    this.sizeY = PADDLE_HEIGHT;
	}

	/**
	 * 
	 */
	void update() {
	    x += velocity * FT_STEP;
	}

	/**
	 * 
	 */
	void stopMove() {
	    velocity = 0.0;
	}

	/**
	 * 
	 */
	void moveLeft() {
	    if (left() > 0.0) {
		velocity = -PADDLE_VELOCITY;
	    } else {
		velocity = 0.0;
	    }
	}

	/**
	 * 
	 */
	void moveRight() {
	    if (right() < SCREEN_WIDTH) {
		velocity = PADDLE_VELOCITY;
	    } else {
		velocity = 0.0;
	    }
	}

	void draw(Graphics g) {
	    g.setColor(Color.WHITE);
	    g.fillRect((int) (left()), (int) (top()), (int) sizeX, (int) sizeY);
	}

    }

    /**
     * 
     * @author
     */
    class Ball extends GameObject {

	double x, y;
	double radius = BALL_RADIUS;
	double velocityX = BALL_VELOCITY;
	double velocityY = BALL_VELOCITY;

	Ball(int x, int y) {
	    this.x = x;
	    this.y = y;
	}

	double left() {
	    return x - radius;
	}

	double right() {
	    return x + radius;
	}

	double top() {
	    return y - radius;
	}

	double bottom() {
	    return y + radius;
	}

	void draw(Graphics g) {
	    g.setColor(Color.RED);
	    g.fillOval((int) left(), (int) top(), (int) radius * 1, (int) radius * 1);
	}

	void update(ScoreBoard scoreBoard, Paddle paddle) {

	    int totalBlocks = COUNT_BLOCKS_X * COUNT_BLOCKS_Y;

	    x += velocityX * FT_STEP;
	    y += velocityY * FT_STEP;

	    if (left() < 0)
		velocityX = BALL_VELOCITY;
	    else if (right() > SCREEN_WIDTH)
		velocityX = -BALL_VELOCITY;
	    if (top() < 0) {
		velocityY = BALL_VELOCITY;
	    } else if (bottom() > SCREEN_HEIGHT) {
		velocityY = -BALL_VELOCITY;
		x = paddle.x;
		y = paddle.y - 50;
		scoreBoard.die();
	    }

	    if ((totalBlocks - destroyedBlocks) < 20) {

		x += velocityX * FT_STEP * 0.5;
		y += velocityY * FT_STEP * 0.5;
	    } else if ((totalBlocks - destroyedBlocks) < 10) {

		x += velocityX * FT_STEP * 1;
		y += velocityY * FT_STEP * 1;
	    }

	}
    }

    /**
     * Creates and control scoreboard. Includes 4 functions (increaseScore, die,
     * updateScoreboard, draw).
     * 
     * @author
     */
    public class ScoreBoard {

	Brick brickPoints = new Brick(0, 0);
	int index = 0;
	int totalPoints = brickPoints.calculateScoreByColorBrick(index);

	int score = 0;
	int lives = PLAYER_LIVES;
	boolean win = false;
	boolean gameOver = false;
	String text = "";

	Font font;

	/**
	 * Constructor
	 */
	ScoreBoard() {
	    font = new Font(FONT, Font.BOLD, 12);
	    text = "Welcome to Arkanoid Java version";
	}

	/**
	 * Increases score on 1 point with each break brick if destroyed brick is red.
	 * 
	 */
	public void increaseScoreRed() {
	    scoreRed++;
	    score = scoreRed + scoreBlue + scoreGreen + scoreYellow;

	    if (score == totalPoints) {
		win = true;
		text = "You have won! \nYour score was: " + score + "\n\nPress Enter to restart";
	    } else {
		updateScoreboard();
	    }
	}

	/**
	 * Increases score on 2 points with each break brick if destroyed brick is
	 * green.
	 * 
	 */
	void increaseScoreGreen() {
	    scoreGreen += 2;
	    score = scoreRed + scoreBlue + scoreGreen + scoreYellow;

	    if (score == totalPoints) {
		win = true;
		text = "You have won! \nYour score was: " + score + " points" + "\n\nPress Enter to restart";
	    } else {
		updateScoreboard();
	    }
	}

	/**
	 * Increases score on 5 points with each break brick if destroyed brick is
	 * yellow.
	 * 
	 */
	void increaseScoreYellow() {
	    scoreYellow += 5;
	    score = scoreRed + scoreBlue + scoreGreen + scoreYellow;

	    if (score == totalPoints) {
		win = true;
		text = "You have won! \nYour score was: " + score + " points" + "\n\nPress Enter to restart";
	    } else {
		updateScoreboard();
	    }
	}

	/**
	 * Increases score on 7 points with each break brick if destroyed brick is blue.
	 * 
	 */
	void increaseScoreBlue() {
	    scoreBlue += 7;
	    score = scoreRed + scoreBlue + scoreGreen + scoreYellow;

	    if (score == totalPoints) {
		win = true;
		text = "You have won! \nYour score was: " + score + " points" + "\n\nPress Enter to restart";
	    } else {
		updateScoreboard();
	    }
	}

	/**
	 * Decreases lives when ball overcome the barrier of the ship and if lives is
	 * equals to 0 you lose.
	 */
	void die() {
	    lives--;
	    if (lives == 0) {
		gameOver = true;
		text = "You have lose! \nYour score was: " + score + " points" + "\n\nPress Enter to restart";
	    } else {
		updateScoreboard();
	    }
	}

	/**
	 * FER: UPDATE Upadate score board and every some limits of break bricks and 1
	 * live is added.
	 */
	void updateScoreboard() {
	    text = "Score: " + score + "  Lives: " + lives;

	    if ((score == 50 || score == 90) || (score == 150 || score == 200)) {
		lives += 1;
		text = "Yo got 1 more live, \n Let´s gooo.. ";
	    }
	}

	/**
	 * Shows text with live scoreboard and shows final text if you win or lose
	 * adapted to screen.
	 * 
	 * @param g
	 */
	void draw(Graphics g) {

	    if (win || gameOver) {
		font = font.deriveFont(50f);
		FontMetrics fontMetrics = g.getFontMetrics(font);
		g.setColor(Color.WHITE);
		g.setFont(font);
		int titleHeight = fontMetrics.getHeight();
		int lineNumber = 1;
		for (String line : text.split("\n")) {
		    int titleLen = fontMetrics.stringWidth(line);
		    g.drawString(line, (SCREEN_WIDTH / 2) - (titleLen / 2),
			    (SCREEN_HEIGHT / 3) + (titleHeight * lineNumber));
		    lineNumber++;

		}
	    } else {
		font = font.deriveFont(34f);
		FontMetrics fontMetrics = g.getFontMetrics(font);
		g.setColor(Color.YELLOW);
		g.setFont(font);
		int titleLen = fontMetrics.stringWidth(text);
		int titleHeight = fontMetrics.getHeight();
		g.drawString(text, (SCREEN_WIDTH / 2) - (titleLen / 2), titleHeight + 12);

	    }
	}

    }

    /**
     * Return boolean true or false if there is collision taking into account the
     * coordinates of ball and bricks.
     * 
     * @param mA mA represents an object for the bricks
     * @param mB mB represents an object for the ball
     * @return true or false
     */
    boolean isIntersecting(GameObject mA, GameObject mB) {
	return mA.right() >= mB.left() && mA.left() <= mB.right() && mA.bottom() >= mB.top() && mA.top() <= mB.bottom();
    }

    /**
     * It allows the ball to bounce off the paddle in a predictable way after a
     * collision.
     * 
     * The function first checks if the ball and paddle are colliding with each
     * other using the isIntersecting function. If there is no collision, the
     * function does nothing.
     * 
     * If there is a collision, the function changes the direction of the ball on in
     * the opposite direction of where it was moving before the collision.
     * Additionally, the function also changes the direction of the ball depending
     * on where it hit the paddle. If the ball hits the paddle to the left of
     * center, it moves to the left, and if it hits the paddle to the right of
     * center, it moves to the right.
     * 
     * @param mPaddle
     * @param mBall
     */
    void testCollision(Paddle mPaddle, Ball mBall) {
	if (!isIntersecting(mPaddle, mBall))
	    return; // Si no hay colisión sale de la función y no hace nada.
	mBall.velocityY = -BALL_VELOCITY;
	if (mBall.x < mPaddle.x)
	    mBall.velocityX = -BALL_VELOCITY;
	else
	    mBall.velocityX = BALL_VELOCITY;
    }

    /**
     * Checks if there is a collision between a brick and the ball. If there is no
     * collision the function does nothing if there is a collision the brcik is
     * destroyed and the score is updates according the color of the brick.
     * 
     * Also, the function calculates the overlap of the ball and brick taking into
     * account the frame (right, left, top and bottom) and set the ball direction to
     * go to the opposite direction.
     * 
     * @param mBrick
     * @param mBall
     * @param scoreboard
     */
    void testCollision(Brick mBrick, Ball mBall, ScoreBoard scoreboard) {
	if (!isIntersecting(mBrick, mBall))
	    return; // Si no hay colisión sale de la función y no hace nada.
	mBrick.destroyed = true;

	if (mBrick.colorIndex == 0) {
	    scoreboard.increaseScoreRed();
	    destroyedBlocks++;
	} else if (mBrick.colorIndex == 1) {
	    scoreboard.increaseScoreGreen();
	    destroyedBlocks++;
	} else if (mBrick.colorIndex == 2) {
	    scoreboard.increaseScoreYellow();
	    destroyedBlocks++;
	} else {
	    scoreboard.increaseScoreBlue();
	    destroyedBlocks++;
	}

	// scoreboard.increaseScore();

	double overlapLeft = mBall.right() - mBrick.left();
	double overlapRight = mBrick.right() - mBall.left();
	double overlapTop = mBall.bottom() - mBrick.top();
	double overlapBottom = mBrick.bottom() - mBall.top();

	boolean ballFromLeft = overlapLeft < overlapRight;
	boolean ballFromTop = overlapTop < overlapBottom;

	double minOverlapX = ballFromLeft ? overlapLeft : overlapRight;
	double minOverlapY = ballFromTop ? overlapTop : overlapBottom;

	if (minOverlapX < minOverlapY) {
	    mBall.velocityX = ballFromLeft ? -BALL_VELOCITY : BALL_VELOCITY;
	} else {
	    mBall.velocityY = ballFromTop ? -BALL_VELOCITY : BALL_VELOCITY;
	}
    }

    /**
     * Creates the list of bricks cleaning from initially and then filling the
     * arraylist again.
     * 
     * @param bricks
     */
    void initializeBricks(List<Brick> bricks) {
	// deallocate old bricks
	bricks.clear();

	for (int iX = 0; iX < COUNT_BLOCKS_X; ++iX) {
	    for (int iY = 0; iY < COUNT_BLOCKS_Y; ++iY) {
		bricks.add(new Brick((iX + 1) * (BLOCK_WIDTH + 3) + 22, (iY + 2) * (BLOCK_HEIGHT + 3) + 20));
	    }
	}
    }

    /**
     * Creates main class Arkanoid which invoke several functions:
     * setDefaultCloseOperation setUndecorated setResizable setVisible
     * addKeyListener setLocationRelativeTo createBufferStrategy initializeBricks
     */
    public Arkanoid() {

	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.setUndecorated(false);
	this.setResizable(false);
	this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
	this.setVisible(true);
	this.addKeyListener(this);
	this.setLocationRelativeTo(null);

	this.createBufferStrategy(2);

	initializeBricks(bricks);

    }

    /**
     * Creates main loop of the game which update of screen and time control is
     * managed.
     */
    void run() {

	BufferStrategy bf = this.getBufferStrategy();
	Graphics g = bf.getDrawGraphics();
	g.setColor(Color.black);
	g.fillRect(0, 0, getWidth(), getHeight());

	running = true;

	while (running) {

	    long time1 = System.currentTimeMillis();

	    if (!scoreboard.gameOver && !scoreboard.win) {
		tryAgain = false;
		update();
		drawScene(ball, bricks, scoreboard);

		// to simulate low FPS
		try {
		    Thread.sleep(10);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}

	    } else {
		if (tryAgain) {
		    tryAgain = false;
		    initializeBricks(bricks);
		    scoreboard.lives = PLAYER_LIVES;
		    scoreboard.score = 0;
		    scoreboard.win = false;
		    scoreboard.gameOver = false;
		    scoreboard.updateScoreboard();
		    ball.x = SCREEN_WIDTH / 2;
		    ball.y = SCREEN_HEIGHT / 2;
		    paddle.x = SCREEN_WIDTH / 2;
		}
	    }

	    long time2 = System.currentTimeMillis();
	    double elapsedTime = time2 - time1;

	    lastFt = elapsedTime;

	    double seconds = elapsedTime / 1000.0;
	    if (seconds > 0.0) {
		double fps = 1.0 / seconds;
		this.setTitle("FPS: " + fps);
	    }

	}

	this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

    }

    /**
     * Updates the paddle, bricks and the ball state according the time elapsed
     * since last update.
     */
    private void update() {

	currentSlice += lastFt;

	for (; currentSlice >= FT_SLICE; currentSlice -= FT_SLICE) {

	    ball.update(scoreboard, paddle);
	    paddle.update();
	    testCollision(paddle, ball);

	    Iterator<Brick> it = bricks.iterator();
	    while (it.hasNext()) {
		Brick brick = it.next();
		testCollision(brick, ball, scoreboard);
		if (brick.destroyed) {
		    it.remove();
		}
	    }

	}
    }

    /**
     * Creates the draw scene on the frame using several functions to set the
     * graphics to draw the paddle, ball, bricks and background.
     * 
     * @param ball
     * @param bricks
     * @param scoreboard
     */
    private void drawScene(Ball ball, List<Brick> bricks, ScoreBoard scoreboard) {
	// Code for the drawing goes here.
	BufferStrategy bf = this.getBufferStrategy();
	Graphics g = null;

	try {

	    g = bf.getDrawGraphics();

	    g.setColor(Color.black);
	    g.fillRect(0, 0, getWidth(), getHeight());

	    ball.draw(g);
	    paddle.draw(g);
	    for (Brick brick : bricks) {
		brick.draw(g);
	    }
	    scoreboard.draw(g);

	} finally {
	    g.dispose();
	}

	bf.show();

	Toolkit.getDefaultToolkit().sync();

    }

    /**
     * Implements the interface keyListener to manage the key events when player is
     * using the keyboard to control the game.
     */
    @Override
    public void keyPressed(KeyEvent event) {
	if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
	    running = false;
	}
	if (event.getKeyCode() == KeyEvent.VK_ENTER) {
	    tryAgain = true;
	}
	switch (event.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	    paddle.moveLeft();
	    break;
	case KeyEvent.VK_RIGHT:
	    paddle.moveRight();
	    break;
	default:
	    break;
	}
    }

    /**
     * Implements the interface keyListener to manage the key events when player is
     * using the keyboard to control the game.
     */
    @Override
    public void keyReleased(KeyEvent event) {
	switch (event.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_RIGHT:
	    paddle.stopMove();
	    break;
	default:
	    break;
	}
    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    /**
     * Main function for the starting the game creating and instance of main class
     * Arkanoid and the loop run to keep the game updated while an user is playingg
     * 
     * @param args
     */
    public static void main(String[] args) {
	new Arkanoid().run();
    }

}
