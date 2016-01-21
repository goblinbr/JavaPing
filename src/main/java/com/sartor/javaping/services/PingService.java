package com.sartor.javaping.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;

import com.sartor.javaping.db.DbManager;
import com.sartor.javaping.db.dao.HostStatusDao;
import com.sartor.javaping.db.dao.PingDao;
import com.sartor.javaping.db.entity.Host;
import com.sartor.javaping.db.entity.HostStatus;
import com.sartor.javaping.db.entity.Ping;
import com.sartor.javaping.types.EnumOnline;

public class PingService {

    private final PingDao pingDao;
    private final HostStatusDao hostStatusDao;

    public PingService() throws SQLException {
        this.pingDao = new PingDao();
        this.hostStatusDao = new HostStatusDao();
    }

    public void insertPing( Host host, boolean successful, int ms ) throws Exception {
        Connection connection = DbManager.getInstance().getConnection();
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try{
            Ping ping = new Ping();
            ping.setId( DbManager.getInstance().createPingId( host ) );
            ping.setHostId( host.getId() );
            ping.setMs( (successful) ? ms : -1 );
            ping.setWhen( Calendar.getInstance() );
            
            this.pingDao.insert( ping );
            
            HostStatus hostStatus = this.hostStatusDao.findOpenStatus( host );
            if( hostStatus != null ){
                if( hostStatus.getOnline() == EnumOnline.ONLINE && !successful ||
                        hostStatus.getOnline() == EnumOnline.OFFLINE && successful ){
                    hostStatus.setFinish( Calendar.getInstance() );
                    
                    this.hostStatusDao.update(hostStatus);
                    
                    hostStatus = null;
                }
            }
            if( hostStatus == null ){
                hostStatus = new HostStatus();
                hostStatus.setId( DbManager.getInstance().createHostStatusId(host) );
                hostStatus.setHostId(host.getId());
                hostStatus.setStart(Calendar.getInstance());
                hostStatus.setOnline( (successful) ? EnumOnline.ONLINE : EnumOnline.OFFLINE );
                hostStatus.setFinish(null);
                
                this.hostStatusDao.insert(hostStatus);
            }
            
            if( autoCommit ){
                connection.commit();
            }
        }
        catch( Exception ex ){
            if( autoCommit ){
                connection.rollback();
            }
            throw ex;
        }
        finally {
            connection.setAutoCommit(autoCommit);
        }
    }
}
