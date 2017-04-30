package simpledb.buffer;

import java.util.HashMap;
import simpledb.file.*;
 
/**
 * Manages the pinning and unpinning of buffers to blocks.
 * @author Edward Sciore
 *
 */
public class BasicBufferMgr {
   private Buffer[] bufferpool;
   private int numAvailable;
   public HashMap<Block,Buffer> bufferPoolMap;	//blk-buffer hashmap

   
   /**
    * Creates a buffer manager having the specified number 
    * of buffer slots.
    * This constructor depends on both the {@link FileMgr} and
    * {@link simpledb.log.LogMgr LogMgr} objects 
    * that it gets from the class
    * {@link simpledb.server.SimpleDB}.
    * Those objects are created during system initialization.
    * Thus this constructor cannot be called until 
    * {@link simpledb.server.SimpleDB#initFileAndLogMgr(String)} or
    * is called first.
    * @param numbuffs the number of buffer slots to allocate
    */
   BasicBufferMgr(int numbuffs) {
      bufferpool = new Buffer[numbuffs];
      numAvailable = numbuffs;
      bufferPoolMap=new HashMap<Block,Buffer>();//buffer-blk map initialized
      for (int i=0; i<numbuffs; i++)
         bufferpool[i] = new Buffer();
   }
   
   /**
    * Flushes the dirty buffers modified by the specified transaction.
    * @param txnum the transaction's id number
    */
   synchronized void flushAll(int txnum) {
      for (Buffer buff : bufferpool)
         if (buff.isModifiedBy(txnum))
         buff.flush();
   }
   
   /**
    * Pins a buffer to the specified block. 
    * If there is already a buffer assigned to that block
    * then that buffer is used;  
    * otherwise, an unpinned buffer from the pool is chosen.
    * Returns a null value if there are no available buffers.
    * @param blk a reference to a disk block
    * @return the pinned buffer
    */
   synchronized Buffer pin(Block blk) {
      Buffer buff = findExistingBuffer(blk);
      if (buff == null) {
         buff = chooseUnpinnedBuffer();
         if (buff == null){
            System.out.println("No buffer empty. can't pin!");
        	 return null;
         }
         //if buffer is already assigned a block then it
         //is replaced with new value.
         if(buff.block()!=null)
        	 bufferPoolMap.remove(buff.block());
         bufferPoolMap.put(blk,buff);
         buff.setAssigned();
         buff.assignToBlock(blk);
      }
      if (!buff.isPinned())
         numAvailable--;
      buff.pin();
      //printBufferMap();
      return buff;
   }
   
   /**
    * Allocates a new block in the specified file, and
    * pins a buffer to it. 
    * Returns null (without allocating the block) if 
    * there are no available buffers.
    * @param filename the name of the file
    * @param fmtr a pageformatter object, used to format the new block
    * @return the pinned buffer
    */
   synchronized Buffer pinNew(String filename, PageFormatter fmtr) {
      Buffer buff = chooseUnpinnedBuffer();
      if (buff == null)
         return null;
      buff.assignToNew(filename, fmtr);
      buff.setAssigned();
      numAvailable--;
      bufferPoolMap.put(buff.block(), buff);
      buff.pin();
      return buff;
   }
   
   /**
    * Unpins the specified buffer.
    * @param buff the buffer to be unpinned
    */
   synchronized void unpin(Buffer buff) {
      buff.unpin();
      System.out.println(buff.getLogSequenceNumber()+" unpinned");
      if (!buff.isPinned())
         numAvailable++;
   }
   
   /**
    * Returns the number of available (i.e. unpinned) buffers.
    * @return the number of available buffers
    */
   int available() {
      return numAvailable;
   }
   
   private Buffer findExistingBuffer(Block blk) {
      return bufferPoolMap.get(blk);	//task 2.1
    
	  /**
      *	for (Buffer buff : bufferpool) {
      *   	Block b = buff.block();
      *   	if (b != null && b.equals(blk))
      *      	return buff;
      *	}
      return null;
      */
   }
   /**  
   * Determines whether the map has a mapping from  
   * the block to some buffer.  
   * @paramblk the block to use as a key  
   * @return true if there is a mapping; false otherwise  
   */  
   boolean containsMapping(Block blk) {
		return bufferPoolMap.containsKey(blk);
	}
		
   /**  
   * Returns the buffer that the map maps the specified block to.  
   * @paramblk the block to use as a key  
   * @return the buffer mapped to if there is a mapping; null otherwise  
   */  

   Buffer getMapping(Block blk) {
		return bufferPoolMap.get(blk);
	}
   
   /*   private Buffer chooseUnpinnedBuffer() {
   for (Buffer buff : bufferpool)
      if (!buff.isPinned())
      return buff;
   return null;
}*/

	private Buffer chooseUnpinnedBuffer() {
		int max= -2;
		Buffer maxbuff = null;
		for (Buffer buff : bufferpool) {
			if(!buff.isAssigned()){
				return buff;
			}
			if (!buff.isPinned()) {
				int lsn = buff.getLogSequenceNumber();
				if (max<lsn){
					max=lsn;
					maxbuff=buff;
				}
			}
		}
		return maxbuff;
	}
	public void printBufferMap(){
		for(Block blk:bufferPoolMap.keySet()){
			System.out.println("block:"+blk+"\tbuffer:"+bufferPoolMap.get(blk).getLogSequenceNumber());
		}
	}
	public void getStatistics(){
		System.out.println("Buffer Statistics:");
		int i=0;
		for(Buffer buff : bufferpool){
			System.out.println("Buffer:"+i+"\treadCount:"+buff.getNumberOfReads()+
					"\twriteCount:"+buff.getNumberOfWrites()+"\tlogSequenceNumber:"+buff.getLogSequenceNumber());
			i++;
		}
	}
	
}
