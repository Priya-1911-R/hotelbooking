package com.example.hotelbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.repository.HotelRepository;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository repo) {
        this.hotelRepository = repo;
    }

    public @NonNull Hotel createHotel(@NonNull Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public @NonNull Hotel updateHotel(@NonNull Long id, @NonNull Hotel data) {
        Optional<Hotel> o = hotelRepository.findById(id);
        if (o.isEmpty()) throw new RuntimeException("Hotel not found");
        Hotel h = o.get();
        h.setName(data.getName());
        h.setCity(data.getCity());
        h.setAddress(data.getAddress());
        h.setDescription(data.getDescription());
        h.setRating(data.getRating());
        h.setTotalRooms(data.getTotalRooms());
        return hotelRepository.save(h);
    }

    public void deleteHotel(@NonNull Long id) {
        hotelRepository.deleteById(id);
    }

    public @NonNull List<Hotel> getAll() {
        return hotelRepository.findAll();
    }

    public Optional<Hotel> getById(@NonNull Long id) {
        return hotelRepository.findById(id);
    }

    public List<Hotel> searchByCity(@NonNull String city) {
        return hotelRepository.findByCityContainingIgnoreCase(city);
    }

    public List<Hotel> searchByName(@NonNull String name) {
        return hotelRepository.findByNameContainingIgnoreCase(name);
    }
}
