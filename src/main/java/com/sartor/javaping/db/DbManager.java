/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Rodrigo de Bona Sartor
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sartor.javaping.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.sartor.javaping.db.entity.Host;

public class DbManager {
	
	private static final DbManager instance = new DbManager();
	private String driver;
	private String connectionUrl;
	private String user;
	private String password;
	private final ConcurrentHashMap<Thread,Connection> threadConMap;

	private DbManager() {
	    this.threadConMap = new ConcurrentHashMap<Thread, Connection>();
	    try{
    	    Properties propFile = new Properties();
    	    propFile.load( new FileInputStream( "config.properties" ) );
    	    
    		this.driver = propFile.getProperty("driver");
    		this.connectionUrl = propFile.getProperty("url");
    		this.user = propFile.getProperty("user");
    		this.password = propFile.getProperty("password");
	    }
	    catch( Exception ex ){
	        ex.printStackTrace();
	    }
	}

	public static DbManager getInstance() {
		return instance;
	}
	
	public void closeAll() {
		Collection<Connection> cons = new ArrayList<Connection>( threadConMap.values() );
		threadConMap.clear();
		for( Connection con : cons ){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() throws SQLException {
		Thread t = Thread.currentThread();
		Connection con = threadConMap.get(t);
		if( con == null ){
			try {
                con = createNewConnection();
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
			threadConMap.put(t, con);
		}
		return con;
	}

	private Connection createNewConnection() throws ClassNotFoundException, SQLException {
		Class.forName( driver );
		Connection conn = DriverManager.getConnection(connectionUrl,user,password);
		return conn;
	}

    public String createPingId( Host host ) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String id = sdf.format(new Date()) + host.getId();
        return id;
    }

    public String createHostStatusId(Host host) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String id = sdf.format(new Date()) + host.getId();
        return id;
    }
}
