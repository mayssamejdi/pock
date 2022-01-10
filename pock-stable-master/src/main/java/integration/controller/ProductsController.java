package integration.controller;


import integration.DTO.ProductDto;
import integration.entities.Products;
import integration.service.ProductsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
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
    public void addProduct(@RequestBody ProductDto productsDto, @PathVariable Long id, @RequestHeader String Authorization) {
        Products products = modelMapper.map(productsDto,Products.class);
    this.productsService.addProduct(products,id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public List<Products> getAllProducts() {

        List<Products> productyList =productsService.getAllProducts();

        return productyList;

    }
    @DeleteMapping("{id}")
    public void deleteProducts(@PathVariable Long id,@RequestHeader String Authorization) {

        productsService.deleteProduct(id);
    }

    @PutMapping("{id}")
    public void update(@PathVariable Long id, @RequestBody ProductDto productDto,@RequestHeader String Authorization ) {
        Optional<Products> products1 = productsService.findById(id);
        Products products = modelMapper.map(productDto,Products.class);
        if (products1.isPresent()) {
            productsService.update(id, products);
        } else {
            productsService.addProduct(products,id);
        }
    }
    @CrossOrigin(origins = "*")
    @GetMapping("{id}")
    public  List <Products>getProductById(@PathVariable Long id){

        return productsService.findProduct(id);
    }

}
