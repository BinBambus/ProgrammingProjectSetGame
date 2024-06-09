package persistance;

import org.example.setgame.Cards;

import java.util.ArrayList;
import java.util.List;

public interface cardsDAO {
    List<Cards> getAllCards();
    Cards getCardByID(short id);
    void saveCard(Cards card);
    void saveAllCards(ArrayList<Cards> cards);
    void deleteCard(Cards card);
    void deleteAllCards(List<Cards> cards);
}
