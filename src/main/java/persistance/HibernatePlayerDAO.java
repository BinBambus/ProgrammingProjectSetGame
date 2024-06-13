package persistance;

import org.example.setgame.Cards;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.example.setgame.PlayerWindows;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HibernatePlayerDAO implements playerDAO{

    @Override
    public PlayerWindows getPlayerWindow(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        PlayerWindows playerWindow;
        try{
            tx = session.beginTransaction();
            playerWindow =  session.get(PlayerWindows.class, id);
            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
        return playerWindow;
    }

    @Override
    public List<PlayerWindows> getAllPlayerWindow(){
        List<PlayerWindows> playerWindowsList;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Query<PlayerWindows> query = session.createQuery("From PlayerWindows", PlayerWindows.class);
            playerWindowsList = query.getResultList();
            tx.commit();
        }
        catch(Exception e) {
            if(tx!=null) tx.rollback();
            throw e;
        }
        finally{
            session.close();
        }
        return playerWindowsList;
    }
    @Override
    public void saveAllPlayerWindows(List<PlayerWindows> playerWindows) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try{
            tx = session.beginTransaction();
            for (int i = 0;i<playerWindows.size();i++){
                PlayerWindows player = playerWindows.get(i);
                session.saveOrUpdate(player);
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
    public void deleteAllPlayers(List<PlayerWindows> playerWindows) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            for(PlayerWindows player : playerWindows){
                session.delete(player);
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
