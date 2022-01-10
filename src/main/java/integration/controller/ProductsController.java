package integration.controller;


import integration.DTO.ProductDto;
import integration.entities.Products;
import integration.service.ProductsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/prod")
public class ProductsController {

    private final ProductsService productsService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductsController(ProductsService productsService, ModelMapper modelMapper) {

        this.productsService = productsService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/add/{id}")
    public void addProduct(@RequestBody ProductDto productsDto, @PathVariable Long id) {
        Products products = modelMapper.map(productsDto,Products.class);
    this.productsService.addProduct(products,id);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public List<Products> getAllProducts() {

        List<Products> productyList =productsService.getAllProducts();

        return productyList;

    }
    @DeleteMapping("{id}")
    public void deleteProducts(@PathVariable Long id) {

        productsService.deleteProduct(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Optional<Products> products1 = productsService.findById(id);
        Products products = modelMapper.map(productDto,Products.class);
        if (products1.isPresent()) {
            productsService.update(id, products);
        } else {
            productsService.addProduct(products,id);
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("{id}")
    public  List <Products>getProductById(@PathVariable Long id){

        return productsService.findProduct(id);
    }

}
