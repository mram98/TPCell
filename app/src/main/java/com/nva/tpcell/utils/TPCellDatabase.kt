package com.nva.tpcell.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.nva.tpcell.models.Student
import java.io.Serializable

class TPCellDatabase {
    val db = FirebaseFirestore.getInstance()
    val dbStudentsRef = db.collection("students")
    val dbAdminsRef = db.collection("admins")

    val stringStudentFields = arrayOf(
        "email",
        "institute_id",
        "session_year",
        "enroll",
        "name",
        "course",
        "branch",
        "phone"
    )
    val numberStudentFields =
        arrayOf("aggregate_10th", "aggregate_12th", "aggregate_college", "backlog", "gap_years")

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

    fun addStudentObject(context: Context, student: Student) {

        // Making HashMap from Student object
        val studentData = unpackStudent(student)

        // Writing document with email as ID and hashmap as data
        dbStudentsRef.document(student.email)
            .set(studentData)
            .addOnSuccessListener {
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
            }
    }

    fun deleteStudent(context: Context, email: String) {

        dbStudentsRef.document(email)
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
//        dbStudentsRef.document(student.email)
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
        addStudentObject(context, student)

    }

    fun updateStudentField(field: String, value: String) {

    }

    fun checkAdmin(email: String?): Boolean {
        var isAdmin = false
        if (email != null) {
            dbAdminsRef.document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        isAdmin = true
                        //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
        return isAdmin
    }

    fun filterStudent(
        aggregate_10th: Number,
        aggregate_12th: Number,
        aggregate_college: Number,
        backlog: Number,
        gap_years: Number
    ) {

        // Potentially add branch and course here
        val query = dbStudentsRef.whereGreaterThanOrEqualTo("aggregate_10th", aggregate_10th)
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

        dbStudentsRef.document(email)
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