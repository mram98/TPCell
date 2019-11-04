package com.nva.tpcell.database

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class TPCellDatabase {
    val db = FirebaseFirestore.getInstance()
    val dbStudentRef = db.collection("students")

    private fun unpackStudent(student: Student): HashMap<String, Serializable?> {
        // Converts object to HashMap
        return hashMapOf(
            "email" to student.email,
            "institute_id" to student.institute_id,
            "session_year" to student.session_year,
            "enroll" to student.enroll,
            "name" to student.name,
            "course" to student.course,
            "branch" to student.branch,
            "phone" to student.phone,
            "aggregate_10th" to student.aggregate_10th,
            "aggregate_12th" to student.aggregate_12th,
            "aggregate_college" to student.aggregate_college,
            "backlog" to student.backlog,
            "gap_years" to student.gap_years
        )
    }

    fun addStudent(context: Context, student: Student) {

        // Making HashMap from Student object
        val studentData = unpackStudent(student)

        // Writing document with email as ID and hashmap as data
        dbStudentRef.document(student.email)
            .set(studentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun deleteStudent(context: Context, email: String) {

        dbStudentRef.document(email)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun updateStudent(context: Context, student: Student) {

        // Delete the original document
//        dbStudentRef.document(student.email)
//            .delete()
//            .addOnSuccessListener {
//                // Add the student again if deletion is successful
//                addStudent(context, student)
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
//            }

        // TODO Fix Double Toast here
        deleteStudent(context, student.email)
        addStudent(context, student)

    }

    fun filterStudent(
        aggregate_10th: Number,
        aggregate_12th: Number,
        aggregate_college: Number,
        backlog: Number,
        gap_years: Number
    ) {

        // Potentially add branch and course here
        val query = dbStudentRef.whereGreaterThanOrEqualTo("aggregate_10th", aggregate_10th)
            .whereGreaterThanOrEqualTo("aggregate_12th", aggregate_12th)
            .whereGreaterThanOrEqualTo("aggregate_college", aggregate_college)
            .whereGreaterThanOrEqualTo("backlog", backlog)
            .whereGreaterThanOrEqualTo("gap_years", gap_years)

        //Execute Query
        query.get()
            .addOnSuccessListener { documents ->
                // TODO Show this stuff in admin list of students recycler̥̥
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
    }

    fun getStudent(context: Context, email: String): Student? {

        val studentData: Student? = null

        dbStudentRef.document(email)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                var studentData = documentSnapshot.toObject(Student::class.java)
                // TODO get this data to the student fragment
            }
            .addOnFailureListener {
                Toast.makeText(context, "Student doesn't exist", Toast.LENGTH_LONG).show()
            }
        return studentData
    }
}