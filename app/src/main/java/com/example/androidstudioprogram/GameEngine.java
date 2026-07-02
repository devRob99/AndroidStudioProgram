package com.example.androidstudioprogram;

// Import context class from android
import android.content.Context;

// Import canvas class for drawing on screen
import android.graphics.Canvas;

// Import color class for using predefined colors
import android.graphics.Color;

// Import paint class for setting drawing color, size, and style
import android.graphics.Paint;

// Import MotionEvent class for handling touch input
import android.view.MotionEvent;

// Import random class for generating random numbers
import java.util.Random;

public class GameEngine
{
    // paint object used to draw shapes and text
    private Paint paint;

    // random object used to generate random positions and speeds
    private Random random;

    // default screen width before real canvas size is known
    private int screenWidth = 1000;

    // default screen height before real canvas size is known
    private int screenHeight = 1800;

    // number of stars in the background
    private int starCount = 80;

    // array to store x position of each star
    private float[] starX = new float[starCount];

    // array to store y position of each star
    private float[] starY = new float[starCount];

    // array to store falling speed of each star
    private float[] starSpeed = new float[starCount];

    // player starting X position
    private float playerX = 400;

    // player starting Y position
    private float playerY = 1300;

    // player rectangle width
    private int playerWidth = 140;

    // player rectangle height
    private int playerHeight = 100;

    // maximum number of bullets allowed at one time
    private int bulletCount = 5;

    // array to store X position of each bullet
    private float[] bulletX = new float[bulletCount];

    // array to store Y position of each bullet
    private float[] bulletY = new float[bulletCount];

    // array to store whether each bullet is active or inactive
    private boolean[] bulletActive = new boolean [bulletCount];

    // speed of bullet movement
    private float bulletSpeed = 25;

    // radius of each bullet circle
    private int bulletRadius = 12;

    // nuber of enemies in the game
    private int enemyCount = 4;

    // array to store X position of each enemy
    private float[] enemyX = new float[enemyCount];

    // array to store Y position of each enemy
    private float[] enemyY = new float[enemyCount];

    // array to store falling speed of each enemy
    private float[] enemySpeed = new float[enemyCount];

    // enemy square size
    private int enemySize = 90;

    // player score
    private int score = 0;

    // player lives
    private int lives = 3;

    // current game level
    private int level = 1;

    // stores whether the game is over
    private boolean gameOver = false;

    // constructor of GameEngine
    public GameEngine(Context context)
    {
        // create Paint object
        paint = new Paint();

        // make drawing smoother
        paint.setAntiAlias(true);

        // create random object
        random = new Random();

        // Initialize background stars
        initializeStars();

        // Initialize enemies
        initializeEnemies();
    }

    public void update()
    {
        // if game is over, stop updating
        if(gameOver)
        {
            // exit the method
            return;
        }

        // update current level based on score
        updateLevel();

        // move stars
        updateStars();

        // move bullets
        updateBullets();

        // move enemies
        updateEnemies();

        // check bullet and enemy collisions
        checkCollisions();
    }

    public void draw(Canvas canvas, int fps)
    {
        // get actual screen width from canvas
        screenWidth = canvas.getWidth();

        // get actual screen height from canvas
        screenHeight = canvas.getHeight();

        // fill background with black color
        canvas.drawColor(Color.BLACK);

        // draw moving stars
        drawStars(canvas);

        // draw player
        drawPlayer(canvas);

        // draw bullets
        drawBullets(canvas);

        // draw enemies
        drawEnemies(canvas);

        // draw score, lives, level, and fps
        drawHUD(canvas, fps);

        // cehck if game is over
        if(gameOver)
        {
            // draw game over screen
            drawGameOver(canvas);
        }
    }

    // initialize all stars
    private void initializeStars()
    {
        // loop through all stars
        for(int i = 0; i < starCount; i++)
        {
            // set random x position
            starX[i] = random.nextInt(1000);

            // set random y position
            starY[i] = random.nextInt(1000);

            // set random speed from 4 to 11
            starSpeed[i] = 4 + random.nextInt(8);
        }
    }

