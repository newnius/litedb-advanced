package databasestorage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author charvel
 */
public class SMgrRelation {
    
    
    RelFileNode smgr_rnode;  //Hash looup key
    
    SMgrRelation smgr_owner; //the owner of an SMgrRelation
    
    int smgr_which;   //storage manager selector
    
    _MdfdVec md_fd[]; //the length should be Max_FORNUM+1
}
