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

import com.sartor.javaping.EnumCommand;
import com.sartor.javaping.db.entity.Host;

public class HostDao extends GenericDao<Host> {
	
	public HostDao() throws SQLException {
		super(Host.class);		
	}

    @Override
    protected void setFields(ResultSet rs, Host obj) throws SQLException {
        obj.setAddress(rs.getString("ADDRESS"));
        obj.setId(rs.getInt("ID"));
        obj.setPort(rs.getInt("PORT"));
        obj.setCommand(EnumCommand.getByDatabaseValue( rs.getString("COMMAND") ));
    }

}
