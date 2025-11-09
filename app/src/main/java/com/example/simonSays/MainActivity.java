package com.example.simonSays;

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

public class MainActivity extends AppCompatActivity {

    // --- Variáveis de UI (Componentes Visuais) ---
    private Button buttonVermelho, buttonVerde, buttonAzul, buttonAmarelo;
    private Button buttonIniciar;
    private TextView textViewPontuacao;

    // --- Variáveis de Lógica do Jogo ---
    private ArrayList<Integer> sequenciaJogo = new ArrayList<>();
    private int indiceSequenciaJogador = 0; // Controla qual passo da sequência o jogador está
    private int pontuacao = 0;
    private boolean turnoDoJogador = false; // Controla se o jogador pode clicar nos botões

    // --- Constantes para identificar os botões (melhora a leitura do código) ---
    private static final int VERMELHO = 1;
    private static final int VERDE = 2;
    private static final int AZUL = 3;
    private static final int AMARELO = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Inicialização dos Componentes ---
        inicializarComponentes();
        configurarListeners();
    }

    /**
     * Encontra todos os componentes da UI no layout XML.
     */
    private void inicializarComponentes() {
        buttonVermelho = findViewById(R.id.buttonVermelho);
        buttonVerde = findViewById(R.id.buttonVerde);
        buttonAzul = findViewById(R.id.buttonAzul);
        buttonAmarelo = findViewById(R.id.buttonAmarelo);
        buttonIniciar = findViewById(R.id.buttonIniciar);
        textViewPontuacao = findViewById(R.id.textViewPontuacao);
    }

    /**
     * Configura os listeners de clique para todos os botões interativos.
     */
    private void configurarListeners() {
        buttonIniciar.setOnClickListener(v -> iniciarJogo());

        // Define um listener para cada botão colorido, chamando o método de verificação
        buttonVermelho.setOnClickListener(v -> cliqueDoJogador(VERMELHO));
        buttonVerde.setOnClickListener(v -> cliqueDoJogador(VERDE));
        buttonAzul.setOnClickListener(v -> cliqueDoJogador(AZUL));
        buttonAmarelo.setOnClickListener(v -> cliqueDoJogador(AMARELO));
    }

    /**
     * Inicia um novo jogo, resetando todas as variáveis.
     */
    private void iniciarJogo() {
        sequenciaJogo.clear();
        pontuacao = 0;
        indiceSequenciaJogador = 0;
        atualizarPontuacao(0);
        buttonIniciar.setEnabled(false); // Desativa o botão "Iniciar" durante o jogo
        buttonIniciar.setText("Em Jogo");
        proximaRodada();
    }

    /**
     * Avança para a próxima rodada: adiciona uma nova cor à sequência e a exibe.
     */
    private void proximaRodada() {
        turnoDoJogador = false; // Impede que o jogador clique enquanto a sequência é mostrada
        atualizarPontuacao(pontuacao);
        indiceSequenciaJogador = 0; // Reseta o índice do jogador para o início da sequência

        // Adiciona um novo número aleatório (1 a 4) à sequência do jogo
        sequenciaJogo.add(new Random().nextInt(4) + 1);

        // Mostra a sequência de botões piscando
        mostrarSequencia();
    }

    /**
     * Lida com o clique do jogador em um dos botões coloridos.
     * @param corPressionada O número que representa a cor do botão clicado.
     */
    private void cliqueDoJogador(int corPressionada) {
        if (!turnoDoJogador) {
            // Se não for o turno do jogador, ignora o clique
            return;
        }

        piscarBotao(corPressionada, 300); // Pisca o botão que o jogador clicou

        // Verifica se o botão clicado é o correto na sequência
        if (sequenciaJogo.get(indiceSequenciaJogador) == corPressionada) {
            indiceSequenciaJogador++; // Acertou, avança para o próximo item da sequência

            // Se o jogador completou a sequência da rodada atual
            if (indiceSequenciaJogador == sequenciaJogo.size()) {
                pontuacao++; // Aumenta a pontuação
                Toast.makeText(this, "Você Acertou!", Toast.LENGTH_SHORT).show();

                // Usa um Handler para dar uma pequena pausa antes da próxima rodada
                new Handler(Looper.getMainLooper()).postDelayed(this::proximaRodada, 1000); // 1 segundo de pausa
            }
        } else {
            // O jogador errou a sequência
            gameOver();
        }
    }

    /**
     * Mostra a sequência de cores piscando uma por uma.
     */
    private void mostrarSequencia() {
        Handler handler = new Handler(Looper.getMainLooper());
        // Desativa os botões para o jogador não clicar durante a exibição
        alternarBotoesColoridos(false);

        // Itera sobre a sequência do jogo com um atraso entre cada piscada
        for (int i = 0; i < sequenciaJogo.size(); i++) {
            int cor = sequenciaJogo.get(i);
            long delay = (i + 1) * 800L; // Aumenta o atraso para cada botão na sequência
            handler.postDelayed(() -> piscarBotao(cor, 400), delay);
        }

        // Após a sequência terminar, ativa os botões e passa o turno para o jogador
        long tempoTotalSequencia = sequenciaJogo.size() * 800L;
        handler.postDelayed(() -> {
            turnoDoJogador = true;
            alternarBotoesColoridos(true);
            Toast.makeText(MainActivity.this, "Sua vez!", Toast.LENGTH_SHORT).show();
        }, tempoTotalSequencia);
    }

    /**
     * Pisca um botão específico para dar feedback visual.
     * @param cor O número que representa a cor.
     * @param duracao A duração da piscada em milissegundos.
     */
    private void piscarBotao(int cor, int duracao) {
        Button botao = encontrarBotaoPorCor(cor);
        if (botao == null) return;

        botao.setAlpha(0.5f); // Deixa o botão semi-transparente
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            botao.setAlpha(1.0f); // Restaura a opacidade total
        }, duracao);
    }

    /**
     * Finaliza o jogo e reseta a UI para o estado inicial.
     */
    private void gameOver() {
        Toast.makeText(this, "Fim de Jogo! Sua pontuação: " + pontuacao, Toast.LENGTH_LONG).show();
        turnoDoJogador = false;
        buttonIniciar.setEnabled(true);
        buttonIniciar.setText("Iniciar Novo Jogo");
        alternarBotoesColoridos(true); // Reativa os botões para o próximo jogo
    }

    /**
     * Atualiza o placar na tela.
     * @param valor O novo valor da pontuação.
     */
    private void atualizarPontuacao(int valor) {
        textViewPontuacao.setText("Pontuação: " + valor);
    }

    /**
     * Habilita ou desabilita todos os botões coloridos de uma vez.
     * @param habilitado True para habilitar, false para desabilitar.
     */
    private void alternarBotoesColoridos(boolean habilitado) {
        buttonVermelho.setEnabled(habilitado);
        buttonVerde.setEnabled(habilitado);
        buttonAzul.setEnabled(habilitado);
        buttonAmarelo.setEnabled(habilitado);
    }

    /**
     * Retorna a instância do botão correspondente a um número de cor.
     * @param cor O número da cor.
     * @return O objeto Button correspondente.
     */
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