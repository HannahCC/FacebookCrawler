package org.cl.service;

import java.util.HashSet;
import java.util.Iterator;

/** 管理UID*/
public class RWUid
{
	/** 用户ID集合*/
	private HashSet<String> ids=null;
	
	public RWUid()
	{
		ids=new HashSet<String>();
	}
	
	/** 加入UID*/
	public void setUid(String uid)
	{
		ids.add(uid);
	}
	
	/** 返回下一个，没有返回null*/
	public String getUid()
	{
		if(ids.size()<=0)
		{
			return null;
		}
		Iterator<String> iter=ids.iterator();
		String id=iter.next();
		ids.remove(id);
		return id;
	}
	
	/**返回剩下的ID数*/
	public int getNum()
	{
		return ids.size();
	}
	
	/**判断是否存在某ID*/
	public boolean isExist(String ID){
		return ids.contains(ID);
	}
	
	/**删除某存在ID**/
	public boolean delete(String ID){
		return ids.remove(ID);
	}
	
	public RWUid copy(){
		RWUid ids_copy = new RWUid();
		Iterator<String> it = ids.iterator();
		while(it.hasNext()){
			String str = it.next();
			ids_copy.setUid(str);
		}
		return ids_copy;
	}
}
