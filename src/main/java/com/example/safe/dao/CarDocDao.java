package com.example.safe.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.safe.SafeApplication;
import com.example.safe.dao.entity.CarDoc;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author cblecken
 *
 *  While not exactly having the convenience of a spring boot code, the 
 *  elastic search index interface layer is written in just spring.
 *  It uses the newly published Rest High level API and has the support
 *  for adding documents to the index as well as our two supported search
 *  functions (by year and by make).
 */
@Repository
public class CarDocDao {
	private static final Logger LOG = LoggerFactory.getLogger(CarDocDao.class);

	private final String INDEX = "cars";
	private final String TYPE = "model";  
	private RestHighLevelClient restHighLevelClient;
	private ObjectMapper objectMapper;
	private static String MAKE = "make";
	private static String YEAR = "year";

	public CarDocDao( ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
	    this.objectMapper = objectMapper;
	    this.restHighLevelClient = restHighLevelClient;
	}
	  
	public CarDoc insertCar(CarDoc cares){
	  cares.setId(UUID.randomUUID().toString());
	  Map dataMap = objectMapper.convertValue(cares, Map.class);
	  IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, cares.getId())
	                .source(dataMap);
	  try {
	    IndexResponse response = restHighLevelClient.index(indexRequest);
	  } catch(ElasticsearchException e) {
		LOG.error("Exception as insertCar " + e.getDetailedMessage());
	  } catch (java.io.IOException ex){
		LOG.error("Exception as insertCar " + ex.getLocalizedMessage());
	  }
	  return cares;
	}
	  
	public void insertCarList(List<CarDoc> caresList){
	  BulkRequest bulkIndexRequest = new BulkRequest();
	  for (CarDoc cares : caresList) { 
		  cares.setId(UUID.randomUUID().toString());
		  Map dataMap = objectMapper.convertValue(cares, Map.class);
		  IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, cares.getId())
		                .source(dataMap);
		  bulkIndexRequest.add(indexRequest);
	  }
	  try {
	    BulkResponse response = restHighLevelClient.bulk(bulkIndexRequest);
	  } catch(ElasticsearchException e) {
		  LOG.error("Exception as insertCarList " + e.getDetailedMessage());
	  } catch (java.io.IOException ex){
		  LOG.error("Exception as insertCarList " + ex.getLocalizedMessage());
	  }
	}
	  
	public void deleteCarIndex() {
	  DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(INDEX, TYPE);
	  try {
		  // ugly workaround since the headers field can't be null, trying 
		  // to guess a reasonable number of preexisting headers.
		  // Should be fixed in the next version of the RestHighLevelClient API.
		  Header[] headers = {
				    new BasicHeader("Content-type", "application/application/json")
				    ,new BasicHeader("Accept", "text/html,text/xml,application/xml")
				    ,new BasicHeader("Connection", "keep-alive")
				    ,new BasicHeader("keep-alive", "115")
				    ,new BasicHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
				};
		    restHighLevelClient.indices().delete(deleteIndexRequest, headers);
	  } catch(ElasticsearchException e) {
		  LOG.error("Exception at deleteCarIndex " + e.getDetailedMessage());
	  } catch (java.io.IOException ex){
		  LOG.error("Exception at deleteCarIndex " + ex.getLocalizedMessage());
	  }
	}
	  
    public Map<String, Object> getCarById(String id){
	  GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
	  GetResponse getResponse = null;
	  try {
	      getResponse = restHighLevelClient.get(getRequest);
	  } catch (java.io.IOException e){
		  LOG.error("Exception at getCarById " + e.getLocalizedMessage());
	  }
	  Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
	  return sourceAsMap;
	}

	public List<CarDoc> findByMake(String query) {
		List<CarDoc> retList = new ArrayList<CarDoc>();
	    try {
			SearchRequest searchRequest = createMatchSearchRequest(MAKE, query); 
			executeSearch(retList, searchRequest);
	    } catch (java.io.IOException e){
			LOG.error("Exception at findByMake " + e.getLocalizedMessage());
	    }
	    return retList;
	}

	public List findByYear(String query) {
		List<CarDoc> retList = new ArrayList<CarDoc>();
	    try {
			SearchRequest searchRequest = createMatchSearchRequest(YEAR, query); 
			executeSearch(retList, searchRequest);
	    } catch (java.io.IOException e){
			LOG.error("Exception at findByYear " + e.getLocalizedMessage());
	    }
	    return retList;
	}
	
	private void executeSearch(List<CarDoc> retList, SearchRequest searchRequest) throws IOException {
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
		SearchHits searchHits = searchResponse.getHits();
		LOG.error("Total Hits " + searchHits.getTotalHits());
		for (SearchHit hit : searchHits) {
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			retList.add(new CarDoc((String)sourceAsMap.get("id"), 
					(String)sourceAsMap.get("year"), 
					(String)sourceAsMap.get("make"), 
					(String)sourceAsMap.get("name")));
		}
	}

	private SearchRequest createMatchSearchRequest(String field, String query) {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchQuery(field, query)); 
		searchRequest.source(searchSourceBuilder);
		return searchRequest;
	}

}
