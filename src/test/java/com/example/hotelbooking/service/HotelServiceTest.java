package com.example.hotelbooking.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.repository.HotelRepository;

@ExtendWith(MockitoExtension.class)
class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel testHotel;

    @BeforeEach
    void setUp() {
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Grand Plaza Hotel");
        testHotel.setLocation("New York");
        testHotel.setPricePerNight(new BigDecimal("199.99"));
        testHotel.setRating(5);
        testHotel.setDescription("Luxury hotel");
        testHotel.setFeatured(true);
    }

    @Test
    void testGetHotelByIdWithString() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        Hotel foundHotel = hotelService.getHotelById("1");

        assertNotNull(foundHotel);
        assertEquals("Grand Plaza Hotel", foundHotel.getName());
        assertEquals("New York", foundHotel.getLocation());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHotelByIdWithStringInvalidFormat() {
        assertThrows(RuntimeException.class, () -> {
            hotelService.getHotelById("invalid");
        });
    }

    @Test
    void testGetHotelByIdWithLong() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        Hotel foundHotel = hotelService.getHotelById(1L);

        assertNotNull(foundHotel);
        assertEquals(1L, foundHotel.getId());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetHotelByIdOptional() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        Optional<Hotel> foundHotel = hotelService.getHotelByIdOptional(1L);

        assertTrue(foundHotel.isPresent());
        assertEquals("Grand Plaza Hotel", foundHotel.get().getName());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllHotels() {
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelRepository.findAll()).thenReturn(hotels);

        List<Hotel> foundHotels = hotelService.getAllHotels();

        assertEquals(1, foundHotels.size());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void testSaveHotel() {
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);

        Hotel savedHotel = hotelService.saveHotel(testHotel);

        assertNotNull(savedHotel);
        assertEquals("Grand Plaza Hotel", savedHotel.getName());
        verify(hotelRepository, times(1)).save(testHotel);
    }

    @Test
    void testGetFeaturedHotels() {
        List<Hotel> featuredHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByFeaturedTrue()).thenReturn(featuredHotels);

        List<Hotel> foundHotels = hotelService.getFeaturedHotels();

        assertEquals(1, foundHotels.size());
        assertTrue(foundHotels.get(0).getFeatured()); // FIXED: Use getFeatured() instead of isFeatured()
        verify(hotelRepository, times(1)).findByFeaturedTrue();
    }

    @Test
    void testGetFeaturedHotels_Empty() {
        when(hotelRepository.findByFeaturedTrue()).thenReturn(Arrays.asList());
        when(hotelRepository.findAll()).thenReturn(Arrays.asList(testHotel));

        List<Hotel> foundHotels = hotelService.getFeaturedHotels();

        assertFalse(foundHotels.isEmpty());
        verify(hotelRepository, times(1)).findByFeaturedTrue();
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    void testSearchHotelsWithLocation() {
        List<Hotel> searchResults = Arrays.asList(testHotel);
        when(hotelRepository.findByLocationContainingIgnoreCase("New York"))
            .thenReturn(searchResults);

        List<Hotel> foundHotels = hotelService.searchHotels("New York", 2);

        assertEquals(1, foundHotels.size());
        assertEquals("New York", foundHotels.get(0).getLocation());
        verify(hotelRepository, times(1)).findByLocationContainingIgnoreCase("New York");
    }

    @Test
    void testSearchHotelsWithoutLocation() {
        List<Hotel> featuredHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByFeaturedTrue()).thenReturn(featuredHotels);

        List<Hotel> foundHotels = hotelService.searchHotels(null, 2);

        assertEquals(1, foundHotels.size());
        verify(hotelRepository, times(1)).findByFeaturedTrue();
    }

    @Test
    void testSearchHotelsWithEmptyLocation() {
        List<Hotel> featuredHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByFeaturedTrue()).thenReturn(featuredHotels);

        List<Hotel> foundHotels = hotelService.searchHotels("", 2);

        assertEquals(1, foundHotels.size());
        verify(hotelRepository, times(1)).findByFeaturedTrue();
    }

    @Test
    void testGetPopularDestinations() {
        List<String> destinations = Arrays.asList("New York", "Miami", "Chicago");
        when(hotelRepository.findDistinctLocations()).thenReturn(destinations);

        List<String> foundDestinations = hotelService.getPopularDestinations();

        assertEquals(3, foundDestinations.size());
        assertTrue(foundDestinations.contains("New York"));
        verify(hotelRepository, times(1)).findDistinctLocations();
    }

    @Test
    void testGetPopularDestinations_Empty() {
        when(hotelRepository.findDistinctLocations()).thenReturn(Arrays.asList());

        List<String> foundDestinations = hotelService.getPopularDestinations();

        assertFalse(foundDestinations.isEmpty()); // Should return fallback destinations
        verify(hotelRepository, times(1)).findDistinctLocations();
    }

    @Test
    void testGetHotelsByLocation() {
        List<Hotel> locationHotels = Arrays.asList(testHotel);
        when(hotelRepository.findByLocationContainingIgnoreCase("New York"))
            .thenReturn(locationHotels);

        List<Hotel> foundHotels = hotelService.getHotelsByLocation("New York");

        assertEquals(1, foundHotels.size());
        verify(hotelRepository, times(1)).findByLocationContainingIgnoreCase("New York");
    }

    @Test
    void testGetHotelsByLocation_EmptyLocation() {
        List<Hotel> foundHotels = hotelService.getHotelsByLocation("");

        assertTrue(foundHotels.isEmpty());
        verify(hotelRepository, never()).findByLocationContainingIgnoreCase(anyString());
    }

    @Test
    void testGetHotelsByLocation_NullLocation() {
        List<Hotel> foundHotels = hotelService.getHotelsByLocation(null);

        assertTrue(foundHotels.isEmpty());
        verify(hotelRepository, never()).findByLocationContainingIgnoreCase(anyString());
    }

    @Test
    void testDeleteHotelWithString() {
        hotelService.deleteHotel("1");

        verify(hotelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteHotelWithLong() {
        hotelService.deleteHotel(1L);

        verify(hotelRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteHotelWithStringInvalidFormat() {
        assertThrows(RuntimeException.class, () -> {
            hotelService.deleteHotel("invalid");
        });
    }

    @Test
    void testHotelConstructorWithParameters() {
        Hotel hotel = new Hotel("Test Hotel", "Test Location", new BigDecimal("100.00"));

        assertEquals("Test Hotel", hotel.getName());
        assertEquals("Test Location", hotel.getLocation());
        assertEquals(new BigDecimal("100.00"), hotel.getPricePerNight());
    }

    @Test
    void testHotelPriceCompatibilityMethods() {
        Hotel hotel = new Hotel();
        hotel.setPrice(new BigDecimal("150.00"));

        assertEquals(new BigDecimal("150.00"), hotel.getPrice());
        assertEquals(new BigDecimal("150.00"), hotel.getPricePerNight());
    }

    @Test
    void testHotelDefaultValues() {
        Hotel hotel = new Hotel();

        assertNull(hotel.getRating());
        assertNull(hotel.getFeatured());
        assertNull(hotel.getImage());
        assertNull(hotel.getAmenities());
        assertNull(hotel.getDescription());
    }
}