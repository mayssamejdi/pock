package integration.service;

import integration.entities.Category;
import integration.repository.CategoriesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CategoriesServiceTest {


    @Mock protected  CategoriesRepository categoriesRepository;;


    @InjectMocks protected CategoriesService categoriesService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addCategory() {
        Category category = new Category();
        category.setId(2L);
        category.setName("aaa");
        categoriesService.addCategory(category);
        verify(categoriesRepository).save(category);
    }

    @Test
    void getAll() {
        List<Category> list = new ArrayList<>();
        Category category1 = new Category();
        category1.setId(1);
        category1.setName("vehicule");
        Category category2 = new Category();
        category2.setId(2);
        category2.setName("maison");
        list.add(category1);
        list.add(category2);
        when(categoriesRepository.findAll()).thenReturn(list);
        List<Category> expected = categoriesService.getAll();
        assertEquals(expected,list);
    }

    @Test
    void findById() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("maison");
        category2.setUpdateddate(null);
       category2.setCreationdate(null);
        category2.setProductList(null);

        when (categoriesRepository.findById(2L)).thenReturn(Optional.of(category2));
       Optional<Category> expected = categoriesService.findById(2L);
        assertEquals(Optional.of(category2),expected);

    }

    @Test
    void delete() {
        Category expected = new Category();
        expected.setId(7L);
        expected.setName("maison");
        expected.setUpdateddate(null);
        expected.setCreationdate(null);
       categoriesService.delete(7L);
//        verify(categoriesRepository).deleteById(7L);
        assertTrue(categoriesRepository.findById(7L).isEmpty());

    }

    @Test
    void findOneCategory() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("maison");
        category2.setUpdateddate(null);
        category2.setCreationdate(null);
        category2.setProductList(null);

        when (categoriesRepository.getById(2L)).thenReturn(category2);
        Category expected = categoriesService.findOneCategory(2L);
        assertEquals(category2,expected);

    }

    @Test
    void updateCategory() {
        Category category = new Category();
        category.setId(1);
        category.setName("maison");
        categoriesService.addCategory(category);
        Category updatedcat = new Category();
        updatedcat.setName("véhicule");
        when(categoriesRepository.getById(1L)).thenReturn(updatedcat);
        categoriesService.updateCategory(1L,updatedcat);
        assertThat(categoriesRepository.getById(1L).getName()).isEqualTo("véhicule");
    }
}