package org.example.tulparelectric.service;

import org.example.tulparelectric.model.ElectricianCatalog;
import org.example.tulparelectric.repository.ElectricianCatalogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ElectricianService  {

    private final ElectricianCatalogRepository catalogRepository;

    public ElectricianService(ElectricianCatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<ElectricianCatalog> findAll(){
        return catalogRepository.findAll();
    }
    public Optional<ElectricianCatalog> findById(Long id){
        return catalogRepository.findById(id);
    }

    public ElectricianCatalog save(ElectricianCatalog electricianCatalog){
        return catalogRepository.save(electricianCatalog);
    }

    public ElectricianCatalog update(Long id, ElectricianCatalog updatedElectricianCatalog){
        var existing = catalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Услуга с id " + id + " не найдена"));

        existing.setServiceName(updatedElectricianCatalog.getServiceName());
        existing.setServiceDescription(updatedElectricianCatalog.getServiceDescription());
        existing.setServiceCategory(updatedElectricianCatalog.getServiceCategory());
        existing.setServiceImage(updatedElectricianCatalog.getServiceImage());
        existing.setUpdatedDate(LocalDateTime.now());

        return catalogRepository.save(existing);
    }

    public void deleteById(Long id){
        catalogRepository.deleteById(id);
    }
}
