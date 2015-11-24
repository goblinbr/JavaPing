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
import java.sql.SQLException;
import java.util.List;

import com.sartor.javaping.db.entity.Host;

public class HostDao extends GenericDao<Host> {
	
	private static final String SQL_FIND_BY_ID = "select * from HOST where ID = :id:";
	private static final String SQL_FIND_ALL = "select * from HOST";

	public HostDao(Connection connection) throws SQLException {
		super(connection,Host.class);
	}
	
	public List<Host> findAll() throws SQLException {
		return super.findAll( SQL_FIND_ALL );
	}

}
