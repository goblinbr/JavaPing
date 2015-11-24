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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sartor.javaping.db.entity.IEntity;

public class GenericDao<T extends IEntity> {

	protected Connection connection;
	private Class<T> entityClass;
	private Statement statement;
	
	public GenericDao( Connection connection, Class<T> entityClass ) throws SQLException {
		this.connection = connection;
		this.entityClass = entityClass;
		this.statement = connection.createStatement();
	}

	public List<T> findAll(String sql) throws SQLException {
		List<T> list = new ArrayList<T>();
		ResultSet rs = statement.executeQuery(sql);
		while( rs.next() ){
			list.add( createObjectFromResult( rs ) );
		}
		return list;
	}

	private T createObjectFromResult(ResultSet rs) {
		T obj = null;
		try {
			obj = entityClass.newInstance();
			
			// TODO: set fields
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	private List<T> findAll(String sql, Map<String, Object> sqlParams) throws SQLException {
		sql = createSql( sql, sqlParams );
		return findAll(sql);
	}

	private String createSql(String sql, Map<String, Object> sqlParams) {
		for( String param : sqlParams.keySet() ){
			// TODO: set params
		}
		return sql;
	}
	
}