    // initialize all enemies
    private void initializeEnemies()
    {
        // loop through all enemies
        for(int i = 0; i < enemyCount; i++)
        {
            // set random x position
            enemyX[i] = random.nextInt(900);

            // set random y position above the screen
            enemyY[i] = random.nextInt(1200);

            // set random enemy speed from 6 to 10
            enemySpeed[i] = 6 + random.nextInt(5);
        }
    }

    // update star positions
    private void updateStars()
    {
        // loop through all stars
        for(int i = 0; i < starCount; i++)
        {
            // move star downward
            starY[i] = starY[i] + starSpeed[i];

            // if star moves below screen
            if(starY[i] > screenHeight)
            {
                // move star back to top
                starY[i] = 0;

                // give star a new random X position
                starX[i] = random.nextInt(Math.max(1, screenWidth));
            }
        }
    }

    // update bullet positions
    private void updateBullets()
    {
        // loop through all bullets
        for(int i = 0; i < bulletCount; i++)
        {
            // check if bullet is active
            if(bulletActive[i])
            {
                // move bullet upward
                bulletY[i] = bulletY[i] - bulletSpeed;

                // if bullet leaves top of screen
                if(bulletY[i] < 0)
                {
                    // deactivate bullet
                    bulletActive[i] = false;
                }
            }
        }
    }

    // update enemy position
    private void updateEnemies()
    {
        // loop through all enemies
        for(int i = 0; i < enemyCount; i++)
        {
            // move enemy downward
            enemyY[i] = enemyY[i] + enemySpeed[i];

            // if enemy passes bottom of screen
            if(enemyY[i] > screenHeight)
            {
                // reduce one life
                lives--;

                // reset enemy position
                resetEnemy(i);

                // check if player has no lives left
                if(lives <= 0)
                {
                    // set game over state
                    gameOver = true;
                }
            }
        }
    }

    // check collision between bullets and enemies
    private void checkCollisions()
    {
        // loop through all bullets
        for(int b = 0; b < bulletCount; b++)
        {
            // only check active bullets
            if(bulletActive[b])
            {
                // loop through all enemies
                for(int e = 0; e < enemyCount; e++)
                {
                    // check if bullet position is inside enemy rectangle
                    if(bulletX[b] > enemyX[e] && bulletX[b] < enemyX[e] + enemySize && bulletY[b] > enemyY[e] && bulletY[b] < enemyY[e] + enemySize)
                    {
                        // increase score
                        score++;

                        // deactivate bulleet after hit
                        bulletActive[b] = false;

                        // move enemy back above screen
                        resetEnemy(e);

                        // stop checking other enmies for this bullet
                        break;
                    }
                }
            }
        }
    }

    // reset enemy position after hit or passing screen
    private void resetEnemy(int index)
    {
        // calculate maximum X position for enemy
        int maxX = screenWidth - enemySize;

        // if screen has enough width
        if(maxX > 0)
        {
            // set random x position within screen
            enemyX[index] = random.nextInt(maxX);
        }
        else
        {
            // use default x position if screen width is too small
            enemyX[index] = 100;
        }

        // move enemy above screen
        enemyY[index] = -random.nextInt(800);

        // increase enemy speed based on level
        enemySpeed[index] = 5 + level * 3 + random.nextInt(4);
    }

    // update level according to score
    private void updateLevel()
    {
        // if score is 20 or more
        if(score >= 20)
        {
            // set level 4
            level = 4;
        }
        // if score is 10 or more
        else if(score >= 10)
        {
            // set level 3
            level = 3;
        }
        // if score is 5 or more
        else if(score >= 5)
        {
            // set level 2
            level = 2;
        }
        // otherwise
        else
        {
            // keep level 1
            level = 1;
        }
    }

