package integration.controller;



import integration.DTO.CatgoryDto;
import integration.entities.Category;
import integration.service.CategoriesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categ")
public class CategoriesController {

private  final CategoriesService categoriesService;
private final ModelMapper modelMapper;
    @Autowired
    public CategoriesController(CategoriesService categoriesService, ModelMapper modelMapper) {
        this.categoriesService = categoriesService;

        this.modelMapper = modelMapper;
    }

    @PostMapping(path = "/add")
    public Category addCategory(@RequestBody CatgoryDto catgoryDto,@RequestHeader String Authorization){
    Category category =modelMapper.map(catgoryDto, Category.class);
        return categoriesService.addCategory(category);
    }
    @CrossOrigin(origins = "*")
    @GetMapping

    public List<Category> getAllCategory(@RequestHeader String Authorization) {


          return categoriesService.getAll();
        }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{id}")
    public Optional<Category> getCategoryById(@PathVariable Long id){

        return categoriesService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id,@RequestHeader String Authorization) {

        categoriesService.delete(id);
    }
    @PutMapping(path="{id}/update")
    public Category updateCategory(@RequestBody CatgoryDto categoryDto,@PathVariable long id,@RequestHeader String Authorization){
        Category category = modelMapper.map(categoryDto,Category.class);
        return categoriesService.updateCategory(id,category);
    }


}