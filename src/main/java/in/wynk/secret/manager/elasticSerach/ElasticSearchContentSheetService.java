package in.wynk.secret.manager.elasticSerach;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.wynk.secret.manager.dto.ContentPartner;
import in.wynk.secret.manager.service.ContentWithMissingFields;
import in.wynk.secret.manager.service.ReportEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ElasticSearchContentSheetService {

    @Value(value = "${wynk.elastic.url}")
    private String url;

    @Value(value = "${wynk.elastic.missing.content.index}")
    private String missingContentIndex;
    @Value(value = "${wynk.elastic.error.reporting.index}")
    private String reportIndex;
    @Value(value = "${wynk.elastic.userName}")
    private String userName;
    @Value(value = "${wynk.elastic.password}")
    private String password;


    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchContentSheetService.class);

    private RestHighLevelClient highLevelClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        try {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

            RestClientBuilder clientBuilder = RestClient.builder(
                    new HttpHost(url, 9200, "http"))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider));
            highLevelClient = new RestHighLevelClient(clientBuilder);
        } catch (Exception e) {
            logger.error("MissingContentSheet:: Unable to build rest client for elasticSearch", e);
        }
    }

    public SearchResponse fetchMissingFieldData(Long startTime, Long endTime, ContentPartner cp, Integer size, Integer from, String searchAfter) throws IOException {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        SearchRequest searchRequest = new SearchRequest();
        logger.info("MissingContentReport :: Fetching Missing Field Data for cp {}", cp);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder
                        .must(new TermQueryBuilder("cp.keyword", cp.name()))
                        .must(new RangeQueryBuilder("startTime").gte(startTime).lte(endTime)))
                .size(size)
                .from(from)
                .sort("_id", SortOrder.ASC);
        logger.info("The searchSourceBuilder is ::{}", searchSourceBuilder);
        if (searchAfter != null) {
            searchSourceBuilder.searchAfter(new Object[]{searchAfter});
        }
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(missingContentIndex);
        return highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    }

    public void pushToElasticSearch(ContentWithMissingFields contentWithMissingFields, Date startTime) throws IOException {
        Map<String, Object> source = objectMapper.convertValue(contentWithMissingFields, Map.class);
        source.put("startTime", startTime);
        IndexRequest indexRequest = new IndexRequest(missingContentIndex, "_doc");
        indexRequest.source(source);
        highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void pushReportToElasticSearch(ReportEntity entity, Date startTime) throws IOException {
            Map<String, Object> source = objectMapper.convertValue(entity, Map.class);
            source.put("startTime", startTime);
            IndexRequest indexRequest = new IndexRequest(reportIndex, "_doc");
            indexRequest.source(source);
            highLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void pushReportToElasticSearch(List<ReportEntity> entityList, Date startTime) throws IOException {
        if(CollectionUtils.isEmpty(entityList)) {
            return;
        }
        logger.info("indexing to elastic, report list of size: {}", entityList.size());

        BulkRequest request = new BulkRequest();

        for(ReportEntity entity: entityList) {
            Map<String, Object> source = objectMapper.convertValue(entity, Map.class);
            source.put("startTime", startTime);
            IndexRequest indexRequest = new IndexRequest(reportIndex, "_doc");
            indexRequest.source(source);
            request.add(indexRequest);
        }

        highLevelClient.bulk(request, RequestOptions.DEFAULT);
    }


    @PreDestroy
    private void destroy() {
        try {
            if (Objects.nonNull(highLevelClient))
                highLevelClient.close();
        } catch (Exception e) {
            logger.error("MissingContentSheet:: Error in destroying Rest Client ElasticSearch", e);
        }
    }
}
