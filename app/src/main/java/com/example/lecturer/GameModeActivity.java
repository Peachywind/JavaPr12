    package com.example.lecturer;

    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;

    public class GameModeActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game_mode);

            Button buttonPlayWithBot = findViewById(R.id.buttonPlayWithBot);
            Button buttonPlayWithFriend = findViewById(R.id.buttonPlayWithFriend);

            buttonPlayWithBot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Запускаем активити игры с ботом
                    Intent intent = new Intent(GameModeActivity.this, GameWithBotActivity.class);
                    startActivity(intent);
                }
            });

            buttonPlayWithFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Запускаем активити игры с другим человеком
                    Intent intent = new Intent(GameModeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
