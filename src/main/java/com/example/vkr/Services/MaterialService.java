package com.example.vkr.Services;

import com.example.vkr.Models.Material;
import com.example.vkr.Repositories.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<Material> getAllMaterials() {
        List<Material> materials = materialRepository.findAll();
        return (materials != null) ? materials : new ArrayList<>();
    }

    public Optional<Material> getMaterialById(int id) {
        return materialRepository.findById(id);
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void deleteMaterial(int id) {
        materialRepository.deleteById(id);
    }
}
