package com.wise23.chariteed.service;

import java.util.Random;

public class PractitionerService {

    public static String generatePassword(String firstName, String lastName) {
        Random rand = new Random();
        int randomNumber = 100000 + rand.nextInt(900000);

        return firstName + "_" + lastName + "_" + randomNumber;
    }
}