package com.api.tests;

import com.api.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class UI_tests {

    @BeforeAll
    public static void setup(){
        //ords.uri=https://uinames.com/api/
        RestAssured.baseURI= ConfigurationReader.getProperty("ords.uri");
    }

 // No params test
//1. Send a get request without providing any parameters
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that name, surname, gender, region fields have value

    @Test
    public void NoParamsTest_1(){
        given()
                // .header("accept","application/json; charset=utf-8")
                //  .accept("application/json; charset=utf-8")
                .accept(ContentType.JSON)//3 way to get content type
         .when().get()
         .then().assertThat().statusCode(200)
         .and().assertThat().contentType("application/json; charset=utf-8")
                    .body("name",is(notNullValue()))
                    .body("surname",is(notNullValue()))
                    .body("gender",is(notNullValue()))
                    .body("region",is(notNullValue()))
                   .log().body(true);
    }
// Gender test
//1. Create a request by providing query parameter: gender, male or female
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that value of gender field is same from step 1
    @Test//done
    public void GenderTest_2(){
          JsonPath jsonP=
          given()
                  .accept(ContentType.JSON)
             // pathParam("gender","male").get(???????);//Error does not have pathParam
                  .queryParam("gender","female")
          .when().get()
           .then()
                  .assertThat().statusCode(200)
                  .assertThat().contentType("application/json; charset=utf-8")
                  .extract().jsonPath();

                   Map<String,String> Mymap = jsonP.get();
                   assertEquals("female", Mymap.get("gender"));
     //

    }
//    2 params test
//1. Create a request by providing query parameters: a valid region and gender
// NOTE: Available region values are given in the documentation
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that value of gender field is same from step 1
//4. Verify that value of region field is same from step 1
    @Test
    public void TwoParamsTest_3(){
    JsonPath jsonPath= given()
         .queryParam("region","Brazil")
         .queryParam("gender","male")
         .when().get()
         .then().assertThat().statusCode(200)
         .assertThat().contentType("application/json; charset=utf-8")
         .extract().jsonPath();
                Map<String,String> map = jsonPath.get();
                assertTrue(map.get("region").equals("Brazil") && map.get("gender").equals("male"));

    }
//Invalid gender test
//1. Create a request by providing query parameter: invalid gender
//2. Verify status code 400 and status line contains Bad Request
//3. Verify that value of error field is Invalid gender
    @Test
    public void InvalidGenderTest_4(){
      Response response=  given().
              queryParam("gender","unix")
              .when().get();
      assertEquals(400,response.getStatusCode());
      assertTrue(response.statusLine().contains("Bad Request"));
      assertTrue(response.asString().contains("Invalid gender"));

    }
//  Invalid region test
//1. Create a request by providing query parameter: invalid region
//2. Verify status code 400 and status line contains Bad Request
//3. Verify that value of error field is Region or language not found
    @Test
    public void InvalidRegionTest_5(){
        Response response= given().
                queryParam("region","Uzbekistan").get();
        assertEquals(400,response.getStatusCode());
        assertTrue(response.statusLine().contains("Bad Request"));
        assertTrue(response.asString().contains("Region or language not found"));
    }
// Amount and regions test
//1. Create request by providing query parameters: a valid region and amount (must be bigger than 1)
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that all objects have different name+surname combination
    @Test
public void AmountAndRegionsTest(){
    Response response = given()
                .accept(ContentType.JSON)
                .queryParam("region","Brazil")
                .queryParam("amount",4)
                .when().get();
                 response.then().assertThat().statusCode(200);
        JsonPath json = response.thenReturn().jsonPath();
        List<Map<?,?>>list = json.get();
        Set<String> fullNames = new LinkedHashSet<>();
        for(int i= 0; i < list.size();i++){
            fullNames.add(list.get(i).get("name") + " " + list.get(i).get("surname"));
        }
        assertTrue(fullNames.size() == 4);
    //we gave Map <String> bc Map key always will be String ,<Object> Map value we dont know what data type will be
   // List <Map<String,Object>> Allmap=json.getList("");
   //why we didnt put anithing in path string bc json starting with []array already at root level
    //instead of item:{}, we are geting directly
     //   System.out.println(Allmap);
//        for( Map<String,Object> each: Allmap){
//           // assertNotEquals(each.values(),"llll");
//        }


}
//3 params test
//1. Create a request by providing query parameters: a valid region, gender and amount
// (must be bigger than 1)
//2. Verify status code 200, content type application/json; charset=utf-8
// 3. Verify that all objects the response have the same region and gender passed in step 1
@Test
public void Three_ParamsTest(){

                given().accept(ContentType.JSON)
               .queryParam("region","Brazil")
               .queryParam("gender","male")
               .queryParam("amount",5)
               .when().get()
               .then().assertThat().statusCode(200)
               .assertThat().contentType("application/json; charset=utf-8")
                        .body("region",hasItems("Brazil"))
                        .body("gender",hasItems("male"))
                .log().all(true);

}
// Amount count test
//1. Create a request by providing query parameter: amount (must be bigger than 1)
//2. Verify status code 200, content type application/json; charset=utf-8
//3. Verify that number of objects returned in the response is same as the amount passed in step 1
@Test
public void AmountCountTest(){//done
     given()
            .accept(ContentType.JSON)
            .queryParam("amount",5)
     .when().get()
     .then().assertThat().statusCode(200)
            .assertThat().contentType("application/json; charset=utf-8")
             .body("", hasSize(5))
             .log().all(true);

}

























}
