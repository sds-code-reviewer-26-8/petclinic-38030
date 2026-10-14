package com.example.petclinic.service;

import com.example.petclinic.entity.Pet;
import com.example.petclinic.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> findPets() {
        return petRepository.findAll();
    }

    /**
     * 현재 날짜 기준 생후 {@code year}년 미만이고 종류가 {@code typeName}인 동물을 조회한다.
     * year, type 중 하나만 전달되면 IllegalArgumentException 을 던진다.
     */
    public List<Pet> findPets(Integer year, String typeName) {
        boolean hasYear = year != null;
        boolean hasType = typeName != null && !typeName.isBlank();

        if (!hasYear && !hasType) {
            return findPets();
        }
        if (hasYear != hasType) {
            throw new IllegalArgumentException("year and type must be provided together");
        }
        if (year < 0) {
            throw new IllegalArgumentException("year must not be negative");
        }
        return petRepository.findByBirthDateAfterAndTypeNameIgnoreCase(
                LocalDateTime.now().minusYears(year), typeName);
    }

    public Pet findPet(Long petId) {
        return verifyPetExists(petId);
    }

    private Pet verifyPetExists(Long petId) {
        return petRepository.findById(petId).orElseThrow(
                () -> new NoSuchElementException("Pet does not exist")
        );
    }
}
