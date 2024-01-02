package org.springframework.samples.petclinic.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.mapper.VetMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.api.VetsApi;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping("api")
public class VetRestController implements VetsApi {

	private final ClinicService clinicService;
	private final VetMapper vetMapper;
	private final SpecialtyMapper specialtyMapper;

	public VetRestController(ClinicService clinicService, VetMapper vetMapper, SpecialtyMapper specialtyMapper) {
		this.clinicService = clinicService;
		this.vetMapper = vetMapper;
		this.specialtyMapper = specialtyMapper;
	}

	@PreAuthorize("hasRole(@roles.VET_ADMIN)")
	@Override
	public ResponseEntity<List<VetDto>> listVets() {
		List<VetDto> vets = new ArrayList<>();
		vets.addAll(vetMapper.toVetDtos(this.clinicService.findAllVets()));
		if (vets.isEmpty())
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(vets, HttpStatus.OK);
	}

	@PreAuthorize("hasRole(@roles.VET_ADMIN)")
	@Override
	public ResponseEntity<VetDto> getVet(Integer vetId) {
		Vet vet = this.clinicService.findVetById(vetId);
		if (vet == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(vetMapper.toVetDto(vet), HttpStatus.OK);
	}

	@PreAuthorize("hasRole(@roles.VET_ADMIN)")
	@Override
	public ResponseEntity<VetDto> addVet(VetDto vetDto) {
		HttpHeaders headers = new HttpHeaders();
		Vet vet = this.vetMapper.toVet(vetDto);
		if (vet.getNrOfSpecialties() > 0) {
			List<Specialty> vetSpecialties = this.clinicService.findSpecialtiesByNameIn(
					vet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
			vet.setSpecialties(vetSpecialties);
		}
		this.clinicService.saveVet(vet);
		return new ResponseEntity<>(vetMapper.toVetDto(vet), headers, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole(@roles.VET_ADMIN)")
	@Override
	public ResponseEntity<VetDto> updateVet(Integer vetId, VetDto vetDto) {
		Vet currentVet = this.clinicService.findVetById(vetId);
		if (currentVet == null)
			return new ResponseEntity<>(vetMapper.toVetDto(currentVet), HttpStatus.NO_CONTENT);
		currentVet.setFirstName(vetDto.getFirstName());
		currentVet.setLastName(vetDto.getLastName());
		currentVet.clearSpecialties();
		for (Specialty spec : specialtyMapper.toSpecialties(vetDto.getSpecialties())) {
			currentVet.addSpecialty(spec);
		}
		if (currentVet.getNrOfSpecialties() > 0) {
			List<Specialty> vetSpecialties = this.clinicService.findSpecialtiesByNameIn(
					currentVet.getSpecialties().stream().map(Specialty::getName).collect(Collectors.toSet()));
			currentVet.setSpecialties(vetSpecialties);
		}
		this.clinicService.saveVet(currentVet);
		return new ResponseEntity<>(vetMapper.toVetDto(currentVet), HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole(@roles.VET_ADMIN)")
	@Transactional
	@Override
	public ResponseEntity<VetDto> deleteVet(Integer vetId) {
		Vet vet = this.clinicService.findVetById(vetId);
		if (vet == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		this.clinicService.deleteVet(vet);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
