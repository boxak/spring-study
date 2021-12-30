package dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{
    int counter = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.realConnectionMaker = connectionMaker;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return realConnectionMaker.getConnection();
    }

    public int getCounter() {
        return this.counter;
    }
}
