package kr.hhplus.be.server.product;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.order.infra.OrderItemJpaRepository;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.delete("popularProducts::popularProducts");

        orderItemJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        entityManager.flush();

        Product product1 = Product.builder()
                .name("Product 1")
                .price(1000L)
                .stock(50L)
                .build();

        Product product2 = Product.builder()
                .name("Product 2")
                .price(2000L)
                .stock(30L)
                .build();

        Product product3 = Product.builder()
                .name("Product 3")
                .price(1500L)
                .stock(40L)
                .build();

        Product product4 = Product.builder()
                .name("Product 4")
                .price(1200L)
                .stock(20L)
                .build();

        Product product5 = Product.builder()
                .name("Product 5")
                .price(1800L)
                .stock(25L)
                .build();

        Product product6 = Product.builder()
                .name("Product 6")
                .price(1600L)
                .stock(35L)
                .build();

        Product product7 = Product.builder()
                .name("Product 7")
                .price(1100L)
                .stock(15L)
                .build();

        productJpaRepository.saveAll(Arrays.asList(product1, product2, product3, product4, product5, product6, product7));

        // 주문 데이터 생성 (day 단위로 구분)
        createOrderItemWithQuery(product1.getId(), 10L, LocalDateTime.now().minusDays(1)); // 최근 3일 이내
        createOrderItemWithQuery(product2.getId(), 20L, LocalDateTime.now().minusDays(2)); // 최근 3일 이내
        createOrderItemWithQuery(product3.getId(), 15L, LocalDateTime.now().minusDays(1)); // 최근 3일 이내
        createOrderItemWithQuery(product4.getId(), 30L, LocalDateTime.now().minusDays(3).plusMinutes(1)); // 최근 3일 이내 - 호출 시점간의 시간경과로 인한 분 추가
        createOrderItemWithQuery(product5.getId(), 5L, LocalDateTime.now().minusDays(2));  // 최근 3일 이내
        createOrderItemWithQuery(product6.getId(), 25L, LocalDateTime.now().minusDays(1)); // 최근 3일 이내
        createOrderItemWithQuery(product7.getId(), 50L, LocalDateTime.now().minusDays(5)); // 3일 이전

        // 추가 데이터로 sum 테스트 (day 단위로 구분)
        createOrderItemWithQuery(product5.getId(), 50L, LocalDateTime.now().minusDays(1)); // 3일 이내 55
        createOrderItemWithQuery(product1.getId(), 25L, LocalDateTime.now().minusDays(1)); // 3일 이내 35
        createOrderItemWithQuery(product2.getId(), 10L, LocalDateTime.now().minusDays(2)); // 3일 이내 30
        createOrderItemWithQuery(product6.getId(), 30L, LocalDateTime.now().minusDays(1)); // 3일 이내 25
        createOrderItemWithQuery(product3.getId(), 20L, LocalDateTime.now().minusDays(1)); // 3일 이내 35
        createOrderItemWithQuery(product4.getId(), 10L, LocalDateTime.now().minusDays(2)); // 3일 이내 40
        createOrderItemWithQuery(product7.getId(), 60L, LocalDateTime.now().minusDays(5)); // 3일 이전
    }

    private void createOrderItemWithQuery(Long productId, Long quantity, LocalDateTime created_at) {
        String sql = "INSERT INTO order_item (order_id, product_id, product_name,price, quantity, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        entityManager.createNativeQuery(sql)
                .setParameter(1, 1L)
                .setParameter(2, productId)
                .setParameter(3, "test")
                .setParameter(4, 100L)
                .setParameter(5, quantity)
                .setParameter(6, created_at)
                .setParameter(7, created_at)
                .executeUpdate();
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete("popularProducts::popularProducts");
    }

    @Test
    void 최근_3일_기준_판매량_상위_5개_상품을_조회한다() throws Exception {

        // When & Then
        mockMvc.perform(get("/api/products/popular")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Product 5"))
                .andExpect(jsonPath("$[1].name").value("Product 6"))
                .andExpect(jsonPath("$[2].name").value("Product 4"))
                .andExpect(jsonPath("$[3].name").value("Product 1"))
                .andExpect(jsonPath("$[4].name").value("Product 3"));
    }

    @Test
    void 요청한_페이지와_크기로_모든_상품을_조회한다() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        // When & Then
        mockMvc.perform(get("/api/products")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.length()").value(7))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.totalPages").isNumber())
                .andExpect(jsonPath("$.products[0].name").value("Product 1"));
    }

}
