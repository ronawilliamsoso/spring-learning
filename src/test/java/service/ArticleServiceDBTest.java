package service;

import com.zoro.eu.domain.product.ExchangeableArticle;
import com.wei.eu.pricing.model.Article;
import com.wei.eu.pricing.repository.ArticleRepository;
import com.wei.eu.pricing.repository.SupplierArticleRepository;
import com.wei.eu.pricing.repository.SupplierRepository;
import com.wei.eu.pricing.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
                //@TestInstance( TestInstance.Lifecycle.PER_CLASS )
                //@SpringBootTest
class ArticleServiceDBTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SupplierArticleRepository supplierArticleRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void handleExchangeableArticle() {

        final ExchangeableArticle exchangeableArticle = getExchangeableArticle();

        // articleService.handleExchangeableArticle( exchangeableArticle );

        final Optional<Article> optionalArticle = articleRepository.findById( exchangeableArticle.getArticleId() );
        assertTrue( optionalArticle.isPresent() );

    }

    private ExchangeableArticle getExchangeableArticle() {
        return ExchangeableArticle
                        .builder()
                        .articleId( "Z00000012" )
                        .active( true )
                        .build();
    }

    //    @BeforeAll
    //    void setUp() {
    //        articleService = new ArticleService( articleRepository, supplierArticleRepository, supplierRepository );
    //    }
}
