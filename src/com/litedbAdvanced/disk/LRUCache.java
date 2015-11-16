package com.litedbAdvanced.disk;

import java.util.Hashtable;

public class LRUCache {
	
	class CacheNode {
		CacheNode prev;//前一节点
		CacheNode next;//后一节点
		Object value;//值
		Object key;//键
		CacheNode() {
		}
	}
	public LRUCache(int i) {
		currentSize = 0;
		cacheSize = i;
		nodes = new Hashtable(i);//缓存容器
	}
	
	//获取缓存对象
	public Object get(Object key) {
		CacheNode node = (CacheNode) nodes.get(key);
		if (node != null) {
			moveToHead(node);
			return node.value;
		} else {
			return null;
		}
	}
	
	//添加缓存
	public void put(Object key, Object value) {
		CacheNode node = (CacheNode) nodes.get(key);
		
		if (node == null) {
			//缓存容器是否已经超过大小.
			if (currentSize >= cacheSize) {
				if (last != null)//将最少使用的删除
					nodes.remove(last.key);
				removeLast();
			} else {
				currentSize++;
			}
			
			node = new CacheNode();
		}
		node.value = value;
		node.key = key;
		//将最新使用的节点放到链表头，表示最新使用的.
		moveToHead(node);
		nodes.put(key, node);
	}
	
	
	//将缓存删除
	public Object remove(Object key) {
		CacheNode node = (CacheNode) nodes.get(key);
		if (node != null) {
			if (node.prev != null) {
				node.prev.next = node.next;
			}
			if (node.next != null) {
				node.next.prev = node.prev;
			}
			if (last == node)
				last = node.prev;
			if (first == node)
				first = node.next;
		}
		return node;
	}
	public void clear() {
		first = null;
		last = null;
	}
	
	
	//删除链表尾部节点
	private void removeLast() {
		//链表尾不为空,则将链表尾指向null. 删除连表尾（删除最少使用的缓存对象）
		if (last != null) {
			if (last.prev != null)
				last.prev.next = null;
			else
				first = null;
			last = last.prev;
		}
	}
	
	//移动到链表头部，表示最新使用的缓存对象
	private void moveToHead(CacheNode node) {
		if (node == first)
			return;
		if (node.prev != null)
			node.prev.next = node.next;
		if (node.next != null)
			node.next.prev = node.prev;
		if (last == node)
			last = node.prev;
		if (first != null) {
			node.next = first;
			first.prev = node;
		}
		first = node;
		node.prev = null;
		if (last == null)
			last = first;
	}
	private int cacheSize;
	private Hashtable nodes;//缓存容器
	private int currentSize;
	private CacheNode first;//链表头
	private CacheNode last;//链表尾
}