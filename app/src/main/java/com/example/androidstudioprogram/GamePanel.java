package com.example.androidstudioprogram;

// Import context object
import android.content.Context;
// Import Canvas for drawing
import android.graphics.Canvas;
// Import touch event support
import android.view.MotionEvent;
// Import SurfaceHolder
import android.view.SurfaceHolder;
// Import SurfaceView
import android.view.SurfaceView;
import android.view.View;

// SurfaceView handles rendering
// Runnable allows a separate game thread
public class GamePanel extends SurfaceView implements Runnable
{
    // Thread used for the game loop
    private Thread gameThread;

    // controls whether the game loop continues running
    private boolean running = false;

    // gives access to the drawing surface
    private SurfaceHolder surfaceHolder;

    // contains game logic and game objects
    private GameEngine gameEngine;

    // counts frames rendered
    private long frameCount = 0;

    // stores last FPS calculation time
    private long lastFpsTime = 0;

    // frames per second value
    private int fps = 0;

    // constructor
    public GamePanel(Context context)
    {
        // call surfaceView constructor
        super(context);

        // obtain surfaceHolder from surfaceView
        surfaceHolder = getHolder();

        // create GameEngine object
        gameEngine = new GameEngine(context);

        // allow view to receive focus and touch input
        setFocusable(true);
    }

    // Main game loop
    @Override
    public void run()
    {
        // save current time for FPS calculations
        lastFpsTime = System.currentTimeMillis();

        // continue loop while game is running
        while(running)
        {
            // update game objects
            gameEngine.update();

            // draw everything
            drawGame();

            // compute FPS
            calculateFPS();

            try {
                // pause approximately 16ms
                // ~60 FPS target
                Thread.sleep(16);
            }
            catch(InterruptedException e){
                // print error if thread interrupted
                e.printStackTrace();
            }
        }
    }

    // draw one frame
    private void drawGame()
    {
        // check whether surface is ready
        if(!surfaceHolder.getSurface().isValid())
        {
            return;
        }

        // lock drawing canvas
        Canvas canvas = surfaceHolder.lockCanvas();

        // ensure canvas exists
        if(canvas != null)
        {
            // ask GameEngine to draw game objects
            gameEngine.draw(canvas, fps);

            // show frame on screen
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // calculate FPS value
    private void calculateFPS()
    {
        // increase frame counter
        frameCount++;

        // current system time
        // current time -NOW
        long currentTime = System.currentTimeMillis();

        // every 1 second
        if(currentTime - lastFpsTime >= 1000)
        {
            // FPS equals number of frames drawn
            fps = (int) frameCount;

            // reset counter
            frameCount = 0;

            // reset timer
            lastFpsTime = currentTime;
        }
    }

    // start game thread
    public void startGame()
    {
        // enable game loop
        running = true;

        // create thread
        gameThread = new Thread(this);

        // start thread
        gameThread.start();
    }

    // stop game thread
    public void stopGame()
    {
        // stop loop running
        running = false;

        try {
            // ensure thread exists
            if(gameThread != null)
            {
                // wait until thread finishes
                gameThread.join();
            }
        }
        catch(InterruptedException e)
        {
            // print error
            e.printStackTrace();
        }
    }

    // handle touch events
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // send touch information to GameEngine
        gameEngine.handleTouch(event);

        // continue receiving touch events
        return true;
    }

    // called when view becomes visible
    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        // start game automatically
        startGame();
    }

    // called when view is removed
    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        // stom game safely
        stopGame();
    }
}
