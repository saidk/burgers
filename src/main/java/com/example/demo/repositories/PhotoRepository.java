package com.example.demo.repositories;

import com.example.demo.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
