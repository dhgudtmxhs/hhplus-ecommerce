package kr.hhplus.be.server.product.application;

import kr.hhplus.be.server.product.domain.Product;
import kr.hhplus.be.server.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;
    private final ProductInfoMapper productInfoMapper;

    public ProductListInfo getProducts(int page, int size) {
        List<Product> products = productService.getProducts(page, size);
        long total = productService.getTotalCount();
        long totalPages = (long) Math.ceil((double) total / size);

        return productInfoMapper.toProductListInfo(products, page, size, total, totalPages);
    }

    public List<ProductInfo> getPopularProducts() {
        List<Product> popularProducts = productService.getPopularProducts();
        return productInfoMapper.toProductInfos(popularProducts);
    }
}
