package integration.service;


import integration.entities.Category;
import integration.entities.Products;
import integration.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    private final ProductsRepository productsRepository;

    private final CategoriesService categoriesService;

    @Autowired
    public ProductsService(ProductsRepository productsRepository, CategoriesService categoriesService) {
        this.productsRepository = productsRepository;
        this.categoriesService = categoriesService;
    }


    public Products addProduct(Products products, Long id){
        Category category = this.categoriesService.findOneCategory(id);
       category.addProduct(products);
        products.setCategory(category);
        return this.productsRepository.save(products);
    }



    public List<Products> getAllProducts(){

        return productsRepository.findAll();
    }


    public void deleteProduct(Long id) {
        Optional<Products> product = findById(id);
        product.ifPresent(productsRepository::delete);
    }

    public Optional<Products> findById(long id) {

        return productsRepository.findById(id);
    }

    public void update(long id, Products products) {
        Optional<Products> products1 = findById(id);
        if (products1.isPresent()) {
            Products forUpdate = products1.get();
            forUpdate.setName(products.getName());

            productsRepository.save(forUpdate);
        }
    }

    public List<Products> findProduct(Long id){
        Category category= categoriesService.findOneCategory(id);
        return category.getProductList();
    }


}
