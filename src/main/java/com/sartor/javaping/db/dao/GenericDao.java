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

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.sartor.javaping.db.Column;
import com.sartor.javaping.db.DatabaseEnum;
import com.sartor.javaping.db.DbManager;
import com.sartor.javaping.db.Table;
import com.sartor.javaping.db.entity.IEntity;
import com.sartor.javaping.util.CalendarUtil;

public abstract class GenericDao<T extends IEntity> {

    protected Connection connection;
    private final Class<T> entityClass;
    private final List<Field> columnFields;
    private final PreparedStatement psInsert;
    private final PreparedStatement psUpdate;
    private final PreparedStatement psFindAll;
    private HashMap<Field,Integer> insertFieldPositionMap;
    private HashMap<Field,Integer> updateFieldPositionMap;
    private final String tableName;

    public GenericDao(Class<T> entityClass ) throws SQLException {
		this.connection = DbManager.getInstance().getConnection();
		this.entityClass = entityClass;
		
		Table table = entityClass.getAnnotation(Table.class);
		if( table != null ){
		    this.tableName = table.name();
		}
		else{
		    this.tableName = entityClass.getSimpleName().toUpperCase();
		}
		
		this.columnFields = new ArrayList<Field>();
		for( Field field : entityClass.getDeclaredFields() ){
		    Column column = field.getAnnotation(Column.class);
		    if( column != null ){
		        field.setAccessible(true);
		        this.columnFields.add(field);
		    }
		}
		
		this.psInsert = createPsInsert();
		this.psUpdate = createPsUpdate();
		this.psFindAll = connection.prepareStatement("select * from " + this.tableName);
	}

    private PreparedStatement createPsInsert() throws SQLException {
        this.insertFieldPositionMap = new HashMap<Field, Integer>();
        
        String sqlInsertCols = "";
        String sqlInsertValues = "";
        
        int fieldPosition = 0;
        for( Field field : this.columnFields ){
            fieldPosition++;
            
            Column col = field.getAnnotation(Column.class);
            sqlInsertCols += "," + col.name();
            sqlInsertValues += ",?";
            
            this.insertFieldPositionMap.put(field, fieldPosition);
        }
        
        sqlInsertCols = sqlInsertCols.replaceFirst(",", "(") + ")";
        sqlInsertValues = sqlInsertValues.replaceFirst(",", "(") + ")";
        
        return connection.prepareStatement("insert into " + this.tableName + " " + sqlInsertCols + " values " + sqlInsertValues);
    }
    
    private PreparedStatement createPsUpdate() throws SQLException {
        this.updateFieldPositionMap = new HashMap<Field, Integer>();
        
        String sqlUpdateCols = "";
        String sqlWhere = "";
        
        int fieldPosition = 0;
        for( Field field : this.columnFields ){
            Column column = field.getAnnotation(Column.class);
            if( !column.isId() ){
                fieldPosition++;
                
                sqlUpdateCols += ", " + column.name() + " = ?";
                this.updateFieldPositionMap.put(field, fieldPosition);
            }
        }
        
        for( Field field : this.columnFields ){
            Column column = field.getAnnotation(Column.class);
            if( column.isId() ){
                fieldPosition++;
                
                sqlWhere += " and " + column.name() + " = ?";
                this.updateFieldPositionMap.put(field, fieldPosition);
            }
        }
        
        sqlUpdateCols = sqlUpdateCols.replaceFirst(",", "SET ");
        sqlWhere = sqlWhere.replaceFirst("and", "where");
        
        return connection.prepareStatement("update " + this.tableName + " " + sqlUpdateCols + " " + sqlWhere);
    }
    
    public T find(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return createObjectFromResult(rs);
        }
        return null;
    }
    
    public List<T> findAll() throws SQLException {
        return findAll(this.psFindAll);
    }

    public List<T> findAll(PreparedStatement ps) throws SQLException {
        List<T> list = new ArrayList<T>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(createObjectFromResult(rs));
        }
        return list;
    }

    private T createObjectFromResult(ResultSet rs) throws SQLException {
        T obj = null;
        try {
            obj = entityClass.newInstance();

            setFields(rs, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    private void setFields(ResultSet rs, T obj) throws SQLException {
        for( Field field : this.columnFields ){
            Column column = field.getAnnotation(Column.class);
            Object value = null;
            if( field.getType() == Integer.class || field.getType() == int.class ){
                value = rs.getInt( column.name() );
            }
            else if( field.getType() == Long.class || field.getType() == long.class ){
                value = rs.getLong( column.name() );
            }
            else if( field.getType() == Calendar.class ){
                value = CalendarUtil.getCalendar( rs.getTimestamp( column.name() ) );
            }
            else if( field.getType() == String.class ){
                value = rs.getString( column.name() );
            }
            else if( field.getType().isEnum() && DatabaseEnum.class.isAssignableFrom( field.getType() ) ) {
                Object[] enumValues = field.getType().getEnumConstants();
                
                String fieldValue = rs.getString(column.name());
                value = null;
                for( Object enumValue : enumValues ){
                    if( String.valueOf( ((DatabaseEnum) enumValue).getDatabaseValue() ).equals(fieldValue) ){
                        value = enumValue;
                        break;
                    }
                }
            }
            else{
                throw new RuntimeException( "GenericDao.setFields no treatment for " + field.getType().getName() );
            }
            
            try {
                field.set(obj, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insert( T obj ) throws SQLException {
        try {
            for( Field field : this.insertFieldPositionMap.keySet() ){
                Integer position = this.insertFieldPositionMap.get(field);
                Object value = convertToDatabaseValue( field.get(obj) );
                this.psInsert.setObject( position, value );
            }
            this.psInsert.execute();
        } catch (SQLException e){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update( T obj ) throws SQLException {
        try {
            for( Field field : this.updateFieldPositionMap.keySet() ){
                Integer position = this.updateFieldPositionMap.get(field);
                Object value = convertToDatabaseValue( field.get(obj) );
                this.psUpdate.setObject( position, value );
            }
            this.psUpdate.execute();
        } catch (SQLException e){
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Object convertToDatabaseValue(Object value) {
        if( value instanceof Calendar ){
            Calendar calendar = (Calendar) value;
            value = new Timestamp( calendar.getTimeInMillis() );
        }
        else if( value instanceof DatabaseEnum ) {
            DatabaseEnum databaseEnum = (DatabaseEnum) value;
            value = databaseEnum.getDatabaseValue();
        }
        return value;
    }
}
