package com.example.simonSays;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;
import android.content.res.ColorStateList;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {

    private Button buttonVermelho, buttonVerde, buttonAzul, buttonAmarelo;
    private Button buttonIniciar;
    private TextView textViewPontuacao;

    private ArrayList<Integer> sequenciaJogo = new ArrayList<>();
    private int indiceSequenciaJogador = 0;
    private int pontuacao = 0;
    private boolean turnoDoJogador = false;

    private static final int VERMELHO = 1;
    private static final int VERDE = 2;
    private static final int AZUL = 3;
    private static final int AMARELO = 4;

    private MediaPlayer somVermelho, somVerde, somAzul, somAmarelo, somErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        inicializarSons();
        configurarListeners();
    }

    private void inicializarComponentes() {
        buttonVermelho = findViewById(R.id.buttonVermelho);
        buttonVerde = findViewById(R.id.buttonVerde);
        buttonAzul = findViewById(R.id.buttonAzul);
        buttonAmarelo = findViewById(R.id.buttonAmarelo);
        buttonIniciar = findViewById(R.id.buttonIniciar);
        textViewPontuacao = findViewById(R.id.textViewPontuacao);
        TextView textViewTitulo = findViewById(R.id.textViewTitulo);
    }

    private void inicializarSons() {
        somVermelho = MediaPlayer.create(this, R.raw.beep_red);
        somVerde = MediaPlayer.create(this, R.raw.beep_green);
        somAzul = MediaPlayer.create(this, R.raw.beep_blue);
        somAmarelo = MediaPlayer.create(this, R.raw.beep_yellow);
        somErro = MediaPlayer.create(this, R.raw.beep_wrong);
    }

    private void tocarSom(int cor) {
        switch (cor) {
            case VERMELHO:
                if (somVermelho != null) somVermelho.start();
                break;
            case VERDE:
                if (somVerde != null) somVerde.start();
                break;
            case AZUL:
                if (somAzul != null) somAzul.start();
                break;
            case AMARELO:
                if (somAmarelo != null) somAmarelo.start();
                break;
        }
    }

    private void configurarListeners() {
        buttonIniciar.setOnClickListener(v -> iniciarJogo());
        buttonVermelho.setOnClickListener(v -> cliqueDoJogador(VERMELHO));
        buttonVerde.setOnClickListener(v -> cliqueDoJogador(VERDE));
        buttonAzul.setOnClickListener(v -> cliqueDoJogador(AZUL));
        buttonAmarelo.setOnClickListener(v -> cliqueDoJogador(AMARELO));
    }

    private void iniciarJogo() {
        sequenciaJogo.clear();
        pontuacao = 0;
        indiceSequenciaJogador = 0;
        atualizarPontuacao(0);
        buttonIniciar.setEnabled(false);
        buttonIniciar.setText("Em Jogo");
        proximaRodada();
    }

    private void proximaRodada() {
        turnoDoJogador = false;
        atualizarPontuacao(pontuacao);
        indiceSequenciaJogador = 0;
        sequenciaJogo.add(new Random().nextInt(4) + 1);
        mostrarSequencia();
    }

    private void cliqueDoJogador(int corPressionada) {
        if (!turnoDoJogador) {
            return;
        }

        piscarBotao(corPressionada, 300);

        if (sequenciaJogo.get(indiceSequenciaJogador) == corPressionada) {
            indiceSequenciaJogador++;

            if (indiceSequenciaJogador == sequenciaJogo.size()) {
                pontuacao++;
                Toast.makeText(this, "Você Acertou!", Toast.LENGTH_SHORT).show();
                new Handler(Looper.getMainLooper()).postDelayed(this::proximaRodada, 1000);
            }
        } else {
            gameOver();
        }
    }

    private void mostrarSequencia() {
        Handler handler = new Handler(Looper.getMainLooper());
        alternarBotoesColoridos(false);

        for (int i = 0; i < sequenciaJogo.size(); i++) {
            int cor = sequenciaJogo.get(i);
            long delay = (i + 1) * 800L;
            handler.postDelayed(() -> piscarBotao(cor, 400), delay);
        }

        long tempoTotalSequencia = sequenciaJogo.size() * 800L;
        handler.postDelayed(() -> {
            turnoDoJogador = true;
            alternarBotoesColoridos(true);
            Toast.makeText(MainActivity.this, "Sua vez!", Toast.LENGTH_SHORT).show();
        }, tempoTotalSequencia);
    }

    private void piscarBotao(int cor, int duracao) {
        Button botao = encontrarBotaoPorCor(cor);
        if (botao == null) return;

        tocarSom(cor);

        // "Acende" o botão (cores vibrantes)
        switch (cor) {
            case VERMELHO:
                botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                break;
            case VERDE:
                botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00FF00")));
                break;
            case AZUL:
                botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0000FF")));
                break;
            case AMARELO:
                botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFF00")));
                break;
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // "Apaga" o botão (cores escuras)
            switch (cor) {
                case VERMELHO:
                    botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#800000")));
                    break;
                case VERDE:
                    botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#008000")));
                    break;
                case AZUL:
                    botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000080")));
                    break;
                case AMARELO:
                    botao.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#808000")));
                    break;
            }
        }, duracao);
    }

    private void gameOver() {
        if (somErro != null) somErro.start();
        Toast.makeText(this, "Fim de Jogo! Sua pontuação: " + pontuacao, Toast.LENGTH_LONG).show();
        turnoDoJogador = false;
        buttonIniciar.setEnabled(true);
        buttonIniciar.setText("Iniciar Novo Jogo");
        alternarBotoesColoridos(true);
    }

    private void atualizarPontuacao(int valor) {
        textViewPontuacao.setText("Pontuação: " + valor);
    }

    private void alternarBotoesColoridos(boolean habilitado) {
        buttonVermelho.setEnabled(habilitado);
        buttonVerde.setEnabled(habilitado);
        buttonAzul.setEnabled(habilitado);
        buttonAmarelo.setEnabled(habilitado);
    }

    private Button encontrarBotaoPorCor(int cor) {
        switch (cor) {
            case VERMELHO:
                return buttonVermelho;
            case VERDE:
                return buttonVerde;
            case AZUL:
                return buttonAzul;
            case AMARELO:
                return buttonAmarelo;
            default:
                return null;
        }
    }
}
