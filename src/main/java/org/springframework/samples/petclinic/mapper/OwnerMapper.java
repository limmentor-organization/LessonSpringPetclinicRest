package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;

@Mapper(uses = PetMapper.class)
public class OwnerMapper {

}
