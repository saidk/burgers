package com.example.demo.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Photo {
    @Id
    @GeneratedValue
    Long id;

    String venueName;
    String photo_link;
}