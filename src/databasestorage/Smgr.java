package databasestorage;


import databasestorage.SMgrRelation;
import java.util.Hashtable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author charvel
 */
public class Smgr {
    
    
    static Smgr smgrsw[]={mdinit, NULL, mdclose, mdcreate, mdexists, mdunlink, mdextend,
		mdprefetch, mdread, mdwrite, mdnblocks, mdtruncate, mdimmedsync,
		mdpreckpt, mdsync, mdpostckpt
    };
    
    
    
    static int NSmgr = smgrsw.length;
    
    
    //Each backend has a hashtable that stores all extant SMgrRelation objects
    static Hashtable SMgrRelationHash = null;
    
    static MemoryContext Mdcxt;  //context for all allocations
    
    
    
    
    
    //初始化一些系统需要的状态
    static void init(){}   	
    
    //系统关闭收尾工作
    static private void shutdown(){}
    
    //关闭一个外存文件
    static private void close(SMgrRelation reln, ForkNumber forknum){}
    
    //创建一个relation
    static private void create (SMgrRelation reln, ForkNumber forknum){}
    
    //判断外存文件是否存在
    static private void exists(SMgrRelation reln, ForkNumber forknum){}
    
    //删除一个外存文件
    static private void unlink(SMgrRelation reln, ForkNumber forknum,boolean isRedo){}
      
    //一个“relation”空间不够的时候,自动扩展文件大 小
    static private void extend(SMgrRelation reln, ForkNumber forknum, BlockNumber blocknum, String buffer, boolean isTemp){}
    
    //从外存上预先读入数据到数据缓冲区
    static private void prefetch(SMgrRelation reln, ForkNumber forknum, BlockNumber blocknum){}
    
    //物理读操作
    static private void read(SMgrRelation reln, ForkNumber forknum, BlockNumber blocknum, String buffer){}
      
    //物理写操作,把数据缓冲区中的信息,刷出到外存
    static private void write(SMgrRelation reln, ForkNumber forknum, BlockNumber blocknum, String buffer, boolean isTemp){}
      
    //计算一个“relation”所使用的块(8k 一个块)的个 数
    static private BlockNumber nblocks(SMgrRelation reln, ForkNumber forknum){}
    
    //对一个“relation”空间做截断操作
    static private void truncate (SMgrRelation, ForkNumber, BlockNumber, boolean){}
    
    //刷出一个“relation”的内容到外存
    static private void immedsync (SMgrRelation, ForkNumber){}
    
    //做 checkpoint 之前,预先做一些操作
    static private void pre_ckpt (){}
    
    //同步刷出数据到外存
    static private void sync(){}
    
    //做 checkpoint 之后,收尾做一些操作
    static private void post_ckpt(){}
    
    //smgropen() -- Return an SMgrRelation object, creating it if need be
    static private SMgrRelation open(RelFileNode rnode){
        SMgrRelation reln;
	boolean found;

	if (SMgrRelationHash == NULL)
	{
		/* First time through: initialize the hash table */
		HASHCTL		ctl;

		MemSet(&ctl, 0, sizeof(ctl));
		ctl.keysize = sizeof(RelFileNode);
		ctl.entrysize = sizeof(SMgrRelationData);
		ctl.hash = tag_hash;
		SMgrRelationHash = hash_create("smgr relation table", 400,
									   &ctl, HASH_ELEM | HASH_FUNCTION);
	}

	/* Look up or create an entry */
	reln = (SMgrRelation) hash_search(SMgrRelationHash,
									  (void *) &rnode,
									  HASH_ENTER, &found);

	/* Initialize it if not present before */
	if (!found)
	{
		int			forknum;

		/* hash_search already filled in the lookup key */
		reln->smgr_owner = NULL;
		reln->smgr_which = 0;	/* we only have md.c at present */

		/* mark it not open */
		for (forknum = 0; forknum <= MAX_FORKNUM; forknum++)
			reln->md_fd[forknum] = NULL;
	}

	return reln;
    }
    
    
    
}


