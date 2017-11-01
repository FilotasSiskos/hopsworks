package io.hops.hopsworks.api.device;

import com.google.gson.Gson;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToAvroConverter {

  public static List<GenericData.Record> convertJSONArrayToAvroRecords(String avroSchemaContents, JSONArray records)
    throws Exception{

    ArrayList<GenericData.Record> list = new ArrayList<>();
    Schema.Parser parser = new Schema.Parser();
    Schema schema = parser.parse(avroSchemaContents);

    // Loop through records
    for (int i = 0; i < records.length(); i++) {
      JSONObject object = records.getJSONObject(i);
      Map<String, Object> map = new HashMap<String, Object>();
      map = (Map<String, Object>) new Gson().fromJson(object.toString(), map.getClass());

      //create the avro message
      GenericData.Record avroRecord = new GenericData.Record(schema);
      for (Map.Entry<String, Object> message : map.entrySet()) {
        avroRecord.put(message.getKey(), message.getValue());
      }
      list.add(avroRecord);
    }
    return list;
  }

  public static List<GenericData.Record> toAvro(String avroSchemaContents, JSONArray records)
    throws IOException {

    ArrayList<GenericData.Record> list = new ArrayList<>();
    for (int i = 0; i < records.length(); i++) {
      JSONObject json = records.getJSONObject(i);
      Schema.Parser parser = new Schema.Parser();
      Schema schema = parser.parse(avroSchemaContents);
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, json.toString());
      DatumReader<GenericData.Record> reader = new GenericDatumReader<>(schema);
      list.add(reader.read(null, decoder));
    }
    return list;
  }
}




