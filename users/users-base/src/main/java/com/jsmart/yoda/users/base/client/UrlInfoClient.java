package com.jsmart.yoda.users.base.client;

import com.jsmart.yoda.users.base.UrlInfo;

import java.util.List;

/**
 * @author Sergey Khomich
 *
 */
public interface UrlInfoClient {

	public List<UrlInfo> get(int id);

	public boolean remove(int id);

	public boolean removeAll();

}

