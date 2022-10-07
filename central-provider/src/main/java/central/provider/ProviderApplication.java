package central.provider;

import central.starter.graphql.EnableGraphQL;
import central.starter.orm.EnableOrm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Provider Application
 *
 * @author Alan Yeh
 * @since 2022/07/07
 */
@EnableOrm
@EnableGraphQL
@SpringBootApplication
public class ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }
}
