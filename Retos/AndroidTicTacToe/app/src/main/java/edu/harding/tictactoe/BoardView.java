package edu.harding.tictactoe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by andres on 17/09/17.
 */

public class BoardView extends View {

    /*mGame esta en null*/



    // Represents the internal state of the game
    private TicTacToeGame mGame;

    // Width of the board grid lines
    public static final int GRID_WIDTH = 10;

    public static Bitmap mHumanBitmap;
    public static Bitmap mComputerBitmap;

    private Paint mPaint;

    private int color = Color.LTGRAY;

    public void initialize() {

        mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setPictureHuman(boolean know){

        if( know )mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_img);
        else mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_img);;
    }

    public void setPictureComputer(boolean know){
        if(know) mComputerBitmap=BitmapFactory.decodeResource(getResources(), R.drawable.o_img);
        else mComputerBitmap =BitmapFactory.decodeResource(getResources(), R.drawable.x_img);

    }

    public BoardView(Context context) {
        super(context);
        initialize();
    }
    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }
    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void setGame(TicTacToeGame game) { mGame = game; }

    public int getBoardCellWidth() { return getWidth() / 3;  }

    public int getBoardCellHeight() { return getHeight() / 3; }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        // Make thick, light gray lines
        mPaint.setColor(this.color);
        mPaint.setStrokeWidth(GRID_WIDTH);

        // Draw the two vertical board lines
        int cellWidth = boardWidth / 3;
        canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);

        // Draw the two horizontal board lines
        canvas.drawLine( 0 ,cellWidth, boardWidth, cellWidth, mPaint);
        canvas.drawLine( 0, cellWidth * 2, boardWidth, cellWidth * 2, mPaint);

        // Draw all the X and O images
        for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            // Define the boundaries of a destination rectangle for the image
            int left = col*cellWidth;
            int top = row*cellWidth;
            int right = left + cellWidth;
            int bottom = top + cellWidth;

            if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
                canvas.drawBitmap(mHumanBitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            }
            else if (mGame != null && mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
                canvas.drawBitmap(mComputerBitmap,
                        null,  // src
                        new Rect(left, top, right, bottom),  // dest
                        null);
            }
        }


    }

    public int getColor(){ return color;}

    public void setColor(int color){
        this.color= color;
    }


}
