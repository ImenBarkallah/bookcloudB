package com.bookcloud.smartlibrary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookcloud.smartlibrary.model.LibrarySettings;

public interface LibrarySettingsRepository extends JpaRepository<LibrarySettings, Long> {

	Optional<LibrarySettings> findTopByOrderByIdAsc();
}
