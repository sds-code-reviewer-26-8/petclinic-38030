package com.example.petclinic.service;

import com.example.petclinic.entity.Pet;
import com.example.petclinic.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    PetRepository petRepository;

    @InjectMocks
    PetService petService;

    @Test
    void findPets_withoutParams_returnsAll() {
        List<Pet> all = List.of(new Pet(), new Pet());
        when(petRepository.findAll()).thenReturn(all);

        assertThat(petService.findPets(null, null)).isEqualTo(all);
    }

    @Test
    void findPets_onlyYear_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> petService.findPets(2, null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(petRepository);
    }

    @Test
    void findPets_onlyType_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> petService.findPets(null, "dog"))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(petRepository);
    }

    @Test
    void findPets_yearAndType_queriesByBirthDateAndType() {
        List<Pet> young = List.of(new Pet());
        when(petRepository.findByBirthDateAfterAndTypeNameIgnoreCase(any(LocalDateTime.class), eq("dog")))
                .thenReturn(young);

        LocalDateTime before = LocalDateTime.now().minusYears(2);
        List<Pet> result = petService.findPets(2, "dog");
        LocalDateTime after = LocalDateTime.now().minusYears(2);

        assertThat(result).isEqualTo(young);
        verify(petRepository).findByBirthDateAfterAndTypeNameIgnoreCase(
                org.mockito.ArgumentMatchers.argThat(d -> !d.isBefore(before) && !d.isAfter(after)),
                eq("dog"));
    }
}
