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
package com.sartor.javaping.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sartor.javaping.db.DbManager;
import com.sartor.javaping.db.entity.IEntity;

public abstract class GenericDao<T extends IEntity> {

	protected Connection connection;
	private Class<T> entityClass;
	private Statement statement;
	
	private final PreparedStatement psFindAll;
	
	public GenericDao(Class<T> entityClass ) throws SQLException {
		this.connection = DbManager.getInstance().getConnection();
		this.entityClass = entityClass;
		this.statement = connection.createStatement();
		
		this.psFindAll = connection.prepareStatement("select * from " + entityClass.getSimpleName());
	}
	
	public List<T> findAll() throws SQLException {
	    return findAll( this.psFindAll );
	}

	public List<T> findAll(PreparedStatement ps) throws SQLException {
		List<T> list = new ArrayList<T>();
		ResultSet rs = ps.executeQuery();
		while( rs.next() ){
			list.add( createObjectFromResult( rs ) );
		}
		return list;
	}

	private T createObjectFromResult(ResultSet rs) {
		T obj = null;
		try {
			obj = entityClass.newInstance();
			
			setFields( rs, obj );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	protected abstract void setFields(ResultSet rs, T obj) throws SQLException;
	
}
