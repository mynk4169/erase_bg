package in.mynk.erase_Bg.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoRepositories(basePackages = "in.mynk.erase_Bg.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "erasebg";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .retryReads(true)
                .applyToClusterSettings(builder -> 
                    builder.serverSelectionTimeout(30000, TimeUnit.MILLISECONDS)
                )
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(30000, TimeUnit.MILLISECONDS)
                )
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxSize(100)
                        .minSize(0)
                        .maxWaitTime(150000, TimeUnit.MILLISECONDS)
                        .maxConnectionLifeTime(300000, TimeUnit.MILLISECONDS)
                )
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
} 