# SimpleDB
Project to improve the buffer manager efficiency of SimpleDB.

The SimpleDB buffer manager is grossly inefficient in two ways: 
•	When looking for a buffer to replace, it uses the first unpinned buffer it finds, instead of using some intelligent replacement policy. 
•	When checking to see if a block is already in a buffer, it does a sequential scan of the buffers, instead of keeping a data structure (such as a map) to more quickly locate the buffer. 

We improve these fundamental flaws by implementing following changes:
•	When looking for a buffer to replace, employ the Most Recently Modified (MRM) page replacement policy. 
•	When checking to see if a block is already in a buffer,maintain a Hashmap mapping block to buffer to check in O(1) time.
