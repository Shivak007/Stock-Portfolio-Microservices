package com.portfolio.reportservice.mapper;

import com.portfolio.reportservice.dto.request.ReportRequest;
import com.portfolio.reportservice.dto.response.ReportResponse;
import com.portfolio.reportservice.entity.ReportEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-08T16:01:25+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Eclipse Adoptium)"
)
@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public ReportEntity toEntity(ReportRequest request) {
        if ( request == null ) {
            return null;
        }

        ReportEntity.ReportEntityBuilder reportEntity = ReportEntity.builder();

        reportEntity.userId( request.getUserId() );
        reportEntity.reportType( request.getReportType() );

        return reportEntity.build();
    }

    @Override
    public ReportResponse toResponse(ReportEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReportResponse.ReportResponseBuilder reportResponse = ReportResponse.builder();

        reportResponse.id( entity.getId() );
        reportResponse.userId( entity.getUserId() );
        reportResponse.reportType( entity.getReportType() );
        reportResponse.fileName( entity.getFileName() );
        reportResponse.generatedAt( entity.getGeneratedAt() );

        return reportResponse.build();
    }
}
