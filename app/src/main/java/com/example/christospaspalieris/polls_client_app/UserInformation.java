package com.example.christospaspalieris.polls_client_app;

/**
 * Created by Christos Paspalieris on 12/22/2017.
 */

public class UserInformation {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String age;
    private String sex;
    private String age_group;
    private String topic;



    public UserInformation(String username, String firstname, String lastname, String email, String password, String age, String sex, String topic, String age_group){
        this.username = username;
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.password = password;
        this.age = age;
        this.sex = sex;
        this.topic = topic;
        this.age_group = age_group;
    }


    public String getEmail() { return email; }

    public String getUsername() {
        return username;
    }

    public String getAge() { return age; }

    public String getFirstName(){ return firstName; }

    public String getLastName(){ return lastName; }

    public String getPassword(){ return password; }

    public String getSex() {
        return sex;
    }

    public String getTopic() {
        return topic;
    }

    public String getAge_group() {
        return age_group;
    }
}
