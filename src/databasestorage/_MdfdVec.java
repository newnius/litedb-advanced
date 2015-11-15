package databasestorage;


import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author charvel
 */
public class _MdfdVec {
    File mdfd_vfd;  //VFD文件编号
    BlockNumber mdfd_segno; //表文件段编号
    _MdfdVec mdfd_chain; // 下一个块
}