    // draw background stars
    private void drawStars(Canvas canvas)
    {
        // set star color to white
        paint.setColor(Color.WHITE);

        // loop through all stars
        for(int i = 0; i < starCount; i++)
        {
            // draw each star as a small circle
            canvas.drawCircle(starX[i], starY[i], 3, paint);
        }
    }

    // draw player object
    private void drawPlayer(Canvas canvas)
    {
        // set player body color to cyan
        paint.setColor(Color.CYAN);

        // draw player body as rectangle
        canvas.drawRect(
                playerX,
                playerY,
                playerX + playerWidth,
                playerY + playerHeight,
                paint);

        // set player cockpit color to blue
        paint.setColor(Color.BLUE);

        // draw player cockpit as circle
        canvas.drawCircle(
                playerX + playerWidth / 2,
                playerY + 30,
                25,
                paint);
    }

    // draw active bullets
    private void drawBullets(Canvas canvas)
    {
        // set bullet color to yellow
        paint.setColor(Color.YELLOW);

        // loop through all bullets
        for(int i = 0; i < bulletCount; i++)
        {
            // draw only active bullets
            if(bulletActive[i])
            {
                // draw bullet as circle
                canvas.drawCircle(
                        bulletX[i],
                        bulletY[i],
                        bulletRadius,
                        paint);
            }
        }
    }

    // draw enemies
    private void drawEnemies(Canvas canvas)
    {
        // set enemy color to red
        paint.setColor(Color.RED);

        // loop through all enemies
        for(int i = 0; i < enemyCount; i++)
        {
            // draw enemy as square
            canvas.drawRect(
                    enemyX[i],
                    enemyY[i],
                    enemyX[i] + enemySize,
                    enemyY[i] + enemySize,
                    paint);
        }
    }

    // draw score, lives, level, and fps
    private void drawHUD(Canvas canvas, int fps)
    {
        // set text color to white
        paint.setColor(Color.WHITE);

        // set text size
        paint.setTextSize(42);

        // draw score text
        canvas.drawText("Score: " + score, 40, 70, paint);

        // draw lives text
        canvas.drawText("Lives: " + lives, 40, 120, paint);

        // draw level text
        canvas.drawText("Level: " + level, 40, 170, paint);

        // draw fps text
        canvas.drawText("FPS: " + fps, 40, 220, paint);

        // draw instruction text
        canvas.drawText("Tap to move and shoot", 40, 270, paint);
    }

    // draw game over message
    private void drawGameOver(Canvas canvas)
    {
        // set text color to red
        paint.setColor(Color.RED);

        // set large text size
        paint.setTextSize(85);

        // draw game over text
        canvas.drawText("GAME OVER", 170, 700, paint);

        // set text color to white
        paint.setColor(Color.WHITE);

        // set smaller text size
        paint.setTextSize(50);

        // draw final score
        canvas.drawText("Final Score: " + score, 230, 790, paint);

        // draw restart instruction
        canvas.drawText("Restart app to play again", 140, 870, paint);
    }

    // fire a bullet from player position
    private void fireBullet()
    {
        // loop through all bullets
        for(int i = 0; i < bulletCount; i++)
        {
            // fine first inactive bullet
            if(!bulletActive[i])
            {
                // set bullet X to center of player
                bulletX[i] = playerX + playerWidth / 2;

                // set bullet Y to player top position
                bulletY[i] = playerY;

                // activate bullet
                bulletActive[i] = true;

                // stop after firing one bullet
                break;
            }
        }
    }

    // handle user touch input
    public void handleTouch(MotionEvent event) {
        // if game is over, ignore touch
        if(gameOver)
        {
            // exit method
            return;
        }

        // check if user touched the screen
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            // move plyaer center to touch position
            playerX = event.getX() - playerWidth / 2;

            // if player moves beyond left edge
            if(playerX < 0)
            {
                // keep player inside left boundary
                playerX = 0;
            }

            // if player moves beyond right edge
            if(playerX + playerWidth > screenWidth)
            {
                // keep player inside right boundary
                playerX = screenWidth - playerWidth;
            }

            // fire bullet after touch
            fireBullet();
        }

    }
}