package com.sample.common

enum class Gender(val gender: String) {
    Male("Male"),
    Female("Female"),
    Transgender("Transgender"),
}

enum class BloodGroup(val blood: String) {
    A_POS("A +ve"),
    B_POS("B +ve"),
    O_POS("O +ve"),
    AB_POS("AB +ve"),
    A_NEG("A -ve"),
    B_NEG("B -ve"),
    O_NEG("O -ve"),
    AB_NEG("AB -ve"),
    UNKNOWN("Unknown"),
}

enum class HospitalType(val type: String) {
    PRIVATE("Private"),
    GOVERNMENT("Government"),
}
