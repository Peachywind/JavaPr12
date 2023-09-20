package com.example.lecturer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    ImageButton switchButton;
    private GridLayout gameBoard;
    private ImageView[][] cellViews;
    private int currentPlayer = 0; // 0 - игрок X, 1 - игрок O
    private int[][] board = new int[3][3];


    private int playerXWins = 0;
    private int playerOWins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("GameStats", MODE_PRIVATE);

        settings = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (settings.contains("MODE_NIGHT_ON")) {
            getCurrentTheme();
        } else {
            editor = settings.edit();
            editor.putBoolean("MODE_NIGHT_ON", false);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Slam, Brat", Toast.LENGTH_SHORT).show();
        }

        if (settings.getBoolean("MODE_NIGHT_ON", false)) {
            setTheme(R.style.DarkTheme); // Здесь замените на тему для ночного режима
        } else {
            setTheme(R.style.LightTheme); // Здесь замените на тему для дневного режима
        }

        setContentView(R.layout.activity_main);
        switchButton = findViewById(R.id.switch_button);

        if (settings.getBoolean("MODE_NIGHT_ON", false)) {
            switchButton.setImageResource(R.drawable.baseline_wb_sunny_24);
        } else {
            switchButton.setImageResource(R.drawable.baseline_whatshot_24);
        }
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = settings.edit();
                if (settings.getBoolean("MODE_NIGHT_ON", false)) {
                    editor.putBoolean("MODE_NIGHT_ON", false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    editor.putBoolean("MODE_NIGHT_ON", true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.apply();
                recreate(); // Пересоздаем активити для применения изменений темы
            }
        });

        gameBoard = findViewById(R.id.game_board);

        // Инициализация массива ячеек
        cellViews = new ImageView[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int cellId = getResources().getIdentifier("imageView" + (i * 3 + j + 1), "id", getPackageName());
                cellViews[i][j] = findViewById(cellId);
                cellViews[i][j].setOnClickListener(new CellClickListener(i, j));
            }
        }

        // Найдите TextView для отображения счета побед
        TextView playerXWinsTextView = findViewById(R.id.player_x_wins);
        TextView playerOWinsTextView = findViewById(R.id.player_o_wins);

        // Получите сохраненные значения счетчиков побед из SharedPreferences
        playerXWins = preferences.getInt("playerXWins", 0);
        playerOWins = preferences.getInt("playerOWins", 0);

        // Установите текст счета побед
        playerXWinsTextView.setText("Player X Wins: " + playerXWins);
        playerOWinsTextView.setText("Player O Wins: " + playerOWins);

        Button startGameButton = findViewById(R.id.start_game_button);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создайте интент для перехода к GameModeActivity
                Intent intent = new Intent(MainActivity.this, GameModeActivity.class);
                startActivity(intent);
            }
        });

    }

    // Обработчик нажатия на ячейку
    private class CellClickListener implements View.OnClickListener {
        private int row;
        private int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (board[row][col] == 0) {
                if (currentPlayer == 0) {
                    cellViews[row][col].setImageResource(R.drawable.in_x); // Изображение крестика
                    board[row][col] = 1; // 1 представляет крестик
                } else {
                    cellViews[row][col].setImageResource(R.drawable.ic_o); // Изображение нолика
                    board[row][col] = 2; // 2 представляет нолик
                }
                currentPlayer = 1 - currentPlayer; // Переключение игрока

                // Проверка на выигрыш
                if (checkWin(row, col)) {
                    String winner = (currentPlayer == 0) ? "Игрок O" : "Игрок X";
                    Toast.makeText(MainActivity.this, winner + " выиграл!", Toast.LENGTH_SHORT).show();

                    // Увеличьте счетчик побед соответствующего игрока
                    if (currentPlayer == 0) {
                        playerOWins++;
                    } else {
                        playerXWins++;
                    }

                    // Обновите значения счетчиков побед в SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("GameStats", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("playerXWins", playerXWins);
                    editor.putInt("playerOWins", playerOWins);
                    editor.apply();

                    // Обновите текст счета побед
                    TextView playerXWinsTextView = findViewById(R.id.player_x_wins);
                    TextView playerOWinsTextView = findViewById(R.id.player_o_wins);
                    playerXWinsTextView.setText("Player X Wins: " + playerXWins);
                    playerOWinsTextView.setText("Player O Wins: " + playerOWins);

                    // Очистка доски
                    resetBoard();
                }
            }
        }
    }

    // Проверка на выигрыш
    private boolean checkWin(int row, int col) {
        // Проверяем по горизонтали
        if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
            return true;
        }

        // Проверяем по вертикали
        if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
            return true;
        }

        // Проверяем по диагонали (слева направо)
        if (row == col && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return true;
        }

        // Проверяем по диагонали (справа налево)
        if (row + col == 2 && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return true;
        }

        return false;
    }

    // Очистка доски
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cellViews[i][j].setImageResource(0);
                board[i][j] = 0;
            }
        }
        currentPlayer = 0;
    }

    private void getCurrentTheme() {
        if (settings.getBoolean("MODE_NIGHT_ON", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
