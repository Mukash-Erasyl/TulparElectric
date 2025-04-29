package org.example.tulparelectric.repository;

import org.example.tulparelectric.model.ElectricianCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectricianCatalogRepository extends JpaRepository<ElectricianCatalog, Long> {
}
