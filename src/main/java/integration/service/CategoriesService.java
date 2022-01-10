package integration.service;

import integration.entities.Category;
import integration.entities.Products;
import integration.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }



    public Category addCategory(Category categories) {
// test null

        return categoriesRepository.save(categories);
    }


    public List<Category> getAll() {
       
        return categoriesRepository.findAll();
    }

    public Optional<Category> findById(Long id) {
        return categoriesRepository.findById(id);
    }

    public void delete(long id) {
        Optional<Category> category = findById(id);
       if (category.isPresent()){
            categoriesRepository.deleteById(id);
        }
    }

    public Category findOneCategory(long id) {

        return this.categoriesRepository.getById(id);
    }


    public Category updateCategory(long id, Category categoryUpdated) {
        Category category = this.categoriesRepository.getById(id);
        category.setName(categoryUpdated.getName());
        category.setUpdateddate(categoryUpdated.getUpdateddate());
        return categoriesRepository.save(category);
    }
}

