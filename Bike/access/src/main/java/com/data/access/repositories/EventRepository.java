package com.data.access.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.data.access.domain.Event;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByParkingId(Long parkingId);

    List<Event> findByParkingIdAndTimestampBetween(Long parkingId, Instant from, Instant to);

    List<Event> findByTimestamp(Instant timestamp);

    Optional<Event> findTopByParkingIdAndTimestampLessThanEqualOrderByTimestampDesc(Long parkingId, Instant timestamp);


}
