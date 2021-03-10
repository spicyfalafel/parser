package ru.my.parser.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.SneakyThrows;
import ru.my.parser.model.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVOrdersFile implements OrdersFile {

    @Override
    @SneakyThrows
    public void toFile(List<Order> orders, String filename) {
        String json = listToJson(orders);

        JsonNode jsonTree = new ObjectMapper().readTree(json);

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();

        OutputStream outstream = new FileOutputStream(
                filename == null || filename.equals("") ? "orders.csv" : filename, true);
        OutputStreamWriter osw =  new OutputStreamWriter(outstream,
                StandardCharsets.UTF_8);
        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(osw, jsonTree);

    }

    public String listToJson(List<Order> orders) throws IOException {
        final StringWriter sw = new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(sw, orders);
        sw.close();
        return sw.toString();
    }
}
