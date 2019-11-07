package com.nva.tpcell.models


data class Student(
    //only email compulsory for now, may change later
    val email: String,
    val institute_id: String? = null,
    val session_year: String? = null,
    val enroll: String? = null,
    val name: String? = null,
    val course: String? = null,
    val branch: String? = null,
    val phone: String? = null,
    val aggregate_10th: Number? = null,
    val aggregate_12th: Number? = null,
    val aggregate_college: Number? = null,
    val backlog: Number? = null,
    val gap_years: Number? = null
)