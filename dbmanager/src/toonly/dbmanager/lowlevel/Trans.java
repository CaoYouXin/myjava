/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package toonly.dbmanager.lowlevel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CPU
 */
public interface Trans {

    void trans(Connection conn) throws SQLException;

}
