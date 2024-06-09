package persistance;

import org.example.setgame.Cards;
import org.example.setgame.PlayerWindows;

import java.util.ArrayList;
import java.util.List;

public interface playerDAO {
    PlayerWindows getPlayerWindow(int id);
    List<PlayerWindows> getAllPlayerWindow();
    void saveAllPlayerWindows(PlayerWindows[] playerWindows);
    void deleteAllPlayers(List<PlayerWindows> playerWindows);
}
