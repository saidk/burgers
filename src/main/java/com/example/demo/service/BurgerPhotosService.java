package com.example.demo.service;

import com.example.demo.models.Photo;
import com.example.demo.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BurgerPhotosService {
    @Autowired
    BurgerPhotos burgerPhotos;

    @Autowired
    PhotoRepository photoRepository;

    private String clientId = "SGFXH34FPXEDJ3LIOS1USMY34XZWJ2WLW32B1FMEZDC0VQ52";
    private String clientSecret = "1K2QRZZWYDNYYYH53F1W3FSBDV5YXOS1OWRWMLNMOVJ533WB";

    public List<Photo> getBurgerPhotos(){
        if (photoRepository.findAll().size() != 0){
            return photoRepository.findAll();
        }
        else{
            try {
                HashMap<String,String> places = burgerPhotos.findPlaces(this.clientId, this.clientSecret);
                HashMap<String,String> photos = burgerPhotos.getPhotos(places);
                for (Map.Entry<String, String> photo : photos.entrySet()){
                    Photo photo_db = new Photo();
                    photo_db.setVenueName(photo.getValue());
                    photo_db.setPhoto_link(photo.getKey());
                    photoRepository.save(photo_db);
                }
                return photoRepository.findAll();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
