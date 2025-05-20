package com.quickconnect.customerManagerService.datastore;

import com.quickconnect.customerManagerService.model.Customer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoCustomerPersistence extends CustomerPersistence {

    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final String TABLE_NAME = "Customers";

    @Override
    public List<Customer> load() {
        ScanResponse scan = dynamoDb.scan(ScanRequest.builder().tableName(TABLE_NAME).build());
        return scan.items().stream().map(this::mapToCustomer).collect(Collectors.toList());
    }

    @Override
    public void save(List<Customer> customers) {
        for (Customer c : customers) {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.fromN(c.getId().toString()));
            item.put("firstName", AttributeValue.fromS(c.getFirstName()));
            item.put("lastName", AttributeValue.fromS(c.getLastName()));
            item.put("age", AttributeValue.fromN(String.valueOf(c.getAge())));

            PutItemRequest req = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();

            dynamoDb.putItem(req);
        }
    }

    private Customer mapToCustomer(Map<String, AttributeValue> item) {
        Customer c = new Customer();
        c.setId(Integer.valueOf(item.get("id").n()));
        c.setFirstName(item.get("firstName").s());
        c.setLastName(item.get("lastName").s());
        c.setAge(Integer.parseInt(item.get("age").n()));
        return c;
    }
}

