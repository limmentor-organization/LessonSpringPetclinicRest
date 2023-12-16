package org.springframework.samples.petclinic.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;

public interface ClinicService {

	public Specialty findSpecialtyById(int specialtyId);

	public Collection<Specialty> findAllSpecialties() throws DataAccessException;

	public void saveSpecialty(Specialty specialty) throws DataAccessException;

	public void deleteSpecialty(Specialty specialty) throws DataAccessException;

	List<Specialty> findSpecialtiesByNameIn(Set<String> names) throws DataAccessException;

}
