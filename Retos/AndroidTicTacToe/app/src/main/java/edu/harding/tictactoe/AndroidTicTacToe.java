package edu.harding.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;

public class AndroidTicTacToe extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    //is the variable who defines when the game is over
    private boolean mGameOver;




    private BoardView mBoardView;

    private int humanCount=0;
    private int tiesCount=0;
    private int androidCount=0;

    public int winner;

    private char PLAYER;
    private int chooseRandPlayer;
    private int chooseShapeRand;

    public int generalComputerPos;

    //sounds
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mInfoCountView;
    private String showCount="";

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;
    static final int DIALOG_ABOUT=2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //menu.getItem(0).setIcon(R.drawable.ic_playingagain_tictactoe);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_sound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer_sound);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }



    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.
                int selected=2;

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog

                            // TODO: Set the diff level of mGame based on which item was selected.
                            if( item==0 ) {
                                mGame.setDifficultyLevel(mGame.getDifficultyLevel().Easy);
                                startNewGame();
                            }
                            else if( item==1) {
                                mGame.setDifficultyLevel(mGame.getDifficultyLevel().Harder);
                                startNewGame();
                            }
                            else {
                                mGame.setDifficultyLevel(mGame.getDifficultyLevel().Expert);
                                startNewGame();
                            }

                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                dialog = builder.create();

                break;

            ///////////////////////////////////////

            case DIALOG_QUIT_ID:
            // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToe.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case DIALOG_ABOUT:

                //AlertDialog.Builder builder = new AlertDialog.Builder(AndroidTicTacToe.this);

                View view = LayoutInflater.from(AndroidTicTacToe.this).inflate(R.layout.about_dialog, null);
                TextView title = (TextView) view.findViewById(R.id.title);
                ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
                title.setText("Hello There!");
                imageButton.setImageResource(R.drawable.ic_my_about);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AndroidTicTacToe.this, "Thank you", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setView(view);
                builder.show();


                break;
        }

        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_tic_tac_toe);


        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoCountView = (TextView) findViewById(R.id.informationCount);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        ///////////
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);
        startNewGame();
    }

    // Set up the game board.
    private void startNewGame(){
        showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
        mInfoCountView.setText(showCount);
        mGameOver=false;
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board

        chooseShapeRand = mGame.mRand.nextInt(2);
        if( chooseShapeRand==1){
            mGame.setShapeHuman('X'); mGame.setShapeComputer('O');
            mBoardView.setPictureHuman(true); mBoardView.setPictureComputer(true);
        }
        else{
            mGame.setShapeHuman('O'); mGame.setShapeComputer('X');
            mBoardView.setPictureHuman(false); mBoardView.setPictureComputer(false);
        }

        chooseRandPlayer= mGame.mRand.nextInt(2);
        if(chooseRandPlayer==1) PLAYER=mGame.HUMAN_PLAYER;
        else PLAYER=mGame.COMPUTER_PLAYER;



        if(PLAYER==mGame.COMPUTER_PLAYER) {
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
        }
        mInfoTextView.setText(R.string.first_human);
        // Human goes first
        //mInfoTextView.setText(R.string.first_human);

    }// End of startNewGame



    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mGame.isEnable(TicTacToeGame.HUMAN_PLAYER, pos) )	{

                // If no winner yet, let the computer make a move

                mInfoTextView.setText(R.string.first_human );
                setMove(TicTacToeGame.HUMAN_PLAYER, pos);


                // If no winner yet, let the computer make a move
                winner = mGame.checkForWinner();
                if (winner == 0) {

                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    handlerTime();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();

                    /////////////
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.first_human );
                }
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    tiesCount++;
                    mGameOver=true;
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    humanCount++;
                    mGameOver=true;
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidCount++;
                    mGameOver=true;
                }

                showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
                mInfoCountView.setText(showCount);

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
    private boolean setMove(char player, int location) {

        generalComputerPos=location;
        if(mGame.isEnable(player, location) ) {
            if(player==mGame.HUMAN_PLAYER){
                mHumanMediaPlayer.start();
                mGame.setMove(mGame.HUMAN_PLAYER, location);
            }
            else handlerTime();


            mBoardView.invalidate(); // Redraw the board
            return true;
        }

        return false;
    }

    private void handlerTime(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mComputerMediaPlayer.start();
                mGame.setMove(mGame.COMPUTER_PLAYER, generalComputerPos);
            }
        }, 200);
    }


}
