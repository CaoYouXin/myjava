/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package toonly.dbmanager.lowlevel;

/**
 *
 * @author CPU
 */
public interface Batch {
	
	Object[] row(int rowId);
	
}
