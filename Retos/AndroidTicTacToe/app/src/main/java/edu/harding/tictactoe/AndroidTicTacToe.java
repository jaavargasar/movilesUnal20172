package edu.harding.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
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


    private int difficultLevel=2;


    private BoardView mBoardView;

    private int humanCount=0;
    private int tiesCount=0;
    private int androidCount=0;

    public int winner;

    private char PLAYER;
    private int chooseRandPlayer=-1;
    private int chooseShapeRand=-1;

    public int generalComputerPos;

    //sounds
    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    private  boolean mSoundOn = true;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mInfoCountView;
    private String showCount="";

    //static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_RESET_SCORES = 1;
    static final int DIALOG_ABOUT=2;

    private SharedPreferences mPrefs;

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
                resetRandVariables();
                setBackDifficultLevel();
                startNewGame();
                return true;
            case R.id.ai_settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.reset_scores:
                showDialog(DIALOG_RESET_SCORES);
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
            ///////////////////////////////////////

            case DIALOG_RESET_SCORES:
            // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question) //reset the scores
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //AndroidTicTacToe.this.finish();
                                humanCount=0; tiesCount=0; androidCount=0;
                                showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
                                mInfoCountView.setText(showCount);
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
        mGame = new TicTacToeGame();
        //mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level",
                getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

        // Restore the scores
        humanCount = mPrefs.getInt("mHumanWins", 0);
        androidCount = mPrefs.getInt("mComputerWins", 0);
        tiesCount = mPrefs.getInt("mTies", 0);
        showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
        difficultLevel = mPrefs.getInt("difficultLevelUpdate1",0);





        //mInfoCountView.setText(showCount);
        //mInfoCountView =mPrefs.getString("countInfo","");




        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoCountView = (TextView) findViewById(R.id.informationCount);

        mBoardView = (BoardView) findViewById(R.id.board);

        mBoardView.setColor(mPrefs.getInt("board_color", 0xFFCCCCCC));

        mBoardView.setGame(mGame);
        setBackDifficultLevel();
        ///////////
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        if( savedInstanceState==null) startNewGame();


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the game's state
        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        humanCount = savedInstanceState.getInt("mHumanWins");
        androidCount = savedInstanceState.getInt("mComputerWins");
        tiesCount = savedInstanceState.getInt("mTies");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        PLAYER = savedInstanceState.getChar("mGoFirst");
        chooseRandPlayer= savedInstanceState.getInt("mRandPlayer");
        chooseShapeRand= savedInstanceState.getInt("mRandShape");
        mInfoCountView.setText(savedInstanceState.getCharSequence("countInfo"));
        //difficultLevel =savedInstanceState.getInt("difficultLevelUpdate");

    }

    //guardar las instancias del programa
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(humanCount));
        outState.putInt("mComputerWins", Integer.valueOf(androidCount));
        outState.putInt("mTies", Integer.valueOf(tiesCount));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putChar("mGoFirst", PLAYER);
        outState.putInt("mRandPlayer", Integer.valueOf(chooseRandPlayer) );
        outState.putInt("mRandShape", Integer.valueOf(chooseShapeRand) );
        outState.putCharSequence("countInfo",mInfoCountView.getText());
        //outState.putInt("difficultLevelUpdate", Integer.valueOf(difficultLevel) );

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", humanCount);
        ed.putInt("mComputerWins", androidCount);
        ed.putInt("mTies", tiesCount);
        ed.putInt("difficultLevelUpdate1",difficultLevel);
        ed.putString("countInfo", mInfoCountView.getText().toString() );
        ed.commit();
    }

    // Set up the game board.
    private void startNewGame(){
        showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
        mInfoCountView.setText(showCount);
        mGameOver=false;
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board


        //chooseShapeRand=-1;
        //chooseRandPlayer=-1;




        if( chooseShapeRand == -1) chooseShapeRand = mGame.mRand.nextInt(2);
        if (chooseShapeRand == 1) {
            mGame.setShapeHuman('X');
            mGame.setShapeComputer('O');
            mBoardView.setPictureHuman(true);
            mBoardView.setPictureComputer(true);
        } else {
            mGame.setShapeHuman('O');
            mGame.setShapeComputer('X');
            mBoardView.setPictureHuman(false);
            mBoardView.setPictureComputer(false);
        }


        if( chooseRandPlayer==-1) {
            chooseRandPlayer = mGame.mRand.nextInt(2);
            if (chooseRandPlayer == 1) PLAYER = mGame.HUMAN_PLAYER;
            else PLAYER = mGame.COMPUTER_PLAYER;
        }



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
                    resetRandVariables();
                }
                else if (winner == 2) {
                    String defaultMessage = getResources().getString(R.string.result_human_wins);
                    mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                    humanCount++;

                    mGameOver=true;
                    resetRandVariables();
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidCount++;
                    mGameOver=true;
                    resetRandVariables();
                }

                showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
                mInfoCountView.setText(showCount);

            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private void resetRandVariables(){
        chooseRandPlayer= -1;
        chooseShapeRand=  -1;
    }


    private boolean setMove(char player, int location) {

        generalComputerPos=location;
        if(mGame.isEnable(player, location) ) {
            if(player==mGame.HUMAN_PLAYER){
                if( mSoundOn)  mHumanMediaPlayer.start();
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
                if( mSoundOn ) mComputerMediaPlayer.start();
                mGame.setMove(mGame.COMPUTER_PLAYER, generalComputerPos);
            }
        }, 200);
    }

    private void setBackDifficultLevel(){
        if( difficultLevel==0) mGame.setDifficultyLevel(mGame.getDifficultyLevel().Easy);
        else if(difficultLevel==1)mGame.setDifficultyLevel(mGame.getDifficultyLevel().Harder);
        else mGame.setDifficultyLevel(mGame.getDifficultyLevel().Expert);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);

            mBoardView.setColor(mPrefs.getInt("board_color", 0xFFCCCCCC));
        }
    }


}
