package com.softwells.fanops.service;

import com.softwells.fanops.repository.IbanBicMappingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BicLookupService {

  private final IbanBicMappingRepository ibanBicMappingRepository;

  public Optional<String> getBicForIban(String iban) {
    if (iban == null || iban.length() < 8) {
      return Optional.empty();
    }
    // El código de banco son los 4 dígitos después del código de país y el dígito de control
    String bankCode = iban.substring(4, 8);
    return ibanBicMappingRepository.findById(bankCode).map(mapping -> mapping.getBic());
  }
}