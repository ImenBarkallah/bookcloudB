package com.bookcloud.smartlibrary.service;

import com.bookcloud.smartlibrary.dto.UpdateLibrarySettingsRequest;
import com.bookcloud.smartlibrary.model.LibrarySettings;

public interface LibrarySettingsService {
	LibrarySettings getOrDefault();
	LibrarySettings update(UpdateLibrarySettingsRequest req);
}
