/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Core;

import java.sql.Connection;

import Config.DB;

/**
 *
 * @author Muhammad Nor Kholit
 */
public abstract class Model extends QueryBuilder{
    protected DB db;

    public Model( String tableName) {
        super(tableName);
    }

}
