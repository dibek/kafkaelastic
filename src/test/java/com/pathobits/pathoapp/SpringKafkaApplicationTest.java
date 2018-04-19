package com.pathobits.pathoapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.pathobits.pathoapp.domain.Message;
import com.pathobits.pathoapp.repository.MessageRepository;
import com.pathobits.pathoapp.repository.search.MessageSearchRepository;
import com.pathobits.pathoapp.service.Receiver;
import com.pathobits.pathoapp.service.Sender;
import com.pathobits.pathoapp.web.rest.TestUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaelasticApp.class)
public class SpringKafkaApplicationTest {

    private static final String HELLO_MESSAGE = "Hello Boot!";
    private static final String TEST_CODE = "10";
    private static String BOOT_TOPIC = "boot.t";

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private MessageSearchRepository messageSearchRepository;

    @Autowired
    private MessageRepository messageRepository;

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, BOOT_TOPIC);


    @Test
    public void testReceive() throws Exception {

        Message message = new Message().code(TEST_CODE).messageBody(HELLO_MESSAGE);
        sender.send(BOOT_TOPIC, TestUtil.convertObjectToJsonString(message));

        receiver.getLatch().await(30000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);

        // Validate the Message in Elasticsearch
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
            .add(QueryBuilders.boolQuery().should(QueryBuilders.matchQuery("code", "10")),
                ScoreFunctionBuilders.weightFactorFunction(1000));

        // 创建搜索 DSL 查询
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(functionScoreQueryBuilder).build();
        Message messageEs = messageSearchRepository.search(searchQuery).getContent().get(0);
        assertThat(messageEs.getMessageBody()).isEqualTo(HELLO_MESSAGE);
    }
}
