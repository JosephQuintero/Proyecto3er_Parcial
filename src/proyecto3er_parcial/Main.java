package proyecto3er_Parcial;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Crear un reproductor de música
//        MusicPlayer musicPlayer = new MusicPlayer();
//        String musicPath = "src\\proyecto3er_parcial\\music.wav";
//        musicPlayer.playMusic(musicPath);

        // Iniciar la animación
        SwingUtilities.invokeLater(cilindro2::new);
    }
}

