package com.portfolio.reportservice.mapper;

import com.portfolio.reportservice.dto.request.ReportRequest;
import com.portfolio.reportservice.dto.response.ReportResponse;
import com.portfolio.reportservice.entity.ReportEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportEntity toEntity(ReportRequest request);

    ReportResponse toResponse(ReportEntity entity);
}