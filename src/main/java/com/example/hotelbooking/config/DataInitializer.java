package com.example.hotelbooking.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.hotelbooking.model.Hotel;
import com.example.hotelbooking.model.User;
import com.example.hotelbooking.repository.HotelRepository;
import com.example.hotelbooking.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(HotelRepository hotelRepository, 
                                     UserRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        return args -> {
            initHotels(hotelRepository);
            initUsers(userRepository, passwordEncoder);
        };
    }

    private void initHotels(HotelRepository hotelRepository) {
        // Only add data if database is empty
        if (hotelRepository.count() == 0) {
            System.out.println("📝 Initializing sample hotel data...");
            
            // Hotel 1
            Hotel hotel1 = new Hotel();
            hotel1.setName("Grand Plaza Hotel");
            hotel1.setLocation("New York, NY");
            hotel1.setPricePerNight(new BigDecimal("249.99"));
            hotel1.setDescription("Luxury hotel in the heart of Manhattan with stunning city views and premium amenities.");
            hotel1.setRating(4);
            hotel1.setFeatured(true);
            hotel1.setAmenities("Free WiFi,Swimming Pool,Spa,Fitness Center,Restaurant");
            hotel1.setImage("https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80");
            hotelRepository.save(hotel1);

            // Hotel 2
            Hotel hotel2 = new Hotel();
            hotel2.setName("Luxury Suites Central");
            hotel2.setLocation("New York, NY");
            hotel2.setPricePerNight(new BigDecimal("349.99"));
            hotel2.setDescription("Premium suites with panoramic city views and exclusive services.");
            hotel2.setRating(5);
            hotel2.setFeatured(true);
            hotel2.setAmenities("Breakfast Included,Fitness Center,Bar,SPA,Concierge");
            hotel2.setImage("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80");
            hotelRepository.save(hotel2);

            // Hotel 3
            Hotel hotel3 = new Hotel();
            hotel3.setName("Riverside Inn");
            hotel3.setLocation("Chicago, IL");
            hotel3.setPricePerNight(new BigDecimal("199.99"));
            hotel3.setDescription("Comfortable stay by the river with modern amenities and friendly service.");
            hotel3.setRating(4);
            hotel3.setFeatured(false);
            hotel3.setAmenities("Free Parking,Restaurant,Pet Friendly,Free WiFi");
            hotel3.setImage("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80");
            hotelRepository.save(hotel3);

            System.out.println("✅ Added " + hotelRepository.count() + " sample hotels to database");
        } else {
            System.out.println("✅ Database already contains " + hotelRepository.count() + " hotels");
        }
    }

    private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@hotelbooking.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEnabled(true); // ENABLED
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin/admin123");
        } else {
            // Enable existing admin user
            User admin = userRepository.findByUsername("admin").get();
            if (!admin.isEnabled()) {
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("✅ Enabled existing admin user");
            }
        }

        // Create test user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("user@hotelbooking.com");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("USER");
            user.setEnabled(true); // ENABLED
            userRepository.save(user);
            System.out.println("✅ Test user created: user/user123");
        } else {
            // Enable existing user
            User user = userRepository.findByUsername("user").get();
            if (!user.isEnabled()) {
                user.setEnabled(true);
                userRepository.save(user);
                System.out.println("✅ Enabled existing user");
            }
        }

        System.out.println("🔐 ========== DATABASE USERS ==========");
        System.out.println("👑 Admin: username='admin' password='admin123'");
        System.out.println("👤 User: username='user' password='user123'");
        System.out.println("=======================================");
    }
}