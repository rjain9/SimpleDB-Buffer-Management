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
		Block[] blklist=new Block[8];
		for(int j=0;j<6;j++){
			Block blk=new Block(("number"+j),j);
			blklist[j]=blk;
			System.out.println(blklist[j]);
			bfr.pin(blklist[j]);
			System.out.println(bfr.getMapping(blklist[j]));
		}
		System.out.println(bfr.getMapping(blklist[6]));
		//Page p2 = new Page();
		//p2.setString(20, "hello");
		//PageFormatter fmtr = null;
		//fmtr.format(p2);
		//bfr.pinNew("filename",fmtr);
		System.out.println("blocks pinned successfully!");
		System.out.println(bfr.containsMapping(blklist[2]));
		bfr.getMapping(blklist[2]).setInt(2, 1, 5, 30);
		bfr.bufferMgr.getStatistics();
		bfr.bufferMgr.bufferPoolMap.get(blklist[5]).setInt(2, 1, 15, 50);
		bfr.bufferMgr.getStatistics();
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[2]));
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[5]));
		Block blk9=new Block("number9",9);
		bfr.pin(blk9);
		bfr.bufferMgr.printBufferMap();
		//  bfr.bufferMgr.bufferPoolMap.get(blklist[7]).setInt(2, 1, 15, 70);
		//bfr.bufferMgr.bufferPoolMap.get(blklist[7]).setInt(2, 1, 15, 90);
		bfr.bufferMgr.bufferPoolMap.get(blklist[4]).setInt(2, 1, 15, 80);
		bfr.pin(blklist[2]);
		System.out.println(bfr.bufferMgr.bufferPoolMap.get(blklist[2]).isPinned());
		bfr.bufferMgr.printBufferMap();
		bfr.unpin(bfr.bufferMgr.bufferPoolMap.get(blklist[4]));
		bfr.bufferMgr.printBufferMap();
		bfr.pin(blklist[5]);
		System.out.println("Break");
		bfr.bufferMgr.printBufferMap();
		bfr.containsMapping(blk9);


	}
}