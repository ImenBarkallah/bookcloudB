package com.bookcloud.smartlibrary.service;

import java.util.List;
import java.util.Set;

public interface BookFavoriteService {
	boolean toggle(String userUid, Long bookId);
	Set<Long> favoriteIdsAmong(String userUid, List<Long> bookIds);
	List<Long> listAllFavoriteBookIds(String userUid);
	int countFavorites(String userUid);
}
