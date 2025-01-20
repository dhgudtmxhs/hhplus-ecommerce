// OrderControllerIntegrationTest.java
package kr.hhplus.be.server.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.order.interfaces.OrderRequest;
import kr.hhplus.be.server.order.interfaces.ProductOrderRequest;
import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.infra.ProductJpaRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.infra.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    private User existingUser;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();

        // 유저 데이터 생성 및 저장
        existingUser = User.builder()
                .name("Test User")
                .build();
        existingUser = userJpaRepository.save(existingUser);

        productJpaRepository.deleteAll();
        Product product = Product.builder()
                .id(null)
                .name("Test Product")
                .price(1000L)
                .stock(10L)
                .build();
        productJpaRepository.save(product);

    }

    @Test
    void 유효한_주문_요청을_처리하고_주문정보를_반환한다() throws Exception {
        // Given
        OrderRequest request = new OrderRequest(existingUser.getId(), List.of(
                new ProductOrderRequest(1L, 2L)
        ));

        // When & Then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(existingUser.getId()))
                .andExpect(jsonPath("$.orderItems[0].productId").value(1L))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2L));
    }
}
