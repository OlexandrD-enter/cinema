package com.project.cinemaservice.domain.mapper;


import com.project.cinemaservice.domain.dto.order.OrderClientResponse;
import com.project.cinemaservice.persistence.model.Order;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface responsible for mapping between Movie entity and related DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

  OrderClientResponse toOrderClientResponse(Order order, Long showtimeId, List<Long> bookedSeatIds);
}
