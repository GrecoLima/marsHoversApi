package com.marshoversapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marshoversapi.vo.HomeVO;

public interface PreferencesRepository extends JpaRepository<HomeVO, Long> {

  HomeVO findByUserId(Long userId);

}
