package edu.harding.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToe extends AppCompatActivity {

    // Represents the internal state of the game
    private TicTacToeGame mGame;
    //is the variable who defines when the game is over
    private boolean mGameOver;

    private int humanCount=0;
    private int tiesCount=0;
    private int androidCount=0;

    private char PLAYER;
    private int chooseRandPlayer;
    private int chooseShapeRand;

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

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mInfoCountView = (TextView) findViewById(R.id.informationCount);
        mGame = new TicTacToeGame();
        startNewGame();
    }

    // Set up the game board.
    private void startNewGame(){
        showCount = "Human: "+humanCount+"  Ties: "+tiesCount+"  Android: "+androidCount;
        mInfoCountView.setText(showCount);
        mGameOver=false;
        mGame.clearBoard();

        chooseShapeRand = mGame.mRand.nextInt(2);
        if( chooseShapeRand==1){
            mGame.setShapeHuman('X'); mGame.setShapeComputer('O');
        }
        else{
            mGame.setShapeHuman('O'); mGame.setShapeComputer('X');
        }

        chooseRandPlayer= mGame.mRand.nextInt(2);
        if(chooseRandPlayer==1) PLAYER=mGame.HUMAN_PLAYER;
        else PLAYER=mGame.COMPUTER_PLAYER;



        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
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

    private void setMove(char player, int location) {

        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {


                mInfoTextView.setText(R.string.first_human );
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {

                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.first_human );
                    return;
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
        }
    }
}
