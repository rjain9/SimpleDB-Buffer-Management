package simpledb.test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import simpledb.buffer.BasicBufferMgr;
import simpledb.buffer.Buffer;
import simpledb.buffer.BufferMgr;
import simpledb.buffer.PageFormatter;
import simpledb.file.Block;
import simpledb.file.Page;
import simpledb.remote.RemoteDriver;
import simpledb.remote.RemoteDriverImpl;
import simpledb.server.SimpleDB;

public class BufferTest{
	public static void main(String args[]) throws Exception {
		// configure and initialize the database
		SimpleDB.init("SimpleDB");
		BufferMgr bfr=SimpleDB.bufferMgr();  
		System.out.println("database server ready");
		//creating a block list of capacity 8
		Block[] blklist=new Block[8];
		//printing BufferMap to see that already all buffers are assigned to some blocks in fldcat.tbl
		bfr.bufferMgr.printBufferMap();
		//creating 6 blocks and pinning them
		for(int j=0;j<6;j++){
			Block blk=new Block(("number"+j),j);
			blklist[j]=blk;
			System.out.println(blklist[j]);
			bfr.pin(blklist[j]);
			System.out.println(bfr.getMapping(blklist[j]));
		}
		//Checking to see if 7th block is pinned and in the bufferPoolMap. It should print null since it is not pinned.
		System.out.println(bfr.getMapping(blklist[6]));
		bfr.bufferMgr.printBufferMap();
		//Page p2 = new Page();
		//p2.setString(20, "hello");
		//PageFormatter fmtr = null;
		//fmtr.format(p2);
		//bfr.pinNew("filename",fmtr);
		System.out.println("blocks pinned successfully!");
		//Checking to see if 3rd block is pinned and in the bufferPoolMap. It should print true since it is  pinned.
		System.out.println(bfr.containsMapping(blklist[2]));
	//Testing for MRM
		bfr.getMapping(blklist[2]).setInt(2, 1, 5, 30);
		bfr.bufferMgr.getStatistics();
		bfr.bufferMgr.bufferPoolMap.get(blklist[5]).setInt(2, 1, 15, 50);
		bfr.bufferMgr.getStatistics();
		bfr.bufferMgr.printBufferMap();
		//Unpinning the modified buffers
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[2]));
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[5]));
		bfr.bufferMgr.printBufferMap();
		Block blk9=new Block("number9",9);
		//Pinning the Block9. It should be pinned to buffer with LSN=50 and bufferPoolMap should be updated.
		bfr.pin(blk9);
		bfr.bufferMgr.printBufferMap();
		//  bfr.bufferMgr.bufferPoolMap.get(blklist[7]).setInt(2, 1, 15, 70);
		//bfr.bufferMgr.bufferPoolMap.get(blklist[7]).setInt(2, 1, 15, 90);
		bfr.bufferMgr.bufferPoolMap.get(blklist[4]).setInt(2, 1, 15, 80);
		//Check whether block 3 is pinned.It should be false because it was unpinned above.
		System.out.println(bfr.bufferMgr.bufferPoolMap.get(blklist[2]).isPinned());
		bfr.pin(blklist[2]);
		//Now it's pinned and it should be true and check the Map
		System.out.println(bfr.bufferMgr.bufferPoolMap.get(blklist[2]).isPinned());
		bfr.bufferMgr.printBufferMap();
		//Unpin the one with LSN=80
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[4]));
		//Check the difference in bufferPoolMap before and after pinning
		bfr.bufferMgr.printBufferMap();
		bfr.pin(blklist[5]);
		System.out.println("Break");
		bfr.bufferMgr.printBufferMap();
		//Should be true since 9 was pinned and never unpinned again
		System.out.println(bfr.containsMapping(blk9));
		//Creating new blocks to overflow and trigger the exception.
		Block blk10=new Block("number10",10);
		bfr.pin(blk10);
		bfr.bufferMgr.printBufferMap();
		Block blk11=new Block("number11",11);
		bfr.pin(blk11);
		bfr.bufferMgr.printBufferMap();
		/*To throw exception*/
		 /* Block blk12=new Block("number12",12);
		bfr.pin(blk12);
		bfr.bufferMgr.printBufferMap();
*/

	}
}