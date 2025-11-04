package com.thebrideside.crm.crm.config;

import com.thebrideside.crm.crm.dto.EmployeeDTO;
import com.thebrideside.crm.crm.entity.Employees;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // Skip ID mapping confusion
        mapper.typeMap(EmployeeDTO.class, Employees.class)
                .addMappings(m -> m.skip(Employees::setId));

        // ✅ Add this mapping for the reverse direction
        mapper.addMappings(new PropertyMap<Employees, EmployeeDTO>() {
            @Override
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        });

        return mapper;
    }
}