package persistance;

import javax.naming.Referenceable;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.example.setgame.Cards;

import java.util.ArrayList;
import java.util.List;

public class HibernateCardsDAO implements cardsDAO{
    @Override
    public List<Cards> getAllCards(){
        List<Cards> cards;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Query<Cards> query = session.createQuery("From Cards", Cards.class);
            cards = query.getResultList();


            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
        return cards;
    }

    @Override
    public void saveCard(Cards card){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            session.saveOrUpdate(card);

            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }

    }


    @Override
    public void deleteCard(Cards card){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            session.delete(card);
            tx.commit();

        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }

    }

    @Override
    public Cards getCardByID(short id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        Cards card;
        try{
            tx = session.beginTransaction();
            card =  session.get(Cards.class, id);

            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
        return card;
    }
    @Override
    public void saveAllCards(ArrayList<Cards> cards){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            for(int i = 0; i< cards.size();i++){
                Cards card = cards.get(i);
                session.saveOrUpdate(card);
            }

            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
    }

    @Override
    public void deleteAllCards(List<Cards> cards){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            for(Cards card : cards){
                session.delete(card);
            }

            tx.commit();

        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
    }
}
