package com.datapulse.ai.infrastructure.mapper;

import com.datapulse.ai.core.domain.Dataset;
import com.datapulse.ai.core.domain.DatasetVersion;
import com.datapulse.ai.infrastructure.entity.DatasetEntity;
import com.datapulse.ai.infrastructure.entity.DatasetVersionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DatasetMapper {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DatasetEntity toEntity(Dataset dataset);

    Dataset toDomain(DatasetEntity entity);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    DatasetVersionEntity toEntity(DatasetVersion version);

    DatasetVersion toDomain(DatasetVersionEntity entity);
}
