package com.koligrum.test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import javax.print.DocFlavor;

import static io.restassured.RestAssured.given;

public class UsersTest {
    final static String url = "http://localhost";
    final static int port = 1234;


    @Test
    public void User(){
        // Create a user
        System.out.println("==== Create User ====");
        //mendeklarasikan isian yang akan dimasukkan ke dalam form
        String firstName = "Peni";
        String lastName = "Kurniawati";
        int age = 23;
        String occupation = "Quality Assurance";
        String nationality = "Indonesia";
        String hobby = "Cooking";
        String gender = "FEMALE";

        //memasukkan isian ke dalam body POST
        String body = "{\n"
                + "\n"
                + "  \"firstName\": \"" + firstName + "\",\n"
                + "  \"lastName\": \"" + lastName + "\",\n"
                + "  \"age\": " + age + ",\n"
                + "  \"occupation\": \"" + occupation + "\",\n"
                + "  \"nationality\": \"" + nationality + "\",\n"
                + "  \"hobbies\": [\n"
                + "    \""+ hobby +"\"\n"
                + "  ],\n"
                + "  \"gender\": \""+ gender +"\"\n"
                + "}";

        //untuk cek response hasil dari input POST
        Response responsePostUser = given()
                .baseUri(url)
                .port(port)
                .basePath("v1")
                .header("accept","application/json")
                .contentType(ContentType.JSON)
                .body(body).log().all() // ini untuk lihat isi dari body yang diinputkan
                .when()
                .post("users");

        responsePostUser.getBody().prettyPrint(); //ini juga untuk lihat hasil setelah dicreate, bakalan ada id dan juga waktu create

        // Verify user successfully created by ID
        System.out.println("===== Verify user successfully created by ID ====");
        String id = responsePostUser.path("id");
        Assert.assertEquals(200, responsePostUser.statusCode());
        System.out.println("Response API saat create user yaitu : " + responsePostUser.statusCode());

        //Verify user successfully created using json path
        System.out.println("===== Verify user successfully created using json path ====");
        Response responseGetById = given() // Response ini berguna untuk mengeluarkan object
                .baseUri(url)
                .port(port)
                .header("accept", "application/json")
                .pathParam("id", id)
                .basePath("v1")
                .log().all()
                .when()
                .get("users/{id}"); // id dikurung kurawal, itu jadi nanti diisi angka 0 yang udah kita declare

        responseGetById.getBody().prettyPrint();
        Assert.assertEquals(200, responseGetById.statusCode());
        Assert.assertEquals(firstName, responseGetById.path("firstName"));
        Assert.assertEquals(lastName, responseGetById.path("lastName"));
        Assert.assertEquals(Integer.valueOf(age), responseGetById.path("age"));
        Assert.assertEquals(occupation, responseGetById.path("occupation"));
        Assert.assertEquals(nationality, responseGetById.path("nationality"));
        Assert.assertEquals(hobby, responseGetById.path("hobbies[0]"));
        Assert.assertEquals(gender, responseGetById.path("gender"));

        // Update a user
        System.out.println("==== Update User ====");
        String firstNameUpdate = "Kurnia";
        String lastNameUpdate = "Koligrum";
        int ageUpdate = 22;
        String occupationUpdate = "Quality Assurance Engineer";
        String nationalityUpdate = "Indonesia";
        String hobbyUpdate = "Cooking";
        String genderUpdate = "FEMALE";

        String bodyUpdate = "{\"id\": \"" + id + "\",\n"
                + "  \"firstName\": \"" + firstNameUpdate + "\",\n"
                + "  \"lastName\": \"" + lastNameUpdate + "\",\n"
                + "  \"age\": " + ageUpdate + ",\n"
                + "  \"occupation\": \"" + occupationUpdate + "\",\n"
                + "  \"nationality\": \"" + nationalityUpdate + "\",\n"
                + "  \"hobbies\": [\n"
                + "    \""+ hobbyUpdate +"\"\n"
                + "  ],\n"
                + "  \"gender\": \""+ genderUpdate +"\"\n"
                + "}";

        Response responsePutUser = given()
                .baseUri(url)
                .port(port)
                .basePath("v1")
                .header("accept","application/json")
                .contentType(ContentType.JSON)
                .body(bodyUpdate).log().all()
                .when()
                .put("users");

        responsePutUser.getBody().prettyPrint();

        //Verify user successfully updated by id
        System.out.println("==== Verify user successfully update by id ====");
        String idPut = responsePutUser.path("id");
        Assert.assertEquals(200, responsePutUser.statusCode());

        Response responseGetByIdFromUpdate = given()
                .baseUri(url)
                .port(port)
                .basePath("v1")
                .header("accept", "application/json")
                .pathParam("id", idPut)
                .log().all()
                .when()
                .get("users/{id}");

        //Verify user successfully update from json path
        System.out.println("==== Verify user successfully update from JSON path ====");
        responseGetByIdFromUpdate.getBody().prettyPrint();
        Assert.assertEquals(200, responseGetByIdFromUpdate.statusCode());
        Assert.assertEquals(firstNameUpdate, responseGetByIdFromUpdate.path("firstName"));
        Assert.assertEquals(lastNameUpdate, responseGetByIdFromUpdate.path("lastName"));
        Assert.assertEquals(Integer.valueOf(ageUpdate), responseGetByIdFromUpdate.path("age"));
        Assert.assertEquals(occupationUpdate, responseGetByIdFromUpdate.path("occupation"));
        Assert.assertEquals(nationalityUpdate, responseGetByIdFromUpdate.path("nationality"));
        Assert.assertEquals(hobbyUpdate, responseGetByIdFromUpdate.path("hobbies[0]"));
        Assert.assertEquals(genderUpdate, responseGetByIdFromUpdate.path("gender"));

        //Delete User
        System.out.println("=== Delete User ====");
        Response responseDeleteById = given()
              .baseUri(url)
              .port(port)
              .basePath("v1")
              .pathParam("id", idPut)
              .header("accept","*/*")
                .log().all()
                .when()
                .delete("users/{id}");

        //verify user successfully deleted by id
        System.out.println("==== Verify user successfully deleted by id ====");
        Assert.assertEquals(200, responseDeleteById.statusCode());

        Response responseGetByIdFromDelete = given()
                .baseUri(url)
                .port(port)
                .basePath("v1")
                .header("accept", "application/json")
                .pathParam("id", idPut)
                .log().all()
                .when()
                .delete("users/{id}");

        responseGetByIdFromDelete.getBody().prettyPrint();
        //verify data after delete by id
        System.out.println("=== Verify data after deleted ====");
        Assert.assertEquals("user not found", responseGetByIdFromDelete.path("message"));
        System.out.println("Message setelah didelete yaitu : " + responseGetByIdFromDelete.path("message"));
        Assert.assertEquals(404, responseGetByIdFromDelete.statusCode());
        System.out.println("Response API saat data sudah didelete yaitu :" + responseGetByIdFromDelete.statusCode());
    }
}